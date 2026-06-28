package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class Company {
    private UUID id;
    private String publicCompanyId;
    private String name;
    private String brandName;
    private String sector;
    private String state;
    private String city;
    private String companyType;
    private Integer incorporationYear;
    private String description;
    private String website;
    private String logoUrl;
    private CompanyStatus status;
    private UUID createdBy;
    private UUID approvedBy;
    private Instant approvedAt;
    private Instant createdAt;
    private Instant updatedAt;

    // Registration fields
    private String cin;
    private String gstin;
    private String pan;
    private String tan;
    private String msmeRegistration;
    private String startupIndiaRegistration;

    // Registered Office
    private String registeredAddressLine1;
    private String registeredAddressLine2;
    private String registeredCity;
    private String registeredState;
    private String registeredPinCode;
    private String registeredCountry;

    // Contact Details
    private String officialEmail;
    private String officialPhone;
    private String linkedinProfile;
    private String socialMediaLinks;

    // Authorized Representative
    private String authorizedRepName;
    private String authorizedRepDesignation;
    private String authorizedRepMobile;
    private String authorizedRepEmail;
    private String authorizedRepIdentityProofUrl;
    private String authorizedRepDigitalSignatureUrl;

    // Financial Information
    private String annualTurnover;
    private String paidUpCapital;
    private String authorizedCapital;
    private Integer employeeCount;
    private String financialYear;
    private String balanceSheetUrl;
    private String auditorDetails;

    // Business Information
    private String productsServices;
    private String businessDescription;
    private String exportImportStatus;
    private Integer numBranches;
    private String operationalStates;
    private String certifications;

    public void approve(UUID adminId) {
        if (status != CompanyStatus.PENDING) {
            throw new IllegalStateException("Only pending companies can be approved");
        }
        this.status = CompanyStatus.APPROVED;
        this.approvedBy = adminId;
        this.approvedAt = Instant.now();
    }

    public void reject(UUID adminId) {
        if (status != CompanyStatus.PENDING) {
            throw new IllegalStateException("Only pending companies can be rejected");
        }
        this.status = CompanyStatus.REJECTED;
        this.approvedBy = adminId;
        this.approvedAt = Instant.now();
    }

    public void suspend() {
        this.status = CompanyStatus.SUSPENDED;
    }

    public boolean isActive() {
        return status == CompanyStatus.APPROVED;
    }

    public static String generatePublicCompanyId() {
        var random = new SecureRandom();
        var hex = new byte[4];
        random.nextBytes(hex);
        var sb = new StringBuilder("DOB-");
        for (byte b : hex) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public enum CompanyStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED
    }
}
