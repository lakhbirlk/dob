package com.dob.application.dto;

import java.time.Instant;
import java.util.UUID;

public record VideoDto(
    UUID id,
    UUID companyId,
    String videoUrl,
    String thumbnailUrl,
    String title,
    Integer durationSeconds,
    Instant uploadedAt
) {}
