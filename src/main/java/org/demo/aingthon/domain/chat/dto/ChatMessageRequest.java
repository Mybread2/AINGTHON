package org.demo.aingthon.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull Long roomId,
        @NotNull Long senderId,
        @NotBlank String content
) {}
