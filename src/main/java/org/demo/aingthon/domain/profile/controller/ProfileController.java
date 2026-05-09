package org.demo.aingthon.domain.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.profile.dto.ProfileCreateRequest;
import org.demo.aingthon.domain.profile.dto.ProfileResponse;
import org.demo.aingthon.domain.profile.dto.ProfileUpdateRequest;
import org.demo.aingthon.domain.profile.entity.Grade;
import org.demo.aingthon.domain.profile.service.ProfileService;
import org.demo.aingthon.global.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Profile", description = "프로필 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "프로필 목록 검색", description = "키워드·기술스택·같은학교·학년 필터로 프로필을 검색합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProfileResponse>>> searchProfiles(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String techStack,
            @RequestParam(required = false) Boolean sameUniversity,
            @RequestParam(required = false) Grade grade,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProfileResponse> response = profileService.searchProfiles(
                user, keyword, techStack, sameUniversity, grade, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "프로필 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileCreateRequest request) {
        ProfileResponse response = profileService.createProfile(user, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @Operation(summary = "내 프로필 수정")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileResponse response = profileService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {
        ProfileResponse response = profileService.getMyProfile(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "프로필 조회")
    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @PathVariable Long profileId) {
        ProfileResponse response = profileService.getProfile(profileId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "프로필 이미지 업로드", description = "multipart/form-data로 이미지 파일을 전송합니다. (key: file)")
    @PutMapping("/me/image")
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadProfileImage(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        ProfileResponse response = profileService.uploadProfileImage(user, file);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
