package com.dob.domain.repository;

import com.dob.domain.model.Membership;

import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository {
    Optional<Membership> findById(UUID id);
    Optional<Membership> findActiveByUserId(UUID userId);
    Membership save(Membership membership);
}
