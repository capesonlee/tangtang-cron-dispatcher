package com.lijuyong.startup.manager.amqp;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by john on 2017/3/27.
 */
@Configuration
public class ExchangeConfig {

    @Bean
    DirectExchange delayMessageExchange(AmqpAdmin amqpAdmin){

        Map<String, Object> args = new IdentityHashMap<String, Object>();

       // args.put("x-delayed-type","direct");
        DirectExchange directExchange =
                new DirectExchange("cron-task-exchange");

        directExchange.setDelayed(true);


        amqpAdmin.declareExchange(directExchange);
        return directExchange;
    }

    @Bean
    Queue cronTaskQueue(AmqpAdmin amqpAdmin){
        Queue queue = new Queue("cron-task-queue");
        amqpAdmin.declareQueue(queue);
        return  queue;
    }


   @Bean
   Binding bindCronTaskQueue(AmqpAdmin amqpAdmin,
                             Queue cronTaskQueue,
                             DirectExchange delayMessageExchange){
       Binding binding = BindingBuilder.bind(cronTaskQueue).
               to(delayMessageExchange)
               .withQueueName();
       amqpAdmin.declareBinding(binding);
       return binding;
   }

}
