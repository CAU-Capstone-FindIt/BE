package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        if (message instanceof PersonalMessage) {
            ((PersonalMessage) message).setTimestamp(LocalDateTime.now());
        }
        kafkaTemplate.send(topic, message);
        System.out.println("Sent message: " + message);
    }

}
