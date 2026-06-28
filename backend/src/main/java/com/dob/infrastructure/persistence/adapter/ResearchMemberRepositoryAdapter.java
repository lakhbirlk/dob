package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.ResearchMember;
import com.dob.domain.repository.ResearchMemberRepository;
import com.dob.infrastructure.persistence.entity.ResearchMemberEntity;
import com.dob.infrastructure.persistence.repository.ResearchMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResearchMemberRepositoryAdapter implements ResearchMemberRepository {

    private final ResearchMemberJpaRepository jpa;

    @Override
    public Optional<ResearchMember> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ResearchMember> findByUserId(UUID userId) {
        return jpa.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public ResearchMember save(ResearchMember researchMember) {
        var entity = toEntity(researchMember);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    private ResearchMember toDomain(ResearchMemberEntity e) {
        return ResearchMember.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .fullName(e.getFullName())
            .occupation(e.getOccupation())
            .organization(e.getOrganization())
            .designation(e.getDesignation())
            .researchPurpose(e.getResearchPurpose())
            .country(e.getCountry())
            .state(e.getState())
            .city(e.getCity())
            .industriesOfInterest(e.getIndustriesOfInterest())
            .companySizePreference(e.getCompanySizePreference())
            .notificationPreferences(e.getNotificationPreferences())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    private ResearchMemberEntity toEntity(ResearchMember m) {
        return ResearchMemberEntity.builder()
            .id(m.getId())
            .userId(m.getUserId())
            .fullName(m.getFullName())
            .occupation(m.getOccupation())
            .organization(m.getOrganization())
            .designation(m.getDesignation())
            .researchPurpose(m.getResearchPurpose())
            .country(m.getCountry())
            .state(m.getState())
            .city(m.getCity())
            .industriesOfInterest(m.getIndustriesOfInterest())
            .companySizePreference(m.getCompanySizePreference())
            .notificationPreferences(m.getNotificationPreferences())
            .createdAt(m.getCreatedAt())
            .updatedAt(m.getUpdatedAt())
            .build();
    }
}
