package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.DomainException;
import com.dob.domain.exception.UnauthorizedException;
import com.dob.domain.model.Membership;
import com.dob.domain.model.User;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.config.PricingProperties;
import com.dob.infrastructure.security.JwtProvider;
import com.dob.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MembershipRepository membershipRepository;
    private final PricingProperties pricing;
    private final RedisTemplate<String, String> redisTemplate;

    // In-memory fallback for local dev when Redis is unavailable
    private final Map<String, String> localTokenStore = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider,
                       MembershipRepository membershipRepository,
                       PricingProperties pricing,
                       @Autowired(required = false) RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.membershipRepository = membershipRepository;
        this.pricing = pricing;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DomainException("Email already registered");
        }

        var user = User.builder()
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .fullName(request.fullName())
            .phone(request.phone())
            .role(User.UserRole.RESEARCH_MEMBER)
            .emailVerified(false)
            .active(true)
            .build();

        user = userRepository.save(user);

        // Auto-create Guest membership for new research members
        if (user.getRole() == User.UserRole.RESEARCH_MEMBER) {
            PricingProperties.CreditPlan guest = pricing.getGuestPlan();
            LocalDate now = LocalDate.now();
            Membership guestMembership = Membership.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .planType(guest.getId())
                .status(Membership.MembershipStatus.ACTIVE)
                .startDate(now)
                .endDate(now.plusYears(10))
                .downloadLimit(guest.getCredits())
                .downloadsUsed(0)
                .build();
            membershipRepository.save(guestMembership);
            log.info("Assigned GUEST membership ({} credits) to user: {}", guest.getCredits(), user.getEmail());
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        storeRefreshToken(user.getId(), refreshToken);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900)
            .user(toDto(user))
            .build();
    }

    public AuthResponse login(AuthRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // No OTP — direct login with email+password
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        storeRefreshToken(user.getId(), refreshToken);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900)
            .user(toDto(user))
            .build();
    }

    public AuthResponse refresh(String refreshToken) {
        var claims = jwtProvider.validateToken(refreshToken);
        String userId = jwtProvider.getUserId(claims);

        // Verify refresh token exists
        String storedToken = getStoredToken(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        var user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Rotate refresh token
        removeStoredToken(userId);
        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());
        storeRefreshToken(user.getId(), newRefreshToken);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(900)
            .user(toDto(user))
            .build();
    }

    public void logout(UUID userId) {
        removeStoredToken(userId.toString());
    }

    public UserDto getCurrentUser(UserPrincipal principal) {
        var user = userRepository.findById(principal.id())
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserDto updatePan(UserPrincipal principal, String pan) {
        var user = userRepository.findById(principal.id())
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        user.updatePan(pan);
        user = userRepository.save(user);
        return toDto(user);
    }

    @Transactional
    public UserDto updateProfile(UserPrincipal principal, String fullName, String phone) {
        var user = userRepository.findById(principal.id())
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        user.updateProfile(fullName, phone);
        user = userRepository.save(user);
        return toDto(user);
    }

    private void storeRefreshToken(UUID userId, String token) {
        String key = "refresh:" + userId;
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, token, Duration.ofDays(7));
        } else {
            localTokenStore.put(key, token);
        }
    }

    private String getStoredToken(String userId) {
        String key = "refresh:" + userId;
        if (redisTemplate != null) {
            return redisTemplate.opsForValue().get(key);
        }
        return localTokenStore.get(key);
    }

    private void removeStoredToken(String userId) {
        String key = "refresh:" + userId;
        if (redisTemplate != null) {
            redisTemplate.delete(key);
        } else {
            localTokenStore.remove(key);
        }
    }

    private UserDto toDto(User user) {
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
