package org.demo.aingthon.domain.chat.dto;

import org.demo.aingthon.domain.chat.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long id,
        Long user1Id,
        Long user2Id,
        LocalDateTime createdAt
) {
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getUser1Id(),
                chatRoom.getUser2Id(),
                chatRoom.getCreatedAt()
        );
    }
}
