package org.demo.aingthon.domain.profile.dto;

import org.demo.aingthon.domain.profile.entity.ActivityReport;

import java.time.LocalDateTime;

public record ActivityReportResponse(
        Long id,
        Long scheduleId,
        Long userId,
        String insights,
        String nextGoal,
        String attachmentUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ActivityReportResponse from(ActivityReport report, String attachmentUrl) {
        return new ActivityReportResponse(
                report.getId(),
                report.getSchedule().getId(),
                report.getUser().getId(),
                report.getInsights(),
                report.getNextGoal(),
                attachmentUrl,
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
