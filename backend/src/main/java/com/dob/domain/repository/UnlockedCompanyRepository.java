package com.dob.domain.repository;

import com.dob.domain.model.UnlockedCompany;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnlockedCompanyRepository {
    UnlockedCompany save(UnlockedCompany unlockedCompany);
    Optional<UnlockedCompany> findByMemberIdAndCompanyId(UUID memberId, UUID companyId);
    List<UnlockedCompany> findByMemberId(UUID memberId, int page, int size);
    long countByMemberId(UUID memberId);
    boolean existsByMemberIdAndCompanyId(UUID memberId, UUID companyId);
    List<UUID> findCompanyIdsByMemberId(UUID memberId);
}
