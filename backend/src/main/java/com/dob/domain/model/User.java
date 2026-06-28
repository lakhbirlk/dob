package com.dob.domain.model;

import com.dob.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class User {
    private UUID id;
    private String email;
    private String passwordHash;
    private String pan;
    private String fullName;
    private String phone;
    private UserRole role;
    private boolean emailVerified;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void updatePan(String pan) {
        if (pan != null && !pan.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
            throw new DomainException("Invalid PAN format");
        }
        this.pan = pan;
    }

    public void updateProfile(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean canCreateCompany() {
        return role == UserRole.COMPANY_USER;
    }

    public boolean canDownload() {
        return role == UserRole.RESEARCH_MEMBER;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }

    public enum UserRole {
        SUPER_ADMIN, ADMIN, COMPANY_USER, RESEARCH_MEMBER, AUDITOR
    }
}
