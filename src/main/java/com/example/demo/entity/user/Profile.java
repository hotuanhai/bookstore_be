package com.example.demo.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private String facebookUrl;

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

    public Profile(User user) {
        this.user = user;
        this.displayName = user.getUsername();
        this.avatarUrl = null;
        this.bio = null;
        this.location = null;
        this.facebookUrl = null;
        this.dateOfBirth = null;
    }
}