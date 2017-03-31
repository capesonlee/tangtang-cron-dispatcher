package com.lijuyong.startup.service;

import com.lijuyong.startup.manager.amqp.DelayMessagePostProcessor;
import com.rabbitmq.client.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by john on 2017/3/30.
 */
@Service
public class TaskDispatcher {

    @Autowired
    AmqpTemplate amqpTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //增加一个定时调度任务
    public void addNewIndicator(String expression){

        //to do: 与数据库配合
        //这里应该有一个写数据库的动作，用于保存配 expression。


        CronTrigger cronTrigger = new CronTrigger(expression);
        TriggerContext triggerContext = new SimpleTriggerContext();
        Date date = cronTrigger.nextExecutionTime(triggerContext);
        Date currentTime = new Date();
        Long delay = date.getTime() - currentTime.getTime();
        log.info("we will fire the event in sevreal minutes later {}",delay);

        amqpTemplate.convertAndSend("cron-task-exchange",
                "cron-task-queue",
                "command:time to excute task",
                new DelayMessagePostProcessor(new Integer(delay.intValue())));

    }

    //手动触发定时任务：通过发送一个特定的任务消息。
    public void manualInvokeTask(){
        amqpTemplate.convertAndSend("dcs-task-queue","invoke task manually");
    }

    //评估并发送任务执行消息。
    @RabbitListener(queues = "cron-task-queue")
    void fireDcsTask(@Payload String msg, Channel channel, Message message){

        MessageProperties messageProperties = message.getMessageProperties();
        long tag =messageProperties.getDeliveryTag();
        log.info("here is the message:{},tag is {}",msg,tag);

        try {

            //1.0 从数据库中读取该条配置
            //2.0 判断数据库中此条配置是否被删除，
            //2.A 如果过删除那么直接拒绝并且不重入入队列，本函数流程结束。
            //      channel.basicReject(tag, false);
            //2.B 如果状态正常，那么发消息给下一个队列
            amqpTemplate.convertAndSend("dcs-task-queue",
                    "invoke task by cron expression");


            //3.0 根据数据库重新计算表达式expresion, 然后重新生成下轮的出发消息
            //     这里模拟已经完成为15000
            Integer delay = 15000;
            amqpTemplate.convertAndSend("cron-task-exchange",
                    "cron-task-queue",
                    "command:time to excute task",
                    new DelayMessagePostProcessor(delay));



            //4.0  发送ack,表示此消息已经处理完成
            channel.basicAck(tag,false);
        }
        catch (Exception exc){
            log.error("we hav exception",exc);
        }

    }
}
