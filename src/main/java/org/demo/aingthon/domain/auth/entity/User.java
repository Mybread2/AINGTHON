package org.demo.aingthon.domain.auth.entity;

import jakarta.persistence.*;
import org.demo.aingthon.global.entity.BaseEntity;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String university;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {}

    public User(String email, String name, String university, Role role) {
        this.email = email;
        this.name = name;
        this.university = university;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getUniversity() { return university; }
    public Role getRole() { return role; }
}
