package org.demo.aingthon.domain.profile.service;

import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.auth.repository.UserRepository;
import org.demo.aingthon.domain.match.entity.Schedule;
import org.demo.aingthon.domain.match.repository.ScheduleRepository;
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
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ScheduleRepository scheduleRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponse createReview(User reviewer, ReviewCreateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getMatch().isParticipant(reviewer.getId())) {
            throw new BusinessException(ErrorCode.MATCH_NOT_PARTICIPANT);
        }

        if (reviewRepository.existsByScheduleIdAndReviewerId(request.scheduleId(), reviewer.getId())) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Long revieweeId = schedule.getMatch().getApplicantId().equals(reviewer.getId())
                ? schedule.getMatch().getReceiverId()
                : schedule.getMatch().getApplicantId();

        User reviewee = userRepository.findById(revieweeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Review review = new Review(schedule, reviewer, reviewee,
                request.satisfaction(), request.oneLineReview(), request.mainContent());

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
