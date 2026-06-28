package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.CompanyNotFoundException;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Company;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private static final List<String> ADMIN_ROLES = List.of("ADMIN", "SUPER_ADMIN");

    private final CompanyRepository companyRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    /**
     * Search companies and return appropriate response based on user's subscription.
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

        boolean isPremium = principal != null && isPremiumUser(principal);

        var content = companies.stream()
            .map(c -> isPremium ? (Object) toPremiumResponse(c) : (Object) toFreeResponse(c))
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
     */
    public Object getById(UUID id, UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException(id.toString()));

        if (company.getStatus() != Company.CompanyStatus.APPROVED) {
            throw new DomainException("Company profile is not publicly available");
        }

        boolean isPremium = principal != null && isPremiumUser(principal);
        return isPremium ? toPremiumDetailResponse(company) : toFreeResponse(company);
    }

    /**
     * Get company by public DoB ID. Returns masked or full data based on subscription.
     */
    public Object getByPublicCompanyId(String publicCompanyId, UserPrincipal principal) {
        var company = companyRepository.findByPublicCompanyId(publicCompanyId)
            .orElseThrow(() -> new CompanyNotFoundException(publicCompanyId));

        if (company.getStatus() != Company.CompanyStatus.APPROVED) {
            throw new DomainException("Company profile is not publicly available");
        }

        boolean isPremium = principal != null && isPremiumUser(principal);
        return isPremium ? toPremiumDetailResponse(company) : toFreeResponse(company);
    }

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
            .status(Company.CompanyStatus.PENDING)
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

    // ──────────── Premium / Subscription check ────────────

    /**
     * Determines whether the user should receive premium (unrestricted) data.
     * ADMIN and SUPER_ADMIN users always have premium access without needing a subscription.
     * All other users must have an active, non-expired membership.
     */
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

    /**
     * Build a FreeCompanyResponse — never includes company name, CIN, GST, PAN,
     * directors, contact details, address, or any identifying information.
     */
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
            null,       // employeeRange — requires join with company_profiles
            null,       // revenueRange  — requires join with company_profiles
            "Medium",   // riskScore     — placeholder (to be calculated by risk engine)
            c.getStatus() == Company.CompanyStatus.APPROVED,
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

        return new PremiumCompanyResponse(
            c.getPublicCompanyId(),
            c.getName(),
            null,       // cin
            null,       // gstin
            null,       // pan
            null,       // registrationNumber
            c.getSector(),
            null,       // subSector
            c.getCompanyType(),
            c.getIncorporationYear(),
            age,
            c.getState(),
            c.getCity(),
            null,       // address
            null,       // email
            c.getWebsite(),
            null,       // employeeRange
            null,       // revenueRange
            "Medium",   // riskScore
            c.getStatus() == Company.CompanyStatus.APPROVED,
            false,      // locked
            c.getDescription(),
            c.getLogoUrl(),
            List.of(),  // keyExecutives
            List.of(),  // shareholding
            List.of(),  // financials
            List.of(),  // certificates
            List.of(),  // videos
            null,       // aiAnalysis
            null,       // riskReportUrl
            true,       // canDownload
            null,       // companyUrl
            c.getStatus().name(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }

    /**
     * Build a full PremiumCompanyResponse for detail view — includes financials,
     * certificates, videos loaded from respective repositories.
     * In production, wire to CompanyProfileRepository, FinancialStatementRepository, etc.
     */
    private PremiumCompanyResponse toPremiumDetailResponse(Company c) {
        int age = c.getIncorporationYear() != null
            ? Year.now().getValue() - c.getIncorporationYear()
            : 0;

        return new PremiumCompanyResponse(
            c.getPublicCompanyId(),
            c.getName(),
            null,       // cin — wire to company_profile or dedicated field
            null,       // gstin
            null,       // pan
            null,       // registrationNumber
            c.getSector(),
            null,       // subSector
            c.getCompanyType(),
            c.getIncorporationYear(),
            age,
            c.getState(),
            c.getCity(),
            null,       // address
            null,       // email
            c.getWebsite(),
            null,       // employeeRange
            null,       // revenueRange
            "Medium",   // riskScore
            c.getStatus() == Company.CompanyStatus.APPROVED,
            false,      // locked
            c.getDescription(),
            c.getLogoUrl(),
            List.of(),  // keyExecutives
            List.of(),  // shareholding
            List.of(),  // financials — wire to FinancialStatementRepository
            List.of(),  // certificates — wire to CertificateRepository
            List.of(),  // videos — wire to CompanyVideoRepository
            null,       // aiAnalysis
            null,       // riskReportUrl
            true,       // canDownload
            null,       // companyUrl
            c.getStatus().name(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
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
}
