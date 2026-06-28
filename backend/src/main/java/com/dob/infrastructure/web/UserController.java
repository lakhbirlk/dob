package com.dob.infrastructure.web;

import com.dob.application.dto.UserDto;
import com.dob.application.dto.UserMembershipResponse;
import com.dob.application.service.AuthService;
import com.dob.application.service.SubscriptionService;
import com.dob.domain.exception.DomainException;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Authenticated user profile management endpoints")
public class UserController {

    private final AuthService authService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/me")
    @Operation(summary = "Get profile", description = "Returns the currently authenticated user's profile.")
    public UserDto getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return authService.getCurrentUser(principal);
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile", description = "Update the authenticated user's name and/or phone number.")
    public UserDto updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                  @RequestBody Map<String, String> body) {
        return authService.updateProfile(principal,
            body.get("fullName"),
            body.get("phone"));
    }

    @PutMapping("/pan")
    @Operation(summary = "Update PAN", description = "Update the authenticated user's PAN card number. Triggers verification.")
    public UserDto updatePan(@AuthenticationPrincipal UserPrincipal principal,
                              @RequestBody Map<String, String> body) {
        String pan = body.get("pan");
        if (pan == null || pan.isEmpty()) {
            throw new DomainException("PAN is required");
        }
        return authService.updatePan(principal, pan.toUpperCase());
    }

    @GetMapping("/me/membership")
    @Operation(summary = "Get my membership", description = "Returns the authenticated user's current active membership details (plan, status, expiry).")
    public UserMembershipResponse getMembership(@AuthenticationPrincipal UserPrincipal principal) {
        return subscriptionService.getUserMembership(principal.id());
    }
}
