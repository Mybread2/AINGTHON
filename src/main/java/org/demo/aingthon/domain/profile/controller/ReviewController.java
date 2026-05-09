package org.demo.aingthon.domain.profile.controller;

import jakarta.validation.Valid;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.profile.dto.ReviewCreateRequest;
import org.demo.aingthon.domain.profile.dto.ReviewResponse;
import org.demo.aingthon.domain.profile.service.ReviewService;
import org.demo.aingthon.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse response = reviewService.createReview(user, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @GetMapping("/written")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getWrittenReviews(
            @AuthenticationPrincipal User user) {
        List<ReviewResponse> response = reviewService.getWrittenReviews(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReceivedReviews(
            @AuthenticationPrincipal User user) {
        List<ReviewResponse> response = reviewService.getReceivedReviews(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
