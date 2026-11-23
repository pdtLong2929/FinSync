# PA4-2026 — Team AI Usage Notes — FinSync (Group 04)
> *Performed by: Pham Dinh Tieu Long (24120087) | Reviewed by: Vuong Dac Gia Khiem (24120342) | Edited by: Pham Dinh Tieu Long (24120087)*

**Course:** CS300 - CSC13002 - Introduction to Software Engineering  
**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Submission:** Project Assignment 4 (PA4-2026) — Sprint 4  
**Reporting Period:** `2026-11-17 to 2026-12-06`  
**Group:** Group 04  

---

## 📌 Declaration of Academic Integrity & AI Transparency
All five team members of Group 04 utilized generative AI assistants (Claude 3.5 Sonnet, ChatGPT-4o, and Google Gemini 1.5 Pro) during Sprint 4 / PA4 to accelerate software architecture documentation, format Mermaid diagrams, draft OpenAPI 3.0 schemas, and structure E2E test cases. In strict accordance with HCMUS CS300 academic integrity guidelines, **no generated code or documentation was incorporated without critical evaluation, manual modification, and mandatory peer review**.

---

## 1. Pham Dinh Tieu Long (`24120087` — Project Manager / Group Leader)
> *Performed by: Pham Dinh Tieu Long (24120087) | Reviewed by: Ngo Thai Hoa (24120051) | Edited by: Pham Dinh Tieu Long (24120087)*

### Log 1: Jira Sprint 4 WBS & Story Point Allocation
* **Date & Time:** `2026-11-17 10:15`
* **Tool / Model:** Claude 3.5 Sonnet (Anthropic)
* **Prompt Text:** *"Generate a balanced 15-task Work Breakdown Structure for Sprint 4 of our FinSync personal finance software engineering project based on PA4-2026 requirements, spanning C4 architecture diagrams, Spec Kit 2 functional groups, and Scrum ceremonies."*
* **AI Output Summary:** Provided 15 candidate task titles with estimated hours and standard Scrum deliverable milestones.
* **Student Validation & Modification:** Mapped tasks to team roles, assigned realistic capacity (176 hours total, 65 Story Points), aligned checkpoints with member availability, and logged on Jira.

### Log 2: Scrum Minutes Formatting & Action Register
* **Date & Time:** `2026-11-23 20:45`
* **Tool / Model:** Gemini 1.5 Pro (Google)
* **Prompt Text:** *"Format our raw meeting notes from Weekly Scrum #1 into standard Agile meeting minutes with an Action Register tracking table."*
* **AI Output Summary:** Produced structured Markdown tables with Action IDs, Owners, and Reviewers.
* **Student Validation & Modification:** Replaced placeholder items with actual discussions on Flyway database migrations and Android Retrofit integration.

---

## 2. Nguyen Phu Dat (`24120038` — UI/UX Designer & Frontend Lead)
> *Performed by: Nguyen Phu Dat (24120038) | Reviewed by: Nguyen Le Duc Nhat (24120403) | Edited by: Nguyen Phu Dat (24120038)*

### Log 1: C4 Level 3 Frontend Component Mermaid Structuring
* **Date & Time:** `2026-11-22 14:30`
* **Tool / Model:** ChatGPT-4o (OpenAI)
* **Prompt Text:** *"Help me structure a valid Mermaid flowchart TD diagram showing the Clean Architecture / MVVM layers of our Android Jetpack Compose app, including Screen Composables, StateFlow ViewModels, Data Repositories, and Retrofit HTTP Client."*
* **AI Output Summary:** Generated Mermaid diagram syntax representing layers.
* **Student Validation & Modification:** Renamed all node identifiers and labels to match the physical Kotlin classes and packages in `/src/mobile/` (e.g. `HomeScreenComposable`, `HomeViewModel`, `EncryptedPreferencesHelper`).

---

## 3. Nguyen Le Duc Nhat (`24120403` — Backend Lead / System Architect)
> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Ngo Thai Hoa (24120051) | Edited by: Nguyen Le Duc Nhat (24120403)*

