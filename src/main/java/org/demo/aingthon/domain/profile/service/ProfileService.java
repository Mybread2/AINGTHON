package org.demo.aingthon.domain.profile.service;

import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.profile.dto.ProfileCreateRequest;
import org.demo.aingthon.domain.profile.dto.ProfileResponse;
import org.demo.aingthon.domain.profile.dto.ProfileUpdateRequest;
import org.demo.aingthon.domain.profile.entity.Grade;
import org.demo.aingthon.domain.profile.entity.Profile;
import org.demo.aingthon.domain.profile.entity.Review;
import org.demo.aingthon.domain.profile.repository.ProfileRepository;
import org.demo.aingthon.domain.profile.repository.ProfileSpecification;
import org.demo.aingthon.domain.profile.repository.ReviewRepository;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.demo.aingthon.global.storage.GcsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ReviewRepository reviewRepository;
    private final GcsService gcsService;

    public ProfileService(ProfileRepository profileRepository, ReviewRepository reviewRepository, GcsService gcsService) {
        this.profileRepository = profileRepository;
        this.reviewRepository = reviewRepository;
        this.gcsService = gcsService;
    }

    @Transactional
    public ProfileResponse createProfile(User user, ProfileCreateRequest request) {
        if (profileRepository.existsByUserId(user.getId())) {
            throw new BusinessException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }

        Profile profile = new Profile(
                user,
                request.name(),
                request.introduction(),
                request.fields(),
                request.major(),
                request.techStacks(),
                request.university(),
                request.grade(),
                request.careers(),
                request.projectExperiences(),
                request.goal(),
                request.link()
        );

        return toResponse(profileRepository.save(profile));
    }

    @Transactional
    public ProfileResponse updateProfile(User user, ProfileUpdateRequest request) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        profile.update(
                request.name(),
                request.introduction(),
                request.fields(),
                request.major(),
                request.techStacks(),
                request.grade(),
                request.careers(),
                request.projectExperiences(),
                request.goal(),
                request.link()
        );

        if (request.featuredReviewIds() != null) {
            if (request.featuredReviewIds().size() > 3) {
                throw new BusinessException(ErrorCode.FEATURED_REVIEWS_LIMIT_EXCEEDED);
            }
            List<Review> featured = reviewRepository.findAllById(request.featuredReviewIds());
            for (Review review : featured) {
                if (!review.getReviewee().getId().equals(user.getId())) {
                    throw new BusinessException(ErrorCode.REVIEW_NOT_RECEIVED);
                }
            }
            profile.updateFeaturedReviews(featured);
        }

        return toResponse(profile);
    }

    public ProfileResponse getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        return toResponse(profile);
    }

    public ProfileResponse getMyProfile(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        return toResponse(profile);
    }

    public Page<ProfileResponse> searchProfiles(
            User user, String keyword, String techStack,
            Boolean sameUniversity, Grade grade, Pageable pageable) {

        Specification<Profile> spec = ProfileSpecification.notUser(user.getId());

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(ProfileSpecification.containsKeyword(keyword));
        }
        if (techStack != null && !techStack.isBlank()) {
            spec = spec.and(ProfileSpecification.hasTechStack(techStack));
        }
        if (Boolean.TRUE.equals(sameUniversity)) {
            spec = spec.and(ProfileSpecification.sameUniversity(user.getUniversity()));
        }
        if (grade != null) {
            spec = spec.and(ProfileSpecification.hasGrade(grade));
        }

        return profileRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional
    public ProfileResponse uploadProfileImage(User user, MultipartFile file) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        if (profile.getProfileImageObjectName() != null) {
            gcsService.delete(profile.getProfileImageObjectName());
        }

        String objectName = gcsService.upload(file, "profiles/" + user.getId());
        profile.updateProfileImage(objectName);
        return toResponse(profile);
    }

    private ProfileResponse toResponse(Profile profile) {
        Long userId = profile.getUser().getId();
        Double averageRating = reviewRepository.findAverageRatingByRevieweeId(userId);
        long reviewCount = reviewRepository.countByRevieweeId(userId);
        String profileImageUrl = gcsService.getSignedUrl(profile.getProfileImageObjectName());
        return ProfileResponse.from(profile, averageRating, reviewCount, profileImageUrl);
    }
}
