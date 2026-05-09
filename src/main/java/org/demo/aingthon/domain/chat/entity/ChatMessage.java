package org.demo.aingthon.domain.chat.entity;

import jakarta.persistence.*;
import org.demo.aingthon.global.entity.BaseEntity;

@Entity
@Table(name = "chat_message")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    protected ChatMessage() {}

    public ChatMessage(ChatRoom chatRoom, Long senderId, String content) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.content = content;
    }

    public Long getId() { return id; }
    public ChatRoom getChatRoom() { return chatRoom; }
    public Long getSenderId() { return senderId; }
    public String getContent() { return content; }
}
