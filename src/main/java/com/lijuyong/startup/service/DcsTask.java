package com.lijuyong.startup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;

/**
 * Created by john on 2017/3/30.
 */
@Service
public class DcsTask {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RabbitListener(queues = "cron-task-queue")
    void doDcsTask(Channel channel,String msg){
        log.info("here is the message:{}",msg);
    }
}
