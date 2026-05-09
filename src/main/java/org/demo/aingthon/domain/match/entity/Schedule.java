package org.demo.aingthon.domain.match.entity;

import jakarta.persistence.*;
import org.demo.aingthon.global.entity.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private LocalTime scheduledTime;

    @Column(nullable = false)
    private String location;

    protected Schedule() {}

    public Schedule(Match match, LocalDate scheduledDate, LocalTime scheduledTime, String location) {
        this.match = match;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.location = location;
    }

    public void update(LocalDate scheduledDate, LocalTime scheduledTime, String location) {
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.location = location;
    }

    public boolean isPast() {
        return LocalDateTime.of(scheduledDate, scheduledTime).isBefore(LocalDateTime.now());
    }

    public boolean isUpcoming() {
        return LocalDateTime.of(scheduledDate, scheduledTime).isAfter(LocalDateTime.now());
    }

    public Long getId()                 { return id; }
    public Match getMatch()             { return match; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public LocalTime getScheduledTime() { return scheduledTime; }
    public String getLocation()         { return location; }
}
