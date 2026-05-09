package org.demo.aingthon.domain.profile.repository;

import org.demo.aingthon.domain.profile.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewerId(Long reviewerId);
    List<Review> findByRevieweeId(Long revieweeId);
}
