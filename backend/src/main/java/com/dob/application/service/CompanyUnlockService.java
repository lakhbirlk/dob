package com.dob.application.service;

import com.dob.application.dto.*;
import com.dob.domain.exception.CompanyNotFoundException;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.*;
import com.dob.domain.repository.*;
import com.dob.infrastructure.config.PricingProperties;
import com.dob.infrastructure.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyUnlockService {

    private final CompanyRepository companyRepository;
    private final MembershipRepository membershipRepository;
    private final UnlockedCompanyRepository unlockedCompanyRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final AuditService auditService;
    private final AuditLogRepository auditLogRepository;
    private final PricingProperties pricing;

    /**
     * Unlock a company for the authenticated research member.
     * Deducts one credit and records the unlock transaction atomically.
     */
    @Transactional
    public UnlockCompanyResponse unlockCompany(UUID companyId, UserPrincipal principal, HttpServletRequest request) {
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String ipAddress = request != null ? request.getRemoteAddr() : null;
        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        // 1. Validate company exists
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException(companyId.toString()));

        // 2. Check user has active membership
        var membership = membershipRepository.findActiveByUserId(principal.id())
            .orElseThrow(() -> new DomainException("No active membership found. Please subscribe to a plan."));

        // 3. Check not already unlocked
        if (unlockedCompanyRepository.existsByMemberIdAndCompanyId(principal.id(), companyId)) {
            auditService.log(principal.id(), companyId, "COMPANY_UNLOCK", "FAILED",
                "Duplicate unlock attempt — company already unlocked", ipAddress, userAgent, transactionId);
            return UnlockCompanyResponse.builder()
                .transactionId(transactionId)
                .companyId(companyId)
                .status("ALREADY_UNLOCKED")
                .message("Company already unlocked")
                .build();
        }

        int creditCost = pricing.getCreditCost();
        int availableCredits = membership.getDownloadLimit() - membership.getDownloadsUsed();

        // 4. Check sufficient credits
        if (availableCredits < creditCost) {
            // Record failed transaction
            creditTransactionRepository.save(CreditTransaction.builder()
                .id(UUID.randomUUID())
                .memberId(principal.id())
                .companyId(companyId)
                .creditsUsed(creditCost)
                .transactionType("UNLOCK")
                .balanceBefore(membership.getDownloadsUsed())
                .balanceAfter(membership.getDownloadsUsed())
                .status("FAILED")
                .transactionId(transactionId)
                .createdAt(Instant.now())
                .build());

            auditService.log(principal.id(), companyId, "COMPANY_UNLOCK", "FAILED",
                "Insufficient credits — required " + creditCost + ", available " + availableCredits,
                ipAddress, userAgent, transactionId);

            return UnlockCompanyResponse.builder()
                .transactionId(transactionId)
                .companyId(companyId)
                .creditsUsed(0)
                .remainingCredits(availableCredits)
                .status("INSUFFICIENT_CREDITS")
                .message("Insufficient credits. Required: " + creditCost + ", Available: " + availableCredits + ". Please upgrade your plan.")
                .build();
        }

        // 5. Deduct credit
        int balanceBefore = membership.getDownloadsUsed();
        membership.incrementDownloads();
        membershipRepository.save(membership);

        // 6. Create unlock record
        unlockedCompanyRepository.save(UnlockedCompany.builder()
            .id(UUID.randomUUID())
            .memberId(principal.id())
            .companyId(companyId)
            .creditsUsed(creditCost)
            .unlockedAt(Instant.now())
            .unlockedBy(principal.id())
            .build());

        // 7. Create credit transaction record
        creditTransactionRepository.save(CreditTransaction.builder()
            .id(UUID.randomUUID())
            .memberId(principal.id())
            .companyId(companyId)
            .creditsUsed(creditCost)
            .transactionType("UNLOCK")
            .balanceBefore(balanceBefore)
            .balanceAfter(membership.getDownloadsUsed())
            .status("SUCCESS")
            .transactionId(transactionId)
            .createdAt(Instant.now())
            .build());

        // 8. Audit log
        auditService.log(principal.id(), companyId, "COMPANY_UNLOCK", "SUCCESS",
            "Unlocked company " + company.getName() + " using " + creditCost + " credit(s). Remaining: " +
                (membership.getDownloadLimit() - membership.getDownloadsUsed()),
            ipAddress, userAgent, transactionId);

        log.info("Company {} unlocked by member {}. Credits remaining: {}",
            companyId, principal.id(), membership.getDownloadLimit() - membership.getDownloadsUsed());

        return UnlockCompanyResponse.builder()
            .transactionId(transactionId)
            .companyId(companyId)
            .creditsUsed(creditCost)
            .remainingCredits(membership.getDownloadLimit() - membership.getDownloadsUsed())
            .status("SUCCESS")
            .message("Company unlocked successfully!")
            .build();
    }

    /**
     * Check if a specific company is unlocked by the current user.
     */
    public UnlockStatusResponse checkUnlockStatus(UUID companyId, UserPrincipal principal) {
        var unlocked = unlockedCompanyRepository.findByMemberIdAndCompanyId(principal.id(), companyId);
        return unlocked.map(u -> UnlockStatusResponse.builder()
                .companyId(companyId)
                .unlocked(true)
                .unlockedAt(u.getUnlockedAt())
                .creditsUsed(u.getCreditsUsed())
                .build())
            .orElseGet(() -> UnlockStatusResponse.builder()
                .companyId(companyId)
                .unlocked(false)
                .build());
    }

    /**
     * Return a batch unlock status map for multiple company IDs.
     */
    public List<UnlockStatusResponse> checkBatchUnlockStatus(List<UUID> companyIds, UserPrincipal principal) {
        return companyIds.stream()
            .map(id -> checkUnlockStatus(id, principal))
            .toList();
    }

    /**
     * List all companies unlocked by the current member.
     */
    public List<UnlockedCompanyDto> getUnlockedCompanies(UserPrincipal principal, int page, int size) {
        var unlockedList = unlockedCompanyRepository.findByMemberId(principal.id(), page, size);
        return unlockedList.stream().map(u -> {
            var company = companyRepository.findById(u.getCompanyId()).orElse(null);
            return UnlockedCompanyDto.builder()
                .companyId(u.getCompanyId())
                .publicCompanyId(company != null ? company.getPublicCompanyId() : null)
                .companyName(company != null ? company.getName() : "Unknown")
                .sector(company != null ? company.getSector() : null)
                .city(company != null ? company.getCity() : null)
                .state(company != null ? company.getState() : null)
                .creditsUsed(u.getCreditsUsed())
                .unlockedAt(u.getUnlockedAt())
                .build();
        }).toList();
    }

    /**
     * Get total count of unlocked companies for the member.
     */
    public long getUnlockedCompaniesCount(UserPrincipal principal) {
        return unlockedCompanyRepository.countByMemberId(principal.id());
    }

    /**
     * Get credit transaction history for the member.
     */
    public List<CreditTransactionDto> getCreditHistory(UserPrincipal principal, int page, int size) {
        var transactions = creditTransactionRepository.findByMemberId(principal.id(), page, size);
        return transactions.stream().map(t -> {
            var company = t.getCompanyId() != null ? companyRepository.findById(t.getCompanyId()).orElse(null) : null;
            return CreditTransactionDto.builder()
                .id(t.getId())
                .companyId(t.getCompanyId())
                .companyName(company != null ? company.getName() : null)
                .creditsUsed(t.getCreditsUsed())
                .transactionType(t.getTransactionType())
                .balanceBefore(t.getBalanceBefore())
                .balanceAfter(t.getBalanceAfter())
                .status(t.getStatus())
                .transactionId(t.getTransactionId())
                .createdAt(t.getCreatedAt())
                .build();
        }).toList();
    }

    /**
     * Get total credit history count.
     */
    public long getCreditHistoryCount(UserPrincipal principal) {
        return creditTransactionRepository.countByMemberId(principal.id());
    }

    /**
     * Get activity log (audit trail) for the current user.
     */
    public List<AuditLog> getActivityLog(UserPrincipal principal, int page, int size) {
        return auditLogRepository.findByUserId(principal.id(), page, size);
    }

    /**
     * Get activity log count.
     */
    public long getActivityLogCount(UserPrincipal principal) {
        return auditLogRepository.countByUserId(principal.id());
    }

    // ──────── Activity Tracker ────────

    /**
     * Activity type categories for convenient grouping.
     */
    public static final String CATEGORY_COMPANY = "COMPANY";
    public static final String CATEGORY_CREDITS = "CREDITS";
    public static final String CATEGORY_AUTH = "AUTH";
    public static final String CATEGORY_SUBSCRIPTION = "SUBSCRIPTION";
    public static final String CATEGORY_SEARCH = "SEARCH";
    public static final String CATEGORY_DOWNLOADS = "DOWNLOADS";
    public static final String CATEGORY_OTHER = "OTHER";

    /**
     * Get paginated, filtered activities for the research member.
     *
     * @param category     Activity category filter (COMPANY, CREDITS, AUTH, etc.) or null for all
     * @param dateFrom     Start date for range filter, or null
     * @param dateTo       End date for range filter, or null
     * @param search       Free-text search in description/action, or null
     * @param page         Page number (0-based)
     * @param size         Page size
     */
    public List<ActivityEntry> getActivities(UserPrincipal principal, String category,
                                              Instant dateFrom, Instant dateTo,
                                              String search, int page, int size) {
        // Map category to action prefix
        String actionFilter = mapCategoryToActionFilter(category);

        var logs = auditLogRepository.findByUserIdWithFilters(
            principal.id(), actionFilter, dateFrom, dateTo, search, page, size);

        return logs.stream().map(this::toActivityEntry).toList();
    }

    /**
     * Get total count of filtered activities.
     */
    public long getActivitiesCount(UserPrincipal principal, String category,
                                    Instant dateFrom, Instant dateTo, String search) {
        String actionFilter = mapCategoryToActionFilter(category);
        return auditLogRepository.countByUserIdWithFilters(
            principal.id(), actionFilter, dateFrom, dateTo, search);
    }

    /**
     * Map a category string to an action filter pattern.
     * Returns null for "ALL" — no filter applied.
     */
    private String mapCategoryToActionFilter(String category) {
        if (category == null || category.equals("ALL")) return null;
        return switch (category.toUpperCase()) {
            case "COMPANY" -> "COMPANY_";
            case "CREDITS" -> "CREDITS_";
            case "AUTH", "AUTHENTICATION" -> "LOGIN";
            case "SUBSCRIPTION" -> "SUBSCRIPTION_";
            case "SEARCH" -> "SEARCH";
            case "DOWNLOADS" -> "DOWNLOAD";
            default -> null;
        };
    }

    /**
     * Determine category from action string.
     */
    private String categorizeAction(String action) {
        if (action == null) return CATEGORY_OTHER;
        String upper = action.toUpperCase();
        if (upper.startsWith("COMPANY_")) return CATEGORY_COMPANY;
        if (upper.startsWith("CREDITS_")) return CATEGORY_CREDITS;
        if (upper.startsWith("LOGIN") || upper.startsWith("LOGOUT") || upper.contains("PASSWORD") || upper.contains("PROFILE_"))
            return CATEGORY_AUTH;
        if (upper.startsWith("SUBSCRIPTION_")) return CATEGORY_SUBSCRIPTION;
        if (upper.startsWith("SEARCH")) return CATEGORY_SEARCH;
        if (upper.startsWith("DOWNLOAD")) return CATEGORY_DOWNLOADS;
        return CATEGORY_OTHER;
    }

    /**
     * Parse credits from the details JSON field if present.
     */
    private Integer parseCredits(String details) {
        if (details == null) return null;
        try {
            // Try to extract "credits": N pattern from JSON
            int idx = details.indexOf("\"credits\"");
            if (idx < 0) return null;
            int colon = details.indexOf(':', idx);
            int end = details.indexOf(',', colon);
            if (end < 0) end = details.indexOf('}', colon);
            if (colon > 0 && end > colon) {
                return Integer.parseInt(details.substring(colon + 1, end).trim());
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Extract company name from details JSON.
     */
    private String extractCompanyName(String details) {
        if (details == null) return null;
        try {
            int idx = details.indexOf("\"companyName\"");
            if (idx < 0) return null;
            int colon = details.indexOf(':', idx);
            int startQuote = details.indexOf('"', colon + 1);
            int endQuote = details.indexOf('"', startQuote + 1);
            if (startQuote > 0 && endQuote > startQuote) {
                return details.substring(startQuote + 1, endQuote);
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Build a human-readable description from the action and details.
     */
    private String buildDescription(String action, String details) {
        if (details != null && !details.isBlank()) {
            // If details contains a "description" field, use it
            try {
                int idx = details.indexOf("\"description\"");
                if (idx >= 0) {
                    int colon = details.indexOf(':', idx);
                    int startQuote = details.indexOf('"', colon + 1);
                    int endQuote = details.indexOf('"', startQuote + 1);
                    if (startQuote > 0 && endQuote > startQuote) {
                        return details.substring(startQuote + 1, endQuote);
                    }
                }
            } catch (Exception ignored) {}
        }

        // Fall back to a human-readable form of the action
        if (action == null) return "Unknown activity";
        return switch (action.toUpperCase()) {
            case "COMPANY_UNLOCK" -> "Unlocked company";
            case "COMPANY_VIEWED" -> "Viewed company details";
            case "COMPANY_BOOKMARKED" -> "Bookmarked company";
            case "COMPANY_UNBOOKMARKED", "COMPANY_REMOVED_BOOKMARK" -> "Removed company from bookmarks";
            case "COMPANY_DOWNLOADED", "DOWNLOAD_COMPANY_PROFILE" -> "Downloaded company profile";
            case "COMPANY_EXPORTED" -> "Exported company report";
            case "COMPANY_SHARED" -> "Shared company";
            case "COMPANY_WATCHLIST_ADDED" -> "Added company to watchlist";
            case "COMPANY_WATCHLIST_REMOVED" -> "Removed company from watchlist";
            case "CREDITS_DEDUCTED" -> "Credits deducted";
            case "CREDITS_REFUNDED" -> "Credits refunded";
            case "CREDITS_ADDED" -> "Credits added";
            case "PLAN_UPGRADED" -> "Plan upgraded";
            case "PLAN_RENEWED" -> "Plan renewed";
            case "LOGIN" -> "Logged in";
            case "LOGOUT" -> "Logged out";
            case "PASSWORD_CHANGED" -> "Password changed";
            case "EMAIL_UPDATED" -> "Email updated";
            case "PROFILE_UPDATED" -> "Profile updated";
            case "SUBSCRIPTION_PURCHASED" -> "Subscription purchased";
            case "SUBSCRIPTION_RENEWED" -> "Subscription renewed";
            case "SUBSCRIPTION_EXPIRED" -> "Subscription expired";
            case "COMPANY_SEARCHED" -> "Searched companies";
            case "FILTERS_APPLIED" -> "Applied search filters";
            case "SEARCH_SAVED" -> "Saved search";
            case "SUPPORT_TICKET" -> "Created support ticket";
            case "FEEDBACK_SUBMITTED" -> "Submitted feedback";
            case "NOTIFICATION_READ" -> "Read notification";
            default -> action.replace('_', ' ').toLowerCase();
        };
    }

    private ActivityEntry toActivityEntry(AuditLog log) {
        String details = log.getDetails();
        return ActivityEntry.builder()
            .id(log.getId())
            .activityType(log.getAction())
            .category(categorizeAction(log.getAction()))
            .description(buildDescription(log.getAction(), details))
            .companyId(log.getCompanyId())
            .companyName(extractCompanyName(details))
            .creditsUsed(parseCredits(details))
            .status(log.getOutcome())
            .ipAddress(log.getIpAddress())
            .device(log.getUserAgent())
            .transactionId(log.getTransactionId())
            .timestamp(log.getCreatedAt())
            .build();
    }

    /**
     * Get credit summary for the dashboard.
     */
    public CreditSummaryDto getCreditSummary(UserPrincipal principal) {
        var membership = membershipRepository.findActiveByUserId(principal.id()).orElse(null);
        if (membership == null) {
            return CreditSummaryDto.builder()
                .totalCredits(0)
                .creditsUsed(0)
                .availableCredits(0)
                .totalUnlocked(0)
                .planType("NONE")
                .planName("No Plan")
                .build();
        }

        long totalUnlocked = unlockedCompanyRepository.countByMemberId(principal.id());
        String planName = pricing.getGuestPlan().getId().equals(membership.getPlanType())
            ? "Guest" : pricing.getCreditPlan(membership.getPlanType()).getName();

        return CreditSummaryDto.builder()
            .totalCredits(membership.getDownloadLimit())
            .creditsUsed(membership.getDownloadsUsed())
            .availableCredits(membership.getDownloadLimit() - membership.getDownloadsUsed())
            .totalUnlocked((int) totalUnlocked)
            .planType(membership.getPlanType())
            .planName(planName)
            .build();
    }
}
