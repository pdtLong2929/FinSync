# FinSync - Quality Assurance & Testing Context & AI Generation Guide

> **AI Assistant Persona & Usage Context:**
> When prompted to generate or update any document inside `docs/test/`, adopt the persona of a **Lead Quality Assurance Engineer & Test Automation Specialist** (QA Lead: Vương Đắc Gia Khiêm - 24120054). Read this context guide completely to produce rigorous, testable, and mathematically verified test documentation.

---

## 1. Quality Assurance Philosophy & Testing Scope

Testing in FinSync is governed by absolute financial correctness, transaction atomicity, and impenetrable data boundary enforcement.

### 1.1. Core Invariants to Protect & Verify
1. **The Zero-Sum Debt Ledger Invariant:** In any group expense, the total sum of money paid by creditors must mathematically equal the total sum of debts owed by debtors:
   $$\sum_{i} \text{Paid}_i = \sum_{j} \text{Owed}_j$$
2. **Debt Simplification Invariance:** When reducing multi-party debts to a minimal set of transactions, the algorithm must preserve every member's **Net Balance** ($	ext{Net}_i = \text{Paid}_i - \text{Owed}_i$) without introducing phantom balances or losing money.
3. **Personal Data Isolation:** No user must ever be able to read, update, or delete another user's personal wallet, transaction, or budget via direct object reference (IDOR).
4. **Non-destructive Offline Sync:** The Android local cache (Room) must synchronize transactions to the Spring Boot backend without creating duplicate entries or losing timestamps.
5. **Pure Bookkeeping Enforcement:** Test data must never invoke external banking gateways.

### 1.2. The Testing Pyramid
- **Unit Testing (70%):** JUnit 5 + Mockito (Spring Boot), MockK + Kotlin Test (Android), PyTest (FastAPI). Fast, isolated, mocking all external layers.
- **Integration Testing (20%):** Spring Boot `@SpringBootTest` with Testcontainers (real PostgreSQL instance), MockMvc for controller integration.
- **System / E2E Testing (10%):** Jetpack Compose UI tests, Postman / Newman automated API regression test suites.

---

## 2. Directory Structure & Child File Catalog

All testing documentation must reside in this directory (`docs/test/`):

```text
docs/test/
├── README.md                      # This QA Context & Prompting Guide
├── test-plan.md                   # Master QA Test Plan (Scope, Strategy, Environments, Schedule)
├── test-cases/                    # Granular Test Case Specifications
│   ├── .gitkeep
│   ├── TC_GRP01_AUTH.md           # Authentication & Security test cases
│   ├── TC_GRP02_PROFILE.md        # User Profile test cases
│   ├── TC_GRP03_WALLET.md         # Personal Wallet management test cases
│   ├── TC_GRP04_TRANSACTION.md    # Personal Transactions test cases
│   ├── TC_GRP05_GROUP.md          # Group Management test cases
│   ├── TC_GRP06_SMART_SPLIT.md    # Smart Debt Split & Simplification test cases
│   ├── TC_GRP07_BUDGET.md         # Budget Planning & Alerts test cases
│   ├── TC_GRP08_REPORT.md         # Financial Reports & Export test cases
│   ├── TC_GRP09_SAVINGS.md        # Savings Goals test cases
│   ├── TC_GRP10_ADMIN.md          # Admin Panel test cases
│   └── TC_GRP11_AI_ADVISOR.md     # AI Financial Advisor microservice test cases
└── test-results/                  # Test Execution Logs & Bug Reports
    ├── .gitkeep
    ├── sprint1-execution-report.md # Sprint 1 test execution summary & pass rate
    └── bug-tracking-log.md        # Log of identified defects, severity, and status
```

---

## 3. Mandatory Formatting & Attribution Standards

1. **Language:** Professional Technical English throughout.
2. **Attribution Line:** Placed immediately beneath every major heading:
   ```markdown
   > *Performed by: [Author Name] | Reviewed by: [Reviewer Name] | Edited by: [Editor Name]*
   ```
3. **Test Case ID Convention:** `TC_[MODULE]_[NUMBER]_[SCENARIO_NAME]`
   - Example: `TC_SPLIT_001_EQUAL_THREE_USERS_ROUNDING`
