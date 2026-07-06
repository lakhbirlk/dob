package com.dob.application.service;

import com.dob.TestTokenProvider;
import com.dob.application.dto.AuthResponse;
import com.dob.infrastructure.security.JwtProvider;
import com.dob.application.dto.CompanyRegistrationRequest;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Company;
import com.dob.domain.model.User;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Company Registration Service")
class CompanyRegistrationServiceTest {

    private CompanyRegistrationService service;

    @Mock private UserRepository userRepository;
    @Mock private CompanyRepository companyRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtProvider jwtProvider = TestTokenProvider.instance();

    private CompanyRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        service = new CompanyRegistrationService(userRepository, companyRepository, passwordEncoder, jwtProvider);

        validRequest = new CompanyRegistrationRequest(
            "company@test.com",      // email
            "9876543210",            // mobile
            "SecurePass123",         // password
            "SecurePass123",         // confirmPassword
            "TechVentures India Pvt Ltd", // legalCompanyName
            "TechVentures",          // brandName
            "Private Limited",       // companyType
            "Information Technology", // industry
            "Software Development",  // businessCategory
            "2015-06-15",            // dateOfIncorporation
            "U72300KA2016PTC123456", // cin
            "29ABCDE1234F1Z5",       // gstNumber
            "AABCT1234E",            // pan
            "BANP12345A",            // tan
            "UDYAM-MH-01-0001234",   // msmeRegistration
            "DIPP12345",             // startupIndiaRegistration
            "123, MG Road",          // addressLine1
            "Indiranagar",           // addressLine2
            "Bengaluru",             // city
            "Karnataka",             // state
            "560038",                // pinCode
            "India",                 // country
            "contact@company.com",   // officialEmail
            "9876543210",            // officialPhone
            "https://company.com",   // website
            "https://linkedin.com/company/company", // linkedinProfile
            null,                    // socialMediaLinks
            "Rajesh Kumar",          // authorizedRepName
            "Director",              // authorizedRepDesignation
            "9876543211",            // authorizedRepMobile
            "rajesh@company.com",    // authorizedRepEmail
            null,                    // authorizedRepIdentityProofUrl
            null,                    // authorizedRepDigitalSignatureUrl
            "₹50Cr-100Cr",          // annualTurnover
            "₹1Cr",                 // paidUpCapital
            "₹5Cr",                 // authorizedCapital
            200,                     // employeeCount
            "2024-25",              // financialYear
            null,                    // balanceSheetUrl
            "ABC & Co, Chartered Accountants", // auditorDetails
            "Enterprise SaaS platform", // productsServices
            "Leading enterprise software company", // businessDescription
            "Both",                  // exportImportStatus
            5,                       // numBranches
            "Karnataka, Maharashtra, Tamil Nadu", // operationalStates
            "ISO 9001:2015, ISO 27001", // certifications
            true,                    // acceptTerms
            true                     // acceptPrivacy
        );

