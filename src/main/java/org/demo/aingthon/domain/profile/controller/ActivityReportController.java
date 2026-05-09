package org.demo.aingthon.domain.profile.controller;

import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.profile.dto.ActivityReportCreateRequest;
import org.demo.aingthon.domain.profile.dto.ActivityReportResponse;
import org.demo.aingthon.domain.profile.dto.ActivityReportUpdateRequest;
import org.demo.aingthon.domain.profile.service.ActivityReportService;
import org.demo.aingthon.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ActivityReportController {

    private final ActivityReportService activityReportService;

    public ActivityReportController(ActivityReportService activityReportService) {
        this.activityReportService = activityReportService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityReportResponse>> createReport(
            @AuthenticationPrincipal User user,
            @RequestBody ActivityReportCreateRequest request) {
        ActivityReportResponse response = activityReportService.createReport(user, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> updateReport(
            @AuthenticationPrincipal User user,
            @PathVariable Long reportId,
            @RequestBody ActivityReportUpdateRequest request) {
        ActivityReportResponse response = activityReportService.updateReport(user, reportId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ActivityReportResponse>>> getMyReports(
            @AuthenticationPrincipal User user) {
        List<ActivityReportResponse> response = activityReportService.getMyReports(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> getReport(
            @PathVariable Long reportId) {
        ActivityReportResponse response = activityReportService.getReport(reportId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{reportId}/attachment")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> uploadAttachment(
            @AuthenticationPrincipal User user,
            @PathVariable Long reportId,
            @RequestParam("file") MultipartFile file) {
        ActivityReportResponse response = activityReportService.uploadAttachment(user, reportId, file);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
