package com.dob.domain.repository;

import com.dob.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Optional<Payment> findById(UUID id);
    Optional<Payment> findByRazorpayOrderId(String orderId);
    List<Payment> findByUserId(UUID userId);
    Payment save(Payment payment);
}
