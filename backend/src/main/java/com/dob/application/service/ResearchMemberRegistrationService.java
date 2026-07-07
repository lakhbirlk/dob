package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Membership;
import com.dob.domain.model.ResearchMember;
import com.dob.domain.model.User;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.ResearchMemberRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.config.PricingProperties;
import com.dob.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResearchMemberRegistrationService {

    private final UserRepository userRepository;
    private final ResearchMemberRepository researchMemberRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final PricingProperties pricing;

    @Transactional
    public AuthResponse register(ResearchMemberRegistrationRequest request) {
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
            .fullName(request.fullName())
            .phone(request.mobile())
            .role(User.UserRole.RESEARCH_MEMBER)
            .emailVerified(false)
            .active(true)
            .build();

        user = userRepository.save(user);
        log.info("Created research member user: {} (ID: {})", user.getEmail(), user.getId());

        // ── Create ResearchMember profile ──
        var researchMember = ResearchMember.builder()
            .userId(user.getId())
            .fullName(request.fullName())
            .occupation(request.occupation())
            .organization(request.organization())
            .designation(request.designation())
            .researchPurpose(request.researchPurpose())
            .country(request.country())
            .state(request.state())
            .city(request.city())
            .industriesOfInterest(request.industriesOfInterest())
            .companySizePreference(request.companySizePreference())
            .notificationPreferences(request.notificationPreferences())
            .build();

        researchMemberRepository.save(researchMember);
        log.info("Created research member profile for user: {}", user.getEmail());

        // ── Auto-create Guest membership (2 one-time credits) ──
        PricingProperties.CreditPlan guest = pricing.getGuestPlan();
        LocalDate now = LocalDate.now();
        Membership guestMembership = Membership.builder()
            .id(UUID.randomUUID())
            .userId(user.getId())
            .planType(guest.getId())          // "GUEST"
            .status(Membership.MembershipStatus.ACTIVE)
            .startDate(now)
            .endDate(now.plusYears(10))       // one-time; long expiry so it never expires naturally
            .downloadLimit(guest.getCredits()) // 2
            .downloadsUsed(0)
            .build();

        membershipRepository.save(guestMembership);
        log.info("Assigned GUEST membership ({} credits) to user: {}", guest.getCredits(), user.getEmail());

        // ── Generate JWT tokens ──
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        log.info("Research member registration complete for: {}", request.email());

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
