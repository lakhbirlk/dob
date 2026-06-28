package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ResearchMember {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String occupation;
    private String organization;
    private String designation;
    private String researchPurpose;
    private String country;
    private String state;
    private String city;
    private String industriesOfInterest;
    private String companySizePreference;
    private String notificationPreferences;
    private Instant createdAt;
    private Instant updatedAt;
}
