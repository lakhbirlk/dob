package com.dob.application.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record VideoDto(
    UUID id,
    UUID companyId,
    String title,
    String description,
    String duration,
    String videoUrl,
    String thumbnailUrl,
    String uploadDate,
    String category,
    Integer views,
    Integer likes,
    Integer comments,
    Integer shares,
    String language,
    String resolution,
    String status,
    String transcriptSummary,
    String speaker,
    Integer durationSeconds,
    Instant uploadedAt
) {}
