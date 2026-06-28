package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "User profile information")
public record UserDto(
    @Schema(description = "User UUID", example = "a0000000-0000-0000-0000-000000000001")
    UUID id,

    @Schema(description = "Registered email address", example = "admin@dataofbusiness.in")
    String email,

    @Schema(description = "PAN card number (verified)", example = "ABCDE1234F")
    String pan,

    @Schema(description = "Full legal name", example = "Admin User")
    String fullName,

    @Schema(description = "Mobile phone number", example = "9876543210")
    String phone,

    @Schema(description = "User role", example = "ADMIN", allowableValues = {"SUPER_ADMIN", "ADMIN", "COMPANY_USER", "RESEARCH_MEMBER", "AUDITOR"})
    String role,

    @Schema(description = "Whether email has been verified")
    boolean emailVerified,

    @Schema(description = "Whether the account is active")
    boolean active
) {}
