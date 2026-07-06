package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Full company detail including workflow status, profile, financials, certificates, and videos")
public record CompanyDetailDto(

    @Schema(description = "Company UUID")
    UUID id,

    @Schema(description = "Public DoB Company ID", example = "DOB-7F92A1BC")
    String publicCompanyId,

    @Schema(description = "Company legal name")
    String name,

    @Schema(description = "Industry sector")
    String sector,

    @Schema(description = "Registered state")
    String state,

    @Schema(description = "Registered city")
    String city,

    @Schema(description = "Company type", example = "Private Limited")
    String companyType,

    @Schema(description = "Year of incorporation")
    Integer incorporationYear,

    @Schema(description = "Business description")
    String description,

    @Schema(description = "Company website URL")
    String website,

    @Schema(description = "Company logo URL")
    String logoUrl,

    // ── Workflow Status Fields ──
    @Schema(description = "Listing status", example = "DRAFT",
        allowableValues = {"DRAFT", "PENDING_REVIEW", "REJECTED", "APPROVED_MEMBERSHIP_PENDING", "APPROVED_ACTIVE", "MEMBERSHIP_EXPIRED", "SUSPENDED"})
    String status,

    @Schema(description = "UUID of the user who created this listing")
    UUID createdBy,

    @Schema(description = "UUID of the admin who approved/rejected")
    UUID approvedBy,

    @Schema(description = "Timestamp when the company was approved or rejected")
    Instant approvedAt,

    @Schema(description = "Timestamp when the company was submitted for review")
    Instant submittedAt,

    @Schema(description = "Admin's rejection comment (when status is REJECTED)")
    String rejectionComment,

    @Schema(description = "Date when the listing membership expires")
    LocalDate listingExpiresAt,

    @Schema(description = "Whether the company is currently publicly visible")
    boolean isPubliclyVisible,

    @Schema(description = "Whether the company has an active listing membership")
    boolean hasActiveListingMembership,

    // ── Extended Profile ──
    @Schema(description = "Extended company profile")
    CompanyProfileDto profile,

    @Schema(description = "Historical financial statements (CA-certified)")
    List<FinancialStatementDto> financials,

    @Schema(description = "CA certificates verifying the financial data")
    List<CertificateDto> certificates,

    @Schema(description = "Company introduction/promotional videos")
    List<VideoDto> videos,

    // ── JSON aggregate data (raw, for owner/detail view) ──
    @Schema(description = "Raw financial data JSON")
    String financialDataJson,

    @Schema(description = "Raw certificates data JSON")
    String certificatesDataJson,

    @Schema(description = "Raw videos data JSON")
    String videosDataJson,

    // ── Timestamps ──
    @Schema(description = "Creation timestamp")
    Instant createdAt,

    @Schema(description = "Last update timestamp")
    Instant updatedAt

) {}
