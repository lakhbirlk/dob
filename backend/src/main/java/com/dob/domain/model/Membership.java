package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class Membership {
    private UUID id;
    private UUID userId;
    private String planType;
    private MembershipStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private int downloadLimit;
    private int downloadsUsed;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE
            && endDate != null
            && !endDate.isBefore(LocalDate.now());
    }

    public boolean canDownload() {
        return isActive() && downloadsUsed < downloadLimit;
    }

    public void incrementDownloads() {
        if (!canDownload()) {
            throw new IllegalStateException("Download limit exceeded or membership inactive");
        }
        this.downloadsUsed++;
    }

    public void cancel() {
        this.status = MembershipStatus.CANCELLED;
    }

    public void markRefunded() {
        this.status = MembershipStatus.REFUNDED;
    }

    public enum MembershipStatus {
        ACTIVE, EXPIRED, CANCELLED, REFUNDED
    }
}
