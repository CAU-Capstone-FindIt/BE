package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<Long, PersonalMessage> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<Long, PersonalMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Long key, PersonalMessage message) {
        kafkaTemplate.send(topic, key, message);
    }
}

