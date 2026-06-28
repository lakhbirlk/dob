package com.dob.infrastructure.web;

import com.dob.application.dto.AuthResponse;
import com.dob.application.dto.CompanyRegistrationRequest;
import com.dob.application.dto.ResearchMemberRegistrationRequest;
import com.dob.application.service.CompanyRegistrationService;
import com.dob.application.service.ResearchMemberRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Public endpoints for role-based registration (Company & Research Member)")
public class RegistrationController {

    private final CompanyRegistrationService companyRegistrationService;
    private final ResearchMemberRegistrationService researchMemberRegistrationService;

    @PostMapping("/register/company")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register as a Company",
               description = "Creates a new company user account with company profile. " +
                             "Collects company information, registered office, contact details, " +
                             "authorized representative, financial info, and business info. " +
                             "Returns JWT tokens on success.")
    @SecurityRequirements
    public AuthResponse registerCompany(@Valid @RequestBody CompanyRegistrationRequest request) {
        return companyRegistrationService.register(request);
    }

    @PostMapping("/register/research")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register as a Research Member",
               description = "Creates a new research member account with researcher profile. " +
                             "Collects personal info, professional info, and preferences. " +
                             "Returns JWT tokens on success.")
    @SecurityRequirements
    public AuthResponse registerResearchMember(@Valid @RequestBody ResearchMemberRegistrationRequest request) {
        return researchMemberRegistrationService.register(request);
    }
}
