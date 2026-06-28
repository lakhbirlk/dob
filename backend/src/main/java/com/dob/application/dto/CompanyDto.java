package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Company summary information (used in search results and listings)")
public record CompanyDto(
    @Schema(description = "Company UUID — internal, never exposed to public API")
    UUID id,

    @Schema(description = "Public DoB Company ID exposed in APIs", example = "DOB-7F92A1BC")
    String publicCompanyId,

    @Schema(description = "Company/business name", example = "TechVentures India Pvt Ltd")
    String name,

    @Schema(description = "Industry sector", example = "Technology")
    String sector,

    @Schema(description = "Registered state", example = "Maharashtra")
    String state,

    @Schema(description = "Registered city", example = "Mumbai")
    String city,

    @Schema(description = "Company type", example = "Private Limited")
    String companyType,

    @Schema(description = "Year of incorporation", example = "2015")
    Integer incorporationYear,

    @Schema(description = "Business description")
    String description,

    @Schema(description = "Company website URL")
    String website,

    @Schema(description = "Company logo URL")
    String logoUrl,

    @Schema(description = "Listing status", example = "APPROVED", allowableValues = {"PENDING", "APPROVED", "REJECTED", "SUSPENDED"})
    String status,

    @Schema(description = "UUID of the user who created this listing")
    UUID createdBy,

    @Schema(description = "Name of the creator")
    String createdByName,

    @Schema(description = "Creation timestamp")
    Instant createdAt,

    @Schema(description = "Last update timestamp")
    Instant updatedAt
) {}
