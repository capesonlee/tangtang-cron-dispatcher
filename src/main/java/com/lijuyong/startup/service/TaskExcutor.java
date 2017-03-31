package com.lijuyong.startup.service;


import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by john on 2017/3/30.
 * 这是一个测试文件，实际上可以单独部署，作为任务执行集群，订阅不同的消息。
 */
@Service
public class TaskExcutor {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Autowired
    AmqpTemplate amqpTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @RabbitListener(queues = "dcs-task-queue")
    void excuteDcsTask(@Payload String msg,Channel channel,Message message){
        long tag =message.getMessageProperties().getDeliveryTag();

        try {

            //这里真正的处理任务。
            log.info("now {} here is the message:{},tag is {}",
                    dateFormat.format(new Date()),msg,tag);


            //并且发送ackg
            channel.basicAck(tag,false);
            //如果处理失败
        }
        catch (Exception exc){
            log.error("we hav exception",exc);
        }

    }
}
