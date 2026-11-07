# Quickstart & Validation Guide: FinSync Core System

**Feature Branch**: `001-finsync-core-system` | **Date**: 2026-11-04

This guide provides instructions to spin up the required local infrastructure and execute end-to-end validation scenarios for the FinSync Core System.

---

## 1. Prerequisites

- **Docker & Docker Compose**: v24.0+
- **JDK**: Java 21 LTS
- **Build Tool**: Apache Maven 3.9+ (or use `./mvnw` in `src/backend/`)
- **Python**: 3.11+ (for AI Microservice)
- **Node.js**: 20 LTS (for Admin Web portal)
- **Android Studio**: Ladybug / Jellyfish (Kotlin 2.0+, Gradle 8.5+)

---

## 2. Infrastructure Setup

From the repository root directory:

```bash
# 1. Start PostgreSQL 16
docker compose up -d postgres

# 2. Verify database health
docker compose ps
# Expected: finsync-postgres is healthy on port 5432
```

---

## 3. Running Services

### 3.1. Backend API (Spring Boot 4.1.1)

```bash
cd src/backend
./mvnw clean spring-boot:run
```
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification endpoint: `http://localhost:8080/v3/api-docs`
- Flyway migrations run automatically upon startup.

### 3.2. AI & Notification Parsing Microservice (Python FastAPI)

```bash
cd src/ai-service
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --port 8001 --reload
```
- Interactive API Docs: `http://localhost:8001/docs`

---

## 4. End-to-End Validation Scenarios

### Scenario 1: Authentication & Multi-Wallet Net Worth (User Story 1, FR-002)

1. **Register & Login**:
   - `POST /api/v1/auth/register` with email `alice@example.com` and password `Password123!`.
   - `POST /api/v1/auth/login` → copy returned JWT token.
2. **Create Wallets**:
   - Create Bank wallet: `POST /api/v1/wallets` (`name: "VCB"`, `type: "BANK"`, `balance: 10000000`).
   - Create Credit Card wallet: `POST /api/v1/wallets` (`name: "Visa"`, `type: "CREDIT"`, `balance: 0`).
3. **Log Credit Expense & Verify Net Worth**:
   - `POST /api/v1/transactions` (`walletId: Visa`, `amount: 2000000`, `direction: "EXPENSE"`).
   - `GET /api/v1/wallets`
   - **Expected Outcome**:
     - Visa balance reflects `2000000` (outstanding debt).
     - Calculated `netWorth` equals `8000000` (10,000,000 Bank - 2,000,000 Credit Debt).

---

### Scenario 2: User-Initiated SMS Parsing (FR-003, Principle I)

1. **Paste Notification Text**:
   - `POST /api/v1/parse-notification` (on port 8001) with payload:
     `{"rawText": "TK 123456 tai VCB -150,000 VND vao 04/11/2026. ND: An trua com tam"}`
2. **Expected Outcome**:
   - Response status `200 OK`.
   - Extracted payload: `{"amount": 150000.0, "direction": "EXPENSE", "suggestedCategory": "Dining Out", "counterparty": "An trua com tam"}`.
   - User reviews and confirms draft in mobile UI before persisting to backend.

---

### Scenario 3: Group Expense Sharing & Debt Simplification (User Story 2, FR-007, FR-008, SC-002)

1. **Create Shared Ledger**:
   - `POST /api/v1/ledgers` with title `"Chuyen di Vung Tau"`.
   - Invite Members B and C.
2. **Log Expenses**:
   - Member A pays `900,000 VND` split equally (A, B, C) → B owes A 300,000; C owes A 300,000.
   - Member B pays `300,000 VND` split for A and B (50/50: 150,000 each) → A owes B 150,000.
3. **Query Settlement Plan**:
   - `GET /api/v1/ledgers/{id}/settlement-plan`
4. **Expected Outcome**:
   - System reduces pairwise transfers into simplified plan:
     - Member B pays Member A: `150,000 VND`.
     - Member C pays Member A: `300,000 VND`.
   - Total transfers reduced from 3 transactions to 2 transactions (33.3% reduction, satisfying SC-002).

---

### Scenario 4: Manual Settlement & Owner Dispute Override (User Story 2, FR-009)

1. **Submit Settlement**:
   - Member C pays Member A `300,000 VND` via external banking app and records it in FinSync:
     `POST /api/v1/ledgers/{id}/settlements` (`payeeId: Member A`, `amount: 300000`). Status = `PENDING`.
2. **Owner Dispute Override**:
   - Member A is inactive or unresponsive.
   - Ledger Owner inspects bank transfer screenshot and resolves:
     `PUT /api/v1/ledgers/{id}/settlements/{settlementId}/resolve` with `{"decision": "CONFIRM", "reason": "Verified bank transfer receipt via screenshot"}`.
3. **Expected Outcome**:
   - Settlement transitions to `OWNER_OVERRIDDEN`.
   - Member C net balance updates to `0.00`.
   - Audit log records owner action and justification.

---

### Scenario 5: Admin Account Governance (User Story 4, FR-013, SC-005)

1. **Lock User**:
   - Admin sends `PUT /api/v1/admin/users/{userId}/lock`.
2. **Verify Session Invalidation**:
   - User attempts `GET /api/v1/wallets` with previously issued JWT.
   - **Expected Outcome**: HTTP `401 Unauthorized` / `403 Forbidden` returned in < 2 seconds.
