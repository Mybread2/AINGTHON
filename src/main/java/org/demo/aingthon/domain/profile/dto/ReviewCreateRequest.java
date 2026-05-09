package org.demo.aingthon.domain.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(
        @NotNull Long revieweeId,
        @NotNull @Min(1) @Max(5) Integer satisfaction,
        @NotBlank @Size(max = 100) String oneLineReview,
        String mainContent
) {}
