package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration payload")
public record RegisterRequest(
    @Schema(description = "Email address (will be used for login)", example = "user@example.com")
    @NotBlank @Email String email,

    @Schema(description = "Password — minimum 8 characters", example = "SecurePass123")
    @NotBlank @Size(min = 8, max = 100) String password,

    @Schema(description = "Full legal name", example = "Rahul Sharma")
    @NotBlank @Size(max = 255) String fullName,

    @Schema(description = "Mobile phone number (optional, for OTP)", example = "9876543210")
    @Size(max = 15) String phone
) {}
