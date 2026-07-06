package com.dob.infrastructure.web;

import com.dob.application.dto.*;
import com.dob.application.service.CompanyService;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Company search, listing, and management endpoints")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "Search companies", description = """
        Paginated company search with filters.
        Only returns publicly visible companies (APPROVED_ACTIVE with active listing membership).
        For FREE users: returns masked results with DoB Company ID only (no name, CIN, GST, PAN, etc.).
        For PREMIUM (subscribed) users: returns full company profiles.
        """)
    public PageDto<?> search(
        @Parameter(description = "Free-text search query (matches name, CIN, GST, PAN, director, website)")
        @RequestParam(required = false) String query,
        @Parameter(description = "Filter by sector") @RequestParam(required = false) String sector,
        @Parameter(description = "Filter by state") @RequestParam(required = false) String state,
        @Parameter(description = "Filter by company type") @RequestParam(required = false) String companyType,
        @Parameter(description = "Filter by revenue range (e.g. 0-10cr)") @RequestParam(required = false) String revenueRange,
        @Parameter(description = "Filter by membership (FREE/PREMIUM)") @RequestParam(required = false) String membershipFilter,
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return companyService.search(principal, query, sector, state, companyType,
            revenueRange, membershipFilter, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company detail by UUID", description = """
        Returns company profile with financials, certificates, and videos.
        FREE users: masked response with DoB ID only.
        PREMIUM users: complete company profile.
        Owners: complete company profile regardless of visibility status.
        """)
    public Object getById(
        @Parameter(description = "Company UUID (internal)") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return companyService.getById(id, principal);
    }

    @GetMapping("/public/{publicCompanyId}")
    @Operation(summary = "Get company detail by public DoB ID", description = """
        Lookup a company by its public DoB Company ID (e.g. DOB-7F92A1BC).
        FREE users: masked response.
        PREMIUM users: complete company profile.
        """)
    public Object getByPublicCompanyId(
        @Parameter(description = "Public DoB Company ID (e.g. DOB-7F92A1BC)")
        @PathVariable String publicCompanyId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return companyService.getByPublicCompanyId(publicCompanyId, principal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create company listing (DRAFT)", description = """
        Submit a new company listing in DRAFT status.
        The company must be completed and submitted for review before it can be approved.
        """)
    public CompanyDto create(@AuthenticationPrincipal UserPrincipal principal,
                              @Valid @RequestBody CreateCompanyRequest request) {
        return companyService.create(principal, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update company listing", description = "Update an existing company listing owned by the authenticated user. Only allowed in DRAFT or REJECTED status.")
    public CompanyDto update(@Parameter(description = "Company UUID") @PathVariable UUID id,
                              @AuthenticationPrincipal UserPrincipal principal,
                              @Valid @RequestBody CreateCompanyRequest request) {
        return companyService.update(id, principal, request);
    }

    @GetMapping("/my")
    @Operation(summary = "My companies", description = "Returns all company listings created by the authenticated COMPANY_USER.")
    public List<CompanyDto> getMyCompanies(@AuthenticationPrincipal UserPrincipal principal) {
        return companyService.getMyCompanies(principal);
    }

    @GetMapping("/my/details")
    @Operation(summary = "My companies with details", description = "Returns all company listings created by the authenticated COMPANY_USER with full workflow details (status, submission info, membership).")
    public List<CompanyDetailDto> getMyCompanyDetails(@AuthenticationPrincipal UserPrincipal principal) {
        return companyService.getMyCompanyDetails(principal);
    }

    @GetMapping("/{id}/owner-view")
    @Operation(summary = "Get company detail for owner", description = "Returns full company details for the listing owner, regardless of visibility status.")
    public CompanyDetailDto getCompanyDetailForOwner(
        @Parameter(description = "Company UUID") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.getCompanyDetailForOwner(id, principal);
    }

    // ──────── Submission Workflow ────────

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit company for review", description = "Submit a DRAFT company for admin review. Status changes to PENDING_REVIEW.")
    public CompanyDto submitForReview(
        @Parameter(description = "Company UUID") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.submitForReview(id, principal);
    }

    @PostMapping("/{id}/resubmit")
    @Operation(summary = "Resubmit rejected company", description = "Resubmit a REJECTED company for admin review after making corrections.")
    public CompanyDto resubmitForReview(
        @Parameter(description = "Company UUID") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.resubmitForReview(id, principal);
    }

    // ──────── Listing Membership ────────

    @PostMapping("/{id}/activate-listing")
    @Operation(summary = "Activate listing membership", description = """
        Activate listing membership for an approved company (after ₹500+GST payment).
        Sets listing expiry to 1 year and transitions to APPROVED_ACTIVE status,
        making the company publicly visible in search results.
        """)
    public CompanyDto activateListingMembership(
        @Parameter(description = "Company UUID") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal principal) {
        return companyService.activateListingMembership(id, principal);
    }
}
