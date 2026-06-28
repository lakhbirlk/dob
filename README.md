# DataOfBusiness (DoB)

India's Private Company Intelligence Platform — corporate research and due-diligence information service.

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌────────────┐
│  React      │────▶│  Spring Boot │────▶│ PostgreSQL │
│  Native     │     │  Java 21     │     │            │
│  Expo       │     │  REST API    │     │  Redis     │
│  TypeScript │     │              │     │  MinIO/S3  │
└─────────────┘     └──────────────┘     └────────────┘
                           │
                     ┌─────┴─────┐
                     │  Razorpay │
                     │  Payments │
                     └───────────┘
```

## Quick Start

### Prerequisites
- Java 21
- Node.js 20+
- Docker & Docker Compose

### Local Development

```bash
# 1. Start infrastructure
docker-compose up -d postgres redis minio

# 2. Start backend
cd backend
./gradlew bootRun

# 3. Start frontend
cd frontend
npm install
npx expo start
```

### Docker Compose (All Services)

```bash
cp .env.example .env
# Edit .env with your keys
docker-compose up -d
```

Services:
- Frontend: http://localhost:8081
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MinIO Console: http://localhost:9001

## Key Business Rules

- **Membership**: ₹2500 + GST/month, 50 downloads/month limit
- **Company Listing**: ₹500 + GST/year
- **Auth**: Email + password with JWT (PAN verification is a profile update, not a login gate)
- **Compliance**: DPDP Act 2023, IT Act grievance redressal
- **Platform**: Corporate intelligence only — NOT a stock exchange, broker, or investment advisor

## Project Structure

```
├── backend/              # Spring Boot 3.x Java 21
│   ├── domain/           # Domain models, events, exceptions (no framework deps)
│   ├── application/      # DTOs, services, ports
│   └── infrastructure/   # JPA, security, payment, storage, web
├── frontend/             # React Native Expo TypeScript
│   └── src/
│       ├── app/          # Expo Router pages
│       ├── components/   # Reusable UI
│       ├── services/     # API client
│       └── store/        # Zustand state
├── docker/               # Dockerfiles & nginx config
├── k8s/                  # Kubernetes manifests
├── .github/workflows/    # CI/CD
├── docker-compose.yml    # Local dev orchestration
└── spec.md               # Architecture specification
```

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | No | Register |
| POST | `/api/auth/login` | No | Login (email+password) |
| POST | `/api/auth/refresh` | No | Refresh JWT |
| GET | `/api/companies` | No | Search companies |
| GET | `/api/companies/{id}` | No | Company detail |
| POST | `/api/companies` | COMPANY_USER | Create listing |
| GET | `/api/memberships/me` | Auth | My membership |
| POST | `/api/payments/create-order` | Auth | Create Razorpay order |
| POST | `/api/payments/verify` | Auth | Verify payment |
| POST | `/api/grievances` | Auth | File grievance |
| GET | `/api/admin/companies/pending` | ADMIN | Pending approvals |

Full API docs: http://localhost:8080/swagger-ui.html
