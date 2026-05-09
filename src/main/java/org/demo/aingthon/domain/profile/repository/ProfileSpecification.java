package org.demo.aingthon.domain.profile.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.demo.aingthon.domain.profile.entity.Grade;
import org.demo.aingthon.domain.profile.entity.Profile;
import org.springframework.data.jpa.domain.Specification;

public class ProfileSpecification {

    public static Specification<Profile> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("introduction")), pattern),
                    cb.like(cb.lower(root.get("goal")), pattern),
                    cb.like(cb.lower(root.get("careers")), pattern),
                    cb.like(cb.lower(root.get("projectExperiences")), pattern)
            );
        };
    }

    public static Specification<Profile> hasTechStack(String techStack) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Profile, String> join = root.join("techStacks", JoinType.INNER);
            return cb.like(cb.lower(join.as(String.class)), "%" + techStack.toLowerCase() + "%");
        };
    }

    public static Specification<Profile> sameUniversity(String university) {
        return (root, query, cb) -> cb.equal(root.get("university"), university);
    }

    public static Specification<Profile> hasGrade(Grade grade) {
        return (root, query, cb) -> cb.equal(root.get("grade"), grade);
    }

    public static Specification<Profile> notUser(Long userId) {
        return (root, query, cb) -> cb.notEqual(root.get("user").get("id"), userId);
    }
}
