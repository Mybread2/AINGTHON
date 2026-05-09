package org.demo.aingthon.domain.match.repository;

import org.demo.aingthon.domain.match.entity.Match;
import org.demo.aingthon.domain.match.entity.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByApplicantIdOrderByCreatedAtDesc(Long applicantId);

    List<Match> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    boolean existsByApplicantIdAndReceiverIdAndStatus(Long applicantId, Long receiverId, MatchStatus status);
}
