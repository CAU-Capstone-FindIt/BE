package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    private final List<PersonalMessage> messageStore = new ArrayList<>();

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(PersonalMessage message) {
        // 메시지 수신 후 메모리 또는 데이터베이스에 저장
        messageStore.add(message);
    }

    public List<PersonalMessage> getMessages(String topic, Long receiverId, Long senderId) {
        return messageStore.stream()
                .filter(message -> message.getReceiverId().equals(receiverId) &&
                        (senderId == null || message.getSenderId().equals(senderId)))
                .collect(Collectors.toList());
    }

    public List<PersonalMessage> getLatestMessages(String topic, Long receiverId) {
        return messageStore.stream()
                .filter(message -> message.getReceiverId().equals(receiverId))
                .collect(Collectors.groupingBy(PersonalMessage::getSenderId))
                .values().stream()
                .map(messages -> messages.get(messages.size() - 1))
                .collect(Collectors.toList());
    }

    public List<PersonalMessage> getLatestMessagesForReceiverAndSender(String topic, Long userId) {
        // 사용자와 주고받은 모든 메시지 필터링
        return messageStore.stream()
                .filter(message -> message.getSenderId().equals(userId) || message.getReceiverId().equals(userId))
                .collect(Collectors.groupingBy(message -> {
                    // 상대방 기준으로 그룹화
                    if (message.getSenderId().equals(userId)) {
                        return message.getReceiverId();
                    } else {
                        return message.getSenderId();
                    }
                }))
                .values().stream()
                .map(messages -> messages.stream().max(Comparator.comparing(PersonalMessage::getTimestamp)).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<PersonalMessage> getConversationForBoth(String topic, Long userA, Long userB) {
        return messageStore.stream()
                .filter(message ->
                        (message.getSenderId().equals(userA) && message.getReceiverId().equals(userB)) ||
                                (message.getSenderId().equals(userB) && message.getReceiverId().equals(userA)))
                .sorted(Comparator.comparing(PersonalMessage::getTimestamp).reversed()) // 최신 순 정렬
                .collect(Collectors.toList());
    }


}
