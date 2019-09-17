package com.bank.atm.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    private RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    @Autowired
    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value ("${amsalj.rabbitmq.exchange}")
    private String exchange;
    @Value ("${amsalj.rabbitmq.routingkey}")
    private String routingKey;
    @Value ("${amsalj.rabbitmq.queue}")
    private String queueName;

    public void produceMsg(String msg){
        logger.info (exchange);
        logger.info (routingKey);
        rabbitTemplate.convertAndSend (exchange,routingKey,msg);

    }


}
