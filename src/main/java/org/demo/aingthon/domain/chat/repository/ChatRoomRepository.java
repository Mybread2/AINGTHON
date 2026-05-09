package org.demo.aingthon.domain.chat.repository;

import org.demo.aingthon.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);

    @Query("SELECT r FROM ChatRoom r WHERE (r.user1Id = :a AND r.user2Id = :b) OR (r.user1Id = :b AND r.user2Id = :a)")
    Optional<ChatRoom> findByParticipants(@Param("a") Long userAId, @Param("b") Long userBId);
}