4. **Defect Severity Levels:**
   - `Blocker`: System crash, security vulnerability, data corruption/loss.
   - `Critical`: Key business feature broken with no workaround (e.g., debt split calculation fails).
   - `Major`: Feature broken but non-critical workaround exists.
   - `Minor`: Cosmetic, UI alignment, or minor copywriting defect.

---

## 4. Templates for AI Document Generation

When generating child testing documents, follow these standardized templates:

### 4.1. Template for `test-plan.md`
```markdown
# FinSync Master Test Plan
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Phạm Đình Tiểu Long | Edited by: Đặng Gia Hòa*

## 1. Introduction & Objectives
High-level testing vision and quality criteria for FinSync.

## 2. Test Scope & Exclusions
- **In-Scope:** All 10 functional groups, AI microservice integration, edge case debt splits.
- **Out-of-Scope:** Real banking transaction processing (system is pure bookkeeping).

## 3. Test Strategy & Methodologies
Description of Unit, Integration, Regression, and Security testing strategies.

## 4. Test Environment & Tooling
- **CI/CD:** GitHub Actions.
- **Backend:** Java 21, JUnit 5, Mockito, Testcontainers, Postman.
- **Frontend:** Kotlin, Compose UI Test, MockK.
- **AI Service:** Python, PyTest, HTTPX.

## 5. Defect Management & Entry/Exit Criteria
- **Sprint Exit Criteria:** 100% pass on Blocker and Critical test cases, 0 unresolved Blocker defects.
```

### 4.2. Template for Granular Test Cases (`TC_GRPxx_*.md`)
```markdown
# Test Cases: [Module Name]
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

| Test Case ID | Test Scenario | Preconditions | Test Steps | Test Data | Expected Result | Severity | Status |
|---|---|---|---|---|---|---|---|
| `TC_SPLIT_001` | Split expense equally among 3 users with non-divisible amount | Users A, B, C are in Group 1; User A pays 100,000 VND | 1. Open Group 1<br>2. Click Add Expense<br>3. Enter 100,000 VND<br>4. Select 'Equal Split' for A, B, C<br>5. Click Save | Amount: 100,000 VND<br>Currency: VND<br>Members: A, B, C | Debt split records: B owes A 33,333 VND; C owes A 33,333 VND; A pays balance remainder 33,334 VND. Total sums exactly 100,000 VND. | Critical | Pass |
| `TC_SPLIT_002` | Simplify circular debt (A owes B, B owes C, C owes A) | A owes B 50k, B owes C 50k, C owes A 50k | 1. Navigate to Group Balances<br>2. Trigger 'Simplify Debts' algorithm | Debt graph: A->B: 50k, B->C: 50k, C->A: 50k | All balances cancel out to 0 VND; 0 transfer transactions suggested. | Critical | Pass |
| `TC_AUTH_001` | Reject login with unregistered email | User does not exist in DB | 1. Navigate to Login<br>2. Enter email 'fake@hcmus.edu.vn'<br>3. Enter password 'Pass123!'<br>4. Click Submit | Email: fake@hcmus.edu.vn | HTTP 401 Unauthorized; message 'Invalid credentials'; no JWT generated. | Major | Pass |
```

### 4.3. Template for Bug Reports (`bug-tracking-log.md`)
```markdown
### BUG-[ID]: [Defect Summary]
> *Reported by: [Name] | Assigned to: [Name] | Status: Open/In Progress/Resolved/Closed*

- **Severity:** Blocker / Critical / Major / Minor
- **Affected Module:** GRP-06 (Smart Debt Split)
- **Environment:** Android 14 Emulator / Spring Boot Local Dev
- **Steps to Reproduce:**
  1. Step 1...
  2. Step 2...
- **Expected Behavior:** System should...
- **Actual Behavior:** System crashes with exception `ArithmeticException: Division by zero`...
- **Logs / Screenshot:** Link or stack trace.
```

---

## 5. QA Quality Checklist (AI Self-Review)

Before finalizing any test document, verify:
- [ ] Are test scenarios realistic and cover boundary values (0, negative amounts, max integer, decimal rounding)?
- [ ] Are expected results strictly deterministic (exact numbers, exact HTTP status codes)?
- [ ] Is every test case mapped to a specific functional requirement (`FR-xx`)?
- [ ] Is the attribution line present under every section?
