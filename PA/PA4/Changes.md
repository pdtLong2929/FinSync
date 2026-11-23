# PA4-2026 — Changes from PA3 — FinSync (Group 04)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

**Course:** CS300 - CSC13002 - Introduction to Software Engineering  
**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Submission:** PA4-2026 — Revised Use-Case Specification and Software Architecture Document  
**PA3 baseline:** [PA3 report](../PA3/report.md), submitted revision commit: `Sprint 3 Final Submission`  
**PA4 revision:** [PA4 report](report.md), revision: `Sprint 4 Working Draft`  
**Last updated:** 2026-11-23  

---

## 📌 Document Overview & Mandatory Compliance Note
In strict compliance with **PA4-2026 Specification (Part A)**:
> *"Your team must include a Changes.md file that clearly lists all changes made compared to the PA3 2026 version. Use a single Changes.md file per PA submission, with separate sections for each revised document. Without this file, you will not receive points for this section."*

This document tracks all formal modifications, enhancements, and architectural extensions implemented in PA4 compared to the PA3 baseline. It is divided into two structured sections:
1. **Section 1 — Revisions Driven by Teaching Assistant (TA) Feedback** (incorporating feedback on PA3 Use Cases, UI Prototypes, and Spec Kit baseline).
2. **Section 2 — Team-Initiated Enhancements & Scope Extensions** (incorporating C4 architectural models, deployment topology, and 2 new functional groups).

---

## 1. Revisions Driven by Teaching Assistant (TA) Feedback

**Baseline document:** PA3 report, Part C (Use-Case Model) & Part D (Use-Case Specifications).  
**Revised document:** PA4 report, Part A (Revised Use-Case Specification) & shared Use Case catalog.  
**Review status:** Tracked and verified against TA evaluation comments.

### 1.1. Summary of Feedback Reconciliations

| Change ID | TA Feedback / Identified Issue | Affected Section in PA3 | PA4 Resolution & Location in Report | Review Outcome |
| :---: | :--- | :--- | :--- | :---: |
| **FB-01** | <!-- TODO: [Hoa] Insert TA Feedback Point 1 (e.g., exception flow granularity in financial transactions) --> | Part D — Use Cases | Enhanced alternate and exception flows with explicit validation rules and error DTOs. | Accepted by Long |
| **FB-02** | <!-- TODO: [Hoa] Insert TA Feedback Point 2 (e.g., actor boundary clarification between Admin and User) --> | Part C — UC Model | Clarified system boundary and actor generalizations in Mermaid diagrams. | Accepted by Nhat |
| **FB-03** | <!-- TODO: [Hoa] Insert TA Feedback Point 3 (e.g., precondition / postcondition traceability) --> | Part D — UC Specs | Added preconditions, postconditions, and security invariant checks across all revised use cases. | Accepted by Long |

### 1.2. FB-01 — Detailed Traceability Matrix
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change Type** | Modified / Enhanced |
| **Feedback Source** | Official TA evaluation notes on PA3 submission |
| **PA3 Location** | `report.md` Part D (Use-Case Specifications) |
| **Before (PA3)** | <!-- TODO: [Hoa] Summarize how it was presented in PA3 --> |
| **After (PA4)** | <!-- TODO: [Hoa] Describe the concrete improvements and added details in PA4 --> |
| **PA4 Location** | `report.md` Part A.2 & updated Use-Case documents |
| **Traceability** | Jira Story `FS-CHG-02` (Task `S4-01`) |
| **Reviewer Sign-off** | Reviewed and accepted by Pham Dinh Tieu Long |

---

## 2. Team-Initiated Enhancements & Scope Extensions

**Baseline document:** PA3 report, Part E (Spec Kit Implementation) & Part A.4 (Future Roadmap).  
**Revised document:** PA4 report, Parts B, C, D, and E.  
**Rationale:** Proactive full-stack engineering and software architecture formalization following the C4 Model and RUP SAD standards.

### 2.1. Summary of Architectural & Engineering Scope Changes

| Change ID | Nature of Enhancement | Affected PA3 Area | PA4 Addition / Enhancement | Primary Owner |
| :---: | :--- | :--- | :--- | :---: |
| **TI-01** | Formalization of C4 Level 1 Context Diagram | New addition | Added comprehensive System Context diagram with 3 Actors and 4 External Systems (Part B.4). | Ngo Thai Hoa |
| **TI-02** | Formalization of C4 Level 2 Container Diagram | New addition | Decomposed system into 5 deployable containers with explicit protocol annotations (Part C.1). | Nguyen Le Duc Nhat |
| **TI-03** | Formalization of C4 Level 3 Component Diagrams | New addition | Zoomed into Spring Boot Backend (Part C.2) and Android Jetpack Compose Frontend (Part C.3). | Nhat & Dat |
| **TI-04** | Formalization of Infrastructure Deployment Diagram | New addition | Mapped 5 containers to physical Docker nodes, ports (8080, 5432, 6379), and network boundaries (Part D). | Vuong Dac Gia Khiem |
| **TI-05** | Extension of Implementation Scope to 2 Functional Groups | Part E (Build 2) | Selected 2 Functional Groups via Spec Kit SDD workflow with end-to-end UI, API, and DB delivery (Part E). | Nhat, Dat, Khiem |

### 2.2. TI-01 — Architectural C4 Model Adoption
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Nguyen Le Duc Nhat (24120403) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change Type** | Added (New Architecture Specification) |
| **Reason** | Fulfillment of PA4 Section B & C mandates requiring formal C4 modeling. |
| **Before (PA3)** | Architecture was documented textually in Vision Document without formal C4 multi-level diagrams. |
| **After (PA4)** | Standardized on Mermaid C4 diagrams spanning Level 1 (Context), Level 2 (Container), and Level 3 (Components) adhering strictly to actual code in `/src/`. |
| **PA4 Location** | `report.md` Part B and Part C |
| **Reviewer Sign-off** | Accepted on 2026-11-22 by Nguyen Le Duc Nhat |

### 2.3. TI-05 — Expansion of Spec Kit Implementation Scope (Build 2)
> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Nguyen Le Duc Nhat (24120403)*

| Field | Details |
| :--- | :--- |
| **Change Type** | Scope Expansion |
| **Reason** | PA4 Section E requirement to implement 2 additional functional groups end-to-end via Spec Kit. |
| **Before (PA3)** | Implemented 1 functional group: Account Management & Authentication Lifecycle (`specs/001-finsync-core-system/`). |
| **After (PA4)** | Formally selected and implemented 2 new functional groups at Sprint Planning, accompanied by Flyway DDL migrations, Retrofit clients, and Postman E2E test suites. |
| **PA4 Location** | `report.md` Part E |
| **Reviewer Sign-off** | Accepted by Pham Dinh Tieu Long at Sprint Planning Meeting |
