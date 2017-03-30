package com.lijuyong.startup.service;


import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;



/**
 * Created by john on 2017/3/30.
 */
@Service
public class DcsTask {

    @Autowired
    AmqpTemplate amqpTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RabbitListener(queues = "cron-task-queue")
    void fireDcsTask(@Payload String msg, Channel channel, Message message){

        long tag =message.getMessageProperties().getDeliveryTag();
        log.info("here is the message:{},tag is {}",msg,tag);

        try {
            //判断数据库中此条配置是否被删除，如果过删除那么,直接拒绝并且不入队列
            //channel.basicReject(tag, false);
            //判断数据库中没有删除，那么发消息给下一个队列
            amqpTemplate.convertAndSend("dcs-task-queue","good to go with dcs");
            //并且发送ackg
            channel.basicAck(tag,false);
            //如果处理失败
        }
        catch (Exception exc){
            log.error("we hav exception",exc);
        }

    }
    @RabbitListener(queues = "dcs-task-queue")
    void excuteDcsTask(@Payload String msg,Channel channel,Message message){
        long tag =message.getMessageProperties().getDeliveryTag();

        try {

            //这里真正的处理任务。
            log.info("here is the message:{},tag is {}",msg,tag);


            //并且发送ackg
            channel.basicAck(tag,false);
            //如果处理失败
        }
        catch (Exception exc){
            log.error("we hav exception",exc);
        }

    }
}
