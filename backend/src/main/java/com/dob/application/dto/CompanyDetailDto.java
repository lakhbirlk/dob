package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Full company detail including profile, financials, certificates, and videos")
public record CompanyDetailDto(
    @Schema(description = "Company summary")
    CompanyDto company,

    @Schema(description = "Extended company profile (executives, social links)")
    CompanyProfileDto profile,

    @Schema(description = "Historical financial statements (CA-certified)")
    List<FinancialStatementDto> financials,

    @Schema(description = "CA certificates verifying the financial data")
    List<CertificateDto> certificates,

    @Schema(description = "Company introduction/promotional videos")
    List<VideoDto> videos
) {}
