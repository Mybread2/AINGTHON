package org.demo.aingthon.domain.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequest(
        @NotNull LocalDate scheduledDate,
        @NotNull LocalTime scheduledTime,
        @NotBlank String location
) {}
