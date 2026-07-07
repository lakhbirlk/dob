package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.CreditTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditTransactionJpaRepository extends JpaRepository<CreditTransactionEntity, UUID> {
    Optional<CreditTransactionEntity> findByTransactionId(String transactionId);
    Page<CreditTransactionEntity> findByMemberIdOrderByCreatedAtDesc(UUID memberId, Pageable pageable);
    long countByMemberId(UUID memberId);
}
