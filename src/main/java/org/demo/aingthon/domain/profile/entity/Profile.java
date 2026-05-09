package org.demo.aingthon.domain.profile.entity;

import jakarta.persistence.*;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "profiles")
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "profile_fields", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "field")
    private Set<Field> fields = new LinkedHashSet<>();

    @Column(nullable = false)
    private boolean major;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "profile_tech_stacks", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "tech_stack")
    private List<String> techStacks = new ArrayList<>();

    @Column
    private String university;

    @Enumerated(EnumType.STRING)
    @Column
    private Grade grade;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> careers = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> projectExperiences = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column
    private String link;

    protected Profile() {}

    public Profile(User user, String name, String introduction, Set<Field> fields,
                   boolean major, List<String> techStacks, String university, Grade grade,
                   List<String> careers, List<String> projectExperiences, String goal, String link) {
        this.user = user;
        this.name = name;
        this.introduction = introduction;
        this.fields = fields != null ? new LinkedHashSet<>(fields) : new LinkedHashSet<>();
        this.major = major;
        this.techStacks = techStacks != null ? new ArrayList<>(techStacks) : new ArrayList<>();
        this.university = university;
        this.grade = grade;
        this.careers = careers != null ? new ArrayList<>(careers) : new ArrayList<>();
        this.projectExperiences = projectExperiences != null ? new ArrayList<>(projectExperiences) : new ArrayList<>();
        this.goal = goal;
        this.link = link;
    }

    public void update(String name, String introduction, Set<Field> fields,
                       boolean major, List<String> techStacks, Grade grade,
                       List<String> careers, List<String> projectExperiences, String goal, String link) {
        this.name = name;
        this.introduction = introduction;
        this.fields.clear();
        if (fields != null) this.fields.addAll(fields);
        this.major = major;
        this.techStacks.clear();
        if (techStacks != null) this.techStacks.addAll(techStacks);
        this.grade = grade;
        this.careers = careers != null ? new ArrayList<>(careers) : new ArrayList<>();
        this.projectExperiences = projectExperiences != null ? new ArrayList<>(projectExperiences) : new ArrayList<>();
        this.goal = goal;
        this.link = link;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getName() { return name; }
    public String getIntroduction() { return introduction; }
    public Set<Field> getFields() { return fields; }
    public boolean isMajor() { return major; }
    public List<String> getTechStacks() { return techStacks; }
    public String getUniversity() { return university; }
    public Grade getGrade() { return grade; }
    public List<String> getCareers() { return careers; }
    public List<String> getProjectExperiences() { return projectExperiences; }
    public String getGoal() { return goal; }
    public String getLink() { return link; }
}
