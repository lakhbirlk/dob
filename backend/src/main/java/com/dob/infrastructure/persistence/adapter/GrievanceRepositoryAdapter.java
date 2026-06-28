package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.Grievance;
import com.dob.domain.repository.GrievanceRepository;
import com.dob.infrastructure.persistence.entity.GrievanceEntity;
import com.dob.infrastructure.persistence.repository.GrievanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GrievanceRepositoryAdapter implements GrievanceRepository {

    private final GrievanceJpaRepository jpa;

    @Override
    public Optional<Grievance> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Grievance> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Grievance> findByStatus(Grievance.GrievanceStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findByStatus(GrievanceEntity.GrievanceStatus.valueOf(status.name()), pageable)
            .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Grievance> findByAssignedTo(UUID adminId) {
        return jpa.findByAssignedTo(adminId).stream().map(this::toDomain).toList();
    }

    @Override
    public Grievance save(Grievance grievance) {
        var entity = toEntity(grievance);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    private Grievance toDomain(GrievanceEntity e) {
        return Grievance.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .complaintType(e.getComplaintType())
            .description(e.getDescription())
            .status(Grievance.GrievanceStatus.valueOf(e.getStatus().name()))
            .priority(Grievance.GrievancePriority.valueOf(e.getPriority().name()))
            .assignedTo(e.getAssignedTo())
            .resolution(e.getResolution())
            .resolvedAt(e.getResolvedAt())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    private GrievanceEntity toEntity(Grievance g) {
        return GrievanceEntity.builder()
            .id(g.getId())
            .userId(g.getUserId())
            .complaintType(g.getComplaintType())
            .description(g.getDescription())
            .status(GrievanceEntity.GrievanceStatus.valueOf(g.getStatus().name()))
            .priority(GrievanceEntity.GrievancePriority.valueOf(g.getPriority().name()))
            .assignedTo(g.getAssignedTo())
            .resolution(g.getResolution())
            .resolvedAt(g.getResolvedAt())
            .createdAt(g.getCreatedAt())
            .updatedAt(g.getUpdatedAt())
            .build();
    }
}
