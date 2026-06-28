package com.dob.domain.exception;

public class CompanyNotFoundException extends DomainException {
    public CompanyNotFoundException(String id) {
        super("Company not found: " + id);
    }
}
