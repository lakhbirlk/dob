package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByRazorpayOrderId(String orderId);
    List<PaymentEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<PaymentEntity> findByStatus(PaymentEntity.PaymentStatus status);
}
