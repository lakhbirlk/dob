package com.dob.application.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CertificateDto(
    UUID id,
    UUID companyId,
    String certificateName,
    String certificateNumber,
    String issuingAuthority,
    String issueDate,
    String expiryDate,
    String status,
    String verificationUrl,
    String pdfUrl,
    String thumbnailUrl,
    String description,
    String certificateUrl,
    String caName,
    String caMembershipNo,
    boolean verified,
    Instant uploadedAt
) {}
