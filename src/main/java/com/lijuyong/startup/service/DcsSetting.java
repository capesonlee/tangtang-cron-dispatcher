package com.lijuyong.startup.service;

import com.lijuyong.startup.manager.amqp.DcsTaskMessagePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by john on 2017/3/30.
 */
@Service
public class DcsSetting {

    @Autowired
    AmqpTemplate amqpTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void addNewIndicator(String expression){
        CronTrigger cronTrigger = new CronTrigger(expression);
        TriggerContext triggerContext = new SimpleTriggerContext();
        Date date = cronTrigger.nextExecutionTime(triggerContext);

        amqpTemplate.convertAndSend("cron-task-exchange",
                "cron-task-queue",
                "command:time to retrieve data",
                new DcsTaskMessagePostProcessor());

    }
}
