package org.demo.aingthon.domain.profile.repository;

import org.demo.aingthon.domain.profile.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewerId(Long reviewerId);
    List<Review> findByRevieweeId(Long revieweeId);
    boolean existsByScheduleIdAndReviewerId(Long scheduleId, Long reviewerId);
    long countByRevieweeId(Long revieweeId);

    @Query("SELECT AVG(r.satisfaction) FROM Review r WHERE r.reviewee.id = :revieweeId")
    Double findAverageRatingByRevieweeId(@Param("revieweeId") Long revieweeId);
}
