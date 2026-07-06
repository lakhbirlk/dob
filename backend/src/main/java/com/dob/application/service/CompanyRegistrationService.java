package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Company;
import com.dob.domain.model.User;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyRegistrationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse register(CompanyRegistrationRequest request) {
        // ── Validate passwords match ──
        if (!request.password().equals(request.confirmPassword())) {
            throw new DomainException("Passwords do not match");
        }

        // ── Check duplicate email ──
        if (userRepository.existsByEmail(request.email())) {
            throw new DomainException("Email already registered");
        }

        // ── Create User ──
        var user = User.builder()
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .fullName(request.legalCompanyName())
            .phone(request.mobile())
            .role(User.UserRole.COMPANY_USER)
            .emailVerified(false)
            .active(true)
            .build();

        user = userRepository.save(user);
        log.info("Created company user: {} (ID: {})", user.getEmail(), user.getId());

        // ── Parse incorporation year ──
        Integer incorporationYear = null;
        if (request.dateOfIncorporation() != null && !request.dateOfIncorporation().isBlank()) {
            try {
                incorporationYear = LocalDate.parse(request.dateOfIncorporation(), DateTimeFormatter.ISO_DATE).getYear();
            } catch (Exception e) {
                try {
                    incorporationYear = Year.parse(request.dateOfIncorporation()).getValue();
                } catch (Exception ex) {
                    log.warn("Could not parse incorporation date: {}", request.dateOfIncorporation());
                }
            }
        }

        // ── Create Company ──
        var company = Company.builder()
            .publicCompanyId(Company.generatePublicCompanyId())
            .name(request.legalCompanyName())
            .brandName(request.brandName())
            .sector(request.industry())
            .state(request.state())
            .city(request.city())
            .companyType(request.companyType())
            .incorporationYear(incorporationYear)
            .description(request.businessDescription())
            .website(request.website())
            .status(Company.CompanyStatus.DRAFT)
            .createdBy(user.getId())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            // Registration fields
            .cin(request.cin())
            .gstin(request.gstNumber())
            .pan(request.pan())
            .tan(request.tan())
            .msmeRegistration(request.msmeRegistration())
            .startupIndiaRegistration(request.startupIndiaRegistration())
            .registeredAddressLine1(request.addressLine1())
            .registeredAddressLine2(request.addressLine2())
            .registeredCity(request.city())
            .registeredState(request.state())
            .registeredPinCode(request.pinCode())
            .registeredCountry(request.country())
            .officialEmail(request.officialEmail())
            .officialPhone(request.officialPhone())
            .linkedinProfile(request.linkedinProfile())
            .socialMediaLinks(request.socialMediaLinks())
            .authorizedRepName(request.authorizedRepName())
            .authorizedRepDesignation(request.authorizedRepDesignation())
            .authorizedRepMobile(request.authorizedRepMobile())
            .authorizedRepEmail(request.authorizedRepEmail())
            .authorizedRepIdentityProofUrl(request.authorizedRepIdentityProofUrl())
            .authorizedRepDigitalSignatureUrl(request.authorizedRepDigitalSignatureUrl())
            .annualTurnover(request.annualTurnover())
            .paidUpCapital(request.paidUpCapital())
            .authorizedCapital(request.authorizedCapital())
            .employeeCount(request.employeeCount())
            .financialYear(request.financialYear())
            .balanceSheetUrl(request.balanceSheetUrl())
            .auditorDetails(request.auditorDetails())
            .productsServices(request.productsServices())
            .businessDescription(request.businessDescription())
            .exportImportStatus(request.exportImportStatus())
            .numBranches(request.numBranches())
            .operationalStates(request.operationalStates())
            .certifications(request.certifications())
            .build();

        companyRepository.save(company);
        log.info("Created company listing: {} (ID: {} / Public: {})", company.getName(), company.getId(), company.getPublicCompanyId());

        // ── Generate JWT tokens ──
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        log.info("Company registration complete for: {}", request.email());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900)
            .user(toUserDto(user))
            .build();
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .pan(user.getPan())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .role(user.getRole().name())
            .emailVerified(user.isEmailVerified())
            .active(user.isActive())
            .build();
    }
}
