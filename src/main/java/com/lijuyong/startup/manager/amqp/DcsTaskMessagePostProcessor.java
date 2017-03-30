package com.lijuyong.startup.manager.amqp;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * Created by john on 2017/3/28.
 */
public class DcsTaskMessagePostProcessor implements MessagePostProcessor {
    Integer delayMs;
    @Override
    public Message postProcessMessage(Message message) throws AmqpException{


       // message.getMessageProperties().setHeader("my-header","Hello John Nash");
        message.getMessageProperties().setDelay(delayMs);//延迟5秒发送
        return  message;
    }
    public DcsTaskMessagePostProcessor(Integer delayMs){
        this.delayMs = delayMs;
    }

}
