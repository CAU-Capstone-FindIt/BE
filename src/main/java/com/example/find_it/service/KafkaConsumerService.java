package com.example.find_it.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "your-topic-name", groupId = "${kafka.consumer.group-id}")
    public void consumeMessage(Object message) {
        System.out.println("Received message: " + message);
    }
}
