package com.example.find_it.controller;

import com.example.find_it.dto.PersonalMessage;
import com.example.find_it.service.KafkaConsumerService;
import com.example.find_it.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;

    public MessageController(KafkaProducerService kafkaProducerService, KafkaConsumerService kafkaConsumerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    /**
     * 개인 쪽지 전송
     *
     * @param senderId  메시지 보낸 사람 ID
     * @param receiverId 메시지 받는 사람 ID
     * @param message   메시지 내용
     * @return 전송 상태
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestBody String message) {

        // 사용자 ID 기반의 토픽 생성
        String topic = "private-message-" + receiverId;

        // Kafka에 메시지 전송
        kafkaProducerService.sendMessage(topic, new PersonalMessage(senderId, receiverId, message));
        return ResponseEntity.ok("Message sent successfully.");
    }

    /**
     * 개인 쪽지 수신
     *
     * @param receiverId 메시지 받는 사람 ID
     * @param senderId   메시지 보낸 사람 ID (선택 사항)
     * @return 수신된 메시지 목록
     */
    @GetMapping("/receive")
    public ResponseEntity<List<PersonalMessage>> receiveMessages(
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long senderId) {
        // 사용자 ID 기반의 토픽 생성
        String topic = "private-message-" + receiverId;

        // Kafka에서 메시지 가져오기
        List<PersonalMessage> messages = kafkaConsumerService.getMessages(topic, senderId);
        return ResponseEntity.ok(messages);
    }

}
