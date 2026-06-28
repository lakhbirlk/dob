package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Full company information returned to premium (subscribed) users.
 * Contains all business information including identifying fields.
 */
@Schema(description = "Complete company profile for premium (subscribed) users")
public record PremiumCompanyResponse(
    @Schema(description = "Public DoB Company ID", example = "DOB-7F92A1BC")
    String companyId,

    @Schema(description = "Company legal name", example = "TechVentures India Pvt Ltd")
    String companyName,

    @Schema(description = "CIN (Corporate Identification Number)")
    String cin,

    @Schema(description = "GSTIN (Goods and Services Tax Identification Number)")
    String gstin,

    @Schema(description = "PAN (Permanent Account Number)")
    String pan,

    @Schema(description = "Registration number")
    String registrationNumber,

    @Schema(description = "Industry sector", example = "Information Technology")
    String industry,

    @Schema(description = "Sub-sector")
    String subSector,

    @Schema(description = "Business/company type", example = "Private Limited")
    String businessType,

    @Schema(description = "Year of incorporation", example = "2016")
    Integer incorporationYear,

    @Schema(description = "Company age in years", example = "8")
    Integer companyAge,

    @Schema(description = "Registered state", example = "Karnataka")
    String state,

    @Schema(description = "Registered city", example = "Bengaluru")
    String city,

    @Schema(description = "Full registered address")
    String address,

    @Schema(description = "Contact email")
    String email,

    @Schema(description = "Company website URL")
    String website,

    @Schema(description = "Employee range", example = "200-500")
    String employeeRange,

    @Schema(description = "Revenue range", example = "₹50Cr-100Cr")
    String revenueRange,

    @Schema(description = "Risk score", example = "Medium")
    String riskScore,

    @Schema(description = "Whether the company profile is CA-verified", example = "true")
    boolean verified,

    @Schema(description = "Always false for premium users")
    boolean locked,

    @Schema(description = "Business description")
    String description,

    @Schema(description = "Company logo URL")
    String logoUrl,

    @Schema(description = "Key executives/directors")
    List<Map<String, String>> keyExecutives,

    @Schema(description = "Shareholding pattern")
    List<Map<String, Object>> shareholding,

    @Schema(description = "Historical financial statements (CA-certified)")
    List<FinancialStatementDto> financials,

    @Schema(description = "CA certificates verifying financial data")
    List<CertificateDto> certificates,

    @Schema(description = "Company introduction/promotional videos")
    List<VideoDto> videos,

    @Schema(description = "AI-generated analysis/report")
    String aiAnalysis,

    @Schema(description = "Link to downloadable risk report")
    String riskReportUrl,

    @Schema(description = "Whether the user can download the report")
    boolean canDownload,

    @Schema(description = "Public company URL / slug")
    String companyUrl,

    @Schema(description = "Listing status", example = "APPROVED")
    String status,

    @Schema(description = "Creation timestamp")
    Instant createdAt,

    @Schema(description = "Last update timestamp")
    Instant updatedAt
) {}
