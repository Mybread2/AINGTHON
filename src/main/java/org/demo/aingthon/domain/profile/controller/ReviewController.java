package org.demo.aingthon.domain.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Review", description = "리뷰 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "리뷰 작성", description = "완료된 매칭 상대방에게 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse response = reviewService.createReview(user, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회")
    @GetMapping("/written")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getWrittenReviews(
            @AuthenticationPrincipal User user) {
        List<ReviewResponse> response = reviewService.getWrittenReviews(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "내가 받은 리뷰 목록 조회")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReceivedReviews(
            @AuthenticationPrincipal User user) {
        List<ReviewResponse> response = reviewService.getReceivedReviews(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
