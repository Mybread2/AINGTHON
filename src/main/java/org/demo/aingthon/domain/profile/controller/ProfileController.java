package org.demo.aingthon.domain.profile.controller;

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

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

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

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileCreateRequest request) {
        ProfileResponse response = profileService.createProfile(user, request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileResponse response = profileService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {
        ProfileResponse response = profileService.getMyProfile(user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @PathVariable Long profileId) {
        ProfileResponse response = profileService.getProfile(profileId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
