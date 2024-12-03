package com.example.find_it.service;

import com.example.find_it.domain.LostItem;
import com.example.find_it.domain.FoundItem;
import com.example.find_it.dto.PersonalMessageDto;
import com.example.find_it.repository.FoundItemRepository;
import com.example.find_it.repository.LostItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class KafkaConsumerService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;

    private final List<PersonalMessageDto> messageStore = new ArrayList<>();

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(PersonalMessageDto messageDto) {
        // Store the message in memory
        messageDto.setItemName(getItemName(messageDto.getItemId(), messageDto.getItemType()));
        messageDto.setItemImageUrl(getItemImageUrl(messageDto.getItemId(), messageDto.getItemType()));
        messageStore.add(messageDto);
    }

    // 특정 수신자(receiverId)와 발신자(senderId)의 메시지 조회 (물건 기준 포함)
    public List<PersonalMessageDto> getMessages(Long receiverId, Long senderId, Long itemId, String itemType) {
        return messageStore.stream()
                .filter(message -> message.getReceiverId().equals(receiverId)
                        && (senderId == null || message.getSenderId().equals(senderId))
                        && (itemId == null || message.getItemId().equals(itemId))
                        && (itemType == null || message.getItemType().equalsIgnoreCase(itemType)))
                .collect(Collectors.toList());
    }

    // 특정 사용자(userId)와 상대방 간의 최신 메시지 조회 (물건 기준 추가)
    public List<PersonalMessageDto> getLatestMessagesForReceiverAndSender(Long userId) {
        return messageStore.stream()
                .filter(message -> message.getSenderId().equals(userId) || message.getReceiverId().equals(userId))
                .collect(Collectors.groupingBy(message ->
                        message.getSenderId().equals(userId)
                                ? message.getReceiverId() + "-" + message.getItemId() + "-" + message.getItemType()
                                : message.getSenderId() + "-" + message.getItemId() + "-" + message.getItemType()
                ))
                .values().stream()
                .map(messages -> messages.stream()
                        .max(Comparator.comparing(PersonalMessageDto::getTimestamp))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 두 사용자 간의 대화 조회 (물건 기준 추가)
    public List<PersonalMessageDto> getConversationForBoth(Long userA, Long userB, Long itemId, String itemType) {
        return messageStore.stream()
                .filter(message ->
                        ((message.getSenderId().equals(userA) && message.getReceiverId().equals(userB)) ||
                                (message.getSenderId().equals(userB) && message.getReceiverId().equals(userA)))
                                && (itemId == null || message.getItemId().equals(itemId))
                                && (itemType == null || message.getItemType().equalsIgnoreCase(itemType))
                )
                .sorted(Comparator.comparing(PersonalMessageDto::getTimestamp))
                .collect(Collectors.toList());
    }

    // Helper to get the item's name based on its ID and type
    private String getItemName(Long itemId, String itemType) {
        if ("LOST".equalsIgnoreCase(itemType)) {
            return lostItemRepository.findById(itemId)
                    .map(LostItem::getName)
                    .orElse("Unknown Lost Item");
        } else if ("FOUND".equalsIgnoreCase(itemType)) {
            return foundItemRepository.findById(itemId)
                    .map(FoundItem::getName)
                    .orElse("Unknown Found Item");
        }
        return "Unknown Item";
    }

    // Helper to get the item's image URL based on its ID and type
    private String getItemImageUrl(Long itemId, String itemType) {
        if ("LOST".equalsIgnoreCase(itemType)) {
            return lostItemRepository.findById(itemId)
                    .map(LostItem::getImage)
                    .orElse(null);
        } else if ("FOUND".equalsIgnoreCase(itemType)) {
            return foundItemRepository.findById(itemId)
                    .map(FoundItem::getImage)
                    .orElse(null);
        }
        return null;
    }
}
