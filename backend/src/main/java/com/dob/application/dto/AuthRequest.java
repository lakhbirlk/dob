package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login credentials")
public record AuthRequest(
    @Schema(description = "Registered email address", example = "admin@dataofbusiness.in")
    @NotBlank @Email String email,

    @Schema(description = "Account password", example = "password123")
    @NotBlank @Size(min = 8, max = 100) String password
) {}
