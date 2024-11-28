package com.example.find_it.controller;

import com.example.find_it.dto.PersonalMessage;
import com.example.find_it.service.KafkaConsumerService;
import com.example.find_it.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Message API", description = "Kafka를 활용한 개인 메시지 송수신 API")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;

    public MessageController(KafkaProducerService kafkaProducerService, KafkaConsumerService kafkaConsumerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @Operation(
            summary = "개인 메시지 전송",
            description = "지정된 수신자 ID로 개인 메시지를 전송합니다."
    )
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @Parameter(description = "메시지를 보낸 사용자 ID", required = true) @RequestParam Long senderId,
            @Parameter(description = "메시지를 받는 사용자 ID", required = true) @RequestParam Long receiverId,
            @Parameter(description = "메시지 내용", required = true) @RequestBody String message
    ) {
        String topic = "private-message-" + receiverId;
        kafkaProducerService.sendMessage(topic, new PersonalMessage(senderId, receiverId, message));
        return ResponseEntity.ok("Message sent successfully.");
    }

    @Operation(
            summary = "개인 메시지 수신",
            description = "지정된 수신자 ID에 도착한 메시지 목록을 반환합니다. 보낸 사람 ID를 선택적으로 필터링할 수 있습니다."
    )
    @GetMapping("/receive")
    public ResponseEntity<List<PersonalMessage>> receiveMessages(
            @Parameter(description = "메시지를 받는 사용자 ID", required = true) @RequestParam Long receiverId,
            @Parameter(description = "메시지를 보낸 사용자 ID (선택)", required = false) @RequestParam(required = false) Long senderId
    ) {
        String topic = "private-message-" + receiverId;
        List<PersonalMessage> messages = kafkaConsumerService.getMessages(topic, senderId);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "상대방별 최신 메시지 조회",
            description = "본인(receiverId)에게 온 메시지 중 상대방별 최신 메시지를 반환합니다."
    )
    @GetMapping("/latest-messages")
    public ResponseEntity<List<PersonalMessage>> getLatestMessages(
            @Parameter(description = "메시지를 받는 사용자 ID", required = true) @RequestParam Long receiverId
    ) {
        String topic = "private-message-" + receiverId;
        List<PersonalMessage> messages = kafkaConsumerService.getLatestMessages(topic);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "특정 상대방과의 전체 메시지 내역 조회",
            description = "본인(receiverId)과 특정 상대방(senderId)의 모든 메시지 내역을 반환합니다."
    )
    @GetMapping("/conversation")
    public ResponseEntity<List<PersonalMessage>> getConversation(
            @Parameter(description = "메시지를 받는 사용자 ID", required = true) @RequestParam Long receiverId,
            @Parameter(description = "메시지를 보낸 사용자 ID", required = true) @RequestParam Long senderId
    ) {
        String topic = "private-message-" + receiverId;
        List<PersonalMessage> messages = kafkaConsumerService.getConversation(topic, senderId);
        return ResponseEntity.ok(messages);
    }
}
