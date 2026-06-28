# DataOfBusiness (DoB) - Master Architecture & Code Generation Specification

## Objective

Analyze the attached HTML files and generate a complete enterprise-grade solution consisting of:

* React Native Frontend (TypeScript)
* Spring Boot Backend (Java 21)
* PostgreSQL Database
* Redis
* Razorpay Integration
* AWS S3 / MinIO Storage
* Docker & Kubernetes Deployment

---

# Source Files (Mandatory Analysis)

The following HTML files are attached and MUST be analyzed completely before generating any code, architecture, APIs, database design, UI components, workflows, or diagrams.

## 1. dataofbusiness_website.html

Primary application source.

Contains:

* Landing Page
* Hero Section
* Company Database
* Company Detail View
* Membership Flow
* Company Listing Flow
* Pricing
* Research Member Features
* Search & Filters
* Download Limits
* Dashboard Concepts
* Design System
* Navigation Structure
* User Journey

## 2. privacy.html

Contains:

* DPDP Compliance Requirements
* Privacy Rules
* Data Collection Policies
* Consent Management
* Data Retention Requirements
* User Rights
* Security Requirements

## 3. terms.html

Contains:

* Platform Rules
* Membership Rules
* Download Restrictions
* Company Listing Rules
* User Obligations
* Legal Disclaimers
* Service Conditions

## 4. refund.html

Contains:

* Membership Refund Logic
* Listing Refund Logic
* Cooling-Off Period Rules
* Refund Eligibility Rules
* Refund Workflow
* Razorpay Refund Requirements

## 5. grievance.html

Contains:

* Grievance Workflow
* Complaint Lifecycle
* Escalation Process
* Contact Management
* Compliance Requirements
* Resolution SLA

---

# HTML Files Are The Single Source Of Truth

The generated solution MUST:

* Preserve all business workflows.
* Preserve all legal disclaimers.
* Preserve all compliance requirements.
* Preserve all forms.
* Preserve all validations.
* Preserve all user journeys.
* Preserve all pricing rules.
* Preserve all membership restrictions.
* Preserve all download limits.
* Preserve all refund policies.
* Preserve all grievance workflows.
* Preserve all privacy requirements.
* Preserve all terms and conditions.

If any requirement exists inside the HTML files but is not mentioned elsewhere, the HTML requirement takes precedence.

Never generate features that contradict the HTML files.

---

# Platform Overview

Platform Name: DataOfBusiness (DoB)

Business Type:

* Corporate Intelligence Platform
* Company Research Database
* Due-Diligence Information Platform

---

# Important Legal Constraints

## Platform IS

* Corporate intelligence database
* Research platform
* Due diligence platform
* Business information service
* Subscription-based information product

## Platform IS NOT

* Stock exchange
* Securities marketplace
* Broker
* Investment advisor
* NBFC
* P2P platform
* Fundraising platform
* Loan marketplace

The architecture must preserve these constraints throughout the design.

---

# Requirements Extraction

Before generating code:

## Step 1

Analyze all HTML files completely.

## Step 2

Create a consolidated requirements document.

## Step 3

Extract:

### Business Requirements

### Functional Requirements

### Non-Functional Requirements

### Legal Requirements

### Compliance Requirements

### Security Requirements

### UI Requirements

### Workflow Requirements

---

# Mandatory Requirement Traceability

Every generated section must reference its source HTML file.

Example:

Requirement Source:
dataofbusiness_website.html
Section:
Company Database

Requirement Source:
privacy.html
Section:
Data Retention

Requirement Source:
refund.html
Section:
Investor Membership Refunds

Requirement Source:
terms.html
Section:
Download Restrictions

Requirement Source:
grievance.html
Section:
Complaint Lifecycle

---

# HTML to React Native Mapping

Generate a mapping table:

| HTML File | HTML Section | React Native Screen | Components |
| --------- | ------------ | ------------------- | ---------- |

Example:

| dataofbusiness_website.html | Hero Section | HomeScreen | HeroBanner |
| dataofbusiness_website.html | Company Database | CompanySearchScreen | SearchBar, Filters, CompanyCard |
| privacy.html | Privacy Policy | PrivacyPolicyScreen | PolicyRenderer |
| terms.html | Terms Screen | TermsScreen | TermsRenderer |
| refund.html | Refund Policy | RefundScreen | RefundRenderer |
| grievance.html | Grievance | GrievanceScreen | ContactCard, TicketForm |

---

# Frontend

Technology Stack:

* React Native
* TypeScript
* Expo
* React Navigation
* React Query
* Zustand
* Axios
* React Hook Form
* Yup
* NativeWind

---

# Design System

Preserve styling extracted from dataofbusiness_website.html.

Theme:

Primary Navy: #1E2761
Primary Gold: #E8B84B
Background: #F6F7FB
Success: #16A34A
Danger: #DC2626
Blue: #2563EB

---

# Frontend Screens

## Public

