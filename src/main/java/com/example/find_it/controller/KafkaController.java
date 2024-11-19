package com.example.find_it.controller;

import com.example.find_it.dto.MessageDTO;
import com.example.find_it.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;
    private final List<MessageDTO> receivedMessages = new ArrayList<>();

    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    // 메시지 전송
    @PostMapping("/publish")
    public String sendMessage(@RequestParam String topic, @RequestBody Object message) {
        kafkaProducerService.sendMessage(topic, message);
        return "Message sent!";
    }

    // Kafka 메시지 소비
    @KafkaListener(topics = "your-topic-name", groupId = "${kafka.consumer.group-id}")
    public void consumeMessage(ConsumerRecord<String, Object> record) {
        MessageDTO messageDTO = new MessageDTO(
                record.topic(),
                record.partition(),
                record.offset(),
                record.value()
        );
        receivedMessages.add(messageDTO);
        System.out.println("Received message: " + messageDTO);
    }

    // 수신된 메시지 확인용 API
    @GetMapping("/messages")
    public List<MessageDTO> getReceivedMessages() {
        return receivedMessages;
    }
}
