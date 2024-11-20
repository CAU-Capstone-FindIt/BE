package com.example.find_it.service;

import com.example.find_it.dto.PersonalMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
}


