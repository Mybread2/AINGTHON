package org.demo.aingthon.domain.match.entity;

import jakarta.persistence.*;
import org.demo.aingthon.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "matches")
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicantId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(length = 500)
    private String requirements;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferredMode preferredMode;

    private LocalDate preferredDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(nullable = false)
    private Long chatRoomId;

    protected Match() {}

    public Match(Long applicantId, Long receiverId, String reason, String requirements,
                 PreferredMode preferredMode, LocalDate preferredDate, Long chatRoomId) {
        this.applicantId = applicantId;
        this.receiverId = receiverId;
        this.reason = reason;
        this.requirements = requirements;
        this.preferredMode = preferredMode;
        this.preferredDate = preferredDate;
        this.status = MatchStatus.PENDING;
        this.chatRoomId = chatRoomId;
    }

    public void approve() { this.status = MatchStatus.APPROVED; }
    public void reject()  { this.status = MatchStatus.REJECTED; }

    public boolean isParticipant(Long userId) {
        return applicantId.equals(userId) || receiverId.equals(userId);
    }

    public Long getId()               { return id; }
    public Long getApplicantId()      { return applicantId; }
    public Long getReceiverId()       { return receiverId; }
    public String getReason()         { return reason; }
    public String getRequirements()   { return requirements; }
    public PreferredMode getPreferredMode() { return preferredMode; }
    public LocalDate getPreferredDate()    { return preferredDate; }
    public MatchStatus getStatus()    { return status; }
    public Long getChatRoomId()       { return chatRoomId; }
}
