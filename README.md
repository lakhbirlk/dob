# DataOfBusiness (DoB)

India's Private Company Intelligence Platform — a subscription-based corporate research and due-diligence information service. CA-certified financial data for 10,000+ Indian private companies.

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌────────────┐
│  React      │────▶│  Spring Boot │────▶│ PostgreSQL │
│  Native     │     │  Java 21     │     │            │
│  Expo       │     │  REST API    │     │  Redis     │
│  TypeScript │     │  Maven       │     │  MinIO/S3  │
└─────────────┘     └──────────────┘     └────────────┘
                           │
                     ┌─────┴─────┐
                     │  Razorpay │
                     │  Payments │
                     └───────────┘
```

### Design System

| Token      | Value     |
|------------|-----------|
| Navy       | `#1E2761` |
| Navy Deep  | `#141B47` |
| Gold       | `#E8B84B` |
| Gold Dark  | `#C49A35` |
| Teal       | `#0D9488` |
| BG         | `#F6F7FB` |
| Ink        | `#1A2238` |

Full palette in `CLAUDE.md`.

## Quick Start

### Prerequisites
- Java 21
- Node.js 20+
- Docker & Docker Compose (for `dev` profile) or nothing (for `local` profile with H2)

### Local Development (No Docker — H2 in-memory)

```bash
# 1. Start backend with local profile (H2 in-memory database)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 2. Start frontend (separate terminal)
cd frontend
npm install
npx expo start --web

# 3. Open browser
# Frontend: http://localhost:8081
# Backend API: http://localhost:8080
```

### Docker Compose (Full Stack with PostgreSQL)

