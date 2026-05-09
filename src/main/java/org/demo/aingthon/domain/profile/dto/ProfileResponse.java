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
        String profileImageUrl,
        List<ReviewResponse> featuredReviews,
        Double averageRating,
        long reviewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProfileResponse from(Profile profile, Double averageRating, long reviewCount, String profileImageUrl) {
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
                profileImageUrl,
                profile.getFeaturedReviews().stream().map(ReviewResponse::from).toList(),
                averageRating != null ? Math.round(averageRating * 10) / 10.0 : null,
                reviewCount,
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
