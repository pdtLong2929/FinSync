# FinSync — Smart Personal Finance & Group Expense Management with AI

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Flutter](https://img.shields.io/badge/Flutter-3.x-02569B?style=for-the-badge&logo=flutter&logoColor=white)](https://flutter.dev/)
[![React](https://img.shields.io/badge/React-18.x-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Google Gemini](https://img.shields.io/badge/Google_Gemini-API-8E75C2?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-Academic_Use_Only-blue?style=for-the-badge)](#academic-disclaimer)

> **CS300 — CSC13002: Introduction to Software Engineering | Fall 2026**  
> **Faculty of Information Technology, VNU-HCM University of Science (HCMUS)**  
> **Group 04 — FinSync**

---

## 📖 Table of Contents

- [Overview](#-overview)
- [The Problem & Value Proposition](#-the-problem--value-proposition)
- [Key Features](#-key-features)
  - [1. Personal Finance Management](#1-personal-finance-management)
  - [2. Group Expense & Smart Debt Settlement](#2-group-expense--smart-debt-settlement)
  - [3. AI Financial Advisor](#3-ai-financial-advisor)
  - [4. Web Administration Portal](#4-web-administration-portal)
- [System Architecture & Tech Stack](#-system-architecture--tech-stack)
- [Repository Structure](#-repository-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Backend Setup (Spring Boot)](#backend-setup-spring-boot)
  - [Mobile Client Setup (Flutter)](#mobile-client-setup-flutter)
  - [Admin Web Setup (React)](#admin-web-setup-react)
- [Agile & Scrum Engineering Process](#-agile--scrum-engineering-process)
- [Team Members & Contribution](#-team-members--contribution)
- [Academic Disclaimer](#-academic-disclaimer)

---

## 🌟 Overview

**FinSync** is a unified financial management platform engineered to bridge the gap between **individual asset tracking** (like *Money Lover*) and **collaborative group expense splitting** (like *Splitwise*), empowered by an intelligent **AI Financial Advisor**.

### 🛡️ Core Business Principle
FinSync acts purely as a **manual bookkeeping, debt calculation, and financial advisory engine**. The system **never** directly accesses real banking credentials, holds user funds, or triggers automated bank account deductions. All settlements are finalized externally by users and recorded within the platform for transparent reconciliation.

---

## 💡 The Problem & Value Proposition

| Pain Point in Existing Solutions | FinSync Solution |
|---|---|
| **Fragmented Experience:** Personal finance apps lack multi-user bill splitting; bill-splitting apps lack personal net-worth dashboards and custom budgets. | **Single Unified Ecosystem:** Harmonizes private personal ledgers and shared group funds in one place without context switching. |
| **Complex Group Debts:** Roommates, travel buddies, and event organizers face messy transfer chains ("A owes B, B owes C, C owes A"). | **Smart Debt Simplification:** Graph-based debt optimization algorithm minimizes total transaction hops and payment counts. |
| **Passive Record Keeping:** Traditional expense apps only record past logs without providing proactive future spending insights. | **AI Financial Advisor:** Context-aware LLM scans monthly spending patterns, flags budget anomalies, and suggests optimal categorical allocations. |

---

## 🚀 Key Features

```mermaid
graph TD
    User([FinSync User]) --> Personal[Personal Financial Domain]
    User --> Group[Group Expense Domain]
    User --> AI[AI Advisory Engine]

    subgraph "Personal Finance"
        Personal --> Wallets[Multi-wallet Bookkeeping]
        Personal --> TxTrack[Income/Expense Tracking]
        Personal --> Budgets[Budget Alerts 80%/100%]
        Personal --> Goals[Savings Goals Tracker]
    end

    subgraph "Group Finance"
        Group --> GroupWallet[Shared Group Wallets]
        Group --> SplitEngine[Equal / Custom / % Split]
        Group --> Optimize[Debt Simplification]
        Group --> Settle[Manual Settlement Verification]
    end

    subgraph "AI Assistant"
        AI --> Anomaly[Spending Spike Detection]
        AI --> Recommendation[Budget Recommendation]
        AI --> SavingsAdvice[Savings Goal Health Check]
    end
```

### 1. Personal Finance Management
- **Multi-Wallet Bookkeeping:** Manage distinct manual ledgers (Cash, Bank Accounts, Credit Cards) with real-time aggregate net-worth visualization.
- **Transaction Logging:** Fast expense/income recording with customizable categories, notes, timestamps, and receipt image attachments.
- **Budgeting & Threshold Alerts:** Category-based weekly and monthly caps with automated visual triggers at 80% and 100% capacity.
- **Savings Goals:** Set quantifiable milestones (e.g., emergency fund, travel) and track deposit progress over time.

### 2. Group Expense & Smart Debt Settlement
- **Shared Group Ledgers:** Organize shared expenses for roommates, trips, and projects with granular Owner vs. Member access roles.
- **Flexible Splitting Engine:** Split expenses equally, by exact custom monetary amounts, or by percentage ratios.
- **Smart Debt Minimization:** Automated simplification algorithm minimizes the total number of cross-member bank transfers required.
- **Settlement Tracking:** Real-time "Who owes Whom" matrices, payment reminder alerts, and manual confirmation workflows.

### 3. AI Financial Advisor
- **Pattern Scanning:** Evaluates full previous month transactional logs across personal and shared categories.
- **Anomaly Detection:** Flags sudden categorical expenditure surges (e.g., *"Dining out increased by 40% compared to your 3-month average"*).
- **Proactive Budget Suggestions:** Recommends rational category allocations for the upcoming month based on historical cash flow and savings targets.

### 4. Web Administration Portal
- **User Moderation:** View, lock, and unlock user accounts.
- **Default Taxonomy:** Manage global system expense/income categories.
- **Platform Analytics:** Real-time metrics on user growth, group activity, and platform usage.

---

## 🏗️ System Architecture & Tech Stack

```mermaid
graph LR
    subgraph Clients
        Mobile["📱 Mobile App (Flutter / Dart)"]
        AdminWeb["💻 Admin Panel (React.js)"]
    end

    subgraph Backend Services
        Gateway["REST API Server (Spring Boot 3)"]
        Security["Spring Security + JWT"]
    end

    subgraph External & Storage
        DB[("PostgreSQL")]
        AI["🤖 Google Gemini API"]
    end

    Mobile -->|HTTPS / REST API| Security
    AdminWeb -->|HTTPS / REST API| Security
    Security --> Gateway
    Gateway --> DB
    Gateway --> AI
```

- **Mobile Client:** Flutter (Dart) — Cross-platform Android/iOS client, BLoC / Riverpod state architecture, Dio HTTP client.
- **Web Admin Portal:** React.js — Modern administrative control panel.
- **Backend API:** Spring Boot 3 (Java 17+), RESTful API design, Spring Security with stateless JWT authentication, SpringDoc OpenAPI (Swagger).
- **Database:** PostgreSQL (Cloud instance) with Spring Data JPA / Hibernate ORM.
- **AI Engine:** Google Gemini API / OpenAI API integrated securely via backend server.
- **DevOps & Tooling:** Docker, Git, GitHub Actions, Jira Software.

---

## 📂 Repository Structure

```text
FinSync/
├── .gitignore                          # Global project gitignore (secrets, IDE, OS)
├── README.md                           # Project overview and developer onboarding
├── GEMINI.md                           # AI assistant context & software engineering rules
├── WeeklyReport.md                     # Agile Scrum meeting minutes & weekly retrospectives
├── report.md                           # Project assignment final report
├── src/                                # Source code root
│   ├── backend/                        # Spring Boot REST API service
│   │   ├── src/main/java/com/finsync/  # Application controllers, services, repositories
│   │   ├── pom.xml                     # Maven dependencies
│   │   └── .gitignore                  # Java / Maven specific ignore rules
│   ├── mobile/                         # Flutter mobile application
│   │   └── .gitignore                  # Flutter / Dart / Android ignore rules
│   └── admin-web/                      # React.js administration web application
│       └── .gitignore                  # Node.js / React ignore rules
├── docs/                               # Comprehensive project documentation
│   ├── requirements/                   # Vision, use cases, functional & non-functional specs
│   ├── analysis-and-design/            # Architecture, API design, database schema, diagrams
│   ├── management/                     # Scrum meeting notes, sprint backlogs
│   └── test/                           # Test plans, test cases, and test run reports
└── screenshots/                        # Visual artifacts
    ├── app/                            # Mobile & Web screen captures
    ├── jira/                           # Jira board sprint progress screenshots
    └── git/                            # Git commit history & PR verification images
```

---

## 🛠️ Getting Started

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher
- **Maven:** 3.8+ (or use included `./mvnw`)
- **Flutter SDK:** 3.19+ and Dart SDK
- **Node.js:** 18+ and npm
- **PostgreSQL Server:** 15+

### Backend Setup (Spring Boot)
1. Navigate to the backend directory:
   ```bash
   cd src/backend
   ```
2. Configure your environment variables in `src/main/resources/application.properties` (or set environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `GEMINI_API_KEY`).
3. Build and launch the backend server:
   ```bash
   ./mvnw clean spring-boot:run
   ```
4. Access Swagger API documentation at: `http://localhost:8080/swagger-ui.html`

### Mobile Client Setup (Flutter)
1. Navigate to the mobile directory:
   ```bash
   cd src/mobile
   ```
2. Install dependencies:
   ```bash
   flutter pub get
   ```
3. Run the development app on an emulator or connected device:
   ```bash
   flutter run
   ```

### Admin Web Setup (React)
1. Navigate to the admin web directory:
   ```bash
   cd src/admin-web
   ```
2. Install packages and start Vite/Webpack dev server:
   ```bash
   npm install
   npm run dev
   ```

---

## 🔄 Agile & Scrum Engineering Process

The project adheres to Agile/Scrum methodologies across multiple 2-to-3-week Sprints:
- **Sprint Cadence:** 1 Sprint Planning $\rightarrow$ 2 Weekly Scrum Standups $\rightarrow$ 1 Sprint Review & Retrospective.
- **Traceability:** Every technical commit, design task, or document draft is mapped to a dedicated **Jira Issue**.
- **Peer Review & Git Flow:** All functional changes are delivered via dedicated feature branches (`feature/*`, `fix/*`, `docs/*`) and merged strictly through peer-reviewed **Pull Requests**.

---

## 👥 Team Members & Contribution

> **Group 04 — Introduction to Software Engineering (CS300 / CSC13002)**  
> *All members act as Full-Stack Engineers across all lifecycle phases.*

| # | Student ID | Full Name | Email | Primary Responsibility |
|---|:---:|---|---|---|
| 1 | **24120087** | **Phạm Đình Tiểu Long** | phamlongkh2006@gmail.com | **Group Leader** / Project Manager / Scrum Master |
| 2 | **24120403** | **Nguyễn Lê Đức Nhật** | nldnhat182006@gmail.com | UI/UX Designer & Mobile Frontend Lead (Flutter) |
| 3 | **24120051** | **Ngô Thái Hòa** | ngothaihoa235@gmail.com | Backend Architecture & API Lead (Spring Boot) |
| 4 | **24120342** | **Vương Đắc Gia Khiêm** | vuongkhiemvl10@gmail.com | QA Lead & DevOps Engineer (CI/CD, Test Automation) |
| 5 | **24120038** | **Nguyễn Phú Đạt** | nguyennphuudatt@gmail.com | AI Feature Lead & Technical Documentation Lead |

---

## ⚖️ Academic Disclaimer

This project is developed exclusively for academic evaluation under the **CS300 - CSC13002: Introduction to Software Engineering** course at the **Faculty of Information Technology, VNU-HCM University of Science**. All trademarks, brand names, and references (such as Money Lover, Splitwise, Google Gemini) belong to their respective owners and are referenced solely for comparative analysis and educational purposes.
