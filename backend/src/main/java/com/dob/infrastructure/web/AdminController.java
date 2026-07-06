package com.dob.infrastructure.web;

import com.dob.application.dto.*;
import com.dob.application.service.CompanyService;
import com.dob.application.service.GrievanceService;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Company;
import com.dob.domain.model.AuditLog;
import com.dob.domain.model.Membership;
import com.dob.domain.model.User;
import com.dob.domain.repository.AuditLogRepository;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.PaymentRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin and SUPER_ADMIN endpoints for company approvals, members, refunds, grievances, and audit logs")
public class AdminController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final PaymentRepository paymentRepository;
    private final GrievanceService grievanceService;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    // --- Company Approval ---

    @GetMapping("/companies/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get pending companies", description = "Lists companies pending admin review (status = PENDING_REVIEW).")
    public List<CompanyDto> getPendingCompanies() {
        return companyRepository.findByStatus(Company.CompanyStatus.PENDING_REVIEW)
            .stream()
            .map(c -> CompanyDto.builder()
                .id(c.getId()).publicCompanyId(c.getPublicCompanyId()).name(c.getName()).sector(c.getSector())
                .state(c.getState()).city(c.getCity()).companyType(c.getCompanyType())
                .description(c.getDescription()).website(c.getWebsite())
                .status(c.getStatus().name()).createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build())
            .toList();
    }

    @GetMapping("/companies/pending-with-details")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get pending companies with details", description = "Lists companies pending admin review with full workflow details.")
    public List<CompanyDetailDto> getPendingCompaniesWithDetails() {
        return companyRepository.findByStatus(Company.CompanyStatus.PENDING_REVIEW)
            .stream()
            .map(c -> toCompanyDetailDto(c))
            .toList();
    }

    @PostMapping("/companies/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Approve company", description = "Approve a pending-review company listing. Company moves to APPROVED_MEMBERSHIP_PENDING (not publicly visible until listing membership purchased).")
    public CompanyDto approveCompany(@Parameter(description = "Company UUID") @PathVariable UUID id,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.approveCompany(id, principal);
    }

    @PostMapping("/companies/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Reject company", description = "Reject a pending-review company listing with a mandatory rejection comment.")
    public CompanyDto rejectCompany(@Parameter(description = "Company UUID") @PathVariable UUID id,
                                     @AuthenticationPrincipal UserPrincipal principal,
                                     @RequestBody ReviewRequest reviewRequest) {
        String comment = reviewRequest != null ? reviewRequest.comment() : null;
        return companyService.rejectCompany(id, principal, comment);
    }

    @PostMapping("/companies/{id}/suspend")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Suspend company", description = "Suspend an active company listing. Immediately removes from public listing.")
    public CompanyDto suspendCompany(@Parameter(description = "Company UUID") @PathVariable UUID id,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.suspendCompany(id, principal);
    }

    @PostMapping("/companies/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Reactivate company", description = "Reactivate a suspended company. Moves to APPROVED_MEMBERSHIP_PENDING.")
    public CompanyDto reactivateCompany(@Parameter(description = "Company UUID") @PathVariable UUID id,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.reactivateCompany(id, principal);
    }

    // --- Members ---

    @GetMapping("/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "List research members", description = "Lists all RESEARCH_MEMBER users with their current subscription details (paginated).")
    public List<MemberDto> getMembers(@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                      @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return userRepository.findByRole(User.UserRole.RESEARCH_MEMBER, page, size)
            .stream()
            .map(user -> {
                var membership = membershipRepository.findActiveByUserId(user.getId());
                return toMemberDto(user, membership.orElse(null));
            })
            .toList();
    }

    @GetMapping("/members/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Count research members", description = "Returns the total count of RESEARCH_MEMBER users for pagination.")
    public Map<String, Long> getMemberCount() {
        return Map.of("count", userRepository.countByRole(User.UserRole.RESEARCH_MEMBER));
    }

    @GetMapping("/members/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get member detail", description = "Returns a single research member's profile and all their memberships (active + history).")
    public MemberDetailDto getMember(@Parameter(description = "User UUID") @PathVariable UUID id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new DomainException("Member not found"));

        if (user.getRole() != User.UserRole.RESEARCH_MEMBER) {
            throw new DomainException("User is not a research member");
        }

        var memberships = membershipRepository.findByUserId(id);

        return MemberDetailDto.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .pan(user.getPan())
            .role(user.getRole().name())
            .active(user.isActive())
            .emailVerified(user.isEmailVerified())
            .createdAt(user.getCreatedAt())
            .memberships(memberships.stream().map(m -> MembershipDto.builder()
                .id(m.getId())
                .userId(m.getUserId())
                .planType(m.getPlanType())
                .status(m.getStatus().name())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .downloadLimit(m.getDownloadLimit())
                .downloadsUsed(m.getDownloadsUsed())
                .createdAt(m.getCreatedAt())
                .build()).toList())
            .build();
    }

    @PutMapping("/members/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update member", description = "Update a research member's profile (name, phone, PAN, active status). Only provided fields are updated.")
    public MemberDto updateMember(@Parameter(description = "User UUID") @PathVariable UUID id,
                                   @RequestBody UpdateMemberRequest body) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new DomainException("Member not found"));

        if (user.getRole() != User.UserRole.RESEARCH_MEMBER) {
            throw new DomainException("User is not a research member");
        }

        if (body.fullName() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(user.getPan()).fullName(body.fullName()).phone(user.getPhone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(user.isActive())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        if (body.phone() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(user.getPan()).fullName(user.getFullName()).phone(body.phone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(user.isActive())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        if (body.pan() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(body.pan().toUpperCase()).fullName(user.getFullName()).phone(user.getPhone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(user.isActive())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        if (body.active() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(user.getPan()).fullName(user.getFullName()).phone(user.getPhone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(body.active())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        user = userRepository.save(user);
        var membership = membershipRepository.findActiveByUserId(user.getId());
        return toMemberDto(user, membership.orElse(null));
    }

    @PutMapping("/members/{id}/membership")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update member subscription", description = "Extend, cancel, or activate a member's subscription. EXTEND updates planType/endDate/downloadLimit. CANCEL marks active membership cancelled. ACTIVATE creates a new membership.")
    public MembershipDto updateMembership(@Parameter(description = "User UUID") @PathVariable UUID id,
                                           @RequestBody UpdateMembershipRequest body) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new DomainException("Member not found"));

        if ("CANCEL".equalsIgnoreCase(body.action())) {
            var active = membershipRepository.findActiveByUserId(id)
                .orElseThrow(() -> new DomainException("No active membership to cancel"));
            active.cancel();
            var saved = membershipRepository.save(active);
            return toMembershipDto(saved);
        }

        if ("ACTIVATE".equalsIgnoreCase(body.action())) {
            if (body.planType() == null || body.endDate() == null) {
                throw new DomainException("planType and endDate are required for activation");
            }
            var newMembership = Membership.builder()
                .id(UUID.randomUUID())
                .userId(id)
                .planType(body.planType())
                .status(Membership.MembershipStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(body.endDate())
                .downloadLimit(body.downloadLimit() != null ? body.downloadLimit() : 50)
                .downloadsUsed(0)
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();
            var saved = membershipRepository.save(newMembership);
            return toMembershipDto(saved);
        }

        // EXTEND: modify existing active membership
        var membership = membershipRepository.findActiveByUserId(id)
            .orElseThrow(() -> new DomainException("No active membership to extend"));

        var builder = Membership.builder()
            .id(membership.getId())
            .userId(membership.getUserId())
            .planType(body.planType() != null ? body.planType() : membership.getPlanType())
            .status(membership.getStatus())
            .startDate(membership.getStartDate())
            .endDate(body.endDate() != null ? body.endDate() : membership.getEndDate())
            .downloadLimit(body.downloadLimit() != null ? body.downloadLimit() : membership.getDownloadLimit())
            .downloadsUsed(membership.getDownloadsUsed())
            .createdAt(membership.getCreatedAt())
            .updatedAt(java.time.Instant.now())
            .build();

        var saved = membershipRepository.save(builder);
        return toMembershipDto(saved);
    }

    // --- Company Members ---

    @GetMapping("/company-members")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "List company members", description = "Lists all COMPANY_USER accounts with their primary company listing details (paginated).")
    public List<CompanyMemberDto> getCompanyMembers(@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                                     @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return userRepository.findByRole(User.UserRole.COMPANY_USER, page, size)
            .stream()
            .map(user -> {
                var companies = companyRepository.findByCreatedBy(user.getId(), 0, 1);
                var totalCompanies = companyRepository.countByCreatedBy(user.getId());
                var company = companies.isEmpty() ? null : companies.get(0);
                return toCompanyMemberDto(user, company, (int) totalCompanies);
            })
            .toList();
    }

    @GetMapping("/company-members/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Count company members", description = "Returns the total count of COMPANY_USER accounts for pagination.")
    public Map<String, Long> getCompanyMemberCount() {
        return Map.of("count", userRepository.countByRole(User.UserRole.COMPANY_USER));
    }

    @GetMapping("/company-members/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get company member detail", description = "Returns a single COMPANY_USER's profile and all their company listings with full details.")
    public Map<String, Object> getCompanyMember(@Parameter(description = "User UUID") @PathVariable UUID id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new DomainException("Company member not found"));

        if (user.getRole() != User.UserRole.COMPANY_USER) {
            throw new DomainException("User is not a company member");
        }

        var companies = companyRepository.findByCreatedBy(id, 0, 100);

        var result = new HashMap<String, Object>();
        result.put("id", user.getId());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("pan", user.getPan());
        result.put("role", user.getRole().name());
        result.put("active", user.isActive());
        result.put("emailVerified", user.isEmailVerified());
        result.put("createdAt", user.getCreatedAt());

        var companyList = companies.stream().map(c -> {
            var cm = new HashMap<String, Object>();
            // IDs & Status
            cm.put("id", c.getId());
            cm.put("publicCompanyId", c.getPublicCompanyId());
            cm.put("status", c.getStatus().name());
            cm.put("companyStage", c.getCompanyStage());

            // Basic Info
            cm.put("name", c.getName());
            cm.put("brandName", c.getBrandName());
            cm.put("sector", c.getSector());
            cm.put("city", c.getCity());
            cm.put("state", c.getState());
            cm.put("companyType", c.getCompanyType());
            cm.put("incorporationYear", c.getIncorporationYear());
            cm.put("description", c.getDescription());
            cm.put("businessDescription", c.getBusinessDescription());
            cm.put("website", c.getWebsite());
            cm.put("logoUrl", c.getLogoUrl());
            cm.put("headquarter", c.getHeadquarter());

            // Registration
            cm.put("cin", c.getCin());
            cm.put("gstin", c.getGstin());
            cm.put("pan", c.getPan());
            cm.put("tan", c.getTan());
            cm.put("msmeRegistration", c.getMsmeRegistration());
            cm.put("startupIndiaRegistration", c.getStartupIndiaRegistration());
            cm.put("companyRegistrationNumber", c.getCompanyRegistrationNumber());

            // Registered Office
            cm.put("registeredAddressLine1", c.getRegisteredAddressLine1());
            cm.put("registeredAddressLine2", c.getRegisteredAddressLine2());
            cm.put("registeredCity", c.getRegisteredCity());
            cm.put("registeredState", c.getRegisteredState());
            cm.put("registeredPinCode", c.getRegisteredPinCode());
            cm.put("registeredCountry", c.getRegisteredCountry());

            // Contact
            cm.put("officialEmail", c.getOfficialEmail());
            cm.put("officialPhone", c.getOfficialPhone());
            cm.put("phoneNumber", c.getPhoneNumber());
            cm.put("linkedinProfile", c.getLinkedinProfile());
            cm.put("twitterUrl", c.getTwitterUrl());
            cm.put("socialMediaLinks", c.getSocialMediaLinks());

            // Financial
            cm.put("annualTurnover", c.getAnnualTurnover());
            cm.put("paidUpCapital", c.getPaidUpCapital());
            cm.put("authorizedCapital", c.getAuthorizedCapital());
            cm.put("employeeCount", c.getEmployeeCount());
            cm.put("financialYear", c.getFinancialYear());
            cm.put("totalFunding", c.getTotalFunding());
            cm.put("investors", c.getInvestors());
            cm.put("balanceSheetUrl", c.getBalanceSheetUrl());
            cm.put("auditorDetails", c.getAuditorDetails());

            // Business
            cm.put("productsServices", c.getProductsServices());
            cm.put("exportImportStatus", c.getExportImportStatus());
            cm.put("numBranches", c.getNumBranches());
            cm.put("operationalStates", c.getOperationalStates());
            cm.put("certifications", c.getCertifications());
            cm.put("technologiesUsed", c.getTechnologiesUsed());

            // Extended Profile
            cm.put("ceoName", c.getCeoName());
            cm.put("ctoName", c.getCtoName());
            cm.put("founders", c.getFounders());
            cm.put("businessModel", c.getBusinessModel());
            cm.put("awards", c.getAwards());
            cm.put("cultureSummary", c.getCultureSummary());
            cm.put("mission", c.getMission());
            cm.put("vision", c.getVision());

            // Authorized Representative
            cm.put("authorizedRepName", c.getAuthorizedRepName());
            cm.put("authorizedRepDesignation", c.getAuthorizedRepDesignation());
            cm.put("authorizedRepMobile", c.getAuthorizedRepMobile());
            cm.put("authorizedRepEmail", c.getAuthorizedRepEmail());
            cm.put("authorizedRepIdentityProofUrl", c.getAuthorizedRepIdentityProofUrl());
            cm.put("authorizedRepDigitalSignatureUrl", c.getAuthorizedRepDigitalSignatureUrl());

            // Timestamps
            cm.put("submittedAt", c.getSubmittedAt());
            cm.put("rejectionComment", c.getRejectionComment());
            cm.put("listingExpiresAt", c.getListingExpiresAt());
            cm.put("approvedAt", c.getApprovedAt());
            cm.put("createdAt", c.getCreatedAt());
            cm.put("updatedAt", c.getUpdatedAt());
            return cm;
        }).toList();
        result.put("companies", companyList);
        return result;
    }

    @PutMapping("/company-members/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update company member", description = "Update a COMPANY_USER's profile (name, phone, PAN, active status).")
    public CompanyMemberDto updateCompanyMember(@Parameter(description = "User UUID") @PathVariable UUID id,
                                                 @RequestBody UpdateMemberRequest body) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new DomainException("Company member not found"));

        if (user.getRole() != User.UserRole.COMPANY_USER) {
            throw new DomainException("User is not a company member");
        }

        if (body.fullName() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(user.getPan()).fullName(body.fullName()).phone(user.getPhone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(user.isActive())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        if (body.phone() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(user.getPan()).fullName(user.getFullName()).phone(body.phone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(user.isActive())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        if (body.pan() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(body.pan().toUpperCase()).fullName(user.getFullName()).phone(user.getPhone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(user.isActive())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        if (body.active() != null) user = User.builder()
            .id(user.getId()).email(user.getEmail()).passwordHash(user.getPasswordHash())
            .pan(user.getPan()).fullName(user.getFullName()).phone(user.getPhone())
            .role(user.getRole()).emailVerified(user.isEmailVerified()).active(body.active())
            .createdAt(user.getCreatedAt()).updatedAt(java.time.Instant.now()).build();

        user = userRepository.save(user);
        var companies = companyRepository.findByCreatedBy(user.getId(), 0, 1);
        var totalCompanies = companyRepository.countByCreatedBy(user.getId());
        return toCompanyMemberDto(user, companies.isEmpty() ? null : companies.get(0), (int) totalCompanies);
    }

    @PutMapping("/company-members/{userId}/companies/{companyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update company listing", description = "Update a company listing's details. Only provided fields are updated.")
    public CompanyDto updateCompanyListing(@Parameter(description = "User UUID") @PathVariable UUID userId,
                                            @Parameter(description = "Company UUID") @PathVariable UUID companyId,
                                            @RequestBody UpdateCompanyRequest body) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new DomainException("Company not found"));

        if (!company.getCreatedBy().equals(userId)) {
            throw new DomainException("Company does not belong to this user");
        }

        var c = company;
        var builder = Company.builder()
            .id(c.getId()).publicCompanyId(c.getPublicCompanyId())
            .name(body.name() != null ? body.name() : c.getName())
            .brandName(body.brandName() != null ? body.brandName() : c.getBrandName())
            .sector(body.sector() != null ? body.sector() : c.getSector())
            .state(body.state() != null ? body.state() : c.getState())
            .city(body.city() != null ? body.city() : c.getCity())
            .companyType(body.companyType() != null ? body.companyType() : c.getCompanyType())
            .incorporationYear(body.incorporationYear() != null ? body.incorporationYear() : c.getIncorporationYear())
            .description(body.description() != null ? body.description() : c.getDescription())
            .businessDescription(body.businessDescription() != null ? body.businessDescription() : c.getBusinessDescription())
            .website(body.website() != null ? body.website() : c.getWebsite())
            .logoUrl(body.logoUrl() != null ? body.logoUrl() : c.getLogoUrl())
            .status(c.getStatus()).createdBy(c.getCreatedBy())
            .approvedBy(c.getApprovedBy()).approvedAt(c.getApprovedAt())
            .submittedAt(c.getSubmittedAt()).rejectionComment(c.getRejectionComment())
            .listingExpiresAt(c.getListingExpiresAt())
            .createdAt(c.getCreatedAt()).updatedAt(java.time.Instant.now())
            // Registration fields
            .cin(body.cin() != null ? body.cin() : c.getCin())
            .gstin(body.gstin() != null ? body.gstin() : c.getGstin())
            .pan(body.pan() != null ? body.pan() : c.getPan())
            .tan(body.tan() != null ? body.tan() : c.getTan())
            .msmeRegistration(body.msmeRegistration() != null ? body.msmeRegistration() : c.getMsmeRegistration())
            .startupIndiaRegistration(body.startupIndiaRegistration() != null ? body.startupIndiaRegistration() : c.getStartupIndiaRegistration())
            .companyRegistrationNumber(body.companyRegistrationNumber() != null ? body.companyRegistrationNumber() : c.getCompanyRegistrationNumber())
            // Registered Office
            .registeredAddressLine1(body.registeredAddressLine1() != null ? body.registeredAddressLine1() : c.getRegisteredAddressLine1())
            .registeredAddressLine2(body.registeredAddressLine2() != null ? body.registeredAddressLine2() : c.getRegisteredAddressLine2())
            .registeredCity(body.registeredCity() != null ? body.registeredCity() : c.getRegisteredCity())
            .registeredState(body.registeredState() != null ? body.registeredState() : c.getRegisteredState())
            .registeredPinCode(body.registeredPinCode() != null ? body.registeredPinCode() : c.getRegisteredPinCode())
            .registeredCountry(body.registeredCountry() != null ? body.registeredCountry() : c.getRegisteredCountry())
            // Contact
            .officialEmail(body.officialEmail() != null ? body.officialEmail() : c.getOfficialEmail())
            .officialPhone(body.officialPhone() != null ? body.officialPhone() : c.getOfficialPhone())
            .phoneNumber(body.phoneNumber() != null ? body.phoneNumber() : c.getPhoneNumber())
            .headquarter(body.headquarter() != null ? body.headquarter() : c.getHeadquarter())
            .linkedinProfile(body.linkedinProfile() != null ? body.linkedinProfile() : c.getLinkedinProfile())
            .twitterUrl(body.twitterUrl() != null ? body.twitterUrl() : c.getTwitterUrl())
            .socialMediaLinks(body.socialMediaLinks() != null ? body.socialMediaLinks() : c.getSocialMediaLinks())
            // Financial
            .annualTurnover(body.annualTurnover() != null ? body.annualTurnover() : c.getAnnualTurnover())
            .paidUpCapital(body.paidUpCapital() != null ? body.paidUpCapital() : c.getPaidUpCapital())
            .authorizedCapital(body.authorizedCapital() != null ? body.authorizedCapital() : c.getAuthorizedCapital())
            .employeeCount(body.employeeCount() != null ? body.employeeCount() : c.getEmployeeCount())
            .financialYear(body.financialYear() != null ? body.financialYear() : c.getFinancialYear())
            .totalFunding(body.totalFunding() != null ? body.totalFunding() : c.getTotalFunding())
            .investors(body.investors() != null ? body.investors() : c.getInvestors())
            // Business
            .productsServices(body.productsServices() != null ? body.productsServices() : c.getProductsServices())
            .exportImportStatus(body.exportImportStatus() != null ? body.exportImportStatus() : c.getExportImportStatus())
            .numBranches(body.numBranches() != null ? body.numBranches() : c.getNumBranches())
            .operationalStates(body.operationalStates() != null ? body.operationalStates() : c.getOperationalStates())
            .certifications(body.certifications() != null ? body.certifications() : c.getCertifications())
            .technologiesUsed(body.technologiesUsed() != null ? body.technologiesUsed() : c.getTechnologiesUsed())
            // Extended Profile
            .ceoName(body.ceoName() != null ? body.ceoName() : c.getCeoName())
            .ctoName(body.ctoName() != null ? body.ctoName() : c.getCtoName())
            .founders(body.founders() != null ? body.founders() : c.getFounders())
            .businessModel(body.businessModel() != null ? body.businessModel() : c.getBusinessModel())
            .companyStage(body.companyStage() != null ? body.companyStage() : c.getCompanyStage())
            .awards(body.awards() != null ? body.awards() : c.getAwards())
            .cultureSummary(body.cultureSummary() != null ? body.cultureSummary() : c.getCultureSummary())
            .mission(body.mission() != null ? body.mission() : c.getMission())
            .vision(body.vision() != null ? body.vision() : c.getVision())
            // Authorized Representative
            .authorizedRepName(body.authorizedRepName() != null ? body.authorizedRepName() : c.getAuthorizedRepName())
            .authorizedRepDesignation(body.authorizedRepDesignation() != null ? body.authorizedRepDesignation() : c.getAuthorizedRepDesignation())
            .authorizedRepMobile(body.authorizedRepMobile() != null ? body.authorizedRepMobile() : c.getAuthorizedRepMobile())
            .authorizedRepEmail(body.authorizedRepEmail() != null ? body.authorizedRepEmail() : c.getAuthorizedRepEmail())
            .authorizedRepIdentityProofUrl(c.getAuthorizedRepIdentityProofUrl())
            .authorizedRepDigitalSignatureUrl(c.getAuthorizedRepDigitalSignatureUrl())
            // Financial docs
            .financialYear(c.getFinancialYear())
            .balanceSheetUrl(c.getBalanceSheetUrl())
            .auditorDetails(body.auditorDetails() != null ? body.auditorDetails() : c.getAuditorDetails())
            // JSON aggregates (preserved as-is)
            .financialDataJson(c.getFinancialDataJson())
            .certificatesDataJson(c.getCertificatesDataJson())
            .videosDataJson(c.getVideosDataJson())
            .build();

        company = companyRepository.save(builder);
        return CompanyDto.builder()
            .id(company.getId()).publicCompanyId(company.getPublicCompanyId())
            .name(company.getName()).sector(company.getSector())
            .state(company.getState()).city(company.getCity())
            .companyType(company.getCompanyType())
            .description(company.getDescription()).website(company.getWebsite())
            .status(company.getStatus().name()).createdBy(company.getCreatedBy())
            .createdAt(company.getCreatedAt()).updatedAt(company.getUpdatedAt())
            .build();
    }

    @PostMapping("/company-members/{userId}/companies/{companyId}/suspend")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Suspend company listing", description = "Mark a company listing as SUSPENDED, hiding it from search results.")
    public CompanyDto suspendCompanyListing(@Parameter(description = "User UUID") @PathVariable UUID userId,
                                             @Parameter(description = "Company UUID") @PathVariable UUID companyId,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.suspendCompany(companyId, principal);
    }

    @PostMapping("/company-members/{userId}/companies/{companyId}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Reactivate company listing", description = "Reactivate a suspended company listing.")
    public CompanyDto reactivateCompanyListing(@Parameter(description = "User UUID") @PathVariable UUID userId,
                                                @Parameter(description = "Company UUID") @PathVariable UUID companyId,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.reactivateCompany(companyId, principal);
    }

    // --- Refunds ---

    @GetMapping("/refunds")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get refund requests", description = "Lists all refund requests for admin processing.")
    public List<PaymentDto> getRefunds() {
        return List.of(); // Placeholder — wire to refund-requested payments
    }

    @PostMapping("/refunds/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Process refund", description = "Approve or reject a refund request.")
    public Map<String, String> processRefund(@Parameter(description = "Refund/Payment UUID") @PathVariable UUID id) {
        return Map.of("status", "PROCESSED");
    }

    // --- Grievances ---

    @GetMapping("/grievances")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get grievances", description = "Lists all grievances with optional status filter.")
    public List<GrievanceDto> getGrievances(@Parameter(description = "Filter by status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)") @RequestParam(defaultValue = "OPEN") String status,
                                             @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                             @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return grievanceService.getByStatus(status, page, size);
    }

    @PostMapping("/grievances/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Assign grievance", description = "Assign a grievance to the authenticated admin for resolution.")
    public GrievanceDto assignGrievance(@Parameter(description = "Grievance UUID") @PathVariable UUID id,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return grievanceService.assign(id, principal.id());
    }

    @PostMapping("/grievances/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Resolve grievance", description = "Mark a grievance as resolved with a resolution note.")
    public GrievanceDto resolveGrievance(@Parameter(description = "Grievance UUID") @PathVariable UUID id,
                                          @RequestBody Map<String, String> body) {
        return grievanceService.resolve(id, body.getOrDefault("resolution", "Resolved"));
    }

    // --- Audit Logs ---

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get audit logs", description = "Returns paginated audit trail of admin actions and system events.")
    public List<Map<String, Object>> getAuditLogs(@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                                   @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return auditLogRepository.findAll(page, size)
            .stream()
            .map(log -> Map.<String, Object>of(
                "id", log.getId(),
                "userId", log.getUserId(),
                "action", log.getAction(),
                "companyId", log.getCompanyId(),
                "outcome", log.getOutcome(),
                "details", log.getDetails(),
                "ipAddress", log.getIpAddress(),
                "createdAt", log.getCreatedAt()
            ))
            .toList();
    }

    // --- Helpers ---

    private CompanyDetailDto toCompanyDetailDto(Company c) {
        return CompanyDetailDto.builder()
            .id(c.getId())
            .publicCompanyId(c.getPublicCompanyId())
            .name(c.getName())
            .sector(c.getSector())
            .state(c.getState())
            .city(c.getCity())
            .companyType(c.getCompanyType())
            .incorporationYear(c.getIncorporationYear())
            .description(c.getDescription())
            .website(c.getWebsite())
            .logoUrl(c.getLogoUrl())
            .status(c.getStatus().name())
            .createdBy(c.getCreatedBy())
            .approvedBy(c.getApprovedBy())
            .approvedAt(c.getApprovedAt())
            .submittedAt(c.getSubmittedAt())
            .rejectionComment(c.getRejectionComment())
            .listingExpiresAt(c.getListingExpiresAt())
            .isPubliclyVisible(c.isPubliclyVisible())
            .hasActiveListingMembership(c.hasActiveListingMembership())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }

    private MemberDto toMemberDto(User user, Membership membership) {
        return MemberDto.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .pan(user.getPan())
            .active(user.isActive())
            .emailVerified(user.isEmailVerified())
            .createdAt(user.getCreatedAt())
            .membershipId(membership != null ? membership.getId() : null)
            .planType(membership != null ? membership.getPlanType() : null)
            .membershipStatus(membership != null ? membership.getStatus().name() : null)
            .membershipStartDate(membership != null ? membership.getStartDate() : null)
            .membershipEndDate(membership != null ? membership.getEndDate() : null)
            .downloadLimit(membership != null ? membership.getDownloadLimit() : 0)
            .downloadsUsed(membership != null ? membership.getDownloadsUsed() : 0)
            .build();
    }

    private MembershipDto toMembershipDto(Membership m) {
        return MembershipDto.builder()
            .id(m.getId())
            .userId(m.getUserId())
            .planType(m.getPlanType())
            .status(m.getStatus().name())
            .startDate(m.getStartDate())
            .endDate(m.getEndDate())
            .downloadLimit(m.getDownloadLimit())
            .downloadsUsed(m.getDownloadsUsed())
            .createdAt(m.getCreatedAt())
            .build();
    }

    private CompanyMemberDto toCompanyMemberDto(User user, Company company, int totalCompanies) {
        return CompanyMemberDto.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .pan(user.getPan())
            .active(user.isActive())
            .createdAt(user.getCreatedAt())
            .companyId(company != null ? company.getId() : null)
            .publicCompanyId(company != null ? company.getPublicCompanyId() : null)
            .companyName(company != null ? company.getName() : null)
            .companyStatus(company != null ? company.getStatus().name() : null)
            .sector(company != null ? company.getSector() : null)
            .city(company != null ? company.getCity() : null)
            .state(company != null ? company.getState() : null)
            .companyType(company != null ? company.getCompanyType() : null)
            .totalCompanies(totalCompanies)
            .build();
    }
}
