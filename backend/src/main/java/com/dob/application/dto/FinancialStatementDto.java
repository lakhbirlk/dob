package com.dob.application.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record FinancialStatementDto(
    UUID id,
    String financialYear,
    BigDecimal revenue,
    BigDecimal profit,
    BigDecimal assets,
    BigDecimal liabilities,
    String fileUrl,
    String caName,
    String caMembershipNo,
    boolean verified,
    Instant uploadedAt
) {}
