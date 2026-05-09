package org.demo.aingthon.domain.match.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.match.dto.MatchRequest;
import org.demo.aingthon.domain.match.dto.MatchResponse;
import org.demo.aingthon.domain.match.dto.ScheduleRequest;
import org.demo.aingthon.domain.match.dto.ScheduleResponse;
import org.demo.aingthon.domain.match.service.MatchService;
import org.demo.aingthon.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Match", description = "매칭 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Operation(summary = "매칭 신청", description = "상대방에게 멘토링 매칭을 신청합니다. 채팅방이 자동으로 생성됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MatchResponse>> applyMatch(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                matchService.applyMatch(currentUser.getId(), request)
        ));
    }

    @Operation(summary = "보낸 매칭 목록 조회")
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getSentMatches(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getSentMatches(currentUser.getId())));
    }

    @Operation(summary = "받은 매칭 목록 조회")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getReceivedMatches(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getReceivedMatches(currentUser.getId())));
    }

    @Operation(summary = "매칭 승인", description = "수신자 본인만 가능. PENDING 상태에서만 승인 가능합니다.")
    @PatchMapping("/{matchId}/approve")
    public ResponseEntity<ApiResponse<MatchResponse>> approveMatch(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.approveMatch(matchId, currentUser.getId())));
    }

    @Operation(summary = "매칭 거절", description = "수신자 본인만 가능. PENDING 상태에서만 거절 가능합니다.")
    @PatchMapping("/{matchId}/reject")
    public ResponseEntity<ApiResponse<MatchResponse>> rejectMatch(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.rejectMatch(matchId, currentUser.getId())));
    }

    @Operation(summary = "일정 제안", description = "APPROVED 상태 매칭에서만 가능합니다.")
    @PostMapping("/{matchId}/schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> proposeSchedule(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                matchService.proposeSchedule(matchId, currentUser.getId(), request)
        ));
    }

    @Operation(summary = "일정 수정", description = "양쪽 참여자 모두 가능. 이미 지난 일정은 수정 불가합니다.")
    @PutMapping("/{matchId}/schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.updateSchedule(matchId, currentUser.getId(), request)));
    }

    @Operation(summary = "예정 일정 목록 조회")
    @GetMapping("/schedules/upcoming")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getUpcomingSchedules(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getUpcomingSchedules(currentUser.getId())));
    }

    @Operation(summary = "지난 일정 목록 조회")
    @GetMapping("/schedules/past")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getPastSchedules(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getPastSchedules(currentUser.getId())));
    }
}
