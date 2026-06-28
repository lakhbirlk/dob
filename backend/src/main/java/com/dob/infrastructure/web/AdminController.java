package com.dob.infrastructure.web;

import com.dob.application.dto.*;
import com.dob.application.service.CompanyService;
import com.dob.application.service.GrievanceService;
import com.dob.application.service.PaymentService;
import com.dob.domain.model.Company;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.PaymentRepository;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin and SUPER_ADMIN endpoints for company approvals, refunds, grievances, and audit logs")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final PaymentRepository paymentRepository;
    private final GrievanceService grievanceService;

    // --- Company Approval ---

    @GetMapping("/companies/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get pending companies", description = "Lists companies awaiting admin approval for listing.")
    public List<CompanyDto> getPendingCompanies(@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                                 @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return companyRepository.findByStatus(Company.CompanyStatus.PENDING, page, size)
            .stream()
            .map(c -> CompanyDto.builder()
                .id(c.getId()).publicCompanyId(c.getPublicCompanyId()).name(c.getName()).sector(c.getSector())
                .state(c.getState()).city(c.getCity()).companyType(c.getCompanyType())
                .description(c.getDescription()).website(c.getWebsite())
                .status(c.getStatus().name()).createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build())
            .toList();
    }

    @PostMapping("/companies/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Approve company", description = "Approve a pending company listing for public visibility.")
    public CompanyDto approveCompany(@Parameter(description = "Company UUID") @PathVariable UUID id,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));
        company.approve(principal.id());
        company = companyRepository.save(company);
        return CompanyDto.builder()
            .id(company.getId()).publicCompanyId(company.getPublicCompanyId())
            .name(company.getName()).status(company.getStatus().name())
            .createdAt(company.getCreatedAt()).updatedAt(company.getUpdatedAt())
            .build();
    }

    @PostMapping("/companies/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Reject company", description = "Reject a pending company listing with a reason.")
    public CompanyDto rejectCompany(@Parameter(description = "Company UUID") @PathVariable UUID id,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        var company = companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));
        company.reject(principal.id());
        company = companyRepository.save(company);
        return CompanyDto.builder()
            .id(company.getId()).publicCompanyId(company.getPublicCompanyId())
            .name(company.getName()).status(company.getStatus().name())
            .build();
    }

    // --- Refunds ---

    @GetMapping("/refunds")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get refund requests", description = "Lists all refund requests for admin processing.")
    public List<PaymentDto> getRefunds() {
        return List.of(); // Placeholder — wire to refund-requested payments
    }

    @PostMapping("/refunds/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Process refund", description = "Approve or reject a refund request.")
    public Map<String, String> processRefund(@Parameter(description = "Refund/Payment UUID") @PathVariable UUID id) {
        return Map.of("status", "PROCESSED");
    }

    // --- Grievances ---

    @GetMapping("/grievances")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get grievances", description = "Lists all grievances with optional status filter.")
    public List<GrievanceDto> getGrievances(@Parameter(description = "Filter by status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)") @RequestParam(defaultValue = "OPEN") String status,
                                             @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                             @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return grievanceService.getByStatus(status, page, size);
    }

    @PostMapping("/grievances/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Assign grievance", description = "Assign a grievance to the authenticated admin for resolution.")
    public GrievanceDto assignGrievance(@Parameter(description = "Grievance UUID") @PathVariable UUID id,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return grievanceService.assign(id, principal.id());
    }

    @PostMapping("/grievances/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Resolve grievance", description = "Mark a grievance as resolved with a resolution note.")
    public GrievanceDto resolveGrievance(@Parameter(description = "Grievance UUID") @PathVariable UUID id,
                                          @RequestBody Map<String, String> body) {
        return grievanceService.resolve(id, body.getOrDefault("resolution", "Resolved"));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get audit logs", description = "Returns paginated audit trail of admin actions.")
    public List<Map<String, String>> getAuditLogs() {
        return List.of(); // Placeholder — wire to AuditLogRepository
    }
}
