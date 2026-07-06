package com.dob.application.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record FinancialStatementDto(
    UUID id,
    String financialYear,
    // Documents
    String balanceSheetUrl,
    String profitLossUrl,
    String cashFlowUrl,
    String auditorReportUrl,
    String taxFilingUrl,
    // Metadata
    String uploadDate,
    String uploadedBy,
    String status,
    String version,
    String fileSize,
    String fileType,
    String downloadUrl,
    // Key financial metrics
    BigDecimal revenue,
    BigDecimal expenses,
    BigDecimal ebitda,
    BigDecimal netProfit,
    BigDecimal assets,
    BigDecimal liabilities,
    BigDecimal equity,
    BigDecimal operatingCashFlow,
    BigDecimal capex,
    BigDecimal debt,
    // Legacy fields
    String fileUrl,
    String caName,
    String caMembershipNo,
    boolean verified,
    Instant uploadedAt
) {}
