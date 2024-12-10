package com.example.find_it.controller;

import com.example.find_it.domain.Member;
import com.example.find_it.dto.PersonalMessageDto;
import com.example.find_it.service.ItemService;
import com.example.find_it.service.KafkaConsumerService;
import com.example.find_it.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
@Tag(name = "메시지 API", description = "Kafka를 활용하여 개인 메시지를 송수신하기 위한 API입니다.")
public class MessageController {

    private final ItemService itemService;
    private final KafkaConsumerService kafkaConsumerService;
    private final MemberService memberService;

    @Operation(summary = "게시글 쪽지 전송", description = "지정된 게시글과 관련하여 쪽지를 보냅니다.")
    @PostMapping("/{itemId}/message/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestParam String itemType,
            @RequestParam Long receiverId,
            @RequestBody String content) {
        // 현재 로그인한 사용자 정보 가져오기
        Member sender = memberService.getMemberByPrincipal(userDetails);

        // 메시지 전송 로직 호출
        itemService.sendItemMessage(itemId, itemType, sender.getId(), receiverId, content);
        return ResponseEntity.ok("Message sent successfully.");
    }

    @Operation(summary = "수신 메시지 조회", description = "특정 사용자가 받은 모든 메시지를 반환합니다.")
    @GetMapping("/receive")
    public ResponseEntity<List<PersonalMessageDto>> receiveMessages(
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) String itemType) {
        List<PersonalMessageDto> messages = kafkaConsumerService.getMessages(receiverId, senderId, itemId, itemType);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "최신 메시지 조회", description = "사용자가 주고받은 모든 메시지 중 각 물건별 최신 메시지를 반환합니다.")
    @GetMapping("/latest-messages")
    public ResponseEntity<List<PersonalMessageDto>> getLatestMessages(@RequestParam Long userId) {
        List<PersonalMessageDto> messages = kafkaConsumerService.getLatestMessagesForReceiverAndSender(userId);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "대화 조회", description = "두 사용자 간의 대화를 물건 단위로 조회합니다.")
    @GetMapping("/conversation/between")
    public ResponseEntity<List<PersonalMessageDto>> getConversationBetween(
            @RequestParam Long userA,
            @RequestParam Long userB,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) String itemType) {
        List<PersonalMessageDto> messages = kafkaConsumerService.getConversationForBoth(userA, userB, itemId, itemType);
        return ResponseEntity.ok(messages);
    }
}


