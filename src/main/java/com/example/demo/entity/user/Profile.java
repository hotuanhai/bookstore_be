package com.example.demo.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 30)
    private String displayName;
    private String avatarUrl;

    @Column(length = 255)
    private String bio;

    private LocalDate dateOfBirth;

    private String location;

    private String websiteUrl;
    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Profile() {}

    public Profile(User user) {
        this.user = user;
    }

}
