package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Research member registration payload")
public record ResearchMemberRegistrationRequest(

    // ═══════════════════════════════════════════════════════════════
    // Personal Information
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Full name", example = "Rahul Sharma")
    @NotBlank @Size(max = 255) String fullName,

    @Schema(description = "Email address (login ID)", example = "researcher@example.com")
    @NotBlank @Email String email,

    @Schema(description = "Mobile number", example = "9876543210")
    @NotBlank @Size(max = 15) String mobile,

    @Schema(description = "Password (minimum 8 characters)", example = "SecurePass123")
    @NotBlank @Size(min = 8, max = 100) String password,

    @Schema(description = "Confirm password — must match password")
    @NotBlank String confirmPassword,

    // ═══════════════════════════════════════════════════════════════
    // Professional Information
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Occupation", example = "Analyst")
    @NotBlank String occupation,

    @Schema(description = "Organization/Company (optional)", example = "ABC Corp")
    String organization,

    @Schema(description = "Designation (optional)", example = "Senior Analyst")
    String designation,

    @Schema(description = "Research purpose", example = "Investment Research",
        allowableValues = {"Investment Research", "Market Research", "Academic Research",
                           "Competitive Intelligence", "Vendor Verification", "Personal Research", "Other"})
    @NotBlank String researchPurpose,

    // ═══════════════════════════════════════════════════════════════
    // Address
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Country", example = "India")
    @NotBlank String country,

    @Schema(description = "State", example = "Maharashtra")
    @NotBlank String state,

    @Schema(description = "City", example = "Mumbai")
    @NotBlank String city,

    // ═══════════════════════════════════════════════════════════════
    // Preferences (Optional)
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Industries of interest (comma-separated)", example = "Technology, Healthcare, Finance")
    String industriesOfInterest,

    @Schema(description = "Company size preference", example = "All", allowableValues = {"Startup", "SME", "Large Enterprise", "All"})
    String companySizePreference,

    @Schema(description = "Notification preferences (JSON)")
    String notificationPreferences,

    // ═══════════════════════════════════════════════════════════════
    // Consent
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Accept Terms & Conditions")
    @AssertTrue boolean acceptTerms,

    @Schema(description = "Accept Privacy Policy")
    @AssertTrue boolean acceptPrivacy

) {
    // Cross-field validation: confirmPassword must match password
    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
