package com.dob.application.service;

import com.dob.application.dto.GrievanceDto;
import com.dob.application.dto.GrievanceRequest;
import com.dob.domain.exception.DomainException;
import com.dob.domain.model.Grievance;
import com.dob.domain.repository.GrievanceRepository;
import com.dob.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrievanceService {

    private final GrievanceRepository grievanceRepository;

    @Transactional
    public GrievanceDto create(UserPrincipal principal, GrievanceRequest request) {
        var grievance = Grievance.builder()
            .userId(principal.id())
            .complaintType(request.complaintType())
            .description(request.description())
            .status(Grievance.GrievanceStatus.OPEN)
            .priority(parsePriority(request.priority()))
            .build();

        grievance = grievanceRepository.save(grievance);
        return toDto(grievance);
    }

    public List<GrievanceDto> getMyGrievances(UserPrincipal principal) {
        return grievanceRepository.findByUserId(principal.id())
            .stream().map(this::toDto).toList();
    }

    public GrievanceDto getById(UUID id) {
        return grievanceRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new DomainException("Grievance not found"));
    }

    @Transactional
    public GrievanceDto assign(UUID grievanceId, UUID adminId) {
        var grievance = grievanceRepository.findById(grievanceId)
            .orElseThrow(() -> new DomainException("Grievance not found"));
        grievance.assignTo(adminId);
        grievance = grievanceRepository.save(grievance);
        return toDto(grievance);
    }

    @Transactional
    public GrievanceDto resolve(UUID grievanceId, String resolution) {
        var grievance = grievanceRepository.findById(grievanceId)
            .orElseThrow(() -> new DomainException("Grievance not found"));
        grievance.resolve(resolution);
        grievance = grievanceRepository.save(grievance);
        return toDto(grievance);
    }

    public List<GrievanceDto> getByStatus(String status, int page, int size) {
        return grievanceRepository.findByStatus(
            Grievance.GrievanceStatus.valueOf(status), page, size)
            .stream().map(this::toDto).toList();
    }

    private Grievance.GrievancePriority parsePriority(String priority) {
        if (priority == null) return Grievance.GrievancePriority.MEDIUM;
        try {
            return Grievance.GrievancePriority.valueOf(priority);
        } catch (IllegalArgumentException e) {
            return Grievance.GrievancePriority.MEDIUM;
        }
    }

    private GrievanceDto toDto(Grievance g) {
        return GrievanceDto.builder()
            .id(g.getId())
            .userId(g.getUserId())
            .complaintType(g.getComplaintType())
            .description(g.getDescription())
            .status(g.getStatus().name())
            .priority(g.getPriority().name())
            .assignedTo(g.getAssignedTo())
            .resolution(g.getResolution())
            .resolvedAt(g.getResolvedAt())
            .createdAt(g.getCreatedAt())
            .updatedAt(g.getUpdatedAt())
            .build();
    }
}
