package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CompanyDocument {
    private UUID id;
    private UUID companyId;
    private String documentType;
    private String fileUrl;
    private Instant uploadedAt;
}
