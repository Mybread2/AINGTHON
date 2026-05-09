package org.demo.aingthon.domain.match.dto;

import org.demo.aingthon.domain.match.entity.Match;
import org.demo.aingthon.domain.match.entity.MatchStatus;
import org.demo.aingthon.domain.match.entity.PreferredMode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        Long applicantId,
        Long receiverId,
        String reason,
        String requirements,
        PreferredMode preferredMode,
        LocalDate preferredDate,
        MatchStatus status,
        Long chatRoomId,
        LocalDateTime createdAt
) {
    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getApplicantId(),
                match.getReceiverId(),
                match.getReason(),
                match.getRequirements(),
                match.getPreferredMode(),
                match.getPreferredDate(),
                match.getStatus(),
                match.getChatRoomId(),
                match.getCreatedAt()
        );
    }
}
