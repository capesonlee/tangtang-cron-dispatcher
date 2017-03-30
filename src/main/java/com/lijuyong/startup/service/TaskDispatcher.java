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
        //这里应该有一个写数据库的动作，用于保存配置。


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
            //判断数据库中此条配置是否被删除，如果过删除那么,直接拒绝并且不入队列
            //channel.basicReject(tag, false);
            //判断数据库中没有删除，那么发消息给下一个队列
            amqpTemplate.convertAndSend("dcs-task-queue",
                    "invoke task by cron expression");

            //messageProperties 没有带过来，可以通过消息体带过来。
            //如果过是cron 表达式那么需要重新评估，把上次执行的时间计入到消息体或者数据库中
            //再次通过excutor评估
            //addNewIndicator();
            Integer delay = 15000;

            //并且发送ackg
            channel.basicAck(tag,false);

            amqpTemplate.convertAndSend("cron-task-exchange",
                    "cron-task-queue",
                    "command:time to excute task",
                    new DelayMessagePostProcessor(delay));
            //如果处理失败
        }
        catch (Exception exc){
            log.error("we hav exception",exc);
        }

    }
}
