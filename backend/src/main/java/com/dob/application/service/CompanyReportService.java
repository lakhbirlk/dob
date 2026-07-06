package com.dob.application.service;

import com.dob.domain.exception.CompanyNotFoundException;
import com.dob.domain.exception.DownloadLimitExceededException;
import com.dob.domain.exception.UnauthorizedException;
import com.dob.domain.model.Company;
import com.dob.domain.model.Membership;
import com.dob.domain.model.User;
import com.dob.domain.repository.CompanyRepository;
import com.dob.domain.repository.MembershipRepository;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.security.UserPrincipal;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyReportService {

    private static final String ACTION_REPORT_DOWNLOAD = "REPORT_DOWNLOAD";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' hh:mm a");
    private static final String[] ADMIN_ROLES = {"ADMIN", "SUPER_ADMIN"};

    private final CompanyRepository companyRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    /**
     * Generate and return a company report PDF for the given company.
     * Validates authentication, authorization, and subscription (except for ADMIN).
     */
    @Transactional
    public byte[] generateReport(UUID companyId, UserPrincipal principal, String ipAddress) {
        // 1. Fetch company
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException(companyId.toString()));

        // 2. Fetch user
        var user = userRepository.findById(principal.id())
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        // 3. Validate authorization
        boolean isAdmin = isAdmin(user);
        boolean canDownload = false;

        if (isAdmin) {
            canDownload = true;
        } else if (user.canDownload()) {
            // RESEARCH_MEMBER or COMPANY_USER — check subscription
            Optional<Membership> membership = membershipRepository.findActiveByUserId(principal.id());
            if (membership.isPresent() && membership.get().canDownload()) {
                canDownload = true;
                // Increment download counter
                Membership m = membership.get();
                m.incrementDownloads();
                membershipRepository.save(m);
            } else {
                log.warn("Download blocked for user {}: no active membership or limit reached", principal.id());
                auditService.log(principal.id(), companyId, ACTION_REPORT_DOWNLOAD, "FAILURE",
                    "Download blocked: no active membership or download limit reached", ipAddress);
                throw new DownloadLimitExceededException(
                    membership.map(Membership::getDownloadLimit).orElse(0));
            }
        } else {
            log.warn("Download blocked for user {}: role {} cannot download reports", principal.id(), user.getRole());
            auditService.log(principal.id(), companyId, ACTION_REPORT_DOWNLOAD, "FAILURE",
                "Download blocked: role " + user.getRole() + " cannot download reports", ipAddress);
            throw new UnauthorizedException("Your account type does not have permission to download reports");
        }

        try {
            // 4. Generate PDF
            byte[] pdfBytes = generatePdf(company);

            // 5. Log successful download
            auditService.log(principal.id(), companyId, ACTION_REPORT_DOWNLOAD, "SUCCESS",
                "Report downloaded successfully", ipAddress);

            log.info("Report downloaded: company={}, user={}, role={}", companyId, principal.id(), user.getRole());
            return pdfBytes;
        } catch (Exception e) {
            log.error("Failed to generate report for company {} by user {}", companyId, principal.id(), e);
            auditService.log(principal.id(), companyId, ACTION_REPORT_DOWNLOAD, "FAILURE",
                "Report generation failed: " + e.getMessage(), ipAddress);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    // ──────────── PDF Generation ────────────

    private byte[] generatePdf(Company c) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);

        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Title
        addTitle(document, c);

        // Section 1: Company Identifiers
        addSection(document, "Company Identifiers");
        addField(document, "Company Name", c.getName());
        addField(document, "Public Company ID", c.getPublicCompanyId());
        addField(document, "CIN", c.getCin());
        addField(document, "GSTIN", c.getGstin());
        addField(document, "PAN", c.getPan());
        addField(document, "TAN", c.getTan());
        addField(document, "MSME Registration", c.getMsmeRegistration());
        addField(document, "Startup India Registration", c.getStartupIndiaRegistration());

        // Section 2: Registered Office Address
        addSection(document, "Registered Office Address");
        String addr = c.getRegisteredAddressLine1();
        if (c.getRegisteredAddressLine2() != null) addr += ", " + c.getRegisteredAddressLine2();
        addField(document, "Address", addr);
        addField(document, "City", c.getRegisteredCity());
        addField(document, "State", c.getRegisteredState());
        addField(document, "Country", c.getRegisteredCountry());
        addField(document, "PIN Code", c.getRegisteredPinCode());

        // Section 3: Contact Details
        addSection(document, "Contact Details");
        addField(document, "Official Email", c.getOfficialEmail());
        addField(document, "Official Phone", c.getOfficialPhone());
        addField(document, "Website", c.getWebsite());
        addField(document, "LinkedIn Profile", c.getLinkedinProfile());

        // Section 4: Company Details
        addSection(document, "Company Details");
        addField(document, "Sector", c.getSector());
        addField(document, "Company Type", c.getCompanyType());
        addField(document, "Incorporation Year", c.getIncorporationYear() != null ? c.getIncorporationYear().toString() : null);
        addField(document, "Employee Count", c.getEmployeeCount() != null ? c.getEmployeeCount().toString() : null);
        addField(document, "Number of Branches", c.getNumBranches() != null ? c.getNumBranches().toString() : null);
        addField(document, "Operational States", c.getOperationalStates());
        addField(document, "Status", c.getStatus().name());

        // Section 5: Financial Summary
        addSection(document, "Financial Summary");
        addField(document, "Annual Turnover", c.getAnnualTurnover());
        addField(document, "Paid-up Capital", c.getPaidUpCapital());
        addField(document, "Authorized Capital", c.getAuthorizedCapital());
        addField(document, "Financial Year", c.getFinancialYear());
        addField(document, "Auditor Details", c.getAuditorDetails());

        // Section 6: Business Information
        addSection(document, "Business Information");
        addField(document, "Products / Services", c.getProductsServices());
        addField(document, "Business Description", c.getBusinessDescription());
        addField(document, "Description", c.getDescription());
        addField(document, "Certifications", c.getCertifications());
        addField(document, "Export / Import Status", c.getExportImportStatus());

        // Section 7: Authorized Representative
        addSection(document, "Authorized Representative");
        addField(document, "Name", c.getAuthorizedRepName());
        addField(document, "Designation", c.getAuthorizedRepDesignation());
        addField(document, "Mobile", c.getAuthorizedRepMobile());
        addField(document, "Email", c.getAuthorizedRepEmail());

        // Footer: download timestamp
        document.add(Chunk.NEWLINE);
        Paragraph footerLine = new Paragraph("─".repeat(70), smallFont());
        footerLine.setAlignment(Element.ALIGN_CENTER);
        document.add(footerLine);

        Paragraph ts = new Paragraph(
            "Report downloaded on " + LocalDateTime.now(ZoneId.of("Asia/Kolkata")).format(DATE_FMT) + " IST",
            smallFont());
        ts.setAlignment(Element.ALIGN_CENTER);
        ts.setSpacingBefore(8);
        document.add(ts);

        Paragraph disclaimer = new Paragraph(
            "This report is for informational purposes only and does not constitute investment, legal, or tax advice. " +
            "Data sourced from CA-certified financials and company disclosures. DataOfBusiness (DoB) makes no " +
            "representations regarding the accuracy or completeness of third-party data.",
            tinyFont());
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        disclaimer.setSpacingBefore(4);
        document.add(disclaimer);

        document.close();
        return baos.toByteArray();
    }

    private void addTitle(Document doc, Company c) throws DocumentException {
        // Company name header
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(30, 39, 97));
        Paragraph title = new Paragraph(c.getName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        doc.add(title);

        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(90, 100, 120));
        Paragraph sub = new Paragraph("Company Report  |  " + c.getPublicCompanyId(), subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        doc.add(sub);

        // Separator line
        Paragraph sep = new Paragraph("─".repeat(70), smallFont());
        sep.setAlignment(Element.ALIGN_CENTER);
        sep.setSpacingAfter(14);
        doc.add(sep);
    }

    private void addSection(Document doc, String title) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(30, 39, 97));
        Paragraph p = new Paragraph(title, sectionFont);
        p.setSpacingBefore(14);
        p.setSpacingAfter(6);
        doc.add(p);

        // Underline
        Paragraph line = new Paragraph("─".repeat(60), tinyFont());
        line.setSpacingAfter(4);
        doc.add(line);
    }

    private void addField(Document doc, String label, String value) throws DocumentException {
        if (value == null || value.isBlank()) return;
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(90, 100, 120));
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(26, 34, 56));
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ":  ", labelFont));
        p.add(new Chunk(value, valueFont));
        p.setSpacingAfter(3);
        doc.add(p);
    }

    private Font smallFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(152, 161, 179));
    }

    private Font tinyFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 7, new Color(152, 161, 179));
    }

    // ──────────── Helpers ────────────

    private boolean isAdmin(User user) {
        for (String role : ADMIN_ROLES) {
            if (user.getRole().name().equals(role)) return true;
        }
        return false;
    }

}
