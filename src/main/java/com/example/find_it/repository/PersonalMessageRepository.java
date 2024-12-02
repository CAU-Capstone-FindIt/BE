package com.example.find_it.repository;

import com.example.find_it.domain.PersonalMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalMessageRepository extends JpaRepository<PersonalMessage, Long> {
    // Custom query methods

    // Find messages related to a specific item by itemId
    List<PersonalMessage> findByItemId(Long itemId);

    // Find messages sent by a specific user
    List<PersonalMessage> findBySenderId(Long senderId);

    // Find messages received by a specific user
    List<PersonalMessage> findByReceiverId(Long receiverId);

    // Find messages between two users
    List<PersonalMessage> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // Find all messages related to a specific item and user
    List<PersonalMessage> findByItemIdAndReceiverId(Long itemId, Long receiverId);
}
