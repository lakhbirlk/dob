package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.CompanyDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyDocumentJpaRepository extends JpaRepository<CompanyDocumentEntity, UUID> {
    List<CompanyDocumentEntity> findByCompanyId(UUID companyId);
}
