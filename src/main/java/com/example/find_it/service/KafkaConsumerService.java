package com.example.find_it.service;

import com.example.find_it.domain.LostItem;
import com.example.find_it.domain.FoundItem;
import com.example.find_it.dto.PersonalMessageDto;
import com.example.find_it.repository.FoundItemRepository;
import com.example.find_it.repository.LostItemRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;

    private final List<PersonalMessageDto> messageStore = new ArrayList<>();

    public KafkaConsumerService(LostItemRepository lostItemRepository, FoundItemRepository foundItemRepository) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(PersonalMessageDto messageDto) {
        // Store the message in memory
        messageDto.setItemName(getItemName(messageDto.getItemId(), messageDto.getItemType()));
        messageDto.setItemImageUrl(getItemImageUrl(messageDto.getItemId(), messageDto.getItemType()));
        messageStore.add(messageDto);
    }

    // Retrieve all messages for a receiver with an optional sender filter
    public List<PersonalMessageDto> getMessages(Long receiverId, Long senderId) {
        return messageStore.stream()
                .filter(message -> message.getReceiverId().equals(receiverId) &&
                        (senderId == null || message.getSenderId().equals(senderId)))
                .collect(Collectors.toList());
    }

    // Retrieve the latest messages grouped by sender/receiver
    public List<PersonalMessageDto> getLatestMessagesForReceiverAndSender(Long userId) {
        return messageStore.stream()
                .filter(message -> message.getSenderId().equals(userId) || message.getReceiverId().equals(userId))
                .collect(Collectors.groupingBy(message ->
                        message.getSenderId().equals(userId) ? message.getReceiverId() : message.getSenderId()
                ))
                .values().stream()
                .map(messages -> messages.stream()
                        .max(Comparator.comparing(PersonalMessageDto::getTimestamp))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Retrieve a full conversation between two users
    public List<PersonalMessageDto> getConversationForBoth(Long userA, Long userB) {
        return messageStore.stream()
                .filter(message ->
                        (message.getSenderId().equals(userA) && message.getReceiverId().equals(userB)) ||
                                (message.getSenderId().equals(userB) && message.getReceiverId().equals(userA)))
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
