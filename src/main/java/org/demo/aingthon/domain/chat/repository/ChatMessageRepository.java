package org.demo.aingthon.domain.chat.repository;

import org.demo.aingthon.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_IdOrderByCreatedAtAsc(Long chatRoomId);
}
