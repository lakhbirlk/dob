package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Authentication response with JWT tokens and user profile")
public record AuthResponse(
    @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzUxMiJ9...")
    String accessToken,

    @Schema(description = "Refresh token for obtaining new access tokens", example = "eyJhbGciOiJIUzUxMiJ9...")
    String refreshToken,

    @Schema(description = "Token type", example = "Bearer")
    String tokenType,

    @Schema(description = "Token expiration in seconds", example = "900")
    long expiresIn,

    @Schema(description = "Authenticated user profile")
    UserDto user
) {}
