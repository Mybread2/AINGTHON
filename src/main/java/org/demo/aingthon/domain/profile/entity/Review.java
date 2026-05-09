package org.demo.aingthon.domain.profile.entity;

import jakarta.persistence.*;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.global.entity.BaseEntity;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(nullable = false)
    private Integer satisfaction;

    @Column(nullable = false, length = 100)
    private String oneLineReview;

    @Column(columnDefinition = "TEXT")
    private String mainContent;

    protected Review() {}

    public Review(User reviewer, User reviewee, Integer satisfaction, String oneLineReview, String mainContent) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.satisfaction = satisfaction;
        this.oneLineReview = oneLineReview;
        this.mainContent = mainContent;
    }

    public Long getId() { return id; }
    public User getReviewer() { return reviewer; }
    public User getReviewee() { return reviewee; }
    public Integer getSatisfaction() { return satisfaction; }
    public String getOneLineReview() { return oneLineReview; }
    public String getMainContent() { return mainContent; }
}
