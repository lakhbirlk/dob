package com.dob.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "research_member_unlocked_company",
       uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "company_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UnlockedCompanyEntity {

    @Id
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "credits_used", nullable = false)
    private int creditsUsed;

    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private Instant unlockedAt;

    @Column(name = "unlocked_by", nullable = false)
    private UUID unlockedBy;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (unlockedAt == null) unlockedAt = Instant.now();
    }
}
