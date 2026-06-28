package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "Company registration payload")
public record CompanyRegistrationRequest(

    // ═══════════════════════════════════════════════════════════════
    // Account Information
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Email address (login ID)", example = "company@example.com")
    @NotBlank @Email String email,

    @Schema(description = "Mobile number", example = "9876543210")
    @NotBlank @Size(max = 15) String mobile,

    @Schema(description = "Password (minimum 8 characters)", example = "SecurePass123")
    @NotBlank @Size(min = 8, max = 100) String password,

    @Schema(description = "Confirm password — must match password")
    @NotBlank String confirmPassword,

    // ═══════════════════════════════════════════════════════════════
    // Company Information
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Legal company name", example = "TechVentures India Pvt Ltd")
    @NotBlank @Size(max = 500) String legalCompanyName,

    @Schema(description = "Brand name (optional)", example = "TechVentures")
    @Size(max = 255) String brandName,

    @Schema(description = "Company type", example = "Private Limited", allowableValues = {"Private Limited", "Public Limited", "LLP", "Sole Proprietorship", "Partnership", "Other"})
    @NotBlank String companyType,

    @Schema(description = "Industry/Sector", example = "Information Technology")
    @NotBlank String industry,

    @Schema(description = "Business category", example = "Software Development")
    @NotBlank String businessCategory,

    @Schema(description = "Date of incorporation (ISO format)", example = "2015-06-15")
    @NotBlank String dateOfIncorporation,

    @Schema(description = "CIN (Company Identification Number)", example = "U72300KA2016PTC123456")
    @Size(min = 21, max = 21) String cin,

    @Schema(description = "GST Number", example = "29ABCDE1234F1Z5")
    @Size(max = 15) String gstNumber,

    @Schema(description = "PAN of the company", example = "AABCT1234E")
    @Size(min = 10, max = 10) String pan,

    @Schema(description = "TAN (optional)", example = "BANP12345A")
    @Size(max = 10) String tan,

    @Schema(description = "MSME Registration (optional)", example = "UDYAM-MH-01-0001234")
    @Size(max = 50) String msmeRegistration,

    @Schema(description = "Startup India Registration (optional)", example = "DIPP12345")
    @Size(max = 50) String startupIndiaRegistration,

    // ═══════════════════════════════════════════════════════════════
    // Registered Office
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Address Line 1", example = "123, MG Road")
    @NotBlank String addressLine1,

    @Schema(description = "Address Line 2", example = "Indiranagar")
    String addressLine2,

    @Schema(description = "City", example = "Bengaluru")
    @NotBlank String city,

    @Schema(description = "State", example = "Karnataka")
    @NotBlank String state,

    @Schema(description = "PIN Code", example = "560038")
    @NotBlank @Size(max = 10) String pinCode,

    @Schema(description = "Country", example = "India")
    @NotBlank String country,

    // ═══════════════════════════════════════════════════════════════
    // Contact Details
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Official email", example = "contact@company.com")
    @Email String officialEmail,

    @Schema(description = "Official phone", example = "9876543210")
    @Size(max = 20) String officialPhone,

    @Schema(description = "Website", example = "https://www.company.com")
    String website,

    @Schema(description = "LinkedIn profile URL", example = "https://linkedin.com/company/company")
    String linkedinProfile,

    @Schema(description = "Social media links (optional)")
    String socialMediaLinks,

    // ═══════════════════════════════════════════════════════════════
    // Authorized Representative
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Authorized representative full name")
    @NotBlank String authorizedRepName,

    @Schema(description = "Authorized representative designation", example = "Director")
    @NotBlank String authorizedRepDesignation,

    @Schema(description = "Authorized representative mobile", example = "9876543210")
    @NotBlank @Size(max = 15) String authorizedRepMobile,

    @Schema(description = "Authorized representative email")
    @NotBlank @Email String authorizedRepEmail,

    @Schema(description = "Identity proof document URL (after upload)")
    String authorizedRepIdentityProofUrl,

    @Schema(description = "Digital signature URL (optional)")
    String authorizedRepDigitalSignatureUrl,

    // ═══════════════════════════════════════════════════════════════
    // Financial Information
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Annual turnover", example = "₹50Cr-100Cr")
    String annualTurnover,

    @Schema(description = "Paid-up capital", example = "₹1Cr")
    String paidUpCapital,

    @Schema(description = "Authorized capital", example = "₹5Cr")
    String authorizedCapital,

    @Schema(description = "Number of employees")
    Integer employeeCount,

    @Schema(description = "Financial year", example = "2024-25")
    String financialYear,

    @Schema(description = "Last filed balance sheet URL")
    String balanceSheetUrl,

    @Schema(description = "Auditor details (optional)")
    String auditorDetails,

    // ═══════════════════════════════════════════════════════════════
    // Business Information
    // ═══════════════════════════════════════════════════════════════
    @Schema(description = "Products & services description")
    @NotBlank String productsServices,

    @Schema(description = "Business description")
    String businessDescription,

    @Schema(description = "Export/Import status", example = "Both", allowableValues = {"Export", "Import", "Both", "None"})
    String exportImportStatus,

    @Schema(description = "Number of branches")
    @Min(0) Integer numBranches,

    @Schema(description = "Operational states (comma-separated)", example = "Karnataka, Maharashtra, Tamil Nadu")
    String operationalStates,

    @Schema(description = "Certifications (comma-separated)", example = "ISO 9001:2015, ISO 27001")
    String certifications,

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
