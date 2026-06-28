package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Generic paginated response wrapper")
public record PageDto<T>(
    @Schema(description = "Page content items")
    List<T> content,

    @Schema(description = "Current page number (0-based)", example = "0")
    int page,

    @Schema(description = "Page size", example = "20")
    int size,

    @Schema(description = "Total number of elements across all pages", example = "156")
    long totalElements,

    @Schema(description = "Total number of pages", example = "8")
    int totalPages
) {}
