package org.demo.aingthon.domain.profile.repository;

import org.demo.aingthon.domain.profile.entity.ActivityReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {
    List<ActivityReport> findByUserId(Long userId);
}
