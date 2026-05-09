package org.demo.aingthon.domain.profile.dto;

import org.demo.aingthon.domain.profile.entity.ActivityReport;

import java.time.LocalDateTime;

public record ActivityReportResponse(
        Long id,
        Long userId,
        String insights,
        String nextGoal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ActivityReportResponse from(ActivityReport report) {
        return new ActivityReportResponse(
                report.getId(),
                report.getUser().getId(),
                report.getInsights(),
                report.getNextGoal(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
