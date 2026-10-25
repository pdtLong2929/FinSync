# PA3-2026

## CS300 - CSC13002 - Introduction to Software Engineering

## Project Assignment 3 (PA3-2026)

**Total scores:** 90 points
**Duration:** 2-3 weeks
**This is a teamwork assignment.**

---

## A - Revised Project Plan - 2nd submission (5 points)

Based on the TA feedback from PA2-2026, revise your project plan to address the identified issues and improve its clarity and detail. Additionally, update the schedule and other sections with any new information available at this stage.

**Important:** Your team must include a `Changes.md` file that clearly lists all changes made compared to the PA2-2026 version. Use a single `Changes.md` file per PA submission, with separate sections for each revised document. Without this file, you will not receive points for this section.

## B - Detailed Vision Document - 2nd submission (5 points)

Update and complete the vision document based on the TA feedback from PA2-2026. Detail product functional and non-functional requirements, alternatives and competitors, user environments.

**Important:** Document all changes in the same `Changes.md` file used for Section A. Without documenting the changes, you will not receive points for this section.

## C - Use-Case Model (10 points)

Based on the requirements documented in the vision document, model use cases of the system in the form of use-case diagrams (one or several diagrams). Each diagram includes use cases, actors, and relationships among them.

**Requirements:**

- Use-case diagrams must be drawn using **Mermaid** format and embedded in Markdown.
- Each diagram must include: actors, use cases, and relationships (include, extend, generalization) where applicable.
- Use-case diagrams must cover all functional requirements documented in the vision document.
- Place the use-case diagrams in the use-case specification document or in a separate file.

## D - Use-Case Specification (45 points)

Write a use-case specification for **every** use case in the use-case diagram. You may organize all specifications in a single Markdown file or across multiple files. Each use case must have a clear header title, and use cases should be ordered by workflow or by importance.

Each use-case specification must include:

- Use-case name and ID
- Actor(s)
- Description
- Preconditions
- Basic flow (main success scenario) - described step by step
- Alternative flows — list **all** possible alternative flows, not just the most common ones
- Postconditions
- Special requirements (if any)

**Prototype Requirement:** For each use case, your team must create a UI prototype using design tools (e.g., Figma, Google Stitch, v0, Bolt, Excalidraw, or similar). AI-powered tools are encouraged as they can generate prototypes directly from your use-case specifications. The prototype should show the screens involved in the use case's basic flow and alternative flows. Include screenshots of all prototype screens alongside each use-case specification.

The goal of this prototype requirement is to ensure that each use case is described clearly enough to be implementable. If you cannot generate a coherent UI from your use-case specification, the specification needs to be refined.

## E - Implement 1 Functional Group using Spec Kit (20 points)

Your team must implement **1 functional group** from your project, covering the full stack: frontend, backend, and database. Your team is free to choose any popular and actively maintained tech stack (e.g., React, Vue, Flutter, Django, Express, etc.).

**Requirements:**

- Use **Spec Kit** to drive the implementation process: create specifications, generate plans, break down tasks, and implement following the spec-driven workflow.
- The functional group must be implemented end-to-end (UI + API/logic + data persistence).
- Your team may complete all Spec Kit steps including test generation, but you are NOT required to review or refine the generated test cases at this stage.
- Submit:
  - A video demo with narration explaining the implemented feature. Upload the video to YouTube (Unlisted or Public) and include the link in your submission.
  - The complete source code (exclude `node_modules`, `venv`, build artifacts, and other generated directories).
  - The Spec Kit artifacts generated during the process (specs, plans, tasks files).
- Ensure that dependency folders (e.g., `node_modules`) are removed before submitting.

## F - AI Usage Report and Weekly Report (5 points)

### AI Usage Report

Each team must follow the AI usage guidelines to declare whether AI tools were used during this sprint. If AI tools were used, a detailed log of all usages must be submitted.

### Weekly Report

Conduct and document the following meetings: Sprint Planning, Scrum meetings, and Sprint Review. Follow the instructions in the "WeeklyReports" (provided separately) document.

Include Jira task screenshots showing task assignments and progress for this sprint.

---

## Submission Guidelines

- All documents must be written in **English** using **Markdown** format. Diagrams should be drawn using **Mermaid** syntax.
- In each report/document section, the team must indicate who performed the work, who reviewed it, and who edited the section. Place this information on the first line immediately after the section header.
- Submit both the **original Markdown files (.md)** and **PDF files converted from Markdown**.
- Provide a **Git log** (screenshot or exported log) showing commit history of your repository.
- Compress all files into a directory named `PA3-Group[GroupId]` and submit as a zip file.
  Example: `PA3-Group01.zip`

## Grading Criteria

The assignment will be graded according to how well your team prepares the required documents and implementation. Criteria include:

- English writing: easy to understand, few errors.
- Well-formatted texts, diagrams, and charts.
- Changes are clearly documented in `Changes.md`.
- Use-case diagrams are in Mermaid format and cover key functional requirements from the vision document.
- Each use case has a complete specification with a main flow and is accompanied by prototype screenshots.
- The implemented functional group is functional end-to-end and follows the Spec Kit workflow.
- Source code is well-organized, and the video demo with narration clearly shows working features.
