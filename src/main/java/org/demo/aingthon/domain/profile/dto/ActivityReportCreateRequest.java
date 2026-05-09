package org.demo.aingthon.domain.profile.dto;

import jakarta.validation.constraints.NotNull;

public record ActivityReportCreateRequest(
        @NotNull Long scheduleId,
        String insights,
        String nextGoal
) {}
