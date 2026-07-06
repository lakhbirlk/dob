package com.dob.domain.model;

import com.dob.application.dto.FreeCompanyResponse;
import com.dob.application.dto.PremiumCompanyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests verifying JSON serialization of company DTOs does not leak hidden fields.
 *
 * Acceptance Criteria:
 *  ✅ Free user response JSON never contains company name, CIN, GST, PAN, etc.
 *  ✅ Premium user response JSON includes all business information.
 *  ✅ Hidden fields cannot be accessed via browser inspection or API response inspection.
 */
class CompanySerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("FreeCompanyResponse serialized JSON must NOT contain any identifying fields")
    void freeResponseSerializationShouldNotLeakFields() throws Exception {
        var freeResp = FreeCompanyResponse.locked(
            "DOB-7F92A1BC",
            "Information Technology",
            "Private Limited",
            "Karnataka",
            "Bengaluru",
            8,
            "200-500",
            "₹50Cr-100Cr",
            "Medium",
            true,
            "A tech firm"
        );

        String json = objectMapper.writeValueAsString(freeResp);

        // Must contain allowed non-identifying fields
        assertThat(json).contains("DOB-7F92A1BC");
        assertThat(json).contains("Information Technology");
        assertThat(json).contains("Private Limited");
        assertThat(json).contains("Karnataka");
        assertThat(json).contains("Bengaluru");
        assertThat(json).contains("locked");
        assertThat(json).contains("true");  // locked=true

        // MUST NOT contain any identifying information (field names in JSON)
        assertThat(json).doesNotContain("\"companyName\"");
        assertThat(json).doesNotContain("\"cin\"");
        assertThat(json).doesNotContain("\"gstin\"");
        assertThat(json).doesNotContain("\"pan\"");
        assertThat(json).doesNotContain("\"registrationNumber\"");
        assertThat(json).doesNotContain("\"address\"");
        assertThat(json).doesNotContain("\"email\"");
        assertThat(json).doesNotContain("\"website\"");
        assertThat(json).doesNotContain("\"keyExecutives\"");
        assertThat(json).doesNotContain("\"shareholding\"");
        assertThat(json).doesNotContain("\"financials\"");
        assertThat(json).doesNotContain("\"certificates\"");
        assertThat(json).doesNotContain("\"videos\"");
        assertThat(json).doesNotContain("\"aiAnalysis\"");
        assertThat(json).doesNotContain("\"riskReportUrl\"");
        assertThat(json).doesNotContain("\"canDownload\"");

        // MUST NOT contain internal UUID or bare "id" key at the top level
        assertThat(json).doesNotContain("\"id\" :");
    }

    @Test
    @DisplayName("PremiumCompanyResponse serialized JSON must include full business information")
    void premiumResponseSerializationShouldIncludeFullData() throws Exception {
        var premiumResp = new PremiumCompanyResponse(
            "DOB-7F92A1BC",
            "TechVentures India Pvt Ltd",
            "https://cdn.example.com/logo.png",        // logoUrl
            "U72300KA2016PTC123456",                   // cin
            "29ABCDE1234F1Z5",                         // gstin
            "AABCT1234E",                              // pan
            "12345678",                                // registrationNumber
            null,                                      // companyRegistrationNumber
            "Information Technology",                  // industry
            "Enterprise Software",                     // subSector
            "Private Limited",                         // businessType
            null,                                      // businessModel
            null,                                      // companyStage
            2016,                                      // incorporationYear
            8,                                         // companyAge
            "Karnataka",                               // state
            "Bengaluru",                               // city
            "123, MG Road, Indiranagar",              // address
            null,                                      // headquarter
            null,                                      // numBranches
            "contact@techventures.in",                 // email
            null,                                      // phoneNumber
            "https://techventures.in",                 // website
            null,                                      // linkedinUrl
            null,                                      // twitterUrl
            "200-500",                                 // employeeRange
            null,                                      // employeeCount
            null,                                      // annualRevenue
            "₹50Cr-100Cr",                            // revenueRange
            null,                                      // totalFunding
            null,                                      // investors
            "Medium",                                  // riskScore
            true,                                      // verified
            false,                                     // locked
            "A leading enterprise software company.",  // description
            null,                                      // mission
            null,                                      // vision
            null,                                      // cultureSummary
            List.of(Map.of("name", "Rajesh Kumar", "designation", "CEO")),  // keyExecutives
            null,                                      // ceoName
            null,                                      // ctoName
            null,                                      // founders
            null,                                      // products
            null,                                      // services
            null,                                      // technologiesUsed
            null,                                      // certificationsOverview
            null,                                      // awards
            List.of(Map.of("shareholder", "Rajesh Kumar", "percentage", 60)), // shareholding
            List.of(),                                 // financials
            List.of(),                                 // certificates
            List.of(),                                 // videos
            "Strong financial position with consistent growth.", // aiAnalysis
            "https://cdn.example.com/reports/risk-dob-7f92a1bc.pdf", // riskReportUrl
            true,                                      // canDownload
            "/company/DOB-7F92A1BC",                   // companyUrl
            "APPROVED",                                // status
            null,                                      // dashboardStatus
            java.time.Instant.now(),                   // createdAt
            java.time.Instant.now()                    // updatedAt
        );

        String json = objectMapper.writeValueAsString(premiumResp);

        // Premium response must include company name
        assertThat(json).contains("TechVentures India Pvt Ltd");
        assertThat(json).contains("DOB-7F92A1BC");
        assertThat(json).contains("U72300KA2016PTC123456");  // CIN
        assertThat(json).contains("29ABCDE1234F1Z5");         // GSTIN
        assertThat(json).contains("AABCT1234E");              // PAN
        assertThat(json).contains("123, MG Road, Indiranagar"); // Address
        assertThat(json).contains("contact@techventures.in"); // Email
        assertThat(json).contains("https://techventures.in"); // Website
        assertThat(json).contains("Rajesh Kumar");            // Director
        assertThat(json).contains("locked");
        assertThat(json).contains("false"); // locked=false for premium
    }

    @Test
    @DisplayName("FreeCompanyResponse and PremiumCompanyResponse have completely different field sets")
    void freeAndPremiumResponsesHaveDifferentFields() {
        var freeFields = java.util.Arrays.stream(FreeCompanyResponse.class.getRecordComponents())
            .map(java.lang.reflect.RecordComponent::getName)
            .toList();

        var premiumFields = java.util.Arrays.stream(PremiumCompanyResponse.class.getRecordComponents())
            .map(java.lang.reflect.RecordComponent::getName)
            .toList();

        // Free response should NOT have premium-only fields
        assertThat(freeFields).doesNotContain("companyName", "cin", "gstin", "pan",
            "registrationNumber", "address", "email", "website", "keyExecutives",
            "shareholding", "financials", "certificates", "videos", "aiAnalysis",
            "riskReportUrl", "canDownload", "companyUrl", "description", "logoUrl");

        // Premium response should have these fields
        assertThat(premiumFields).contains("companyName", "cin", "gstin", "pan",
            "address", "email", "website", "keyExecutives", "shareholding",
            "financials", "certificates", "videos", "canDownload");

        // Both should have companyId (the public DoB ID)
        assertThat(freeFields).contains("companyId");
        assertThat(premiumFields).contains("companyId");
    }
}
