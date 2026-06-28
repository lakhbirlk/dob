package com.dob.application.service;

import com.dob.application.dto.PaymentDto;
import com.dob.application.dto.PaymentOrderRequest;
import com.dob.application.dto.PaymentVerifyRequest;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Membership;
import com.dob.domain.model.Payment;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.PaymentRepository;
import com.dob.infrastructure.config.PricingProperties;
import com.dob.infrastructure.security.UserPrincipal;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!local")
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final PricingProperties pricing;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    @Transactional
    public PaymentDto createOrder(UserPrincipal principal, PaymentOrderRequest request) {
        BigDecimal amount, gst;

        if ("MEMBERSHIP".equals(request.paymentType())) {
            amount = pricing.getMembership();
        } else if ("LISTING".equals(request.paymentType())) {
            amount = pricing.getCompanyListing();
        } else {
            throw new DomainException("Invalid payment type");
        }

        gst = amount.multiply(pricing.getGstRate());
        BigDecimal total = amount.add(gst);

        try {
            // Amount in paise for Razorpay
            int amountInPaise = total.multiply(new BigDecimal("100")).intValue();

            var orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "rcpt_" + UUID.randomUUID().toString().substring(0, 8));

            Order order = razorpayClient.orders.create(orderRequest);

            var payment = Payment.builder()
                .userId(principal.id())
                .amount(amount)
                .gst(gst)
                .total(total)
                .razorpayOrderId(order.get("id"))
                .status(Payment.PaymentStatus.CREATED)
                .paymentType(Payment.PaymentType.valueOf(request.paymentType()))
                .build();

            payment = paymentRepository.save(payment);
            return toDto(payment);

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed", e);
            throw new DomainException("Payment initiation failed: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentDto verify(UserPrincipal principal, PaymentVerifyRequest request) {
        // Verify signature
        String payload = request.razorpayOrderId() + "|" + request.razorpayPaymentId();
        String expected = hmacSha256(payload, keySecret);

        if (!expected.equals(request.razorpaySignature())) {
            throw new DomainException("Payment verification failed: invalid signature");
        }

        var payment = paymentRepository.findByRazorpayOrderId(request.razorpayOrderId())
            .orElseThrow(() -> new DomainException("Payment not found"));

        payment.markPaid(request.razorpayPaymentId(), request.razorpaySignature());
        payment = paymentRepository.save(payment);

        // Activate membership on successful payment
        if (payment.getPaymentType() == Payment.PaymentType.MEMBERSHIP) {
            activateMembership(principal.id());
        }

        return toDto(payment);
    }

    @Transactional
    public PaymentDto requestRefund(UserPrincipal principal, String paymentId) {
        var payment = paymentRepository.findById(UUID.fromString(paymentId))
            .orElseThrow(() -> new DomainException("Payment not found"));

        if (!payment.getUserId().equals(principal.id())) {
            throw new DomainException("Not your payment");
        }

        if (!payment.canRefund()) {
            throw new DomainException("Payment cannot be refunded");
        }

        // Process refund via Razorpay
        try {
            var refundRequest = new JSONObject();
            refundRequest.put("payment_id", payment.getRazorpayPaymentId());
            razorpayClient.payments.refund(refundRequest);
        } catch (RazorpayException e) {
            log.error("Razorpay refund failed", e);
            throw new DomainException("Refund failed: " + e.getMessage());
        }

        payment.markRefunded();
        payment = paymentRepository.save(payment);

        // Cancel membership if applicable
        if (payment.getMembershipId() != null) {
            var membership = membershipRepository.findById(payment.getMembershipId());
            membership.ifPresent(m -> {
                m.markRefunded();
                membershipRepository.save(m);
            });
        }

        return toDto(payment);
    }

    public List<PaymentDto> getHistory(UserPrincipal principal) {
        return paymentRepository.findByUserId(principal.id())
            .stream().map(this::toDto).toList();
    }

    private void activateMembership(UUID userId) {
        var existing = membershipRepository.findActiveByUserId(userId);
        existing.ifPresent(m -> { m.cancel(); membershipRepository.save(m); });

        // Monthly membership with 50 downloads from terms.html
        var membership = Membership.builder()
            .userId(userId)
            .planType("MONTHLY")
            .status(Membership.MembershipStatus.ACTIVE)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(1))
            .downloadLimit(50)
            .downloadsUsed(0)
            .build();

        membershipRepository.save(membership);
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new DomainException("Signature verification failed");
        }
    }

    private PaymentDto toDto(Payment p) {
        return PaymentDto.builder()
            .id(p.getId())
            .userId(p.getUserId())
            .membershipId(p.getMembershipId())
            .companyId(p.getCompanyId())
            .amount(p.getAmount())
            .gst(p.getGst())
            .total(p.getTotal())
            .razorpayOrderId(p.getRazorpayOrderId())
            .razorpayPaymentId(p.getRazorpayPaymentId())
            .status(p.getStatus().name())
            .paymentType(p.getPaymentType().name())
            .createdAt(p.getCreatedAt())
            .build();
    }
}
