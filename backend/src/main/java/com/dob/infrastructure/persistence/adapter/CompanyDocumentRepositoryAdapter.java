package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.CompanyDocument;
import com.dob.domain.repository.CompanyDocumentRepository;
import com.dob.infrastructure.persistence.entity.CompanyDocumentEntity;
import com.dob.infrastructure.persistence.repository.CompanyDocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyDocumentRepositoryAdapter implements CompanyDocumentRepository {

    private final CompanyDocumentJpaRepository jpa;

    @Override
    public List<CompanyDocument> findByCompanyId(UUID companyId) {
        return jpa.findByCompanyId(companyId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public CompanyDocument save(CompanyDocument document) {
        var entity = toEntity(document);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    private CompanyDocument toDomain(CompanyDocumentEntity e) {
        return CompanyDocument.builder()
            .id(e.getId())
            .companyId(e.getCompanyId())
            .documentType(e.getDocumentType())
            .fileUrl(e.getFileUrl())
            .uploadedAt(e.getUploadedAt())
            .build();
    }

    private CompanyDocumentEntity toEntity(CompanyDocument d) {
        return CompanyDocumentEntity.builder()
            .id(d.getId())
            .companyId(d.getCompanyId())
            .documentType(d.getDocumentType())
            .fileUrl(d.getFileUrl())
            .uploadedAt(d.getUploadedAt())
            .build();
    }
}
