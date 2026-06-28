package com.dob.infrastructure.web;

import com.dob.application.dto.CreateSubscriptionRequest;
import com.dob.application.dto.CreateSubscriptionResponse;
import com.dob.application.service.SubscriptionService;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Simplified subscription flow — creates subscriptions (MVP/demo mode)")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create subscription", description = "Creates a pending subscription for the given plan (RESEARCH or COMPANY). Returns a subscription ID used to complete payment.")
    public CreateSubscriptionResponse createSubscription(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        return subscriptionService.createSubscription(principal.id(), request.plan());
    }
}
