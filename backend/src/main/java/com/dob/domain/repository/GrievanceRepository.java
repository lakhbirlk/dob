package com.dob.domain.repository;

import com.dob.domain.model.Grievance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrievanceRepository {
    Optional<Grievance> findById(UUID id);
    List<Grievance> findByUserId(UUID userId);
    List<Grievance> findByStatus(Grievance.GrievanceStatus status, int page, int size);
    List<Grievance> findByAssignedTo(UUID adminId);
    Grievance save(Grievance grievance);
}
