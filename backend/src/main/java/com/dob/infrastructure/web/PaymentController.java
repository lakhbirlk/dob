package com.dob.infrastructure.web;

import com.dob.application.dto.PaymentDto;
import com.dob.application.dto.PaymentOrderRequest;
import com.dob.application.dto.PaymentSuccessRequest;
import com.dob.application.dto.PaymentSuccessResponse;
import com.dob.application.dto.PaymentVerifyRequest;
import com.dob.application.service.LocalPaymentService;
import com.dob.application.service.PaymentService;
import com.dob.application.service.SubscriptionService;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment processing via Razorpay — order creation, verification, history, and webhook handling")
public class PaymentController {

    @Autowired(required = false)
    private PaymentService paymentService;

    @Autowired(required = false)
    private LocalPaymentService localPaymentService;

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment order", description = "Creates a Razorpay order for membership subscription (₹2500 + GST).")
    public PaymentDto createOrder(@AuthenticationPrincipal UserPrincipal principal,
                                   @Valid @RequestBody PaymentOrderRequest request) {
        if (paymentService != null) return paymentService.createOrder(principal, request);
        return localPaymentService.createOrder(principal, request);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verifies the Razorpay payment signature and activates the membership.")
    public PaymentDto verify(@AuthenticationPrincipal UserPrincipal principal,
                              @Valid @RequestBody PaymentVerifyRequest request) {
        if (paymentService != null) return paymentService.verify(principal, request);
        return localPaymentService.verify(principal, request);
    }

    @PostMapping("/success")
    @Operation(summary = "Complete payment (simulated)", description = "Simulates a successful payment for the given subscription. Marks payment as PAID, activates the membership, and returns membership details. No real payment gateway involved.")
    public PaymentSuccessResponse completePayment(@AuthenticationPrincipal UserPrincipal principal,
                                                   @Valid @RequestBody PaymentSuccessRequest request) {
        return subscriptionService.completePayment(request.subscriptionId());
    }

    @GetMapping("/history")
    @Operation(summary = "Payment history", description = "Returns the authenticated user's payment transaction history.")
    public List<PaymentDto> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        if (paymentService != null) return paymentService.getHistory(principal);
        return localPaymentService.getHistory(principal);
    }

    @PostMapping("/razorpay-webhook")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Razorpay webhook", description = "Handles incoming Razorpay webhook events for payment status updates. No authentication required.")
    public void handleWebhook(@RequestBody Map<String, Object> payload,
                               @RequestHeader("X-Razorpay-Signature") String signature) {
        // Webhook processing — verify signature and update payment status
    }
}
