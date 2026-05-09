package org.demo.aingthon.domain.profile.dto;

import org.demo.aingthon.domain.profile.entity.Field;
import org.demo.aingthon.domain.profile.entity.Grade;
import org.demo.aingthon.domain.profile.entity.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProfileResponse(
        Long id,
        Long userId,
        String name,
        String introduction,
        Set<Field> fields,
        boolean major,
        List<String> techStacks,
        String university,
        Grade grade,
        List<String> careers,
        List<String> projectExperiences,
        String goal,
        String link,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getName(),
                profile.getIntroduction(),
                profile.getFields(),
                profile.isMajor(),
                profile.getTechStacks(),
                profile.getUniversity(),
                profile.getGrade(),
                profile.getCareers(),
                profile.getProjectExperiences(),
                profile.getGoal(),
                profile.getLink(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
