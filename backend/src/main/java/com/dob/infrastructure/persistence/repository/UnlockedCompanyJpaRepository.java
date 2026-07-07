package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.UnlockedCompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnlockedCompanyJpaRepository extends JpaRepository<UnlockedCompanyEntity, UUID> {
    Optional<UnlockedCompanyEntity> findByMemberIdAndCompanyId(UUID memberId, UUID companyId);
    Page<UnlockedCompanyEntity> findByMemberIdOrderByUnlockedAtDesc(UUID memberId, Pageable pageable);
    long countByMemberId(UUID memberId);
    boolean existsByMemberIdAndCompanyId(UUID memberId, UUID companyId);

    @Query("SELECT u.companyId FROM UnlockedCompanyEntity u WHERE u.memberId = :memberId")
    List<UUID> findCompanyIdsByMemberId(@Param("memberId") UUID memberId);
}