* Splash
* Home
* Pricing
* Company Database
* Company Detail
* About
* Contact
* Privacy
* Terms
* Refund
* Grievance

## Authentication

* Login
* Registration
* PAN Verification
* OTP Verification
* Forgot Password

## Research Member

* Dashboard
* Membership Status
* Search Companies
* Filters
* Company Details
* Download Center
* Profile

## Company User

* Dashboard
* Create Listing
* Edit Listing
* Upload Financials
* Upload CA Certificate
* Upload Video
* Listing Status
* Profile

## Admin

* Dashboard
* User Management
* Membership Management
* Company Approval
* Refund Requests
* Grievance Management
* Audit Logs

---

# Frontend Folder Structure

src/
├── app/
├── navigation/
├── screens/
├── components/
├── services/
├── hooks/
├── store/
├── theme/
├── utils/
├── forms/
├── validations/
└── assets/

---

# Reusable Components

Generate:

* Button
* Card
* Input
* OTP Input
* Modal
* Search Bar
* Filter Drawer
* Data Table
* Upload Component
* Video Player
* Membership Banner
* Download Counter
* Empty State

---

# Backend

Technology:

* Java 21
* Spring Boot 3.x
* Spring Security 6
* JWT
* OAuth2 Ready
* PostgreSQL
* Redis
* Spring Data JPA
* Lombok
* MapStruct
* OpenAPI
* Micrometer
* Resilience4J

---

# Architecture Style

Use:

* Hexagonal Architecture
* Clean Architecture
* DDD Principles

Layers:

* Controller
* Service
* Domain
* Repository
* DTO
* Mapper
* Security
* Event
* Integration
* Configuration

---

# Database Design

Generate ERD and schema.

Entities:

* User
* OTP
* Membership
* Company
* CompanyProfile
* FinancialStatement
* CACertificate
* CompanyVideo
* DownloadHistory
* ResearchAccess
* Subscription
* Payment
* Invoice
* GSTRecord
* AuditLog
* Grievance
* Notification
* Consent
* Session
* ReportAbuse
* AdminAction

Include:

* Relationships
* Constraints
* Indexes
* Audit Fields

---

# Security

Implement:

## Authentication

* PAN + OTP Login
* JWT Access Token
* Refresh Token

## Authorization

Roles:

* SUPER_ADMIN
* ADMIN
* COMPANY_USER
* RESEARCH_MEMBER
* AUDITOR

## Security Controls

* Rate Limiting
* Device Fingerprinting
* Audit Logging
* Encryption At Rest
* Encryption In Transit
* DPDP Compliance
* Secure File Access
* Signed URLs

---

# Payments

Integrate Razorpay.

## Membership

₹2500 + GST

## Company Listing

₹500 per year + GST

Generate:

* Payment Workflow
* Webhooks
* Refund Workflow
* GST Invoice Generation

---

# File Storage

Support:

* Financial Statements
* CA Certificates
* Videos
* Company Documents

Storage:

* AWS S3
* MinIO

Generate:

* Presigned URLs
* Virus Scanning
* Lifecycle Policies

---

# Search

Generate:

* Company Search
* Sector Filter
* State Filter
* Company Type Filter
* Revenue Filter
* Membership Visibility Filter

Use:

* PostgreSQL Full Text Search
  OR
* Elasticsearch

---

# Admin Workflows

Generate:

* Company Approval Workflow
* Membership Management
* Refund Management
* Grievance Management
* Audit Tracking

---

# API Design

Generate complete REST APIs.

For each API provide:

* Endpoint
* Request DTO
* Response DTO
* Validation Rules
* Error Responses
* Authorization Rules

Generate OpenAPI Specifications.

---

# UML Diagrams

Generate Mermaid Diagrams.

## HLD

* System Context Diagram
* Container Diagram
* Deployment Diagram

## LLD

* Class Diagram
* Component Diagram
* Entity Diagram
* Sequence Diagram

Generate sequence diagrams for:

* Login
* OTP Verification
* Membership Purchase
* Company Listing
* Company Approval
* Download Workflow
* Refund Workflow
* Grievance Workflow

---

# DevOps

Generate:

## Docker

* frontend
* backend
* postgres
* redis
* minio

## Kubernetes

* Deployment
* Service
* ConfigMap
* Secret
* HPA

## CI/CD

GitHub Actions

---

# Output Format

Generate output in the following order:

1. HTML Analysis Summary
2. Requirements Traceability Matrix
3. Functional Requirements
4. Non-Functional Requirements
5. UI Analysis
6. HLD
7. LLD
8. ERD
9. Backend Architecture
10. Frontend Architecture
11. Database Schema
12. REST APIs
13. Security Design
14. Payment Design
15. File Storage Design
16. Mermaid Diagrams
17. Folder Structure
18. DevOps Design
19. Development Roadmap
20. Production Deployment Strategy

The output must be enterprise-grade and suitable for Principal Engineer, Staff Engineer, and Solution Architect review.
