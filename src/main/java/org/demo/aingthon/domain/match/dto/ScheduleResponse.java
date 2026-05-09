package org.demo.aingthon.domain.match.dto;

import org.demo.aingthon.domain.match.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ScheduleResponse(
        Long id,
        Long matchId,
        Long applicantId,
        Long receiverId,
        LocalDate scheduledDate,
        LocalTime scheduledTime,
        String location,
        LocalDateTime createdAt
) {
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getMatch().getId(),
                schedule.getMatch().getApplicantId(),
                schedule.getMatch().getReceiverId(),
                schedule.getScheduledDate(),
                schedule.getScheduledTime(),
                schedule.getLocation(),
                schedule.getCreatedAt()
        );
    }
}
