package com.dob.infrastructure.web;

import com.dob.application.dto.GrievanceDto;
import com.dob.application.dto.GrievanceRequest;
import com.dob.application.service.GrievanceService;
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
@RequestMapping("/api/grievances")
@RequiredArgsConstructor
@Tag(name = "Grievances", description = "Grievance filing and tracking for authenticated users (data discrepancies, download issues, etc.)")
public class GrievanceController {

    private final GrievanceService grievanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "File a grievance", description = "Submit a new grievance/complaint. Types include: Data Discrepancy, Download Issue, Billing, etc.")
    public GrievanceDto create(@AuthenticationPrincipal UserPrincipal principal,
                                @Valid @RequestBody GrievanceRequest request) {
        return grievanceService.create(principal, request);
    }

    @GetMapping
    @Operation(summary = "My grievances", description = "Returns all grievances filed by the authenticated user.")
    public List<GrievanceDto> getMyGrievances(@AuthenticationPrincipal UserPrincipal principal) {
        return grievanceService.getMyGrievances(principal);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grievance", description = "Returns details of a specific grievance by ID.")
    public GrievanceDto getById(@Parameter(description = "Grievance UUID") @PathVariable UUID id) {
        return grievanceService.getById(id);
    }
}
