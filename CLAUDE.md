# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project: DataOfBusiness (DoB)

A corporate intelligence and research platform for Indian private companies. Business model: subscription-based access to CA-certified financial information, company profiles, and due-diligence data.

## Current State

This is a **pre-implementation specification repository**. No application code exists yet. The repository contains:

- `spec.md` — Master architecture & code-generation specification (the authoritative document)
- `dataofbusiness_website.html` — Primary HTML mockup: landing page, company database, membership flow, pricing, dashboard concepts, design system, navigation, user journeys
- `privacy.html` — DPDP Act 2023 compliance requirements, data collection policies, consent management, data retention, user rights
- `terms.html` — Platform rules, membership rules, download restrictions, company listing rules, user obligations, legal disclaimers
- `refund.html` — Membership and listing refund logic, cooling-off period rules, Razorpay refund requirements
- `grievance.html` — Grievance workflow, complaint lifecycle, escalation process, resolution SLAs

### Source of Truth

The **HTML files are the single source of truth** for all business requirements. They take precedence over any other document. Any generated code must preserve all workflows, legal disclaimers, compliance requirements, forms, validations, user journeys, pricing rules, membership restrictions, download limits, refund policies, grievance workflows, privacy requirements, and terms from these HTML files. Never generate features that contradict the HTML files.

## Target Technology Stack

### Frontend
- React Native with TypeScript and Expo
- React Navigation, React Query (TanStack Query), Zustand (state management)
- Axios, React Hook Form + Yup (validation)
- NativeWind (Tailwind CSS for React Native)

### Backend
- Java 21, Spring Boot 3.x, Spring Security 6
- JWT + OAuth2 authentication, PAN + OTP login
- PostgreSQL, Redis (caching/sessions)
- Spring Data JPA, Lombok, MapStruct, OpenAPI, Micrometer, Resilience4J
- Architecture: Hexagonal (Ports & Adapters), Clean Architecture, DDD principles
- Search: PostgreSQL full-text search or Elasticsearch

### Infrastructure
- Docker (5 containers: frontend, backend, postgres, redis, minio)
- Kubernetes (Deployment, Service, ConfigMap, Secret, HPA)
- CI/CD: GitHub Actions
- File storage: AWS S3 / MinIO (presigned URLs, virus scanning, lifecycle policies)
- Payments: Razorpay (membership ₹2500+GST, company listing ₹500/yr+GST)

## Design System (from `dataofbusiness_website.html`)

| Token    | Value     |
|----------|-----------|
| Navy     | `#1E2761` |
| Navy-2   | `#2A3580` |
| Navy Deep| `#141B47` |
| Gold     | `#E8B84B` |
| Gold Dark| `#C49A35` |
| Teal     | `#0D9488` |
| Blue     | `#2563EB` |
| Red      | `#DC2626` |
| Green    | `#16A34A` |
| BG       | `#F6F7FB` |
| Card BG  | `#FFFFFF` |
| Ink      | `#1A2238` |
| Muted    | `#5A6478` |
| Faint    | `#98A1B3` |
| Line     | `#E3E7F0` |

Border radius: `12px` (default), `8px` (small). Shadow: `0 1px 3px rgba(20,27,71,.07), 0 8px 24px rgba(20,27,71,.06)`.

## Platform Legal Constraints

**IS:** Corporate intelligence database, research platform, due diligence platform, business information service, subscription-based information product.

**IS NOT:** Stock exchange, securities marketplace, broker, investment advisor, NBFC, P2P platform, fundraising platform, loan marketplace.

The architecture must preserve these constraints throughout the design.

## User Roles

- `SUPER_ADMIN` / `ADMIN` — Company approval, membership management, refund/grievance management, audit tracking
- `COMPANY_USER` — Create/edit listings, upload financials/CA certificates/videos, view listing status
- `RESEARCH_MEMBER` — Search companies, view details, download (with limits), membership dashboard
- `AUDITOR` — Read-only access to audit logs

## Key Business Rules

- Membership: ₹2500 + GST with download limits
- Company listing: ₹500/year + GST
- PAN verification required for registration
- Refunds governed by cooling-off periods (defined in `refund.html`)
- All data handling must comply with India's DPDP Act 2023 (defined in `privacy.html`)
- Grievance resolution with defined SLAs (defined in `grievance.html`)
- CA-certified financials and documents required for company listings

## spec.md Structure

The spec defines a sequential 20-section output for code generation:
1. HTML Analysis → 2. Requirements Traceability → 3. Functional Requirements → 4. Non-Functional Requirements → 5. UI Analysis → 6. HLD → 7. LLD → 8. ERD → 9. Backend Architecture → 10. Frontend Architecture → 11. Database Schema → 12. REST APIs → 13. Security → 14. Payments → 15. File Storage → 16. Mermaid Diagrams → 17. Folder Structure → 18. DevOps → 19. Development Roadmap → 20. Production Deployment Strategy

## Generating Code from This Spec

When instructed to generate implementation code, follow `spec.md` strictly:
- Extract all requirements from the 5 HTML files first (Step 1 of spec)
- Create the HTML-to-React Native mapping table
- Generate output in the 20-section order defined in the spec
- Every generated section must reference its source HTML file and section
