package com.dob.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "pricing")
public class PricingProperties {
    private BigDecimal membership = new BigDecimal("2500.00");
    private BigDecimal companyListing = new BigDecimal("500.00");
    private BigDecimal gstRate = new BigDecimal("0.18");
}
