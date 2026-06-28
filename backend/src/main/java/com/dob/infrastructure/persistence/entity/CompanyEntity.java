package com.dob.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompanyEntity {

    @Id
    private UUID id;

    @Column(name = "public_company_id", nullable = false, length = 12, unique = true)
    private String publicCompanyId;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(length = 100)
    private String sector;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(name = "company_type", length = 50)
    private String companyType;

    @Column(name = "incorporation_year")
    private Integer incorporationYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String website;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    // Registration fields
    @Column(length = 21)
    private String cin;

    @Column(length = 15)
    private String gstin;

    @Column(length = 10)
    private String pan;

    @Column(length = 10)
    private String tan;

    @Column(name = "msme_registration", length = 50)
    private String msmeRegistration;

    @Column(name = "startup_india_registration", length = 50)
    private String startupIndiaRegistration;

    // Registered Office
    @Column(name = "registered_address_line1", length = 255)
    private String registeredAddressLine1;

    @Column(name = "registered_address_line2", length = 255)
    private String registeredAddressLine2;

    @Column(name = "registered_city", length = 100)
    private String registeredCity;

    @Column(name = "registered_state", length = 100)
    private String registeredState;

    @Column(name = "registered_pin_code", length = 10)
    private String registeredPinCode;

    @Column(name = "registered_country", length = 100)
    private String registeredCountry;

    // Contact Details
    @Column(name = "official_email", length = 255)
    private String officialEmail;

    @Column(name = "official_phone", length = 20)
    private String officialPhone;

    @Column(name = "linkedin_profile", length = 255)
    private String linkedinProfile;

    @Column(name = "social_media_links", columnDefinition = "TEXT")
    private String socialMediaLinks;

    // Authorized Representative
    @Column(name = "authorized_rep_name", length = 255)
    private String authorizedRepName;

    @Column(name = "authorized_rep_designation", length = 255)
    private String authorizedRepDesignation;

    @Column(name = "authorized_rep_mobile", length = 15)
    private String authorizedRepMobile;

    @Column(name = "authorized_rep_email", length = 255)
    private String authorizedRepEmail;

    @Column(name = "authorized_rep_identity_proof_url", length = 500)
    private String authorizedRepIdentityProofUrl;

    @Column(name = "authorized_rep_digital_signature_url", length = 500)
    private String authorizedRepDigitalSignatureUrl;

    // Financial Information
    @Column(name = "annual_turnover", length = 50)
    private String annualTurnover;

    @Column(name = "paid_up_capital", length = 50)
    private String paidUpCapital;

    @Column(name = "authorized_capital", length = 50)
    private String authorizedCapital;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(name = "financial_year", length = 9)
    private String financialYear;

    @Column(name = "balance_sheet_url", length = 500)
    private String balanceSheetUrl;

    @Column(name = "auditor_details", columnDefinition = "TEXT")
    private String auditorDetails;

    // Business Information
    @Column(name = "products_services", columnDefinition = "TEXT")
    private String productsServices;

    @Column(name = "business_description", columnDefinition = "TEXT")
    private String businessDescription;

    @Column(name = "export_import_status", length = 20)
    private String exportImportStatus;

    @Column(name = "num_branches")
    private Integer numBranches;

    @Column(name = "operational_states", columnDefinition = "TEXT")
    private String operationalStates;

    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum CompanyStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (publicCompanyId == null) {
            publicCompanyId = generatePublicCompanyId();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    private static String generatePublicCompanyId() {
        var random = new java.security.SecureRandom();
        var hex = new byte[4];
        random.nextBytes(hex);
        var sb = new StringBuilder("DOB-");
        for (byte b : hex) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
