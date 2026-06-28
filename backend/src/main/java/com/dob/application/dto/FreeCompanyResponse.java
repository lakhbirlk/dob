package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Company response returned to free (non-subscribed) users.
 * Contains only non-identifying information — never exposes company name,
 * CIN, GST, PAN, director names, contact details, address, or internal IDs.
 */
@Schema(description = "Masked company information for free users — never reveals identifying data")
public record FreeCompanyResponse(
    @Schema(description = "Public DoB Company ID", example = "DOB-7F92A1BC")
    String companyId,

    @Schema(description = "Industry sector", example = "Information Technology")
    String industry,

    @Schema(description = "Business/company type", example = "Private Limited")
    String businessType,

    @Schema(description = "Registered state", example = "Karnataka")
    String state,

    @Schema(description = "Registered city", example = "Bengaluru")
    String city,

    @Schema(description = "Company age in years", example = "8")
    Integer companyAge,

    @Schema(description = "Employee range", example = "200-500")
    String employeeRange,

    @Schema(description = "Revenue range", example = "₹50Cr-100Cr")
    String revenueRange,

    @Schema(description = "Risk score", example = "Medium")
    String riskScore,

    @Schema(description = "Whether the company profile is CA-verified", example = "true")
    boolean verified,

    @Schema(description = "Always true for free users — indicates data is masked")
    boolean locked,

    @Schema(description = "Short non-identifying business summary")
    String summary
) {
    // Factory: build from masked data
    public static FreeCompanyResponse locked(String companyId, String industry, String businessType,
                                              String state, String city, Integer companyAge,
                                              String employeeRange, String revenueRange,
                                              String riskScore, boolean verified, String summary) {
        return new FreeCompanyResponse(companyId, industry, businessType, state, city,
            companyAge, employeeRange, revenueRange, riskScore, verified, true, summary);
    }
}
