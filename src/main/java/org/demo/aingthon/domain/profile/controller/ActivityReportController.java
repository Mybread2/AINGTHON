package org.demo.aingthon.domain.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "ActivityReport", description = "활동 보고서 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/reports")
public class ActivityReportController {

    private final ActivityReportService activityReportService;

    public ActivityReportController(ActivityReportService activityReportService) {
        this.activityReportService = activityReportService;
    }

    @Operation(summary = "보고서 작성", description = "일정(scheduleId)에 대한 활동 보고서를 작성합니다. 일정당 1개만 허용됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityReportResponse>> createReport(
            @AuthenticationPrincipal User user,
            @RequestBody ActivityReportCreateRequest request) {
        ActivityReportResponse response = activityReportService.createReport(user, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @Operation(summary = "보고서 수정", description = "본인이 작성한 보고서만 수정 가능합니다.")
    @PutMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> updateReport(
            @AuthenticationPrincipal User user,
            @PathVariable Long reportId,
            @RequestBody ActivityReportUpdateRequest request) {
        ActivityReportResponse response = activityReportService.updateReport(user, reportId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "내 보고서 목록 조회")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ActivityReportResponse>>> getMyReports(
            @AuthenticationPrincipal User user) {
        List<ActivityReportResponse> response = activityReportService.getMyReports(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "보고서 단건 조회")
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> getReport(
            @PathVariable Long reportId) {
        ActivityReportResponse response = activityReportService.getReport(reportId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "첨부파일 업로드", description = "보고서에 파일을 첨부합니다. 기존 파일은 삭제됩니다. multipart/form-data (key: file)")
    @PostMapping("/{reportId}/attachment")
    public ResponseEntity<ApiResponse<ActivityReportResponse>> uploadAttachment(
            @AuthenticationPrincipal User user,
            @PathVariable Long reportId,
            @RequestParam("file") MultipartFile file) {
        ActivityReportResponse response = activityReportService.uploadAttachment(user, reportId, file);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
