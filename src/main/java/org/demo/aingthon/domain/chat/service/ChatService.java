package org.demo.aingthon.domain.chat.service;

import org.demo.aingthon.domain.chat.dto.ChatMessageRequest;
import org.demo.aingthon.domain.chat.dto.ChatMessageResponse;
import org.demo.aingthon.domain.chat.dto.ChatRoomResponse;
import org.demo.aingthon.domain.chat.entity.ChatMessage;
import org.demo.aingthon.domain.chat.entity.ChatRoom;
import org.demo.aingthon.domain.chat.repository.ChatMessageRepository;
import org.demo.aingthon.domain.chat.repository.ChatRoomRepository;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional
    public ChatRoomResponse createRoom(Long user1Id, Long user2Id) {
        return chatRoomRepository.findByParticipants(user1Id, user2Id)
                .map(ChatRoomResponse::from)
                .orElseGet(() -> ChatRoomResponse.from(
                        chatRoomRepository.save(new ChatRoom(user1Id, user2Id))
                ));
    }

    public List<ChatRoomResponse> getRooms(Long userId) {
        return chatRoomRepository.findByUser1IdOrUser2Id(userId, userId)
                .stream()
                .map(ChatRoomResponse::from)
                .toList();
    }

    public List<ChatMessageResponse> getMessages(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        return chatMessageRepository.findByChatRoom_IdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.roomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.getUser1Id().equals(request.senderId()) && !room.getUser2Id().equals(request.senderId())) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        ChatMessage message = chatMessageRepository.save(
                new ChatMessage(room, request.senderId(), request.content())
        );
        return ChatMessageResponse.from(message);
    }
}