```bash
cp .env.example .env
# Edit .env with your keys

# Start all services
docker-compose up -d

# Or start infrastructure only (run backend via Maven with dev profile)
docker-compose up -d postgres redis minio
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Services

| Service           | URL                          |
|-------------------|------------------------------|
| Frontend (Expo)   | http://localhost:8081        |
| Backend API       | http://localhost:8080        |
| Swagger UI        | http://localhost:8080/swagger-ui.html |
| H2 Console        | http://localhost:8080/h2-console (local profile only) |
| MinIO Console     | http://localhost:9001        |

## Seed Data

When running with the `local` profile, the application seeds the database with sample data automatically on startup.

### Seeded Users

| Name              | Email                     | Role          | Password     | Membership |
|-------------------|---------------------------|---------------|--------------|------------|
| Rahul Sharma      | rahul.sharma@example.com  | RESEARCH_MEMBER | password123 | CREDITS_10 |
| Sneha Gupta       | sneha.gupta@example.com   | RESEARCH_MEMBER | password123 | None       |
| Vikram Singh      | vikram.singh@example.com  | RESEARCH_MEMBER | password123 | None       |
| Priya Patel       | priya.patel@example.com   | RESEARCH_MEMBER | password123 | None       |
| Ananya Reddy      | ananya.reddy@example.com  | COMPANY_USER    | password123 | N/A        |
| Manoj Joshi       | manoj.joshi@example.com   | COMPANY_USER    | password123 | N/A        |
| Deepa Nair        | deepa.nair@example.com    | COMPANY_USER    | password123 | N/A        |
| Kavita Deshmukh   | kavita@dob.admin          | ADMIN          | password123 | N/A        |
| Arun Mehta        | arun.mehta@example.com    | RESEARCH_MEMBER | password123 | CREDITS_5  |
| Sunita Verma      | sunita.verma@example.com  | RESEARCH_MEMBER | password123 | CREDITS_20 |
| Rohit Kumar       | rohit.kumar@example.com   | RESEARCH_MEMBER | password123 | None       |
| Neha Agarwal      | neha.agarwal@example.com  | RESEARCH_MEMBER | password123 | None       |

Also seeded: 30+ approved companies, 5 company users with pending/approved listings, sample payments, grievances, credit transactions, and audit logs.

## Key Business Rules

- **Research Membership**: Credit-based plans from ₹1,500-₹5,000/month + GST (3–30 credits/month). Each credit unlocks one full company profile.
- **Company Listing**: ₹500 + GST/year for a verified company profile with CA-certified financials.
- **Guest Access**: 2 free credits on sign-up to explore the platform without payment.
- **Credit System**: Research members unlock individual companies using credits. Unlocked companies remain accessible for the membership duration.
- **Auth**: Email + password with JWT, PAN verification for research members.
- **Refunds**: Governed by cooling-off periods under Indian consumer law. Refunded via Razorpay within 5–7 business days.
- **Compliance**: DPDP Act 2023, IT Act grievance redressal with defined SLAs (24h acknowledgement, 30-day resolution).
- **Platform**: Corporate intelligence only — NOT a stock exchange, broker, investment advisor, or NBFC.

## Project Structure

```
├── backend/                          # Spring Boot 3.x Java 21 (Maven)
│   └── src/main/java/com/dob/
│       ├── domain/                   # Domain models, events, exceptions (no framework deps)
│       │   ├── model/                # Core entities: Company, User, Membership, etc.
│       │   ├── repository/           # Repository interfaces (ports)
│       │   ├── exception/            # Domain exceptions
│       │   └── event/                # Domain events
│       ├── application/              # Use cases, DTOs, services
│       │   ├── service/              # Business logic services
│       │   └── dto/                  # Request/response DTOs
│       └── infrastructure/           # Adapters, JPA, security, web, config
│           ├── persistence/          # JPA entities & repository adapters
│           ├── security/             # JWT, OAuth2, UserPrincipal
│           ├── web/                  # REST controllers
│           ├── config/               # Seed data, application config
│           ├── payment/              # Razorpay integration
│           └── storage/              # MinIO/S3 file storage
├── frontend/                         # React Native Expo TypeScript
│   └── src/
│       ├── app/                      # Expo Router pages (file-based routing)
│       │   ├── (public)/             # Landing, pricing, legal pages
│       │   ├── (auth)/               # Login, register, forgot-password
│       │   ├── (authenticated)/      # Companies database, company detail
│       │   ├── (member)/             # Dashboard, downloads, profile, credits
│       │   ├── (company)/            # Company dashboard, listings, financials
│       │   ├── (admin)/              # Admin dashboard, approvals, refunds, grievances
│       │   └── payment.tsx           # Payment / checkout screen
│       ├── components/               # Reusable UI components
│       │   ├── ChatBot.tsx           # Floating FAQ chatbot
│       │   ├── LockedCompanyCard.tsx # Masked company preview for non-members
│       │   ├── PremiumCompanyCard.tsx# Full detail card for unlocked companies
│       │   ├── UpgradeBottomSheet.tsx# Credit upgrade prompt
│       │   ├── Button.tsx, Card.tsx, Badge.tsx, Modal.tsx, etc.
│       │   └── SignOutButton.tsx
│       ├── services/                 # Axios API client & mock API
│       ├── hooks/                    # Custom hooks (useAuth, useCompanies)
│       ├── store/                    # Zustand state management
│       └── theme/                    # Design tokens
├── docker/                           # Dockerfiles & nginx config
├── k8s/                              # Kubernetes manifests (Deployment, Service, ConfigMap, HPA)
├── .github/workflows/                # CI/CD (GitHub Actions)
├── docker-compose.yml                # Local dev orchestration (5 containers)
└── spec.md                           # Architecture specification (20-section codegen spec)
```

## API Endpoints

### Authentication
| Method | Path                   | Auth | Description |
|--------|------------------------|------|-------------|
| POST   | `/api/auth/register`   | No   | Register research member |
| POST   | `/api/auth/login`      | No   | Login (email + password + PAN OTP) |
| POST   | `/api/auth/refresh`    | No   | Refresh JWT token |

### Companies
| Method | Path                              | Auth       | Description |
|--------|-----------------------------------|------------|-------------|
| GET    | `/api/companies`                  | No         | Search companies (masked for free users) |
| GET    | `/api/companies/{id}`             | No         | Company detail (masked if locked) |
| POST   | `/api/companies`                  | COMPANY    | Create listing |
| POST   | `/api/companies/{id}/upload-document` | COMPANY | Upload documents |

### Credit Unlock System
| Method | Path                              | Auth       | Description |
|--------|-----------------------------------|------------|-------------|
| POST   | `/api/unlock/{companyId}`         | MEMBER     | Unlock company profile (costs 1 credit) |
| GET    | `/api/unlock/{companyId}/status`  | MEMBER     | Check if company is unlocked |
| POST   | `/api/unlock/batch-status`        | MEMBER     | Batch check unlock status |
| GET    | `/api/unlock/companies`           | MEMBER     | List unlocked companies |
| GET    | `/api/unlock/credits`             | MEMBER     | Get credit history |
| GET    | `/api/unlock/summary`             | MEMBER     | Get credit summary |
| GET    | `/api/unlock/activity`            | MEMBER     | Get recent activity log |

### Subscriptions & Payments
| Method | Path                              | Auth       | Description |
|--------|-----------------------------------|------------|-------------|
| GET    | `/api/subscriptions/plans`        | No         | List credit plans |
| POST   | `/api/subscriptions/create`       | Auth       | Create subscription |
| POST   | `/api/payments/simulate-success`  | Auth       | Simulate payment success (demo) |

### Memberships
| Method | Path                              | Auth       | Description |
|--------|-----------------------------------|------------|-------------|
| GET    | `/api/memberships/me`             | Auth       | My membership details |
| GET    | `/api/memberships/history`         | Auth       | Membership history |

### Admin
| Method | Path                                        | Auth  | Description |
|--------|----------------------------------------------|-------|-------------|
| GET    | `/api/admin/companies/pending`               | ADMIN | List pending company approvals |
| GET    | `/api/admin/companies/pending/details`       | ADMIN | Pending companies with full details |
| POST   | `/api/admin/companies/{id}/approve`          | ADMIN | Approve company listing |
| POST   | `/api/admin/companies/{id}/reject`           | ADMIN | Reject company listing |
| GET    | `/api/admin/members`                         | ADMIN | List research members |
| GET    | `/api/admin/members/count`                   | ADMIN | Member count |
| GET    | `/api/admin/members/{id}`                    | ADMIN | Member details |
| GET    | `/api/admin/company-members`                 | ADMIN | List company members |
| GET    | `/api/admin/company-members/{id}`            | ADMIN | Company member details |
| POST   | `/api/admin/members/{id}/revoke-membership`  | ADMIN | Revoke membership |
| POST   | `/api/admin/members/{id}/cancel-membership`  | ADMIN | Cancel membership |
| POST   | `/api/admin/members/{id}/refund`             | ADMIN | Process refund |

### Grievances
| Method | Path                   | Auth  | Description |
|--------|------------------------|-------|-------------|
| POST   | `/api/grievances`      | Auth  | File grievance |
| GET    | `/api/grievances`      | Auth  | List my grievances |
| GET    | `/api/grievances/{id}` | Auth  | Grievance detail |

### User Profile
| Method | Path                              | Auth | Description |
|--------|-----------------------------------|------|-------------|
| GET    | `/api/users/me`                   | Auth | Get profile |
| PUT    | `/api/users/me`                   | Auth | Update profile |
| PUT    | `/api/users/me/pan`               | Auth | Update PAN details |

Full API docs: http://localhost:8080/swagger-ui.html (when backend is running)

## Legal

DataOfBusiness is a corporate intelligence database and research platform. It is NOT a stock exchange, securities marketplace, broker, investment adviser, research analyst, NBFC, P2P platform, fundraising platform, or loan marketplace. The platform facilitates no transactions of any kind. All subscription fees are for information access only.

Compliant with India's DPDP Act 2023. All financial data is CA-certified. Payments processed via Razorpay.
