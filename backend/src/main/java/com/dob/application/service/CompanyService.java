package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.CompanyNotFoundException;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Company;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.UnlockedCompanyRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private static final List<String> ADMIN_ROLES = List.of("ADMIN", "SUPER_ADMIN");

    private final CompanyRepository companyRepository;
    private final MembershipRepository membershipRepository;
    private final UnlockedCompanyRepository unlockedCompanyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ──────── Public Search & Detail ────────

    /**
     * Search companies and return appropriate response based on user's subscription.
     * Only returns companies that are publicly visible (APPROVED_ACTIVE with valid membership).
     * Free users receive masked results (FreeCompanyResponse).
     * Premium/subscribed users receive full company details (PremiumCompanyResponse).
     */
    public PageDto<?> search(UserPrincipal principal, String query, String sector, String state,
                              String companyType, String revenueRange, String membershipFilter,
                              int page, int size) {
        var companies = companyRepository.search(query, sector, state, companyType,
            revenueRange, membershipFilter, page, size);
        long total = companyRepository.countSearch(query, sector, state, companyType,
            revenueRange, membershipFilter);

        // Only ADMIN/SUPER_ADMIN can bypass per-company lock checks
        boolean isAdminUser = principal != null && ADMIN_ROLES.contains(principal.role());

        // Fetch unlocked company IDs for per-company access checks
        Set<UUID> unlockedCompanyIds = principal != null
            ? new HashSet<>(unlockedCompanyRepository.findCompanyIdsByMemberId(principal.id()))
            : Collections.emptySet();

        var content = companies.stream()
            .map(c -> {
                boolean canAccess = isAdminUser || unlockedCompanyIds.contains(c.getId());
                return canAccess ? (Object) toPremiumResponse(c) : (Object) toFreeResponse(c);
            })
            .toList();

        int totalPages = (int) Math.ceil((double) total / size);

        return PageDto.builder()
            .content(content)
            .page(page)
            .size(size)
            .totalElements(total)
            .totalPages(totalPages)
            .build();
    }

    /**
     * Get company by internal UUID. Returns masked or full data based on subscription.
     * Only publicly visible companies are accessible via this public endpoint.
     */
    public Object getById(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        // Check visibility: owner, admin, or publicly visible companies
        boolean isOwner = principal != null && company.getCreatedBy().equals(principal.id());
        boolean isAdmin = principal != null && ADMIN_ROLES.contains(principal.role());

        if (!isOwner && !isAdmin && !company.isPubliclyVisible()) {
            throw new DomainException("Company profile is not publicly available");
        }

        boolean isUnlocked = principal != null &&
            unlockedCompanyRepository.existsByMemberIdAndCompanyId(principal.id(), id);

        if (isOwner || isAdmin) {
            return toCompanyDetailDto(company);
        }
        return isUnlocked ? toPremiumDetailResponse(company) : toFreeResponse(company);
    }

    /**
     * Get company by public DoB ID. Returns masked or full data based on subscription.
     */
    public Object getByPublicCompanyId(String publicCompanyId, UserPrincipal principal) {
        var company = companyRepository.findByPublicCompanyId(publicCompanyId)
            .orElseThrow(() -> new CompanyNotFoundException(publicCompanyId));

        boolean isOwner = principal != null && company.getCreatedBy().equals(principal.id());
        boolean isAdmin = principal != null && ADMIN_ROLES.contains(principal.role());

        if (!isOwner && !isAdmin && !company.isPubliclyVisible()) {
            throw new DomainException("Company profile is not publicly available");
        }

        boolean isUnlocked = principal != null &&
            unlockedCompanyRepository.existsByMemberIdAndCompanyId(principal.id(), company.getId());

        if (isOwner || isAdmin) {
            return toCompanyDetailDto(company);
        }
        return isUnlocked ? toPremiumDetailResponse(company) : toFreeResponse(company);
    }

    // ──────── Company CRUD ────────

    @Transactional
    public CompanyDto create(UserPrincipal principal, CreateCompanyRequest request) {
        if (companyRepository.existsByName(request.name())) {
            throw new DomainException("A company with this name already exists");
        }

        var company = Company.builder()
            .publicCompanyId(Company.generatePublicCompanyId())
            .name(request.name())
            .sector(request.sector())
            .state(request.state())
            .city(request.city())
            .companyType(request.companyType())
            .incorporationYear(request.incorporationYear())
            .description(request.description())
            .website(request.website())
            .status(Company.CompanyStatus.DRAFT)
            .createdBy(principal.id())
            .build();

        company = companyRepository.save(company);
        return toDto(company);
    }

    @Transactional
    public CompanyDto update(UUID id, UserPrincipal principal, CreateCompanyRequest request) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        if (!company.getCreatedBy().equals(principal.id())) {
            throw new DomainException("You can only edit your own company listings");
        }

        // Only allow updates in DRAFT or REJECTED state
        if (company.getStatus() != Company.CompanyStatus.DRAFT
            && company.getStatus() != Company.CompanyStatus.REJECTED) {
            throw new DomainException("Company can only be edited in DRAFT or REJECTED status");
        }

        var updated = Company.builder()
            .id(company.getId())
            .publicCompanyId(company.getPublicCompanyId())
            .name(request.name())
            .sector(request.sector())
            .state(request.state())
            .city(request.city())
            .companyType(request.companyType())
            .incorporationYear(request.incorporationYear())
            .description(request.description())
            .website(request.website())
            .logoUrl(company.getLogoUrl())
            .status(company.getStatus())
            .createdBy(company.getCreatedBy())
            .approvedBy(company.getApprovedBy())
            .approvedAt(company.getApprovedAt())
            .submittedAt(company.getSubmittedAt())
            .rejectionComment(company.getRejectionComment())
            .listingExpiresAt(company.getListingExpiresAt())
            .createdAt(company.getCreatedAt())
            .updatedAt(company.getUpdatedAt())
            .build();

        updated = companyRepository.save(updated);
        return toDto(updated);
    }

    public List<CompanyDto> getMyCompanies(UserPrincipal principal) {
        return companyRepository.findByCreatedBy(principal.id())
            .stream().map(this::toDto).toList();
    }

    /**
     * Get the current user's companies with full workflow details (status, submission info, membership).
     */
    public List<CompanyDetailDto> getMyCompanyDetails(UserPrincipal principal) {
        return companyRepository.findByCreatedBy(principal.id())
            .stream().map(this::toCompanyDetailDto).toList();
    }

    /**
     * Get company detail DTO for owner/admin view — returns all fields regardless of status.
     */
    public CompanyDetailDto getCompanyDetailForOwner(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        if (!company.getCreatedBy().equals(principal.id()) && !ADMIN_ROLES.contains(principal.role())) {
            throw new DomainException("Access denied");
        }

        return toCompanyDetailDto(company);
    }

    // ──────── Submission Workflow ────────

    /**
     * Submit a draft or rejected company for admin review.
     */
    @Transactional
    public CompanyDto submitForReview(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        if (!company.getCreatedBy().equals(principal.id())) {
            throw new DomainException("You can only submit your own company listings");
        }

        company.submitForReview();
        company = companyRepository.save(company);
        return toDto(company);
    }

    /**
     * Resubmit a rejected company for review.
     */
    @Transactional
    public CompanyDto resubmitForReview(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        if (!company.getCreatedBy().equals(principal.id())) {
            throw new DomainException("You can only resubmit your own company listings");
        }

        company.resubmitForReview();
        company = companyRepository.save(company);
        return toDto(company);
    }

    // ──────── Admin Actions ────────

    /**
     * Admin approves a pending-review company. Sets to APPROVED_ACTIVE
     * with a 1-year listing membership so the company immediately appears
     * in the company database search results.
     *
     * @throws DomainException with message "Company is already approved" for duplicates
     */
    @Transactional
    public CompanyDto approveCompany(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        // Prevent duplicate approval
        if (company.getStatus() == Company.CompanyStatus.APPROVED_ACTIVE) {
            throw new DomainException("Company is already approved and published");
        }
        if (company.getStatus() == Company.CompanyStatus.APPROVED_MEMBERSHIP_PENDING) {
            throw new DomainException("Company is already approved");
        }

        // Only allow approval from PENDING_REVIEW or DRAFT
        if (company.getStatus() != Company.CompanyStatus.PENDING_REVIEW
            && company.getStatus() != Company.CompanyStatus.DRAFT) {
            throw new DomainException(
                "Only draft or pending-review companies can be approved. Current status: " + company.getStatus());
        }

        var now = Instant.now();
        var oneYearFromNow = LocalDate.now().plusYears(1);

        var updated = Company.builder()
            .id(company.getId())
            .publicCompanyId(company.getPublicCompanyId())
            .name(company.getName())
            .brandName(company.getBrandName())
            .sector(company.getSector())
            .state(company.getState())
            .city(company.getCity())
            .companyType(company.getCompanyType())
            .incorporationYear(company.getIncorporationYear())
            .description(company.getDescription())
            .businessDescription(company.getBusinessDescription())
            .website(company.getWebsite())
            .logoUrl(company.getLogoUrl())
            .status(Company.CompanyStatus.APPROVED_ACTIVE)
            .createdBy(company.getCreatedBy())
            .approvedBy(principal.id())
            .approvedAt(now)
            .submittedAt(company.getSubmittedAt() != null ? company.getSubmittedAt() : now)
            .rejectionComment(null)
            .listingExpiresAt(oneYearFromNow)
            .createdAt(company.getCreatedAt())
            .updatedAt(now)
            // Registration
            .cin(company.getCin())
            .gstin(company.getGstin())
            .pan(company.getPan())
            .tan(company.getTan())
            .msmeRegistration(company.getMsmeRegistration())
            .startupIndiaRegistration(company.getStartupIndiaRegistration())
            .companyRegistrationNumber(company.getCompanyRegistrationNumber())
            // Registered Office
            .registeredAddressLine1(company.getRegisteredAddressLine1())
            .registeredAddressLine2(company.getRegisteredAddressLine2())
            .registeredCity(company.getRegisteredCity())
            .registeredState(company.getRegisteredState())
            .registeredPinCode(company.getRegisteredPinCode())
            .registeredCountry(company.getRegisteredCountry())
            // Contact
            .officialEmail(company.getOfficialEmail())
            .officialPhone(company.getOfficialPhone())
            .phoneNumber(company.getPhoneNumber())
            .headquarter(company.getHeadquarter())
            .linkedinProfile(company.getLinkedinProfile())
            .twitterUrl(company.getTwitterUrl())
            .socialMediaLinks(company.getSocialMediaLinks())
            // Financial
            .annualTurnover(company.getAnnualTurnover())
            .paidUpCapital(company.getPaidUpCapital())
            .authorizedCapital(company.getAuthorizedCapital())
            .employeeCount(company.getEmployeeCount())
            .financialYear(company.getFinancialYear())
            .balanceSheetUrl(company.getBalanceSheetUrl())
            .auditorDetails(company.getAuditorDetails())
            .totalFunding(company.getTotalFunding())
            .investors(company.getInvestors())
            // Business
            .productsServices(company.getProductsServices())
            .exportImportStatus(company.getExportImportStatus())
            .numBranches(company.getNumBranches())
            .operationalStates(company.getOperationalStates())
            .certifications(company.getCertifications())
            .technologiesUsed(company.getTechnologiesUsed())
            // Extended Profile
            .ceoName(company.getCeoName())
            .ctoName(company.getCtoName())
            .founders(company.getFounders())
            .businessModel(company.getBusinessModel())
            .companyStage(company.getCompanyStage())
            .awards(company.getAwards())
            .cultureSummary(company.getCultureSummary())
            .mission(company.getMission())
            .vision(company.getVision())
            .dashboardStatus(company.getDashboardStatus())
            // JSON aggregates
            .financialDataJson(company.getFinancialDataJson())
            .certificatesDataJson(company.getCertificatesDataJson())
            .videosDataJson(company.getVideosDataJson())
            // Authorized Representative
            .authorizedRepName(company.getAuthorizedRepName())
            .authorizedRepDesignation(company.getAuthorizedRepDesignation())
            .authorizedRepMobile(company.getAuthorizedRepMobile())
            .authorizedRepEmail(company.getAuthorizedRepEmail())
            .authorizedRepIdentityProofUrl(company.getAuthorizedRepIdentityProofUrl())
            .authorizedRepDigitalSignatureUrl(company.getAuthorizedRepDigitalSignatureUrl())
            .build();

        updated = companyRepository.save(updated);
        log.info("Company {} approved and published by admin {}. Visible until {}",
            updated.getPublicCompanyId(), principal.id(), oneYearFromNow);
        return toDto(updated);
    }

    /**
     * Admin rejects a pending-review company with a comment.
     */
    @Transactional
    public CompanyDto rejectCompany(UUID id, UserPrincipal principal, String comment) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        company.reject(principal.id(), comment);
        company = companyRepository.save(company);
        log.info("Company {} rejected by admin {}. Reason: {}", company.getPublicCompanyId(), principal.id(), comment);
        return toDto(company);
    }

    /**
     * Admin suspends an approved company.
     */
    @Transactional
    public CompanyDto suspendCompany(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        company.suspend();
        company = companyRepository.save(company);
        log.info("Company {} suspended by admin {}", company.getPublicCompanyId(), principal.id());
        return toDto(company);
    }

    /**
     * Admin reactivates a suspended company.
     */
    @Transactional
    public CompanyDto reactivateCompany(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        company.reactivate();
        company = companyRepository.save(company);
        log.info("Company {} reactivated by admin {}", company.getPublicCompanyId(), principal.id());
        return toDto(company);
    }

    // ──────── Listing Membership ────────

    /**
     * Activate a company's listing membership (e.g. after ₹500 listing fee payment).
     * Sets listingExpiresAt to 1 year from now and transitions status to APPROVED_ACTIVE
     * if the company is in APPROVED_MEMBERSHIP_PENDING or MEMBERSHIP_EXPIRED state.
     */
    @Transactional
    public CompanyDto activateListingMembership(UUID companyId, UserPrincipal principal) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException(companyId.toString()));

        if (!company.getCreatedBy().equals(principal.id()) && !ADMIN_ROLES.contains(principal.role())) {
            throw new DomainException("Access denied");
        }

        if (company.getStatus() != Company.CompanyStatus.APPROVED_MEMBERSHIP_PENDING
            && company.getStatus() != Company.CompanyStatus.MEMBERSHIP_EXPIRED
            && company.getStatus() != Company.CompanyStatus.APPROVED_ACTIVE) {
            throw new DomainException(
                "Listing membership can only be activated for approved companies. Current status: " + company.getStatus());
        }

        // Set listing expiry to 1 year from now
        var updated = Company.builder()
            .id(company.getId())
            .publicCompanyId(company.getPublicCompanyId())
            .name(company.getName())
            .brandName(company.getBrandName())
            .sector(company.getSector())
            .state(company.getState())
            .city(company.getCity())
            .companyType(company.getCompanyType())
            .incorporationYear(company.getIncorporationYear())
            .description(company.getDescription())
            .website(company.getWebsite())
            .logoUrl(company.getLogoUrl())
            .status(company.getStatus())
            .createdBy(company.getCreatedBy())
            .approvedBy(company.getApprovedBy())
            .approvedAt(company.getApprovedAt())
            .submittedAt(company.getSubmittedAt())
            .rejectionComment(company.getRejectionComment())
            .listingExpiresAt(LocalDate.now().plusYears(1))
            .createdAt(company.getCreatedAt())
            .updatedAt(Instant.now())
            .build();

        // Publish the listing if approved
        try {
            updated.publishListing();
        } catch (IllegalStateException e) {
            // If already APPROVED_ACTIVE, just update the expiry
        }

        updated = companyRepository.save(updated);
        log.info("Listing membership activated for company {} until {}",
            updated.getPublicCompanyId(), updated.getListingExpiresAt());
        return toDto(updated);
    }

    // ──────── Scheduled Tasks ────────

    /**
     * Daily task to expire listings whose membership has ended.
     * Runs every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void expireListings() {
        var today = LocalDate.now();
        var expiredCompanies = companyRepository.findExpiredListings(today);

        for (var company : expiredCompanies) {
            try {
                company.expireMembership();
                companyRepository.save(company);
                log.info("Listing membership expired for company {} (was APPROVED_ACTIVE, listing_expires_at was {})",
                    company.getPublicCompanyId(), company.getListingExpiresAt());
            } catch (IllegalStateException e) {
                log.warn("Could not expire listing for company {}: {}", company.getPublicCompanyId(), e.getMessage());
            }
        }

        if (!expiredCompanies.isEmpty()) {
            log.info("Expired {} company listings", expiredCompanies.size());
        }
    }

    // ──────── Premium / Subscription check ────────

    private boolean isPremiumUser(UserPrincipal principal) {
        if (ADMIN_ROLES.contains(principal.role())) return true;
        return hasActiveSubscription(principal.id());
    }

    private boolean hasActiveSubscription(UUID userId) {
        return membershipRepository.findActiveByUserId(userId)
            .map(m -> m.isActive() && m.getEndDate() != null
                && !m.getEndDate().isBefore(java.time.LocalDate.now()))
            .orElse(false);
    }

    // ──────────── DTO mappers ────────────

    private FreeCompanyResponse toFreeResponse(Company c) {
        int age = c.getIncorporationYear() != null
            ? Year.now().getValue() - c.getIncorporationYear()
            : 0;

        return FreeCompanyResponse.locked(
            c.getPublicCompanyId(),
            c.getSector(),
            c.getCompanyType(),
            c.getState(),
            c.getCity(),
            age,
            c.getEmployeeCount() != null ? formatEmployeeRange(c.getEmployeeCount()) : null,
            c.getAnnualTurnover() != null ? "₹" + c.getAnnualTurnover() : null,
            "Medium",
            c.isPubliclyVisible(),
            c.getDescription() != null
                ? c.getDescription().length() > 120
                    ? c.getDescription().substring(0, 120) + "…"
                    : c.getDescription()
                : null
        );
    }

    /**
     * Build a PremiumCompanyResponse for search results — includes full identifying info.
     */
    private PremiumCompanyResponse toPremiumResponse(Company c) {
        int age = c.getIncorporationYear() != null
            ? Year.now().getValue() - c.getIncorporationYear()
            : 0;

        return buildPremiumResponse(c, age, List.of(), List.of(), List.of());
    }

    /**
     * Build a full PremiumCompanyResponse for detail view — includes financials,
     * certificates, videos from JSON aggregate fields.
     */
    private PremiumCompanyResponse toPremiumDetailResponse(Company c) {
        int age = c.getIncorporationYear() != null
            ? Year.now().getValue() - c.getIncorporationYear()
            : 0;

        List<FinancialStatementDto> financials = parseFinancialData(c.getFinancialDataJson());
        List<CertificateDto> certificates = parseCertificatesData(c.getCertificatesDataJson());
        List<VideoDto> videos = parseVideosData(c.getVideosDataJson());

        return buildPremiumResponse(c, age, financials, certificates, videos);
    }

    private PremiumCompanyResponse buildPremiumResponse(Company c, int age,
                                                         List<FinancialStatementDto> financials,
                                                         List<CertificateDto> certificates,
                                                         List<VideoDto> videos) {
        String address = buildAddress(c);
        List<Map<String, String>> keyExecs = buildKeyExecutives(c);

        return new PremiumCompanyResponse(
            c.getPublicCompanyId(),
            c.getName(),
            c.getLogoUrl(),
            c.getCin(),
            c.getGstin(),
            c.getPan(),
            null,                   // registrationNumber (legacy)
            c.getCompanyRegistrationNumber(),
            c.getSector(),
            null,                   // subSector
            c.getCompanyType(),
            c.getBusinessModel(),
            c.getCompanyStage(),
            c.getIncorporationYear(),
            age,
            c.getState(),
            c.getCity(),
            address,
            c.getHeadquarter(),
            c.getNumBranches(),
            c.getOfficialEmail(),
            c.getPhoneNumber(),
            c.getWebsite(),
            c.getLinkedinProfile(),
            c.getTwitterUrl(),
            c.getEmployeeCount() != null ? formatEmployeeRange(c.getEmployeeCount()) : null,
            c.getEmployeeCount(),
            c.getAnnualTurnover(),
            null,                   // revenueRange
            c.getTotalFunding(),
            c.getInvestors(),
            "Medium",
            c.isPubliclyVisible(),
            false,                  // locked
            c.getDescription(),
            c.getMission(),
            c.getVision(),
            c.getCultureSummary(),
            keyExecs,
            c.getCeoName(),
            c.getCtoName(),
            c.getFounders(),
            c.getProductsServices(), // products
            null,                   // services
            c.getTechnologiesUsed(),
            c.getCertifications(),
            c.getAwards(),
            List.of(),              // shareholding
            financials,
            certificates,
            videos,
            null,                   // aiAnalysis
            null,                   // riskReportUrl
            true,                   // canDownload
            null,                   // companyUrl
            c.getStatus().name(),
            c.getDashboardStatus(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }

    /**
     * Build a CompanyDetailDto for the company listing dashboard (owner/admin view).
     */
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
            .financialDataJson(c.getFinancialDataJson())
            .certificatesDataJson(c.getCertificatesDataJson())
            .videosDataJson(c.getVideosDataJson())
            .build();
    }

    private CompanyDto toDto(Company c) {
        return CompanyDto.builder()
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
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }

    // ──────────── Helpers ────────────

    private String formatEmployeeRange(int count) {
        if (count <= 10) return "1-10";
        if (count <= 50) return "11-50";
        if (count <= 200) return "51-200";
        if (count <= 500) return "201-500";
        if (count <= 1000) return "501-1000";
        if (count <= 5000) return "1001-5000";
        return "5000+";
    }

    private String buildAddress(Company c) {
        var parts = new ArrayList<String>();
        if (c.getRegisteredAddressLine1() != null) parts.add(c.getRegisteredAddressLine1());
        if (c.getRegisteredAddressLine2() != null) parts.add(c.getRegisteredAddressLine2());
        if (c.getRegisteredCity() != null) parts.add(c.getRegisteredCity());
        if (c.getRegisteredState() != null) parts.add(c.getRegisteredState());
        if (c.getRegisteredPinCode() != null) parts.add(c.getRegisteredPinCode());
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private List<Map<String, String>> buildKeyExecutives(Company c) {
        var execs = new ArrayList<Map<String, String>>();
        if (c.getCeoName() != null) {
            execs.add(Map.of("name", c.getCeoName(), "designation", "Chief Executive Officer"));
        }
        if (c.getCtoName() != null) {
            execs.add(Map.of("name", c.getCtoName(), "designation", "Chief Technology Officer"));
        }
        if (c.getFounders() != null) {
            var founderNames = c.getFounders().split("\\s*,\\s*");
            for (var f : founderNames) {
                execs.add(Map.of("name", f.trim(), "designation", "Founder"));
            }
        }
        return execs;
    }

    // ──────────── JSON parsing helpers ────────────

    private List<FinancialStatementDto> parseFinancialData(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<FinancialStatementDto>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private List<CertificateDto> parseCertificatesData(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<CertificateDto>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private List<VideoDto> parseVideosData(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<VideoDto>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
