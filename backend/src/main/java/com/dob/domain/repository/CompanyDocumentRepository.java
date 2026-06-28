package com.dob.domain.repository;

import com.dob.domain.model.CompanyDocument;

import java.util.List;
import java.util.UUID;

public interface CompanyDocumentRepository {
    List<CompanyDocument> findByCompanyId(UUID companyId);
    CompanyDocument save(CompanyDocument document);
}
