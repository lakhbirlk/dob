package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.Company;
import com.dob.domain.repository.CompanyRepository;
import com.dob.infrastructure.persistence.entity.CompanyEntity;
import com.dob.infrastructure.persistence.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpa;

    @Override
    public Optional<Company> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Company> findByPublicCompanyId(String publicCompanyId) {
        return jpa.findByPublicCompanyId(publicCompanyId).map(this::toDomain);
    }

    @Override
    public Company save(Company company) {
        var entity = toEntity(company);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<Company> search(String query, String sector, String state, String companyType,
                                String revenueRange, String membershipFilter, int page, int size) {
        int offset = page * size;
        return jpa.search(query, sector, state, companyType, size, offset)
            .stream().map(this::toDomain).toList();
    }

    @Override
    public long countSearch(String query, String sector, String state, String companyType,
                            String revenueRange, String membershipFilter) {
        return jpa.countSearch(query, sector, state, companyType);
    }

    @Override
    public List<Company> findByStatus(Company.CompanyStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findByStatus(CompanyEntity.CompanyStatus.valueOf(status.name()))
            .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Company> findByCreatedBy(UUID userId) {
        return jpa.findByCreatedBy(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return jpa.existsByName(name);
    }

    private Company toDomain(CompanyEntity e) {
        return Company.builder()
            .id(e.getId())
            .publicCompanyId(e.getPublicCompanyId())
            .name(e.getName())
            .brandName(e.getBrandName())
            .sector(e.getSector())
            .state(e.getState())
            .city(e.getCity())
            .companyType(e.getCompanyType())
            .incorporationYear(e.getIncorporationYear())
            .description(e.getDescription())
            .website(e.getWebsite())
            .logoUrl(e.getLogoUrl())
            .status(Company.CompanyStatus.valueOf(e.getStatus().name()))
            .createdBy(e.getCreatedBy())
            .approvedBy(e.getApprovedBy())
            .approvedAt(e.getApprovedAt())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            // Registration fields
            .cin(e.getCin())
            .gstin(e.getGstin())
            .pan(e.getPan())
            .tan(e.getTan())
            .msmeRegistration(e.getMsmeRegistration())
            .startupIndiaRegistration(e.getStartupIndiaRegistration())
            .registeredAddressLine1(e.getRegisteredAddressLine1())
            .registeredAddressLine2(e.getRegisteredAddressLine2())
            .registeredCity(e.getRegisteredCity())
            .registeredState(e.getRegisteredState())
            .registeredPinCode(e.getRegisteredPinCode())
            .registeredCountry(e.getRegisteredCountry())
            .officialEmail(e.getOfficialEmail())
            .officialPhone(e.getOfficialPhone())
            .linkedinProfile(e.getLinkedinProfile())
            .socialMediaLinks(e.getSocialMediaLinks())
            .authorizedRepName(e.getAuthorizedRepName())
            .authorizedRepDesignation(e.getAuthorizedRepDesignation())
            .authorizedRepMobile(e.getAuthorizedRepMobile())
            .authorizedRepEmail(e.getAuthorizedRepEmail())
            .authorizedRepIdentityProofUrl(e.getAuthorizedRepIdentityProofUrl())
            .authorizedRepDigitalSignatureUrl(e.getAuthorizedRepDigitalSignatureUrl())
            .annualTurnover(e.getAnnualTurnover())
            .paidUpCapital(e.getPaidUpCapital())
            .authorizedCapital(e.getAuthorizedCapital())
            .employeeCount(e.getEmployeeCount())
            .financialYear(e.getFinancialYear())
            .balanceSheetUrl(e.getBalanceSheetUrl())
            .auditorDetails(e.getAuditorDetails())
            .productsServices(e.getProductsServices())
            .businessDescription(e.getBusinessDescription())
            .exportImportStatus(e.getExportImportStatus())
            .numBranches(e.getNumBranches())
            .operationalStates(e.getOperationalStates())
            .certifications(e.getCertifications())
            .build();
    }

    private CompanyEntity toEntity(Company c) {
        return CompanyEntity.builder()
            .id(c.getId())
            .publicCompanyId(c.getPublicCompanyId())
            .name(c.getName())
            .brandName(c.getBrandName())
            .sector(c.getSector())
            .state(c.getState())
            .city(c.getCity())
            .companyType(c.getCompanyType())
            .incorporationYear(c.getIncorporationYear())
            .description(c.getDescription())
            .website(c.getWebsite())
            .logoUrl(c.getLogoUrl())
            .status(CompanyEntity.CompanyStatus.valueOf(c.getStatus().name()))
            .createdBy(c.getCreatedBy())
            .approvedBy(c.getApprovedBy())
            .approvedAt(c.getApprovedAt())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            // Registration fields
            .cin(c.getCin())
            .gstin(c.getGstin())
            .pan(c.getPan())
            .tan(c.getTan())
            .msmeRegistration(c.getMsmeRegistration())
            .startupIndiaRegistration(c.getStartupIndiaRegistration())
            .registeredAddressLine1(c.getRegisteredAddressLine1())
            .registeredAddressLine2(c.getRegisteredAddressLine2())
            .registeredCity(c.getRegisteredCity())
            .registeredState(c.getRegisteredState())
            .registeredPinCode(c.getRegisteredPinCode())
            .registeredCountry(c.getRegisteredCountry())
            .officialEmail(c.getOfficialEmail())
            .officialPhone(c.getOfficialPhone())
            .linkedinProfile(c.getLinkedinProfile())
            .socialMediaLinks(c.getSocialMediaLinks())
            .authorizedRepName(c.getAuthorizedRepName())
            .authorizedRepDesignation(c.getAuthorizedRepDesignation())
            .authorizedRepMobile(c.getAuthorizedRepMobile())
            .authorizedRepEmail(c.getAuthorizedRepEmail())
            .authorizedRepIdentityProofUrl(c.getAuthorizedRepIdentityProofUrl())
            .authorizedRepDigitalSignatureUrl(c.getAuthorizedRepDigitalSignatureUrl())
            .annualTurnover(c.getAnnualTurnover())
            .paidUpCapital(c.getPaidUpCapital())
            .authorizedCapital(c.getAuthorizedCapital())
            .employeeCount(c.getEmployeeCount())
            .financialYear(c.getFinancialYear())
            .balanceSheetUrl(c.getBalanceSheetUrl())
            .auditorDetails(c.getAuditorDetails())
            .productsServices(c.getProductsServices())
            .businessDescription(c.getBusinessDescription())
            .exportImportStatus(c.getExportImportStatus())
            .numBranches(c.getNumBranches())
            .operationalStates(c.getOperationalStates())
            .certifications(c.getCertifications())
            .build();
    }
}
