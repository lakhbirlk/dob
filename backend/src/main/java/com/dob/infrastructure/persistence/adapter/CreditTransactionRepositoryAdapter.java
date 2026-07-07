package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.CreditTransaction;
import com.dob.domain.repository.CreditTransactionRepository;
import com.dob.infrastructure.persistence.entity.CreditTransactionEntity;
import com.dob.infrastructure.persistence.repository.CreditTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreditTransactionRepositoryAdapter implements CreditTransactionRepository {

    private final CreditTransactionJpaRepository jpa;

    @Override
    public CreditTransaction save(CreditTransaction transaction) {
        var entity = toEntity(transaction);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<CreditTransaction> findByTransactionId(String transactionId) {
        return jpa.findByTransactionId(transactionId).map(this::toDomain);
    }

    @Override
    public List<CreditTransaction> findByMemberId(UUID memberId, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findByMemberIdOrderByCreatedAtDesc(memberId, pageable).stream().map(this::toDomain).toList();
    }

    @Override
    public long countByMemberId(UUID memberId) {
        return jpa.countByMemberId(memberId);
    }

    private CreditTransaction toDomain(CreditTransactionEntity e) {
        return CreditTransaction.builder()
            .id(e.getId())
            .memberId(e.getMemberId())
            .companyId(e.getCompanyId())
            .creditsUsed(e.getCreditsUsed())
            .transactionType(e.getTransactionType())
            .balanceBefore(e.getBalanceBefore())
            .balanceAfter(e.getBalanceAfter())
            .status(e.getStatus())
            .transactionId(e.getTransactionId())
            .createdAt(e.getCreatedAt())
            .build();
    }

    private CreditTransactionEntity toEntity(CreditTransaction t) {
        return CreditTransactionEntity.builder()
            .id(t.getId())
            .memberId(t.getMemberId())
            .companyId(t.getCompanyId())
            .creditsUsed(t.getCreditsUsed())
            .transactionType(t.getTransactionType())
            .balanceBefore(t.getBalanceBefore())
            .balanceAfter(t.getBalanceAfter())
            .status(t.getStatus())
            .transactionId(t.getTransactionId())
            .createdAt(t.getCreatedAt())
            .build();
    }
}
