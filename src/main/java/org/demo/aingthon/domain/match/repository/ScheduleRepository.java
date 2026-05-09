package org.demo.aingthon.domain.match.repository;

import org.demo.aingthon.domain.match.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByMatchId(Long matchId);

    @Query("SELECT s FROM Schedule s JOIN s.match m WHERE m.applicantId = :userId OR m.receiverId = :userId")
    List<Schedule> findAllByUserId(@Param("userId") Long userId);
}