        // No Mockito stubbing needed — using real JwtProvider
    }

    @Nested
    @DisplayName("Successful Registration")
    class SuccessfulRegistration {

        @Test
        @DisplayName("Should create user with COMPANY_USER role and return auth response")
        void shouldRegisterCompanyUser() {
            lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
            lenient().when(userRepository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                return User.builder()
                    .id(UUID.randomUUID())
                    .email(u.getEmail())
                    .passwordHash(u.getPasswordHash())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .emailVerified(false)
                    .active(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            });
            lenient().when(companyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AuthResponse response = service.register(validRequest);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.accessToken()).contains(".");  // JWT format
            assertThat(response.refreshToken()).isNotBlank();
            assertThat(response.refreshToken()).contains(".");
            assertThat(response.tokenType()).isEqualTo("Bearer");
            assertThat(response.user()).isNotNull();
            assertThat(response.user().email()).isEqualTo("company@test.com");
        }

        @Test
        @DisplayName("Should hash the password before storing")
        void shouldHashPassword() {
            lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
            lenient().when(userRepository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                assertThat(u.getPasswordHash()).isNotEqualTo("SecurePass123");
                assertThat(passwordEncoder.matches("SecurePass123", u.getPasswordHash())).isTrue();
                return User.builder()
                    .id(UUID.randomUUID())
                    .email(u.getEmail())
                    .passwordHash(u.getPasswordHash())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .emailVerified(false)
                    .active(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            });
            lenient().when(companyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.register(validRequest);
        }

        @Test
        @DisplayName("Should create company with PENDING status")
        void shouldCreatePendingCompany() {
            lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
            lenient().when(userRepository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                return User.builder()
                    .id(UUID.randomUUID())
                    .email(u.getEmail())
                    .passwordHash(u.getPasswordHash())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .emailVerified(false)
                    .active(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            });
            lenient().when(companyRepository.save(any())).thenAnswer(inv -> {
                Company c = inv.getArgument(0);
                assertThat(c.getStatus()).isEqualTo(Company.CompanyStatus.DRAFT);
                assertThat(c.getName()).isEqualTo("TechVentures India Pvt Ltd");
                assertThat(c.getBrandName()).isEqualTo("TechVentures");
                assertThat(c.getCin()).isEqualTo("U72300KA2016PTC123456");
                assertThat(c.getGstin()).isEqualTo("29ABCDE1234F1Z5");
                assertThat(c.getPan()).isEqualTo("AABCT1234E");
                assertThat(c.getIncorporationYear()).isEqualTo(2015);
                assertThat(c.getProductsServices()).isEqualTo("Enterprise SaaS platform");
                return c;
            });

            service.register(validRequest);
        }

        @Test
        @DisplayName("Should assign COMPANY_USER role to the created user")
        void shouldAssignCompanyUserRole() {
            lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
            lenient().when(userRepository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                assertThat(u.getRole()).isEqualTo(User.UserRole.COMPANY_USER);
                return User.builder()
                    .id(UUID.randomUUID())
                    .email(u.getEmail())
                    .passwordHash(u.getPasswordHash())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .emailVerified(false)
                    .active(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            });
            lenient().when(companyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.register(validRequest);
        }
    }

    @Nested
    @DisplayName("Validation & Error Handling")
    class ValidationAndErrorHandling {

        @Test
        @DisplayName("Should reject registration when email is already taken")
        void shouldRejectDuplicateEmail() {
            lenient().when(userRepository.existsByEmail("company@test.com")).thenReturn(true);

            assertThrows(DomainException.class, () -> service.register(validRequest),
                "Email already registered");
        }

        @Test
        @DisplayName("Should reject registration when passwords do not match")
        void shouldRejectPasswordMismatch() {
            // Use a minimal request with mismatched passwords
            var badRequest = new CompanyRegistrationRequest(
                "company@test.com", "9876543210", "Password1", "Password2",
                "Test Corp", null, "Private Limited", "Technology", "Software",
                "2015-06-15", null, null, null, null, null, null,
                "123 Main St", null, "Mumbai", "Maharashtra", "400001", "India",
                null, null, null, null, null,
                "John Doe", "Director", "9876543211", "john@test.com", null, null,
                null, null, null, null,
                null, null, null, null,
                "IT services", null, null, null, null,
                true, true
            );

            assertThrows(DomainException.class, () -> service.register(badRequest));
        }

        @Test
        @DisplayName("Should generate a valid public company ID")
        void shouldGeneratePublicCompanyId() {
            lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
            lenient().when(userRepository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                return User.builder()
                    .id(UUID.randomUUID())
                    .email(u.getEmail())
                    .passwordHash(u.getPasswordHash())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .emailVerified(false)
                    .active(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            });
            lenient().when(companyRepository.save(any())).thenAnswer(inv -> {
                Company c = inv.getArgument(0);
                assertThat(c.getPublicCompanyId()).matches("^DOB-[A-F0-9]{8}$");
                return c;
            });

            service.register(validRequest);
        }
    }
}
