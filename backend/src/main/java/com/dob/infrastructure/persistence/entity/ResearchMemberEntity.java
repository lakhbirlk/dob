package com.dob.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "research_members")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResearchMemberEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(length = 100)
    private String occupation;

    @Column(length = 255)
    private String organization;

    @Column(length = 255)
    private String designation;

    @Column(name = "research_purpose", nullable = false, length = 50)
    private String researchPurpose;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(name = "industries_of_interest", columnDefinition = "TEXT")
    private String industriesOfInterest;

    @Column(name = "company_size_preference", length = 50)
    private String companySizePreference;

    @Column(name = "notification_preferences", columnDefinition = "TEXT")
    private String notificationPreferences;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
