# FinSync - UI/UX Design System & AI Wireframe Guide

> **AI Assistant Context & Prompting Guide:**
> When prompted to generate or review UI/UX specifications, mockups, or screen wireflows, use this design system context. FinSync follows modern **Material 3 / Jetpack Compose** design principles for Android, focusing on clarity, trust, and frictionless bookkeeping.

---

## 1. Design System Foundations

### 1.1. Color Palette (Finance & Trust)
- **Primary Brand (Deep Emerald / Navy):**
  - `Primary`: `#0F766E` (Teal 700 - stability, growth)
  - `Primary Container`: `#CCFBF1` (Teal 100)
- **Secondary (Accent & Split):**
  - `Secondary`: `#2563EB` (Blue 600 - technology, connectivity)
- **Semantic Status Colors:**
  - `Income / Surplus`: `#16A34A` (Green 600)
  - `Expense / Debt Owed`: `#DC2626` (Red 600)
  - `Budget Warning (>= 80%)`: `#D97706` (Amber 600)
  - `AI Advisory Accent`: `#8B5CF6` (Purple 600 - intelligence)
- **Neutral Surface & Background:**
  - `Surface Light`: `#F8FAFC` (Slate 50)
  - `Surface Dark`: `#0F172A` (Slate 900)
  - `Card Background`: `#FFFFFF` with 1dp border `#E2E8F0`

### 1.2. Typography (Google Fonts - Inter / Outfit)
- `Display Large`: 32sp, Bold (Total Net Worth, Major Balance displays)
- `Headline Medium`: 22sp, Semi-Bold (Screen Titles, Group Names)
- `Title Medium`: 16sp, Medium (Category Cards, Transaction Titles)
- `Body Large`: 14sp, Regular (Form inputs, notes, transaction details)
- `Label Small`: 11sp, Medium (Tags, timestamps, currency symbols)

---

## 2. Screen Architecture & Navigation Graph

The Android application is organized into **4 Primary Bottom Navigation Tabs**:

```text
FinSync Mobile App
├── Tab 1: Dashboard (Overview, Net Worth, Recent Transactions, AI Insight snippet)
├── Tab 2: Wallets (Personal Accounts: Cash, Bank, Credit Card balance ledgers)
├── Tab 3: Groups (Shared Expenses, Group Wallets, Who Owes Whom, Settle Up)
├── Tab 4: Analytics & Budgets (Cashflow charts, Category limits, Savings goals)
└── Floating Action Button (+): Quick Add Transaction (Personal vs Group selector)
```

---

## 3. Standard Screen Wireframe Documentation Schema

When generating child UI design files or wireframe specs, use this template:

```markdown
# Screen Specification: [Screen Name]
> *Performed by: Nguyễn Phú Đạt | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*

## 1. Overview & User Objective
- **Screen ID:** `SCR-[MODULE]-[NAME]`
- **Target Actor:** Regular User / Group Member
- **Primary Goal:** Enable user to quickly...

## 2. Layout Structure (ASCII / Markdown Wireframe)
```
+------------------------------------------+
|  <- Back       Split Group Expense       |
+------------------------------------------+
|  Total Amount: [ 150,000 VND           ] |
|  Description:  [ Group Dinner at Pizza ] |
|  Paid by:      [ You (Long)            v]|
+------------------------------------------+
|  Split Method:                           |
|  [x] Equal    [ ] By Percent  [ ] Exact  |
+------------------------------------------+
|  Participants (3):                       |
|  [x] You (Long)        -> 50,000 VND     |
|  [x] Đạt               -> 50,000 VND     |
|  [x] Nhật              -> 50,000 VND     |
+------------------------------------------+
|  [               Save & Split           ]|
+------------------------------------------+
```

## 3. UI Components & States
- **Default State:** Form fields empty, split method defaults to Equal Split.
- **Validation State:** Red outline on Total Amount if <= 0 or empty.
- **Success State:** Slide-in snackbar with 'Expense logged successfully'.
```
