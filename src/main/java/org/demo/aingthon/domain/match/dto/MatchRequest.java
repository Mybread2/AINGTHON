package org.demo.aingthon.domain.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.demo.aingthon.domain.match.entity.PreferredMode;

import java.time.LocalDate;

public record MatchRequest(
        @NotNull Long receiverId,
        @NotBlank String reason,
        String requirements,
        @NotNull PreferredMode preferredMode,
        LocalDate preferredDate
) {}
