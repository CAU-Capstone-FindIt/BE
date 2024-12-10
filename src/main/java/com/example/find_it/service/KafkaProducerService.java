package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessageDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<Long, PersonalMessageDto> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<Long, PersonalMessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Long key, PersonalMessageDto messageDto) {
        System.out.println("Sending message to Kafka: " + messageDto);
        kafkaTemplate.send(topic, key, messageDto);
    }
}
