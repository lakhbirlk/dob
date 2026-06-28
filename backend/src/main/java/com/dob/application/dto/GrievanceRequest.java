package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for filing a new grievance")
public record GrievanceRequest(
    @Schema(description = "Type of complaint", example = "Data Discrepancy", allowableValues = {"Data Discrepancy", "Download Issue", "Billing", "Technical", "Other"})
    @NotBlank String complaintType,

    @Schema(description = "Detailed description of the issue", example = "Financial figures reported for company XYZ do not match the CA certificate.")
    @NotBlank String description,

    @Schema(description = "Priority level", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    String priority
) {}
