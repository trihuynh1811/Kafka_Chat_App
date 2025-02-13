package com.kafka.ChatApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static org.apache.kafka.streams.kstream.EmitStrategy.log;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;

//    @KafkaListener(
//            topics = KafkaConstants.KAFKA_TOPIC,
//            groupId = KafkaConstants.GROUP_ID
//    )
//    public void listen(Message message) {
//        System.out.println("sending via kafka listener..");
//        System.out.println("Received message: " + message);
//        log.info(message.getTopic());
//    }
}

