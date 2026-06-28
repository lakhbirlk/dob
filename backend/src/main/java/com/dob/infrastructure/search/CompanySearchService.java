package com.dob.infrastructure.search;

import com.dob.infrastructure.persistence.entity.CompanyEntity;
import com.dob.infrastructure.persistence.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PostgreSQL full-text search for companies.
 * For production scale, replace with Elasticsearch.
 */
@Service
@RequiredArgsConstructor
public class CompanySearchService {

    private final CompanyJpaRepository companyJpaRepository;

    public List<CompanyEntity> search(String query, String sector, String state,
                                       String companyType, int page, int size) {
        int offset = page * size;
        return companyJpaRepository.search(query, sector, state, companyType, size, offset);
    }

    public long count(String query, String sector, String state, String companyType) {
        return companyJpaRepository.countSearch(query, sector, state, companyType);
    }
}
