package com.dob.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CompanyApprovedEvent {
    private UUID companyId;
    private String companyName;
    private UUID approvedBy;
    private UUID createdBy;
    private Instant timestamp;
}
