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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "Memberships", description = "Membership plan viewing and current membership status for research members")
public class MembershipController {

    private final MembershipRepository membershipRepository;
    private final PricingProperties pricing;

    @GetMapping("/me")
    @Operation(summary = "Get my membership", description = "Returns the currently authenticated user's active membership details including credit limits and usage.")
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
    @Operation(summary = "Get pricing plans", description = "Returns the 5 research credit plans, the free guest plan, and company listing pricing with GST breakdown.")
    public Map<String, Object> getPlans() {
        BigDecimal gstRate = pricing.getGstRate();

        // Build credit plans list
        List<Map<String, Object>> creditPlans = new ArrayList<>();
        for (PricingProperties.CreditPlan cp : pricing.getCreditPlans()) {
            BigDecimal gst = cp.getAmount().multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
            Map<String, Object> plan = new LinkedHashMap<>();
            plan.put("id", cp.getId());
            plan.put("name", cp.getName());
            plan.put("credits", cp.getCredits());
            plan.put("amount", cp.getAmount());
            plan.put("gst", gst);
            plan.put("total", cp.getAmount().add(gst));
            plan.put("duration", "MONTHLY");
            creditPlans.add(plan);
        }

        // Guest plan (free one-time credits for new research members)
        PricingProperties.CreditPlan guest = pricing.getGuestPlan();
        Map<String, Object> guestPlan = new LinkedHashMap<>();
        guestPlan.put("id", guest.getId());
        guestPlan.put("name", guest.getName());
        guestPlan.put("credits", guest.getCredits());
        guestPlan.put("amount", guest.getAmount());
        guestPlan.put("gst", BigDecimal.ZERO);
        guestPlan.put("total", BigDecimal.ZERO);
        guestPlan.put("duration", "ONETIME");

        // Company listing
        BigDecimal listingGst = pricing.getCompanyListing().multiply(gstRate).setScale(2, RoundingMode.HALF_UP);

        return Map.of(
            "creditPlans", creditPlans,
            "guestPlan", guestPlan,
            "companyListing", Map.of(
                "amount", pricing.getCompanyListing(),
                "gst", listingGst,
                "total", pricing.getCompanyListing().add(listingGst),
                "duration", "YEARLY"
            )
        );
    }
}
