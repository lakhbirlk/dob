package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Company member details for admin view, including company and listing info")
public record CompanyMemberDto(

    // User fields
    @Schema(description = "User UUID")
    UUID id,

    @Schema(description = "Full name", example = "Vikram Singh")
    String fullName,

    @Schema(description = "Email", example = "company@example.com")
    String email,

    @Schema(description = "Phone", example = "9876543213")
    String phone,

    @Schema(description = "PAN", example = "XYZAB1234C")
    String pan,

    @Schema(description = "Account active")
    boolean active,

    @Schema(description = "Account creation date")
    Instant createdAt,

    // Company fields (primary company)
    @Schema(description = "Primary company UUID")
    UUID companyId,

    @Schema(description = "Company public ID", example = "DOB-XXXXXXXX")
    String publicCompanyId,

    @Schema(description = "Company name", example = "TechVentures India Pvt Ltd")
    String companyName,

    @Schema(description = "Listing status", example = "APPROVED")
    String companyStatus,

    @Schema(description = "Sector", example = "Technology")
    String sector,

    @Schema(description = "City", example = "Mumbai")
    String city,

    @Schema(description = "State", example = "Maharashtra")
    String state,

    @Schema(description = "Company type", example = "Private Limited")
    String companyType,

    @Schema(description = "Number of listings by this user")
    int totalCompanies
) {}
