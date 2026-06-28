package com.dob.application.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CompanyProfileDto(
    String about,
    Integer employeeCount,
    String revenueRange,
    List<Map<String, String>> keyExecutives,
    Map<String, String> socialLinks
) {}
