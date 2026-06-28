package com.dob.infrastructure.web;

import com.dob.application.dto.MembershipDto;
import com.dob.domain.repository.MembershipRepository;
import com.dob.infrastructure.config.PricingProperties;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "Memberships", description = "Membership plan viewing and current membership status for research members")
public class MembershipController {

    private final MembershipRepository membershipRepository;
    private final PricingProperties pricing;

    @GetMapping("/me")
    @Operation(summary = "Get my membership", description = "Returns the currently authenticated user's active membership details including download limits and usage.")
    public MembershipDto getMyMembership(@AuthenticationPrincipal UserPrincipal principal) {
        return membershipRepository.findActiveByUserId(principal.id())
            .map(m -> MembershipDto.builder()
                .id(m.getId())
                .userId(m.getUserId())
                .planType(m.getPlanType())
                .status(m.getStatus().name())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .downloadLimit(m.getDownloadLimit())
                .downloadsUsed(m.getDownloadsUsed())
                .createdAt(m.getCreatedAt())
                .build())
            .orElse(null);
    }

    @GetMapping("/plans")
    @Operation(summary = "Get pricing plans", description = "Returns current pricing for membership (₹2500 + GST) and company listing (₹500 + GST) with GST breakdown.")
    public Map<String, Object> getPlans() {
        BigDecimal gstRate = pricing.getGstRate();
        BigDecimal membershipAmount = pricing.getMembership();
        BigDecimal membershipGst = membershipAmount.multiply(gstRate);
        BigDecimal listingAmount = pricing.getCompanyListing();
        BigDecimal listingGst = listingAmount.multiply(gstRate);

        return Map.of(
            "membership", Map.of(
                "amount", membershipAmount,
                "gst", membershipGst,
                "total", membershipAmount.add(membershipGst),
                "downloadLimit", 50,
                "duration", "MONTHLY"
            ),
            "companyListing", Map.of(
                "amount", listingAmount,
                "gst", listingGst,
                "total", listingAmount.add(listingGst),
                "duration", "YEARLY"
            )
        );
    }
}
