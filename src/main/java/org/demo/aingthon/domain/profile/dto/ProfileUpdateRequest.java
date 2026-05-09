package org.demo.aingthon.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.demo.aingthon.domain.profile.entity.Field;
import org.demo.aingthon.domain.profile.entity.Grade;

import java.util.List;
import java.util.Set;

public record ProfileUpdateRequest(
        @NotBlank String name,
        String introduction,
        @NotEmpty Set<Field> fields,
        @NotNull Boolean major,
        List<String> techStacks,
        Grade grade,
        List<String> careers,
        List<String> projectExperiences,
        String goal,
        String link
) {}
