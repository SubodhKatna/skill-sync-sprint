package com.skillsync.mentor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mentor {

    public enum MentorStatus {
        PENDING, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private String name;
    private String email;
    private String bio;
    private Integer experienceYears;
    private Double hourlyRate;
    private String availability;
    private Double rating = 0.0;
    private Integer totalReviews = 0;

    @Enumerated(EnumType.STRING)
    private MentorStatus status = MentorStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
