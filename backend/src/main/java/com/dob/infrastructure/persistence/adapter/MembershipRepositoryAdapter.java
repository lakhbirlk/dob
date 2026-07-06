package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.Membership;
import com.dob.domain.repository.MembershipRepository;
import com.dob.infrastructure.persistence.entity.MembershipEntity;
import com.dob.infrastructure.persistence.repository.MembershipJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MembershipRepositoryAdapter implements MembershipRepository {

    private final MembershipJpaRepository jpa;

    @Override
    public Optional<Membership> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Membership> findActiveByUserId(UUID userId) {
        return jpa.findByUserIdAndStatus(userId, MembershipEntity.MembershipStatus.ACTIVE)
            .map(this::toDomain);
    }

    @Override
    public List<Membership> findByUserId(UUID userId) {
        return jpa.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Membership save(Membership membership) {
        var entity = toEntity(membership);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    private Membership toDomain(MembershipEntity e) {
        return Membership.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .planType(e.getPlanType())
            .status(Membership.MembershipStatus.valueOf(e.getStatus().name()))
            .startDate(e.getStartDate())
            .endDate(e.getEndDate())
            .downloadLimit(e.getDownloadLimit())
            .downloadsUsed(e.getDownloadsUsed())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    private MembershipEntity toEntity(Membership m) {
        return MembershipEntity.builder()
            .id(m.getId())
            .userId(m.getUserId())
            .planType(m.getPlanType())
            .status(MembershipEntity.MembershipStatus.valueOf(m.getStatus().name()))
            .startDate(m.getStartDate())
            .endDate(m.getEndDate())
            .downloadLimit(m.getDownloadLimit())
            .downloadsUsed(m.getDownloadsUsed())
            .createdAt(m.getCreatedAt())
            .updatedAt(m.getUpdatedAt())
            .build();
    }
}
