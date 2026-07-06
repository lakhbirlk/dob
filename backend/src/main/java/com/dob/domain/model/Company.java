package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Company {
    private UUID id;
    private String publicCompanyId;
    private String name;
    private String brandName;
    private String sector;
    private String state;
    private String city;
    private String companyType;
    private Integer incorporationYear;
    private String description;
    private String website;
    private String logoUrl;
    private CompanyStatus status;
    private UUID createdBy;
    private UUID approvedBy;
    private Instant approvedAt;
    private Instant submittedAt;
    private String rejectionComment;
    private LocalDate listingExpiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    // Registration fields
    private String cin;
    private String gstin;
    private String pan;
    private String tan;
    private String msmeRegistration;
    private String startupIndiaRegistration;
    private String companyRegistrationNumber;

    // Registered Office
    private String registeredAddressLine1;
    private String registeredAddressLine2;
    private String registeredCity;
    private String registeredState;
    private String registeredPinCode;
    private String registeredCountry;

    // Contact Details
    private String officialEmail;
    private String officialPhone;
    private String linkedinProfile;
    private String socialMediaLinks;
    private String twitterUrl;
    private String phoneNumber;
    private String headquarter;

    // Authorized Representative
    private String authorizedRepName;
    private String authorizedRepDesignation;
    private String authorizedRepMobile;
    private String authorizedRepEmail;
    private String authorizedRepIdentityProofUrl;
    private String authorizedRepDigitalSignatureUrl;

    // Financial Information
    private String annualTurnover;
    private String paidUpCapital;
    private String authorizedCapital;
    private Integer employeeCount;
    private String financialYear;
    private String balanceSheetUrl;
    private String auditorDetails;
    private String totalFunding;
    private String investors;

    // Business Information
    private String productsServices;
    private String businessDescription;
    private String exportImportStatus;
    private Integer numBranches;
    private String operationalStates;
    private String certifications;

    // Extended Company Profile
    private String ceoName;
    private String ctoName;
    private String founders;
    private String businessModel;
    private String companyStage;          // STARTUP / GROWTH / ENTERPRISE
    private String technologiesUsed;
    private String awards;
    private String cultureSummary;
    private String mission;
    private String vision;
    private String dashboardStatus;       // PENDING / UNDER_REVIEW / APPROVED / REJECTED / NEEDS_CHANGES

    // JSON aggregate fields (stored as TEXT columns)
    private String financialDataJson;     // Array of annual financial statements with full docs
    private String certificatesDataJson;  // Array of certificates with full metadata
    private String videosDataJson;        // Array of videos with full metadata

    /**
     * Submit company for admin review. Must be in DRAFT state.
     */
    public void submitForReview() {
        if (status != CompanyStatus.DRAFT) {
            throw new IllegalStateException("Only draft companies can be submitted for review");
        }
        this.status = CompanyStatus.PENDING_REVIEW;
        this.submittedAt = Instant.now();
    }

    /**
     * Admin approves the company. Sets status to APPROVED_MEMBERSHIP_PENDING
     * (not publicly visible until an active listing membership exists).
     * Accepts both DRAFT and PENDING_REVIEW statuses.
     */
    public void approve(UUID adminId) {
        if (status != CompanyStatus.PENDING_REVIEW && status != CompanyStatus.DRAFT) {
            throw new IllegalStateException("Only draft or pending-review companies can be approved. Current: " + status);
        }
        this.status = CompanyStatus.APPROVED_MEMBERSHIP_PENDING;
        this.approvedBy = adminId;
        this.approvedAt = Instant.now();
        this.rejectionComment = null;
        if (this.submittedAt == null) {
            this.submittedAt = Instant.now();
        }
    }

    /**
     * Admin rejects the company with a comment.
     * Accepts both DRAFT and PENDING_REVIEW statuses.
     */
    public void reject(UUID adminId, String comment) {
        if (status != CompanyStatus.PENDING_REVIEW && status != CompanyStatus.DRAFT) {
            throw new IllegalStateException("Only draft or pending-review companies can be rejected. Current: " + status);
        }
        this.status = CompanyStatus.REJECTED;
        this.approvedBy = adminId;
        this.approvedAt = Instant.now();
        this.rejectionComment = comment;
        if (this.submittedAt == null) {
            this.submittedAt = Instant.now();
        }
    }

    /**
     * Publish the company listing by setting to APPROVED_ACTIVE.
     * The company becomes publicly visible only in this status.
     */
    public void publishListing() {
        if (status != CompanyStatus.APPROVED_MEMBERSHIP_PENDING && status != CompanyStatus.MEMBERSHIP_EXPIRED) {
            throw new IllegalStateException(
                "Only approved-membership-pending or membership-expired companies can be published. Current: " + status);
        }
        this.status = CompanyStatus.APPROVED_ACTIVE;
    }

    /**
     * Unpublish the company when membership expires.
     */
    public void expireMembership() {
        if (status != CompanyStatus.APPROVED_ACTIVE) {
            throw new IllegalStateException("Only active listings can expire. Current: " + status);
        }
        this.status = CompanyStatus.MEMBERSHIP_EXPIRED;
    }

    /**
     * Resubmit a rejected company for review.
     */
    public void resubmitForReview() {
        if (status != CompanyStatus.REJECTED) {
            throw new IllegalStateException("Only rejected companies can be resubmitted");
        }
        this.status = CompanyStatus.PENDING_REVIEW;
        this.submittedAt = Instant.now();
        this.approvedBy = null;
        this.approvedAt = null;
        this.rejectionComment = null;
    }

    public void suspend() {
        this.status = CompanyStatus.SUSPENDED;
    }

    public void reactivate() {
        if (status != CompanyStatus.SUSPENDED) {
            throw new IllegalStateException("Only suspended companies can be reactivated");
        }
        this.status = CompanyStatus.APPROVED_MEMBERSHIP_PENDING;
    }

    /**
     * Check if the company is publicly visible in search results.
     */
    public boolean isPubliclyVisible() {
        return status == CompanyStatus.APPROVED_ACTIVE
            && listingExpiresAt != null
            && !listingExpiresAt.isBefore(LocalDate.now());
    }

    /**
     * Check if the company has a valid listing membership.
     */
    public boolean hasActiveListingMembership() {
        return listingExpiresAt != null && !listingExpiresAt.isBefore(LocalDate.now());
    }

    public enum CompanyStatus {
        DRAFT,
        PENDING_REVIEW,
        REJECTED,
        APPROVED_MEMBERSHIP_PENDING,
        APPROVED_ACTIVE,
        MEMBERSHIP_EXPIRED,
        SUSPENDED
    }

    public static String generatePublicCompanyId() {
        var random = new SecureRandom();
        var hex = new byte[4];
        random.nextBytes(hex);
        var sb = new StringBuilder("DOB-");
        for (byte b : hex) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
