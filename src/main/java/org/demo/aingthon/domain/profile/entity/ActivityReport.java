package org.demo.aingthon.domain.profile.entity;

import jakarta.persistence.*;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.match.entity.Schedule;
import org.demo.aingthon.global.entity.BaseEntity;

@Entity
@Table(name = "activity_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"schedule_id", "user_id"})
})
public class ActivityReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String insights;

    @Column(columnDefinition = "TEXT")
    private String nextGoal;

    protected ActivityReport() {}

    public ActivityReport(Schedule schedule, User user, String insights, String nextGoal) {
        this.schedule = schedule;
        this.user = user;
        this.insights = insights;
        this.nextGoal = nextGoal;
    }

    public void update(String insights, String nextGoal) {
        this.insights = insights;
        this.nextGoal = nextGoal;
    }

    public Long getId() { return id; }
    public Schedule getSchedule() { return schedule; }
    public User getUser() { return user; }
    public String getInsights() { return insights; }
    public String getNextGoal() { return nextGoal; }
}
