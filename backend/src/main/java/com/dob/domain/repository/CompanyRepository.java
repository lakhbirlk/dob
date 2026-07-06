package com.dob.domain.repository;

import com.dob.domain.model.Company;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Optional<Company> findById(UUID id);
    Optional<Company> findByPublicCompanyId(String publicCompanyId);
    Company save(Company company);
    List<Company> search(String query, String sector, String state, String companyType,
                         String revenueRange, String membershipFilter, int page, int size);
    long countSearch(String query, String sector, String state, String companyType,
                     String revenueRange, String membershipFilter);
    List<Company> findByStatus(Company.CompanyStatus status);
    List<Company> findByStatusIn(List<Company.CompanyStatus> statuses);
    List<Company> findByCreatedBy(UUID userId);
    List<Company> findByCreatedBy(UUID userId, int page, int size);
    long countByCreatedBy(UUID userId);
    List<Company> findExpiredListings(LocalDate today);
    boolean existsByName(String name);
}
