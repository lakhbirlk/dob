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

    @Schema(description = "Company Logo URL")
    String logoUrl,

    // ── Identifiers ──
    @Schema(description = "CIN (Corporate Identification Number)")
    String cin,

    @Schema(description = "GSTIN (Goods and Services Tax Identification Number)")
    String gstin,

    @Schema(description = "PAN (Permanent Account Number)")
    String pan,

    @Schema(description = "Registration number")
    String registrationNumber,

    @Schema(description = "Company Registration Number")
    String companyRegistrationNumber,

    // ── Industry & Classification ──
    @Schema(description = "Industry sector", example = "Information Technology")
    String industry,

    @Schema(description = "Sub-sector")
    String subSector,

    @Schema(description = "Business/company type", example = "Private Limited")
    String businessType,

    @Schema(description = "Business Model", example = "B2B SaaS")
    String businessModel,

    @Schema(description = "Company Stage", example = "Growth")
    String companyStage,

    // ── Incorporation & Age ──
    @Schema(description = "Year of incorporation", example = "2016")
    Integer incorporationYear,

    @Schema(description = "Company age in years", example = "8")
    Integer companyAge,

    // ── Location ──
    @Schema(description = "Registered state", example = "Karnataka")
    String state,

    @Schema(description = "Registered city", example = "Bengaluru")
    String city,

    @Schema(description = "Full registered address")
    String address,

    @Schema(description = "Headquarters", example = "Bengaluru, Karnataka")
    String headquarter,

    @Schema(description = "Number of branch offices")
    Integer numBranches,

    // ── Contact ──
    @Schema(description = "Contact email")
    String email,

    @Schema(description = "Phone Number")
    String phoneNumber,

    @Schema(description = "Company website URL")
    String website,

    @Schema(description = "LinkedIn Profile URL")
    String linkedinUrl,

    @Schema(description = "Twitter/X URL")
    String twitterUrl,

    // ── Size & Revenue ──
    @Schema(description = "Employee range", example = "200-500")
    String employeeRange,

    @Schema(description = "Employee count")
    Integer employeeCount,

    @Schema(description = "Annual Revenue")
    String annualRevenue,

    @Schema(description = "Revenue range", example = "₹50Cr-100Cr")
    String revenueRange,

    @Schema(description = "Total Funding")
    String totalFunding,

    @Schema(description = "Investors")
    String investors,

    // ── Risk & Verification ──
    @Schema(description = "Risk score", example = "Medium")
    String riskScore,

    @Schema(description = "Whether the company profile is CA-verified", example = "true")
    boolean verified,

    @Schema(description = "Always false for premium users")
    boolean locked,

    // ── Description & Brand ──
    @Schema(description = "Business description")
    String description,

    @Schema(description = "Company Mission")
    String mission,

    @Schema(description = "Company Vision")
    String vision,

    @Schema(description = "Company Culture Summary")
    String cultureSummary,

    // ── Leadership ──
    @Schema(description = "Key executives/directors")
    List<Map<String, String>> keyExecutives,

    @Schema(description = "CEO Name")
    String ceoName,

    @Schema(description = "CTO Name")
    String ctoName,

    @Schema(description = "Founder(s)")
    String founders,

    // ── Products & Services ──
    @Schema(description = "Products")
    String products,

    @Schema(description = "Services")
    String services,

    @Schema(description = "Technologies Used")
    String technologiesUsed,

    // ── Certifications & Awards ──
    @Schema(description = "Certifications Overview")
    String certificationsOverview,

    @Schema(description = "Awards")
    String awards,

    // ── Shareholding ──
    @Schema(description = "Shareholding pattern")
    List<Map<String, Object>> shareholding,

    // ── Financials ──
    @Schema(description = "Historical financial statements (CA-certified)")
    List<FinancialStatementDto> financials,

    // ── Certificates ──
    @Schema(description = "CA certificates verifying financial data")
    List<CertificateDto> certificates,

    // ── Videos ──
    @Schema(description = "Company introduction/promotional videos")
    List<VideoDto> videos,

    // ── AI & Reports ──
    @Schema(description = "AI-generated analysis/report")
    String aiAnalysis,

    @Schema(description = "Link to downloadable risk report")
    String riskReportUrl,

    @Schema(description = "Whether the user can download the report")
    boolean canDownload,

    @Schema(description = "Public company URL / slug")
    String companyUrl,

    // ── Status ──
    @Schema(description = "Listing status", example = "APPROVED")
    String status,

    @Schema(description = "Dashboard listing status", example = "Approved")
    String dashboardStatus,

    // ── Timestamps ──
    @Schema(description = "Creation timestamp")
    Instant createdAt,

    @Schema(description = "Last update timestamp")
    Instant updatedAt

) {}
