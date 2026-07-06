package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Detailed member profile including all memberships (active and history)")
public record MemberDetailDto(

    @Schema(description = "User UUID")
    UUID id,

    @Schema(description = "Full name", example = "Rahul Sharma")
    String fullName,

    @Schema(description = "Email address", example = "member@example.com")
    String email,

    @Schema(description = "Phone number", example = "9876543211")
    String phone,

    @Schema(description = "PAN card number", example = "ABCDE1234F")
    String pan,

    @Schema(description = "User role", example = "RESEARCH_MEMBER")
    String role,

    @Schema(description = "Account active")
    boolean active,

    @Schema(description = "Email verified")
    boolean emailVerified,

    @Schema(description = "Account creation date")
    Instant createdAt,

    @Schema(description = "All memberships (current + history)")
    List<MembershipDto> memberships
) {}
