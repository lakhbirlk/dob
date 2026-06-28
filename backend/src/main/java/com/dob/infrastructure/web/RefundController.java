package com.dob.infrastructure.web;

import com.dob.application.dto.PaymentDto;
import com.dob.application.service.LocalPaymentService;
import com.dob.application.service.PaymentService;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/refunds")
@Tag(name = "Refunds", description = "Refund request and status tracking for authenticated users")
public class RefundController {

    @Autowired(required = false)
    private PaymentService paymentService;

    @Autowired(required = false)
    private LocalPaymentService localPaymentService;

    @PostMapping("/request")
    @Operation(summary = "Request refund", description = "Submit a refund request for a completed payment. Subject to cooling-off period rules.")
    public PaymentDto requestRefund(@AuthenticationPrincipal UserPrincipal principal,
                                     @RequestBody Map<String, String> body) {
        String paymentId = body.get("paymentId");
        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("paymentId is required");
        }
        if (paymentService != null) return paymentService.requestRefund(principal, paymentId);
        return localPaymentService.requestRefund(principal, paymentId);
    }

    @GetMapping("/status/{paymentId}")
    @Operation(summary = "Get refund status", description = "Returns the refund status for a given payment.")
    public PaymentDto getRefundStatus(@Parameter(description = "Payment UUID") @PathVariable UUID paymentId,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        // Returns payment with status
        if (paymentService != null) {
            return paymentService.getHistory(principal).stream()
                .filter(p -> p.id().equals(paymentId)).findFirst().orElse(null);
        }
        return localPaymentService.getHistory(principal).stream()
            .filter(p -> p.id().equals(paymentId)).findFirst().orElse(null);
    }
}
