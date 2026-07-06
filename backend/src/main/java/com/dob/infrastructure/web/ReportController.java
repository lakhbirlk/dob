package com.dob.infrastructure.web;

import com.dob.application.service.CompanyReportService;
import com.dob.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Company report download endpoints")
public class ReportController {

    private final CompanyReportService reportService;

    @GetMapping("/{companyId}/report")
    @Operation(summary = "Download company report", description = """
        Generates and returns a PDF report for the specified company.
        - ADMIN/SUPER_ADMIN: no subscription check.
        - RESEARCH_MEMBER: must have active membership with download quota.
        - COMPANY_USER: must have active membership with download quota.
        - Unauthenticated users: 401.
        """)
    public ResponseEntity<byte[]> downloadReport(
            @Parameter(description = "Company UUID") @PathVariable UUID companyId,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        String ipAddress = extractClientIp(request);
        byte[] pdfBytes = reportService.generateReport(companyId, principal, ipAddress);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(pdfBytes.length)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"company-report-" + companyId + ".pdf\"")
            .body(pdfBytes);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank() && !"unknown".equalsIgnoreCase(xff)) {
            return xff.split(",")[0].trim();
        }
        String ri = request.getHeader("X-Real-IP");
        if (ri != null && !ri.isBlank() && !"unknown".equalsIgnoreCase(ri)) {
            return ri;
        }
        return request.getRemoteAddr();
    }
}
