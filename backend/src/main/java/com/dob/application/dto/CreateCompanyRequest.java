package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating or updating a company listing")
public record CreateCompanyRequest(
    @Schema(description = "Company/business name", example = "TechVentures India Pvt Ltd")
    @NotBlank @Size(max = 500) String name,

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

    @Schema(description = "Detailed business description")
    String description,

    @Schema(description = "Company website URL", example = "https://techventures.example.com")
    String website
) {}
