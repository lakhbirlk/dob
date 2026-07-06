package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a company listing's details")
public record UpdateCompanyRequest(

    // Basic Info
    @Schema(description = "Company name", example = "TechVentures India Pvt Ltd")
    String name,

    @Schema(description = "Brand name", example = "TechVentures")
    String brandName,

    @Schema(description = "Sector", example = "Technology")
    String sector,

    @Schema(description = "City", example = "Mumbai")
    String city,

    @Schema(description = "State", example = "Maharashtra")
    String state,

    @Schema(description = "Company type", example = "Private Limited")
    String companyType,

    @Schema(description = "Incorporation year")
    Integer incorporationYear,

    @Schema(description = "Brief description")
    String description,

    @Schema(description = "Business description")
    String businessDescription,

    @Schema(description = "Website URL")
    String website,

    @Schema(description = "Logo URL")
    String logoUrl,

    // Registration
    @Schema(description = "CIN", example = "U72100MH2020PTC123456")
    String cin,

    @Schema(description = "GSTIN", example = "27AABCU1234D1Z0")
    String gstin,

    @Schema(description = "PAN", example = "AABCU1234D")
    String pan,

    @Schema(description = "TAN", example = "MUM12345A")
    String tan,

    @Schema(description = "MSME registration number")
    String msmeRegistration,

    @Schema(description = "Startup India registration number")
    String startupIndiaRegistration,

    @Schema(description = "Company registration number (ROC)")
    String companyRegistrationNumber,

    // Registered Office
    @Schema(description = "Registered address line 1")
    String registeredAddressLine1,

    @Schema(description = "Registered address line 2")
    String registeredAddressLine2,

    @Schema(description = "Registered city")
    String registeredCity,

    @Schema(description = "Registered state")
    String registeredState,

    @Schema(description = "Registered pincode")
    String registeredPinCode,

    @Schema(description = "Registered country")
    String registeredCountry,

    // Contact
    @Schema(description = "Official email")
    String officialEmail,

    @Schema(description = "Official phone")
    String officialPhone,

    @Schema(description = "Phone number")
    String phoneNumber,

    @Schema(description = "Headquarter")
    String headquarter,

    @Schema(description = "LinkedIn profile URL")
    String linkedinProfile,

    @Schema(description = "Twitter URL")
    String twitterUrl,

    @Schema(description = "Social media links JSON")
    String socialMediaLinks,

    // Financial
    @Schema(description = "Annual turnover")
    String annualTurnover,

    @Schema(description = "Paid-up capital")
    String paidUpCapital,

    @Schema(description = "Authorized capital")
    String authorizedCapital,

    @Schema(description = "Employee count")
    Integer employeeCount,

    @Schema(description = "Financial year", example = "2023-24")
    String financialYear,

    @Schema(description = "Total funding raised")
    String totalFunding,

    @Schema(description = "Investors JSON")
    String investors,

    // Business
    @Schema(description = "Products and services")
    String productsServices,

    @Schema(description = "Export/import status")
    String exportImportStatus,

    @Schema(description = "Number of branches")
    Integer numBranches,

    @Schema(description = "Operational states")
    String operationalStates,

    @Schema(description = "Certifications")
    String certifications,

    @Schema(description = "Technologies used")
    String technologiesUsed,

    // Extended Profile
    @Schema(description = "CEO name")
    String ceoName,

    @Schema(description = "CTO name")
    String ctoName,

    @Schema(description = "Founders JSON")
    String founders,

    @Schema(description = "Business model")
    String businessModel,

    @Schema(description = "Company stage (STARTUP / GROWTH / ENTERPRISE)")
    String companyStage,

    @Schema(description = "Awards JSON")
    String awards,

    @Schema(description = "Culture summary")
    String cultureSummary,

    @Schema(description = "Mission")
    String mission,

    @Schema(description = "Vision")
    String vision,

    // Authorized Representative
    @Schema(description = "Authorized representative name")
    String authorizedRepName,

    @Schema(description = "Authorized representative designation")
    String authorizedRepDesignation,

    @Schema(description = "Authorized representative mobile")
    String authorizedRepMobile,

    @Schema(description = "Authorized representative email")
    String authorizedRepEmail,

    // Auditor
    @Schema(description = "Auditor details")
    String auditorDetails
) {}
