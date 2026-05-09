package org.demo.aingthon.domain.profile.service;

import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.auth.repository.UserRepository;
import org.demo.aingthon.domain.profile.dto.ReviewCreateRequest;
import org.demo.aingthon.domain.profile.dto.ReviewResponse;
import org.demo.aingthon.domain.profile.entity.Review;
import org.demo.aingthon.domain.profile.repository.ReviewRepository;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponse createReview(User reviewer, ReviewCreateRequest request) {
        if (reviewer.getId().equals(request.revieweeId())) {
            throw new BusinessException(ErrorCode.CANNOT_REVIEW_SELF);
        }

        User reviewee = userRepository.findById(request.revieweeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Review review = new Review(
                reviewer,
                reviewee,
                request.satisfaction(),
                request.oneLineReview(),
                request.mainContent()
        );

        return ReviewResponse.from(reviewRepository.save(review));
    }

    public List<ReviewResponse> getWrittenReviews(User user) {
        return reviewRepository.findByReviewerId(user.getId()).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public List<ReviewResponse> getReceivedReviews(User user) {
        return reviewRepository.findByRevieweeId(user.getId()).stream()
                .map(ReviewResponse::from)
                .toList();
    }
}
