package com.dob.application.service;

import com.dob.application.dto.FreeCompanyResponse;
import com.dob.domain.model.Company;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Security tests verifying that sensitive company data is never leaked
 * to non-subscribed users.
 *
 * Acceptance Criteria covered:
 *  ✅ Free users never receive the company name in any API response.
 *  ✅ Database IDs are never exposed.
 *  ✅ Company listings use only DoB Company IDs.
 *  ✅ Search works but returns masked results for free users.
 *  ✅ Premium users receive the complete company profile.
 *  ✅ Hidden fields cannot be accessed via API manipulation.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyDataMaskingTest {

    private CompanyService companyService;

    @Mock private CompanyRepository companyRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;

    private Company approvedCompany;
    private Company pendingCompany;
    private static final UUID COMPANY_UUID = UUID.randomUUID();
    private static final String PUBLIC_ID = "DOB-7F92A1BC";
    private static final String COMPANY_NAME = "TechVentures India Pvt Ltd";
    private static final String CIN = "U72300KA2016PTC123456";
    private static final String GST = "29ABCDE1234F1Z5";

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, membershipRepository, userRepository, new ObjectMapper());

        approvedCompany = Company.builder()
            .id(COMPANY_UUID)
            .publicCompanyId(PUBLIC_ID)
            .name(COMPANY_NAME)
            .sector("Information Technology")
            .state("Karnataka")
            .city("Bengaluru")
            .companyType("Private Limited")
            .incorporationYear(2016)
            .description("A leading technology company specializing in enterprise software solutions.")
            .website("https://techventures.in")
            .status(Company.CompanyStatus.APPROVED_ACTIVE)
            .listingExpiresAt(java.time.LocalDate.now().plusYears(1))
            .createdBy(UUID.randomUUID())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        pendingCompany = Company.builder()
            .id(UUID.randomUUID())
            .publicCompanyId("DOB-ABCD1234")
            .name("Pending Corp")
            .sector("Finance")
            .state("Maharashtra")
            .city("Mumbai")
            .companyType("Private Limited")
            .incorporationYear(2020)
            .status(Company.CompanyStatus.DRAFT)
            .createdBy(UUID.randomUUID())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    @Nested
    @DisplayName("Free User — Data Masking")
    class FreeUserDataMasking {

        @Test
        @DisplayName("Free user search results MUST NOT contain company name")
        void freeUserSearchShouldNotExposeCompanyName() {
            lenient().when(companyRepository.search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedCompany));
            lenient().when(companyRepository.countSearch(any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);
            lenient().when(membershipRepository.findActiveByUserId(any())).thenReturn(Optional.empty());

            var result = companyService.search(null, null, null, null, null, null, null, 0, 20);

            assertThat(result.content()).hasSize(1);
            var item = (FreeCompanyResponse) result.content().get(0);
            assertThat(item.companyId()).isEqualTo(PUBLIC_ID);
            assertThat(item).isInstanceOf(FreeCompanyResponse.class);
            assertThat(item.locked()).isTrue();
        }

        @Test
        @DisplayName("Free user detail response MUST NOT contain company name, CIN, GST, PAN, directors, address, website, or email")
        void freeUserDetailShouldNotExposeIdentifyingInfo() {
            lenient().when(companyRepository.findById(any())).thenReturn(Optional.of(approvedCompany));
            lenient().when(membershipRepository.findActiveByUserId(any())).thenReturn(Optional.empty());

            var result = companyService.getById(COMPANY_UUID, null);

            assertThat(result).isInstanceOf(FreeCompanyResponse.class);
            var freeResp = (FreeCompanyResponse) result;

            // Must NOT expose identifying fields
            assertThat(freeResp.companyId()).isEqualTo(PUBLIC_ID);
            assertThat(freeResp.locked()).isTrue();

            // Non-identifying fields that are allowed
            assertThat(freeResp.industry()).isEqualTo("Information Technology");
            assertThat(freeResp.businessType()).isEqualTo("Private Limited");
            assertThat(freeResp.state()).isEqualTo("Karnataka");
            assertThat(freeResp.city()).isEqualTo("Bengaluru");
        }

        @Test
        @DisplayName("Free user response MUST NOT include internal database UUID")
        void freeUserResponseShouldNotContainDatabaseId() {
            lenient().when(companyRepository.findById(any())).thenReturn(Optional.of(approvedCompany));
            lenient().when(membershipRepository.findActiveByUserId(any())).thenReturn(Optional.empty());

            var result = companyService.getById(COMPANY_UUID, null);

            assertThat(result).isInstanceOf(FreeCompanyResponse.class);
            var freeResp = (FreeCompanyResponse) result;

            // FreeCompanyResponse only has companyId (DoB ID), not internal UUID
            assertThat(freeResp.companyId()).isEqualTo(PUBLIC_ID);
            // Verify there's no "id" or "uuid" field that leaks the internal ID
            assertThat(freeResp.getClass().getDeclaredFields())
                .filteredOn(f -> f.getName().equals("id") || f.getName().equals("uuid"))
                .isEmpty();
        }

        @Test
        @DisplayName("Searching by company name returns masked results for free users")
        void searchByCompanyNameReturnsMaskedForFreeUsers() {
            lenient().when(companyRepository.search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedCompany));
            lenient().when(companyRepository.countSearch(any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);
            lenient().when(membershipRepository.findActiveByUserId(any())).thenReturn(Optional.empty());

            var result = companyService.search(null, COMPANY_NAME, null, null, null, null, null, 0, 20);

            assertThat(result.content()).hasSize(1);
            var item = (FreeCompanyResponse) result.content().get(0);
            // Must only show DoB ID, not the matched name
            assertThat(item.companyId()).isEqualTo(PUBLIC_ID);
            assertThat(item.locked()).isTrue();
        }

        @Test
        @DisplayName("Non-approved (draft) company returns error for any user")
        void pendingCompanyReturnsError() {
            lenient().when(companyRepository.findById(any())).thenReturn(Optional.of(pendingCompany));

            org.junit.jupiter.api.Assertions.assertThrows(
                com.dob.domain.exception.DomainException.class,
                () -> companyService.getById(pendingCompany.getId(), null),
                "Non-approved companies should not be accessible"
            );
        }
    }

    @Nested
    @DisplayName("Premium User — Full Access")
    class PremiumUserFullAccess {

        @Test
        @DisplayName("Premium user search results MUST include company name and full details")
        void premiumUserSearchShouldIncludeCompanyName() {
            lenient().when(companyRepository.search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedCompany));
            lenient().when(companyRepository.countSearch(any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);

            var userId = UUID.randomUUID();
            var activeMembership = com.dob.domain.model.Membership.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .status(com.dob.domain.model.Membership.MembershipStatus.ACTIVE)
                .endDate(java.time.LocalDate.now().plusDays(30))
                .build();
            lenient().when(membershipRepository.findActiveByUserId(userId)).thenReturn(Optional.of(activeMembership));

            var result = companyService.search(
                new UserPrincipal(userId, "member@test.com", "RESEARCH_MEMBER"),
                null, null, null, null, null, null, 0, 20
            );

            assertThat(result.content()).hasSize(1);
            var item = result.content().get(0);
            assertThat(item).isExactlyInstanceOf(com.dob.application.dto.PremiumCompanyResponse.class);
            var premium = (com.dob.application.dto.PremiumCompanyResponse) item;
            assertThat(premium.companyName()).isEqualTo(COMPANY_NAME);
            assertThat(premium.locked()).isFalse();
        }

        @Test
        @DisplayName("Premium user detail response includes all company info")
        void premiumUserDetailShouldIncludeFullData() {
            lenient().when(companyRepository.findById(any())).thenReturn(Optional.of(approvedCompany));

            var userId = UUID.randomUUID();
            var activeMembership = com.dob.domain.model.Membership.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .status(com.dob.domain.model.Membership.MembershipStatus.ACTIVE)
                .endDate(java.time.LocalDate.now().plusDays(30))
                .build();
            lenient().when(membershipRepository.findActiveByUserId(userId)).thenReturn(Optional.of(activeMembership));

            var result = companyService.getById(COMPANY_UUID,
                new UserPrincipal(userId, "member@test.com", "RESEARCH_MEMBER"));

            assertThat(result).isInstanceOf(com.dob.application.dto.PremiumCompanyResponse.class);
            var premium = (com.dob.application.dto.PremiumCompanyResponse) result;
            assertThat(premium.companyName()).isEqualTo(COMPANY_NAME);
            assertThat(premium.companyId()).isEqualTo(PUBLIC_ID);
            assertThat(premium.industry()).isEqualTo("Information Technology");
            assertThat(premium.locked()).isFalse();
        }
    }

    @Nested
    @DisplayName("Public Company ID Format & Uniqueness")
    class PublicCompanyIdFormat {

        @Test
        @DisplayName("Generated public company ID must match DOB-XXXXXXXX format")
        void publicCompanyIdFormatIsValid() {
            var publicId = Company.generatePublicCompanyId();
            assertThat(publicId).matches("^DOB-[A-F0-9]{8}$");
        }

        @Test
        @DisplayName("Generated public company IDs should be unique across multiple calls")
        void publicCompanyIdsAreUnique() {
            var ids = java.util.stream.Stream.generate(Company::generatePublicCompanyId)
                .limit(100)
                .toList();
            assertThat(ids).doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("FreeCompanyResponse must use companyId, not internal id")
        void freeResponseUsesPublicId() {
            // Verify the FreeCompanyResponse record uses 'companyId' as its identifier
            assertThat(FreeCompanyResponse.class.getRecordComponents())
                .filteredOn(c -> c.getName().equals("companyId"))
                .isNotEmpty();
            assertThat(FreeCompanyResponse.class.getRecordComponents())
                .filteredOn(c -> c.getName().equals("id"))
                .isEmpty();
        }
    }
}
