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

@Tag(name = "메시지 API", description = "Kafka를 활용하여 개인 메시지를 송수신하기 위한 API입니다. 주어진 사용자의 메시지를 주고받고, 대화 내역을 조회할 수 있습니다.")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final String SHARED_TOPIC = "chat-messages"; // 공유 토픽 이름

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;

    public MessageController(KafkaProducerService kafkaProducerService, KafkaConsumerService kafkaConsumerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @Operation(
            summary = "개인 메시지 전송",
            description = "지정된 수신자(receiverId)에게 메시지를 전송합니다. 송신자(senderId)와 메시지 내용을 입력하세요."
    )
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @Parameter(description = "메시지를 보낸 사용자 ID (송신자)", required = true) @RequestParam Long senderId,
            @Parameter(description = "메시지를 받는 사용자 ID (수신자)", required = true) @RequestParam Long receiverId,
            @Parameter(description = "메시지의 내용. 텍스트 형식이어야 합니다.", required = true) @RequestBody String message
    ) {
        // 공유 토픽으로 메시지 전송, receiverId를 파티션 키로 사용
        kafkaProducerService.sendMessage(SHARED_TOPIC, receiverId, new PersonalMessage(senderId, receiverId, message));
        return ResponseEntity.ok("Message sent successfully.");
    }

    @Operation(
            summary = "개인 메시지 수신",
            description = "수신자(receiverId)가 받은 모든 메시지의 목록을 반환합니다. 선택적으로 특정 송신자(senderId)로부터 받은 메시지만 필터링할 수 있습니다."
    )
    @GetMapping("/receive")
    public ResponseEntity<List<PersonalMessage>> receiveMessages(
            @Parameter(description = "메시지를 받는 사용자 ID (수신자)", required = true) @RequestParam Long receiverId,
            @Parameter(description = "특정 송신자의 메시지만 필터링하려면 송신자 ID를 입력하세요. (선택적 필드)", required = false) @RequestParam(required = false) Long senderId
    ) {
        List<PersonalMessage> messages = kafkaConsumerService.getMessages(SHARED_TOPIC, receiverId, senderId);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "상대방별 최신 메시지 조회",
            description = "사용자(userId)가 주고받은 모든 메시지 중, 각 상대방별 최신 메시지 하나씩을 반환합니다."
    )
    @GetMapping("/latest-messages")
    public ResponseEntity<List<PersonalMessage>> getLatestMessages(
            @Parameter(description = "메시지를 주고받은 사용자 ID", required = true) @RequestParam Long userId
    ) {
        List<PersonalMessage> messages = kafkaConsumerService.getLatestMessagesForReceiverAndSender(SHARED_TOPIC, userId);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "두 사용자 간 대화 내역 조회",
            description = "두 사용자(userA와 userB) 간의 모든 대화 메시지를 조회합니다."
    )
    @GetMapping("/conversation/between")
    public ResponseEntity<List<PersonalMessage>> getConversationBetween(
            @Parameter(description = "대화를 조회할 첫 번째 사용자 ID", required = true) @RequestParam Long userA,
            @Parameter(description = "대화를 조회할 두 번째 사용자 ID", required = true) @RequestParam Long userB
    ) {
        List<PersonalMessage> messages = kafkaConsumerService.getConversationForBoth(SHARED_TOPIC, userA, userB);
        return ResponseEntity.ok(messages);
    }
}
