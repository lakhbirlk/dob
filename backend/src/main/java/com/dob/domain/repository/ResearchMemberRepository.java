package com.dob.domain.repository;

import com.dob.domain.model.ResearchMember;

import java.util.Optional;
import java.util.UUID;

public interface ResearchMemberRepository {
    Optional<ResearchMember> findById(UUID id);
    Optional<ResearchMember> findByUserId(UUID userId);
    ResearchMember save(ResearchMember researchMember);
}
