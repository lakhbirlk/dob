package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Membership;
import com.dob.domain.model.Payment;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.PaymentRepository;
import com.dob.infrastructure.config.PricingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final PricingProperties pricing;

    private static final String PLAN_COMPANY = "COMPANY";

    /**
     * Returns true if the plan is one of the 5 research credit plans.
     */
    private boolean isCreditPlan(String plan) {
        return pricing.getCreditPlans().stream().anyMatch(p -> p.getId().equals(plan));
    }

    /**
     * Creates a pending subscription (payment record) for the given plan.
     */
    @Transactional
    public CreateSubscriptionResponse createSubscription(UUID userId, String plan) {
        boolean isCompany = PLAN_COMPANY.equals(plan);
        boolean isCredit = isCreditPlan(plan);

        if (!isCompany && !isCredit) {
            throw new DomainException("Invalid plan: '" + plan
                + "'. Available credit plans: " + pricing.getCreditPlans().stream().map(PricingProperties.CreditPlan::getId).toList()
                + ", or COMPANY.");
        }

        BigDecimal amount;
        if (isCompany) {
            amount = pricing.getCompanyListing();
        } else {
            var creditPlan = pricing.getCreditPlan(plan);
            amount = creditPlan.getAmount();
        }

        BigDecimal gstRate = pricing.getGstRate();
        BigDecimal gst = amount.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = amount.add(gst);

        Payment.PaymentType paymentType = isCompany
            ? Payment.PaymentType.LISTING
            : Payment.PaymentType.MEMBERSHIP;

        String transactionId = generateTransactionId();

        Payment payment = Payment.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .amount(amount)
            .gst(gst)
            .total(total)
            .razorpayOrderId(transactionId)
            .status(Payment.PaymentStatus.CREATED)
            .paymentType(paymentType)
            .planId(plan)   // store which plan was chosen
            .build();

        Payment saved = paymentRepository.save(payment);

        log.info("Created subscription {} for user {} plan {} amount {}",
            saved.getId(), userId, plan, total);

        return new CreateSubscriptionResponse(
            saved.getId(),
            total,
            "PENDING"
        );
    }

    /**
     * Simulates payment success — marks payment as PAID, activates membership.
     */
    @Transactional
    public PaymentSuccessResponse completePayment(UUID subscriptionId) {
        Payment payment = paymentRepository.findById(subscriptionId)
            .orElseThrow(() -> new DomainException("Subscription not found: " + subscriptionId));

        if (payment.getStatus() != Payment.PaymentStatus.CREATED) {
            throw new DomainException("Subscription " + subscriptionId + " is already "
                + payment.getStatus().name().toLowerCase());
        }

        // Generate transaction details
        String transactionId = payment.getRazorpayOrderId();
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        // Mark payment as paid
        payment.markPaid(paymentId, "simplified-flow");
        paymentRepository.save(payment);

        // Determine plan details and membership params
        String planId = payment.getPlanId() != null ? payment.getPlanId() : "CREDITS_10";
        int creditLimit;
        boolean isCompany;

        if (PLAN_COMPANY.equals(planId)) {
            creditLimit = 0;
            isCompany = true;
        } else if (isCreditPlan(planId)) {
            creditLimit = pricing.getCreditPlan(planId).getCredits();
            isCompany = false;
        } else {
            // Fallback for legacy data
            creditLimit = 10;
            isCompany = false;
        }

        int durationDays = isCompany ? 365 : 30;

        // Cancel any existing active membership for this user
        Optional<Membership> existingActive = membershipRepository.findActiveByUserId(payment.getUserId());
        existingActive.ifPresent(m -> {
            m.cancel();
            membershipRepository.save(m);
            log.info("Cancelled existing membership {} for user {}", m.getId(), payment.getUserId());
        });

        // Create new membership with credit limit
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(durationDays);

        Membership membership = Membership.builder()
            .id(UUID.randomUUID())
            .userId(payment.getUserId())
            .planType(planId)
            .status(Membership.MembershipStatus.ACTIVE)
            .startDate(startDate)
            .endDate(endDate)
            .downloadLimit(creditLimit)
            .downloadsUsed(0)
            .build();

        Membership saved = membershipRepository.save(membership);

        log.info("Payment completed for subscription {} — membership {} activated ({} to {}, {} credits)",
            subscriptionId, saved.getId(), startDate, endDate, creditLimit);

        return new PaymentSuccessResponse(
            saved.getId(),
            planId,
            "ACTIVE",
            startDate,
            endDate,
            transactionId,
            payment.getTotal()
        );
    }

    /**
     * Returns the authenticated user's current active membership.
     */
    @Transactional(readOnly = true)
    public UserMembershipResponse getUserMembership(UUID userId) {
        return membershipRepository.findActiveByUserId(userId)
            .map(m -> new UserMembershipResponse(
                m.getPlanType(),
                m.getStatus().name(),
                m.getStartDate(),
                m.getEndDate()))
            .orElseThrow(() -> new DomainException("No active membership found for user " + userId));
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
