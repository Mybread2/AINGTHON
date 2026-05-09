package org.demo.aingthon.domain.match.controller;

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

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MatchResponse>> applyMatch(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                matchService.applyMatch(currentUser.getId(), request)
        ));
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getSentMatches(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getSentMatches(currentUser.getId())));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getReceivedMatches(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getReceivedMatches(currentUser.getId())));
    }

    @PatchMapping("/{matchId}/approve")
    public ResponseEntity<ApiResponse<MatchResponse>> approveMatch(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.approveMatch(matchId, currentUser.getId())));
    }

    @PatchMapping("/{matchId}/reject")
    public ResponseEntity<ApiResponse<MatchResponse>> rejectMatch(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.rejectMatch(matchId, currentUser.getId())));
    }

    @PostMapping("/{matchId}/schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> proposeSchedule(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                matchService.proposeSchedule(matchId, currentUser.getId(), request)
        ));
    }

    @PutMapping("/{matchId}/schedule")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long matchId,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.updateSchedule(matchId, currentUser.getId(), request)));
    }

    @GetMapping("/schedules/upcoming")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getUpcomingSchedules(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getUpcomingSchedules(currentUser.getId())));
    }

    @GetMapping("/schedules/past")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getPastSchedules(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getPastSchedules(currentUser.getId())));
    }
}
