package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.Payment;
import com.dob.domain.repository.PaymentRepository;
import com.dob.infrastructure.persistence.entity.PaymentEntity;
import com.dob.infrastructure.persistence.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpa;

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByRazorpayOrderId(String orderId) {
        return jpa.findByRazorpayOrderId(orderId).map(this::toDomain);
    }

    @Override
    public List<Payment> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Payment save(Payment payment) {
        var entity = toEntity(payment);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    private Payment toDomain(PaymentEntity e) {
        return Payment.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .membershipId(e.getMembershipId())
            .companyId(e.getCompanyId())
            .planId(e.getPlanId())
            .amount(e.getAmount())
            .gst(e.getGst())
            .total(e.getTotal())
            .razorpayOrderId(e.getRazorpayOrderId())
            .razorpayPaymentId(e.getRazorpayPaymentId())
            .razorpaySignature(e.getRazorpaySignature())
            .status(Payment.PaymentStatus.valueOf(e.getStatus().name()))
            .paymentType(Payment.PaymentType.valueOf(e.getPaymentType().name()))
            .createdAt(e.getCreatedAt())
            .build();
    }

    private PaymentEntity toEntity(Payment p) {
        return PaymentEntity.builder()
            .id(p.getId())
            .userId(p.getUserId())
            .membershipId(p.getMembershipId())
            .companyId(p.getCompanyId())
            .planId(p.getPlanId())
            .amount(p.getAmount())
            .gst(p.getGst())
            .total(p.getTotal())
            .razorpayOrderId(p.getRazorpayOrderId())
            .razorpayPaymentId(p.getRazorpayPaymentId())
            .razorpaySignature(p.getRazorpaySignature())
            .status(PaymentEntity.PaymentStatus.valueOf(p.getStatus().name()))
            .paymentType(PaymentEntity.PaymentType.valueOf(p.getPaymentType().name()))
            .createdAt(p.getCreatedAt())
            .build();
    }
}