### Log 1: C4 Level 2 Container Diagram & Protocol Mapping
* **Date & Time:** `2026-11-21 16:00`
* **Tool / Model:** Claude 3.5 Sonnet (Anthropic)
* **Prompt Text:** *"Draft a C4 Level 2 Container Diagram in Mermaid syntax for a financial system with Android client, React Admin, Spring Boot REST API, PostgreSQL database, and Redis cache, showing communication protocols on arrows."*
* **AI Output Summary:** Provided Mermaid diagram connecting containers with protocol labels.
* **Student Validation & Modification:** Adjusted network port annotations (8080, 5432, 6379), verified Docker Compose internal network bindings, and wrote the container catalog documentation.

### Log 2: OpenAPI 3.0 YAML Schema Formulation
* **Date & Time:** `2026-11-20 21:00`
* **Tool / Model:** ChatGPT-4o (OpenAI)
* **Prompt Text:** *"Generate an OpenAPI 3.0 specification snippet for RESTful financial transaction endpoints with RFC 7807 problem details error responses."*
* **AI Output Summary:** Provided OpenAPI YAML endpoints with request and response schemas.
* **Student Validation & Modification:** Aligned schemas with Java DTOs in Spring Boot, added JWT Bearer security schemes, and committed to `contracts/backend-api.yaml`.

---

## 4. Vuong Dac Gia Khiem (`24120342` — QA Lead & DevOps Engineer)
> *Performed by: Vuong Dac Gia Khiem (24120342) | Reviewed by: Nguyen Le Duc Nhat (24120403) | Edited by: Vuong Dac Gia Khiem (24120342)*

### Log 1: Infrastructure Deployment Mermaid Topology
* **Date & Time:** `2026-11-24 11:20`
* **Tool / Model:** ChatGPT-4o (OpenAI)
* **Prompt Text:** *"Create a Mermaid deployment diagram illustrating an Android physical/emulator device communicating with a Docker Compose host containing Spring Boot, PostgreSQL, and Redis containers on an internal bridge network."*
* **AI Output Summary:** Produced a nested subgraph Mermaid deployment diagram.
* **Student Validation & Modification:** Verified against actual `docker-compose.yml`, updated IP bridge references (`10.0.2.2`), and documented port mapping specifications.

### Log 2: English Video Demo Script Drafting
* **Date & Time:** `2026-12-01 19:00`
* **Tool / Model:** Claude 3.5 Sonnet (Anthropic)
* **Prompt Text:** *"Draft a 6-minute spoken English narration script for a software demonstration video covering C4 architecture, live mobile app walkthrough, and database state verification in PostgreSQL terminal."*
* **AI Output Summary:** Provided timed script outline with professional engineering terminology.
* **Student Validation & Modification:** Rehearsed pronunciation, adjusted pacing to match real screen recordings, and recorded voiceover narration.

---

## 5. Ngo Thai Hoa (`24120051` — Requirement Engineer / Tech Writer)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

### Log 1: Architectural Rationale Synthesis for Tech Stack
* **Date & Time:** `2026-11-19 15:45`
* **Tool / Model:** Claude 3.5 Sonnet (Anthropic)
* **Prompt Text:** *"Synthesize architectural rationales comparing Jetpack Compose vs Android XML, Spring Boot 3 vs Node.js, and PostgreSQL 16 vs MongoDB for a personal finance system requiring strong ACID transactions."*
* **AI Output Summary:** Provided comparative architectural trade-offs across reliability, developer velocity, and ACID integrity.
* **Student Validation & Modification:** Integrated FinSync specific constraints, refined table entries in Section B.2, and reviewed with Backend Lead.

### Log 2: C4 Level 1 Context Diagram Mermaid Layout
* **Date & Time:** `2026-11-20 09:30`
* **Tool / Model:** Gemini 1.5 Pro (Google)
* **Prompt Text:** *"Write Mermaid syntax for a C4 Level 1 System Context Diagram with 3 user roles (Regular User, Group Owner, Admin) and 4 external systems (Google OAuth, Banking Ingest, S3 Storage, Gemini AI)."*
* **AI Output Summary:** Generated Mermaid diagram.
* **Student Validation & Modification:** Verified diagram rendered cleanly without text clipping, added explanatory narrative, and validated with Long.
