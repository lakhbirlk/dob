package com.dob.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "pricing")
public class PricingProperties {
    private BigDecimal companyListing = new BigDecimal("500.00");
    private BigDecimal gstRate = new BigDecimal("0.18");

    /** Research member credit-based plans */
    private List<CreditPlan> creditPlans = List.of(
        new CreditPlan("CREDITS_3",  "Starter",   3,  new BigDecimal("1500.00")),
        new CreditPlan("CREDITS_5",  "Basic",     5,  new BigDecimal("2000.00")),
        new CreditPlan("CREDITS_10", "Pro",      10,  new BigDecimal("3000.00")),
        new CreditPlan("CREDITS_20", "Business", 20,  new BigDecimal("4000.00")),
        new CreditPlan("CREDITS_30", "Enterprise", 30, new BigDecimal("5000.00"))
    );

    /** Free guest plan — one-time 2 credits for all new research members */
    private CreditPlan guestPlan = new CreditPlan("GUEST", "Guest", 2, BigDecimal.ZERO);

    /** Credits required to unlock one company */
    private int creditCost = 1;

    @Getter @Setter
    public static class CreditPlan {
        private String id;
        private String name;
        private int credits;
        private BigDecimal amount;

        public CreditPlan() {}

        public CreditPlan(String id, String name, int credits, BigDecimal amount) {
            this.id = id;
            this.name = name;
            this.credits = credits;
            this.amount = amount;
        }
    }

    /** Lookup a credit plan by its id */
    public CreditPlan getCreditPlan(String planId) {
        return creditPlans.stream()
            .filter(p -> p.getId().equals(planId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown credit plan: " + planId));
    }
}
