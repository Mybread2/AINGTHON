package org.demo.aingthon.domain.profile.service;

import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.match.entity.Schedule;
import org.demo.aingthon.domain.match.repository.ScheduleRepository;
import org.demo.aingthon.domain.profile.dto.ActivityReportCreateRequest;
import org.demo.aingthon.domain.profile.dto.ActivityReportResponse;
import org.demo.aingthon.domain.profile.dto.ActivityReportUpdateRequest;
import org.demo.aingthon.domain.profile.entity.ActivityReport;
import org.demo.aingthon.domain.profile.repository.ActivityReportRepository;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.demo.aingthon.global.storage.GcsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ActivityReportService {

    private final ActivityReportRepository activityReportRepository;
    private final ScheduleRepository scheduleRepository;
    private final GcsService gcsService;

    public ActivityReportService(ActivityReportRepository activityReportRepository,
                                  ScheduleRepository scheduleRepository,
                                  GcsService gcsService) {
        this.activityReportRepository = activityReportRepository;
        this.scheduleRepository = scheduleRepository;
        this.gcsService = gcsService;
    }

    @Transactional
    public ActivityReportResponse createReport(User user, ActivityReportCreateRequest request) {
        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getMatch().isParticipant(user.getId())) {
            throw new BusinessException(ErrorCode.MATCH_NOT_PARTICIPANT);
        }

        if (activityReportRepository.existsByScheduleIdAndUserId(request.scheduleId(), user.getId())) {
            throw new BusinessException(ErrorCode.REPORT_ALREADY_EXISTS);
        }

        ActivityReport report = new ActivityReport(schedule, user, request.insights(), request.nextGoal());
        return toResponse(activityReportRepository.save(report));
    }

    @Transactional
    public ActivityReportResponse updateReport(User user, Long reportId, ActivityReportUpdateRequest request) {
        ActivityReport report = activityReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));

        if (!report.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.REPORT_UNAUTHORIZED);
        }

        report.update(request.insights(), request.nextGoal());
        return toResponse(report);
    }

    @Transactional
    public ActivityReportResponse uploadAttachment(User user, Long reportId, MultipartFile file) {
        ActivityReport report = activityReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));

        if (!report.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.REPORT_UNAUTHORIZED);
        }

        if (report.getAttachmentObjectName() != null) {
            gcsService.delete(report.getAttachmentObjectName());
        }

        String objectName = gcsService.upload(file, "reports/" + reportId);
        report.updateAttachment(objectName);
        return toResponse(report);
    }

    public List<ActivityReportResponse> getMyReports(User user) {
        return activityReportRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public ActivityReportResponse getReport(Long reportId) {
        ActivityReport report = activityReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));
        return toResponse(report);
    }

    private ActivityReportResponse toResponse(ActivityReport report) {
        String attachmentUrl = gcsService.getSignedUrl(report.getAttachmentObjectName());
        return ActivityReportResponse.from(report, attachmentUrl);
    }
}
