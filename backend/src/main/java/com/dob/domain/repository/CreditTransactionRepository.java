package com.dob.domain.repository;

import com.dob.domain.model.CreditTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditTransactionRepository {
    CreditTransaction save(CreditTransaction transaction);
    Optional<CreditTransaction> findByTransactionId(String transactionId);
    List<CreditTransaction> findByMemberId(UUID memberId, int page, int size);
    long countByMemberId(UUID memberId);
}
