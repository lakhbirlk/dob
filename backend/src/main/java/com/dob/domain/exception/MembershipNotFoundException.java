package com.dob.domain.exception;

public class MembershipNotFoundException extends DomainException {
    public MembershipNotFoundException(String id) {
        super("Membership not found: " + id);
    }
}
