package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {

    /**
     * Search companies that are publicly visible (APPROVED_ACTIVE status).
     */
    @Query(value = """
        SELECT * FROM companies c
        WHERE c.status = 'APPROVED_ACTIVE'
        AND (:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:sector IS NULL OR c.sector = :sector)
        AND (:state IS NULL OR c.state = :state)
        AND (:companyType IS NULL OR c.company_type = :companyType)
        ORDER BY c.name
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<CompanyEntity> search(
        @Param("query") String query,
        @Param("sector") String sector,
        @Param("state") String state,
        @Param("companyType") String companyType,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    /**
     * Count search results for publicly visible companies.
     */
    @Query(value = """
        SELECT count(*) FROM companies c
        WHERE c.status = 'APPROVED_ACTIVE'
        AND (:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:sector IS NULL OR c.sector = :sector)
        AND (:state IS NULL OR c.state = :state)
        AND (:companyType IS NULL OR c.company_type = :companyType)
        """, nativeQuery = true)
    long countSearch(
        @Param("query") String query,
        @Param("sector") String sector,
        @Param("state") String state,
        @Param("companyType") String companyType
    );

    List<CompanyEntity> findByStatus(CompanyEntity.CompanyStatus status);

    List<CompanyEntity> findByStatusIn(List<CompanyEntity.CompanyStatus> statuses);

    List<CompanyEntity> findByCreatedBy(UUID userId);

    @Query(value = """
        SELECT * FROM companies c
        WHERE c.created_by = :userId
        ORDER BY c.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<CompanyEntity> findByCreatedBy(@Param("userId") UUID userId,
                                         @Param("limit") int limit,
                                         @Param("offset") int offset);

    @Query(value = """
        SELECT count(*) FROM companies c
        WHERE c.created_by = :userId
        """, nativeQuery = true)
    long countByCreatedBy(@Param("userId") UUID userId);

    /**
     * Find companies with APPROVED_ACTIVE status whose listing membership has expired.
     */
    @Query(value = """
        SELECT * FROM companies c
        WHERE c.status = 'APPROVED_ACTIVE'
        AND c.listing_expires_at < :today
        """, nativeQuery = true)
    List<CompanyEntity> findExpiredListings(@Param("today") LocalDate today);

    boolean existsByName(String name);

    Optional<CompanyEntity> findByPublicCompanyId(String publicCompanyId);
}
