package org.demo.aingthon.domain.profile.dto;

import org.demo.aingthon.domain.profile.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long reviewerId,
        String reviewerName,
        Long revieweeId,
        String revieweeName,
        Integer satisfaction,
        String oneLineReview,
        String mainContent,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getReviewer().getId(),
                review.getReviewer().getName(),
                review.getReviewee().getId(),
                review.getReviewee().getName(),
                review.getSatisfaction(),
                review.getOneLineReview(),
                review.getMainContent(),
                review.getCreatedAt()
        );
    }
}
