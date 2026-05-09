package org.demo.aingthon.domain.chat.entity;

import jakarta.persistence.*;
import org.demo.aingthon.global.entity.BaseEntity;

@Entity
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long user1Id;

    @Column(nullable = false)
    private Long user2Id;

    protected ChatRoom() {}

    public ChatRoom(Long user1Id, Long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public Long getId() { return id; }
    public Long getUser1Id() { return user1Id; }
    public Long getUser2Id() { return user2Id; }
}
