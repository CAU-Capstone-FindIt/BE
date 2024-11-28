package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public List<PersonalMessage> getConversationForBoth(String topic, Long userA, Long userB) {
        return messageStore.stream()
                .filter(message ->
                        (message.getSenderId().equals(userA) && message.getReceiverId().equals(userB)) ||
                                (message.getSenderId().equals(userB) && message.getReceiverId().equals(userA)))
                .collect(Collectors.toList());
    }
}
