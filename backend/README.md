# DataOfBusiness Backend

Spring Boot 3.3 + Java 21 REST API with hexagonal architecture.

## Tech Stack

- **Framework**: Spring Boot 3.3, Spring Security 6, Spring Data JPA
- **Database**: PostgreSQL 16 (Flyway migrations)
- **Cache**: Redis 7
- **Auth**: JWT (access + refresh tokens), BCrypt
- **Payments**: Razorpay
- **Storage**: MinIO (dev) / AWS S3 (prod)
- **API Docs**: OpenAPI 3 (Swagger UI)
- **Resilience**: Resilience4J

## Architecture Layers

```
Controller → Application Service → Domain Model/Repository ← Infrastructure Adapter
```

Hexagonal (Ports & Adapters) with DDD principles:
- **Domain**: Pure business logic, no framework dependencies
- **Application**: DTOs, mappers, application services, port interfaces
- **Infrastructure**: JPA entities, Spring Data repositories, security, payment gateway, storage service

## Setup

```bash
# Database
createdb dob

# Run
./gradlew bootRun

# Test
./gradlew test
```

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/api-docs
