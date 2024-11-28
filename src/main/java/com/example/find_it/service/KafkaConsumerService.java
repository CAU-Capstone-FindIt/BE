package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    private final Map<String, List<PersonalMessage>> messageStore = new ConcurrentHashMap<>();

    @KafkaListener(topics = "#{T(java.util.List).of('private-message-1', 'private-message-2')}", groupId = "private-messages")
    public void consume(PersonalMessage message) {
        String topic = "private-message-" + message.getReceiverId();
        messageStore.computeIfAbsent(topic, k -> new ArrayList<>()).add(message);
    }

    /**
     * 특정 토픽의 메시지 목록 가져오기
     * @param topic   메시지 토픽
     * @param senderId 메시지 보낸 사람 ID (null일 경우 필터링하지 않음)
     * @return 메시지 목록
     */
    public List<PersonalMessage> getMessages(String topic, Long senderId) {
        // Retrieve messages for the topic
        List<PersonalMessage> messages = messageStore.getOrDefault(topic, List.of());

        // Filter by senderId if provided
        if (senderId != null) {
            messages = messages.stream()
                    .filter(message -> senderId.equals(message.getSenderId()))
                    .toList();
        }

        return messages;
    }

    public List<PersonalMessage> getLatestMessages(String topic) {
        List<PersonalMessage> messages = messageStore.getOrDefault(topic, List.of());
        // 상대방(senderId)별 최신 메시지를 반환
        return messages.stream()
                .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp())) // 최신 메시지 기준으로 정렬
                .collect(Collectors.toMap(
                        PersonalMessage::getSenderId,  // SenderId 기준으로 그룹화
                        message -> message,           // 최신 메시지만 유지
                        (existing, replacement) -> existing // 기존 메시지 유지
                ))
                .values()
                .stream()
                .toList();
    }

    public List<PersonalMessage> getConversation(String topic, Long senderId) {
        List<PersonalMessage> messages = messageStore.getOrDefault(topic, List.of());
        // 상대방(senderId)와 주고받은 메시지 필터링
        return messages.stream()
                .filter(message -> senderId.equals(message.getSenderId()) || senderId.equals(message.getReceiverId()))
                .sorted(Comparator.comparing(PersonalMessage::getTimestamp)) // 시간 순 정렬
                .toList();
    }
}


