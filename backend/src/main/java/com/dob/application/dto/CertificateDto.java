package com.dob.application.dto;

import java.time.Instant;
import java.util.UUID;

public record CertificateDto(
    UUID id,
    UUID companyId,
    String certificateUrl,
    String caName,
    String caMembershipNo,
    boolean verified,
    Instant uploadedAt
) {}
