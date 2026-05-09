package org.demo.aingthon.domain.profile.entity;

import jakarta.persistence.*;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.global.entity.BaseEntity;

@Entity
@Table(name = "activity_reports")
public class ActivityReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String insights;

    @Column(columnDefinition = "TEXT")
    private String nextGoal;

    protected ActivityReport() {}

    public ActivityReport(User user, String insights, String nextGoal) {
        this.user = user;
        this.insights = insights;
        this.nextGoal = nextGoal;
    }

    public void update(String insights, String nextGoal) {
        this.insights = insights;
        this.nextGoal = nextGoal;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getInsights() { return insights; }
    public String getNextGoal() { return nextGoal; }
}
