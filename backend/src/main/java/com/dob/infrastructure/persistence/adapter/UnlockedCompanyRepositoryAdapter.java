package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.UnlockedCompany;
import com.dob.domain.repository.UnlockedCompanyRepository;
import com.dob.infrastructure.persistence.entity.UnlockedCompanyEntity;
import com.dob.infrastructure.persistence.repository.UnlockedCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UnlockedCompanyRepositoryAdapter implements UnlockedCompanyRepository {

    private final UnlockedCompanyJpaRepository jpa;

    @Override
    public UnlockedCompany save(UnlockedCompany unlockedCompany) {
        var entity = toEntity(unlockedCompany);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<UnlockedCompany> findByMemberIdAndCompanyId(UUID memberId, UUID companyId) {
        return jpa.findByMemberIdAndCompanyId(memberId, companyId).map(this::toDomain);
    }

    @Override
    public List<UnlockedCompany> findByMemberId(UUID memberId, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findByMemberIdOrderByUnlockedAtDesc(memberId, pageable).stream().map(this::toDomain).toList();
    }

    @Override
    public long countByMemberId(UUID memberId) {
        return jpa.countByMemberId(memberId);
    }

    @Override
    public boolean existsByMemberIdAndCompanyId(UUID memberId, UUID companyId) {
        return jpa.existsByMemberIdAndCompanyId(memberId, companyId);
    }

    @Override
    public List<UUID> findCompanyIdsByMemberId(UUID memberId) {
        return jpa.findCompanyIdsByMemberId(memberId);
    }

    private UnlockedCompany toDomain(UnlockedCompanyEntity e) {
        return UnlockedCompany.builder()
            .id(e.getId())
            .memberId(e.getMemberId())
            .companyId(e.getCompanyId())
            .creditsUsed(e.getCreditsUsed())
            .unlockedAt(e.getUnlockedAt())
            .unlockedBy(e.getUnlockedBy())
            .build();
    }

    private UnlockedCompanyEntity toEntity(UnlockedCompany u) {
        return UnlockedCompanyEntity.builder()
            .id(u.getId())
            .memberId(u.getMemberId())
            .companyId(u.getCompanyId())
            .creditsUsed(u.getCreditsUsed())
            .unlockedAt(u.getUnlockedAt())
            .unlockedBy(u.getUnlockedBy())
            .build();
    }
}
