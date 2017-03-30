package com.lijuyong.startup.service;

/**
 * Created by john on 2017/3/29.
 */


import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        evaluateTask();
    }

    public void evaluateTask(){
        CronTrigger cronTrigger = new CronTrigger("0/15 * * * * ?");
        TriggerContext triggerContext = new SimpleTriggerContext();
        Date date = cronTrigger.nextExecutionTime(triggerContext);
        log.info("next trigger time: {}", dateFormat.format(date));
    }
}