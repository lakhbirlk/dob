package com.dob.domain.repository;

import com.dob.domain.model.Company;

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
    List<Company> findByStatus(Company.CompanyStatus status, int page, int size);
    List<Company> findByCreatedBy(UUID userId);
    boolean existsByName(String name);
}
