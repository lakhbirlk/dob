package com.dob.infrastructure.web;

import com.dob.application.dto.*;
import com.dob.application.service.CompanyUnlockService;
import com.dob.domain.exception.CompanyNotFoundException;
import com.dob.domain.model.AuditLog;
import com.dob.domain.repository.CompanyRepository;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/unlock")
@RequiredArgsConstructor
@Tag(name = "Company Unlock", description = "Research member company unlock operations using credits")
public class CompanyUnlockController {

    private final CompanyUnlockService unlockService;
    private final CompanyRepository companyRepository;

    /**
     * Resolve a company identifier (UUID or public DoB ID) to a UUID.
     */
    private UUID resolveCompanyId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            var company = companyRepository.findByPublicCompanyId(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
            return company.getId();
        }
    }

    @PostMapping("/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Unlock a company", description = """
        Deducts credits from the research member's available credits and unlocks
        the specified company for permanent access. Accepts both internal UUID
        and public DoB ID (e.g. DOB-7F92A1BC).
        """)
    public UnlockCompanyResponse unlockCompany(
        @Parameter(description = "Company UUID or DoB ID") @PathVariable String companyId,
        @AuthenticationPrincipal UserPrincipal principal,
        HttpServletRequest request
    ) {
        UUID id = resolveCompanyId(companyId);
        return unlockService.unlockCompany(id, principal, request);
    }

    @GetMapping("/{companyId}/status")
    @Operation(summary = "Check unlock status", description = "Returns whether the current user has unlocked the specified company. Accepts UUID or public DoB ID.")
    public UnlockStatusResponse checkUnlockStatus(
        @Parameter(description = "Company UUID or DoB ID") @PathVariable String companyId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        UUID id = resolveCompanyId(companyId);
        return unlockService.checkUnlockStatus(id, principal);
    }

    @PostMapping("/batch-status")
    @Operation(summary = "Batch check unlock status", description = "Check unlock status for multiple companies at once.")
    public List<UnlockStatusResponse> batchCheckUnlockStatus(
        @RequestBody Map<String, List<String>> request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        var companyIds = request.getOrDefault("companyIds", List.of()).stream()
            .map(this::resolveCompanyId)
            .toList();
        return unlockService.checkBatchUnlockStatus(companyIds, principal);
    }

    @GetMapping("/companies")
    @Operation(summary = "List unlocked companies", description = "Returns all companies unlocked by the current research member.")
    public Map<String, Object> getUnlockedCompanies(
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        var companies = unlockService.getUnlockedCompanies(principal, page, size);
        long total = unlockService.getUnlockedCompaniesCount(principal);
        return Map.of(
            "content", companies,
            "page", page,
            "size", size,
            "totalElements", total,
            "totalPages", (int) Math.ceil((double) total / size)
        );
    }

    @GetMapping("/credits")
    @Operation(summary = "Get credit history", description = "Returns paginated credit transaction history for the current research member.")
    public Map<String, Object> getCreditHistory(
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        var transactions = unlockService.getCreditHistory(principal, page, size);
        long total = unlockService.getCreditHistoryCount(principal);
        return Map.of(
            "content", transactions,
            "page", page,
            "size", size,
            "totalElements", total,
            "totalPages", (int) Math.ceil((double) total / size)
        );
    }

    @GetMapping("/summary")
    @Operation(summary = "Get credit summary", description = "Returns credit usage summary for the dashboard.")
    public CreditSummaryDto getCreditSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return unlockService.getCreditSummary(principal);
    }

    @GetMapping("/activity")
    @Operation(summary = "Get activity log", description = """
        Returns paginated, filterable activity log for the current research member.
        Supports filtering by category, date range, and free-text search.
        """)
    public Map<String, Object> getActivityLog(
        @Parameter(description = "Activity category filter: ALL, COMPANY, CREDITS, AUTH, SUBSCRIPTION, SEARCH, DOWNLOADS, OTHER")
        @RequestParam(defaultValue = "ALL") String category,
        @Parameter(description = "Filter activities from this date (ISO-8601 instant)")
        @RequestParam(required = false) String dateFrom,
        @Parameter(description = "Filter activities until this date (ISO-8601 instant)")
        @RequestParam(required = false) String dateTo,
        @Parameter(description = "Free-text search in action type or description")
        @RequestParam(required = false) String search,
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        Instant from = dateFrom != null ? Instant.parse(dateFrom) : null;
        Instant to = dateTo != null ? Instant.parse(dateTo) : null;

        var activities = unlockService.getActivities(principal, category, from, to, search, page, size);
        long total = unlockService.getActivitiesCount(principal, category, from, to, search);
        return Map.of(
            "content", activities,
            "page", page,
            "size", size,
            "totalElements", total,
            "totalPages", (int) Math.ceil((double) total / size)
        );
    }
}
