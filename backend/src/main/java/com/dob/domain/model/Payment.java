package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Payment {
    private UUID id;
    private UUID userId;
    private UUID membershipId;
    private UUID companyId;
    private BigDecimal amount;
    private BigDecimal gst;
    private BigDecimal total;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private PaymentStatus status;
    private PaymentType paymentType;
    private Instant createdAt;

    public BigDecimal getTotal() {
        return amount.add(gst);
    }

    public void markPaid(String paymentId, String signature) {
        this.razorpayPaymentId = paymentId;
        this.razorpaySignature = signature;
        this.status = PaymentStatus.PAID;
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean canRefund() {
        return status == PaymentStatus.PAID;
    }

    public enum PaymentStatus {
        CREATED, PAID, FAILED, REFUNDED
    }

    public enum PaymentType {
        MEMBERSHIP, LISTING
    }
}
