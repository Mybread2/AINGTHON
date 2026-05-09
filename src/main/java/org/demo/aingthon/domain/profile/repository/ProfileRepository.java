package org.demo.aingthon.domain.profile.repository;

import org.demo.aingthon.domain.profile.entity.Profile;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {

    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByUserEmail(String email);

    boolean existsByUserId(Long userId);
}
