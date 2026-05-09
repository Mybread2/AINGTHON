package org.demo.aingthon.domain.match.service;

import org.demo.aingthon.domain.chat.dto.ChatRoomResponse;
import org.demo.aingthon.domain.chat.service.ChatService;
import org.demo.aingthon.domain.match.dto.MatchRequest;
import org.demo.aingthon.domain.match.dto.MatchResponse;
import org.demo.aingthon.domain.match.dto.ScheduleRequest;
import org.demo.aingthon.domain.match.dto.ScheduleResponse;
import org.demo.aingthon.domain.match.entity.Match;
import org.demo.aingthon.domain.match.entity.MatchStatus;
import org.demo.aingthon.domain.match.entity.Schedule;
import org.demo.aingthon.domain.match.repository.MatchRepository;
import org.demo.aingthon.domain.match.repository.ScheduleRepository;
import org.demo.aingthon.global.exception.BusinessException;
import org.demo.aingthon.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final ScheduleRepository scheduleRepository;
    private final ChatService chatService;

    public MatchService(MatchRepository matchRepository, ScheduleRepository scheduleRepository, ChatService chatService) {
        this.matchRepository = matchRepository;
        this.scheduleRepository = scheduleRepository;
        this.chatService = chatService;
    }

    @Transactional
    public MatchResponse applyMatch(Long applicantId, MatchRequest request) {
        if (applicantId.equals(request.receiverId())) {
            throw new BusinessException(ErrorCode.CANNOT_MATCH_SELF);
        }
        if (matchRepository.existsByApplicantIdAndReceiverIdAndStatus(applicantId, request.receiverId(), MatchStatus.PENDING)) {
            throw new BusinessException(ErrorCode.MATCH_ALREADY_PENDING);
        }

        ChatRoomResponse chatRoom = chatService.createRoom(applicantId, request.receiverId());
        Match match = matchRepository.save(new Match(
                applicantId,
                request.receiverId(),
                request.reason(),
                request.requirements(),
                request.preferredMode(),
                request.preferredDate(),
                chatRoom.id()
        ));
        return MatchResponse.from(match);
    }

    public List<MatchResponse> getSentMatches(Long userId) {
        return matchRepository.findByApplicantIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MatchResponse::from)
                .toList();
    }

    public List<MatchResponse> getReceivedMatches(Long userId) {
        return matchRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MatchResponse::from)
                .toList();
    }

    @Transactional
    public MatchResponse approveMatch(Long matchId, Long userId) {
        Match match = getMatchOrThrow(matchId);
        if (!match.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.MATCH_NOT_PARTICIPANT);
        }
        if (match.getStatus() != MatchStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_MATCH_STATUS);
        }
        match.approve();
        return MatchResponse.from(match);
    }

    @Transactional
    public MatchResponse rejectMatch(Long matchId, Long userId) {
        Match match = getMatchOrThrow(matchId);
        if (!match.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.MATCH_NOT_PARTICIPANT);
        }
        if (match.getStatus() != MatchStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_MATCH_STATUS);
        }
        match.reject();
        return MatchResponse.from(match);
    }

    @Transactional
    public ScheduleResponse proposeSchedule(Long matchId, Long userId, ScheduleRequest request) {
        Match match = getMatchOrThrow(matchId);
        if (!match.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.MATCH_NOT_PARTICIPANT);
        }
        if (match.getStatus() != MatchStatus.APPROVED) {
            throw new BusinessException(ErrorCode.INVALID_MATCH_STATUS);
        }

        Schedule schedule = scheduleRepository.findByMatchId(matchId)
                .map(existing -> {
                    existing.update(request.scheduledDate(), request.scheduledTime(), request.location());
                    return existing;
                })
                .orElseGet(() -> scheduleRepository.save(
                        new Schedule(match, request.scheduledDate(), request.scheduledTime(), request.location())
                ));
        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long matchId, Long userId, ScheduleRequest request) {
        Match match = getMatchOrThrow(matchId);
        if (!match.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.MATCH_NOT_PARTICIPANT);
        }

        Schedule schedule = scheduleRepository.findByMatchId(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
        if (schedule.isPast()) {
            throw new BusinessException(ErrorCode.SCHEDULE_ALREADY_PASSED);
        }

        schedule.update(request.scheduledDate(), request.scheduledTime(), request.location());
        return ScheduleResponse.from(schedule);
    }

    public List<ScheduleResponse> getUpcomingSchedules(Long userId) {
        return scheduleRepository.findAllByUserId(userId)
                .stream()
                .filter(Schedule::isUpcoming)
                .map(ScheduleResponse::from)
                .toList();
    }

    public List<ScheduleResponse> getPastSchedules(Long userId) {
        return scheduleRepository.findAllByUserId(userId)
                .stream()
                .filter(Schedule::isPast)
                .map(ScheduleResponse::from)
                .toList();
    }

    private Match getMatchOrThrow(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MATCH_NOT_FOUND));
    }
}
