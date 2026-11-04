# FinSync — Use-Case Specification: Admin & Account Subsystem
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

**Course:** CS300 - CSC13002 - Introduction to Software Engineering  
**Assignment:** Project Assignment 3 (PA3-2026) — Sprint 3  
**Subsystem:** User Account Lifecycle & Administrative Governance Portal  
**Lead Author:** Pham Dinh Tieu Long (24120087 - Project Manager / Group Leader / BA)  
**Last Updated:** 2026-11-02  

---

## Catalog of Admin & Account Use Cases

| Use-case ID | Use-case Name | Primary Actor | Target Client | Related Requirements | Prototype Screen IDs |
|---|---|---|---|---|---|
| `UC-ACC-01` | User Sign In & Authentication | Regular User | Mobile Client (Android) | `FR-AUTH-01`, `FR-AUTH-02` | `UC-ACC-01-S01` to `S05` |
| `UC-ACC-02` | Manage User Profile & Credentials | Regular User | Mobile Client (Android) | `FR-PROF-01`, `FR-PROF-02` | `UC-ACC-02-S01` to `S05` |
| `UC-ADM-01` | Admin Portal Login & Role Authorization | System Administrator | Web Admin Portal (React) | `FR-ADMIN-01`, `FR-AUTH-03` | `UC-ADM-01-S01` to `S06` |
| `UC-ADM-02` | Search & Monitor User Account Registry | System Administrator | Web Admin Portal (React) | `FR-ADMIN-02` | `UC-ADM-02-S01` to `S05` |
| `UC-ADM-03` | Lock and Unlock User Account | System Administrator | Web Admin Portal (React) | `FR-ADMIN-03` | `UC-ADM-03-S01` to `S06` |
| `UC-ADM-04` | Configure Global System Taxonomies & Defaults | System Administrator | Web Admin Portal (React) | `FR-ADMIN-04` | `UC-ADM-04-S01` to `S06` |

---

## 1. UC-ACC-01 — User Sign In & Authentication
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

### 1.1. Overview and Preconditions
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Specification |
|---|---|
| **Use-case ID and name** | `UC-ACC-01`: User Sign In & Authentication |
| **Primary actor** | Regular User |
| **Supporting actor(s)** | Spring Security JWT Provider, PostgreSQL User Store |
| **Description / goal** | Authenticates a registered user via email/username and password, granting access to personal and group financial ledgers via stateless JSON Web Tokens. |
| **Trigger** | User launches the FinSync mobile app without an existing valid session, or taps "Sign In" on the onboarding screen. |
| **Related functional requirements** | `FR-AUTH-01` (User Authentication), `FR-AUTH-02` (Token-based Session Management) |
| **Related business rules / NFRs** | `BR-SEC-01` (Stateless short-lived access JWT + refresh token), `NFR-SEC-01` (BCrypt hashing with work factor 12) |
| **Relationships** | None |
| **Preconditions** | 1. Mobile device has internet connectivity (Wi-Fi or Cellular).<br>2. User has previously completed account registration. |

### 1.2. Basic Flow — Main Success Scenario
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Step | Actor action | System response / state change | Prototype screen ID |
|:---:|---|---|:---:|
| 1 | User opens FinSync and views the Sign In screen. | System displays input fields for Email/Username, Password, "Remember Me" toggle, and "Sign In" button. | `UC-ACC-01-S01` |
| 2 | User enters valid email and password, then taps **"Sign In"**. | System performs client-side validation, shows loading spinner, and dispatches `POST /api/v1/auth/login`. | `UC-ACC-01-S02` |
| 3 | Backend Spring Boot API verifies password hash against PostgreSQL, confirms account status is `ACTIVE`, and issues Access Token and Refresh Token. | System persists encrypted tokens in Android EncryptedSharedPreferences, fetches user profile, and routes to **Dashboard (Overview)**. | `UC-ACC-01-S03` |

### 1.3. Alternative and Exception Flows
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Flow ID / name | Branch point / condition | Ordered actions and system responses | Resume step / termination | Resulting state | Screen IDs |
|---|---|---|---|---|---|
| **AF-01: Invalid Credentials** | Step 2: Email does not exist or password mismatch | 1. Backend returns `401 Unauthorized`.<br>2. App displays inline error alert: *"Invalid email or password. Please verify your credentials."*<br>3. Clears password field, keeps email input intact. | Resume at Step 2 | Unauthenticated; retry allowed | `UC-ACC-01-S04` |
| **AF-02: Account Locked by Administrator** | Step 2: User credentials valid, but account status is `LOCKED` | 1. Backend rejects login with `403 Forbidden` and error code `ACCOUNT_LOCKED`.<br>2. App renders error modal: *"Your account has been locked due to a policy violation. Please contact support@finsync.app."* | Terminate flow | Login blocked; zero tokens stored | `UC-ACC-01-S05` |
| **AF-03: Network Connectivity Failure** | Step 2: Mobile device loses network during request | 1. Network interceptor detects socket timeout (>10s).<br>2. App displays bottom snackbar: *"Network unreachable. Please check your internet connection."*<br>3. Enables "Sign In" button for immediate retry. | Resume at Step 2 | Input preserved; offline state handled | `UC-ACC-01-S01` |
| **AF-04: Forgot Password Navigation** | Step 1: User taps "Forgot Password?" | 1. App transitions to Password Reset initiation screen (`/auth/forgot-password`). | Resume at external recovery flow | Navigated away from login form | `UC-ACC-01-S06` |

### 1.4. Postconditions and Special Requirements
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

* **Success postconditions:** User session established; JWT stored securely in Android KeyStore; user profile loaded into reactive state; subsequent API calls automatically inject `Authorization: Bearer <token>`.
* **Failure postconditions:** No tokens stored; security event logged in backend audit logs.
* **Special requirements:** Passwords must be masked by default with an optional show/hide toggle. Sensitive data must be cleared from memory buffers upon transmission.

### 1.5. UI Prototype Screenshots
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Screen ID | Screen / State | Flow Reference | Purpose and Expected Behavior | Screenshot File |
|---|---|---|---|---|
| `UC-ACC-01-S01` | Sign In Screen — Default State | Basic Step 1 | Form with email, masked password, remember me, and sign-in button. | `screenshots/prototypes/UC-ACC-01/S01.png` |
| `UC-ACC-01-S02` | Sign In Screen — Submitting | Basic Step 2 | Disabled button with circular loading animation. | `screenshots/prototypes/UC-ACC-01/S02.png` |
| `UC-ACC-01-S03` | Mobile Dashboard Overview | Basic Step 3 | Home screen showing aggregated net worth, quick actions, and recent transactions. | `screenshots/prototypes/UC-ACC-01/S03.png` |
| `UC-ACC-01-S04` | Sign In — Invalid Credentials Error | AF-01 | Inline red warning banner below password input. | `screenshots/prototypes/UC-ACC-01/S04.png` |
| `UC-ACC-01-S05` | Sign In — Account Locked Modal | AF-02 | Modal dialog informing user of lock status and support email. | `screenshots/prototypes/UC-ACC-01/S05.png` |

---

## 2. UC-ACC-02 — Manage User Profile & Credentials
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

### 2.1. Overview and Preconditions
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Specification |
|---|---|
| **Use-case ID and name** | `UC-ACC-02`: Manage User Profile & Credentials |
| **Primary actor** | Regular User |
| **Supporting actor(s)** | Image Upload Storage (Cloudinary/S3), PostgreSQL DB |
| **Description / goal** | Enables authenticated users to update their profile information (display name, avatar image, phone number), change login password, and select default reporting currency (VND / USD). |
| **Trigger** | User navigates to the "Profile & Settings" tab from the mobile bottom navigation bar. |
| **Related functional requirements** | `FR-PROF-01` (User Profile Management), `FR-PROF-02` (Default Currency Selection) |
| **Related business rules / NFRs** | `BR-FIN-02` (Changing reporting currency recalculates aggregated net-worth displays without altering historical ledger entries) |
| **Relationships** | None |
| **Preconditions** | User is authenticated with an active session (`ROLE_USER`). |

### 2.2. Basic Flow — Main Success Scenario
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Step | Actor action | System response / state change | Prototype screen ID |
|:---:|---|---|:---:|
| 1 | User navigates to **"Profile Settings"** screen. | System loads user data via `GET /api/v1/users/me` and displays current avatar, display name, email, reporting currency, and change password link. | `UC-ACC-02-S01` |
| 2 | User edits display name (e.g., from *"Long Pham"* to *"Pham Dinh Tieu Long"*), selects default currency as `"VND"`, and taps avatar to choose a new photo. | System previews selected avatar locally and validates display name (non-empty, <= 50 characters). | `UC-ACC-02-S02` |
| 3 | User taps **"Save Changes"**. | System sends `PUT /api/v1/users/me` with multipart form data (avatar binary + JSON metadata). | `UC-ACC-02-S03` |
| 4 | Backend updates database record, uploads image, and responds with `200 OK` and updated profile payload. | System updates local profile cache and shows success toast: *"Profile updated successfully."* | `UC-ACC-02-S01` |

### 2.3. Alternative and Exception Flows
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Flow ID / name | Branch point / condition | Ordered actions and system responses | Resume step / termination | Resulting state | Screen IDs |
|---|---|---|---|---|---|
| **AF-01: Change Password Sub-flow** | Step 1: User taps "Change Password" | 1. App navigates to Change Password screen (`/profile/change-password`).<br>2. User inputs current password, new password, and confirms new password.<br>3. Backend validates current password and complexity rules (>=8 chars, 1 digit, 1 uppercase).<br>4. Backend hashes new password and returns success. | Resume at Step 1 | Password updated; existing tokens remain valid | `UC-ACC-02-S04` |
| **AF-02: Incorrect Current Password** | AF-01 Step 3: Current password entered incorrectly | 1. Backend returns `400 Bad Request` with message `INVALID_CURRENT_PASSWORD`.<br>2. App flags current password field in red and displays: *"Current password does not match our records."* | Resume at AF-01 Step 2 | Password unchanged | `UC-ACC-02-S04` |
| **AF-03: Unsupported Avatar File / Oversized Image** | Step 2: User selects image > 5MB or non-image file | 1. Client-side validator intercepts file selection.<br>2. App alerts: *"File size exceeds 5MB limit. Please choose a smaller JPEG or PNG image."*<br>3. Retains existing avatar. | Resume at Step 2 | Avatar remains unchanged | `UC-ACC-02-S05` |
| **AF-04: Discard Unsaved Changes** | Step 2: User navigates back without saving | 1. App detects dirty form state.<br>2. Displays confirmation dialog: *"Discard unsaved profile changes?"*<br>3. User confirms "Discard". App navigates back. | Terminate flow | No updates persisted | `UC-ACC-02-S01` |

### 2.4. Postconditions and Special Requirements
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

* **Success postconditions:** Profile fields and avatar updated in database; dashboard header refreshes with updated display name; default currency applies to all future aggregated wallet calculations.
* **Failure postconditions:** Database state untouched; local cache reflects previously stored data.
* **Special requirements:** Images must be compressed to WebP/JPEG format before uploading to conserve bandwidth.

### 2.5. UI Prototype Screenshots
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Screen ID | Screen / State | Flow Reference | Purpose and Expected Behavior | Screenshot File |
|---|---|---|---|---|
| `UC-ACC-02-S01` | Profile & Account Screen | Basic Step 1 | Overview of user identity, current currency preference, and action items. | `screenshots/prototypes/UC-ACC-02/S01.png` |
| `UC-ACC-02-S02` | Edit Profile Form | Basic Step 2 | Input fields for display name, phone number, and avatar picker modal. | `screenshots/prototypes/UC-ACC-02/S02.png` |
| `UC-ACC-02-S03` | Saving Profile State | Basic Step 3 | Progress indicator while uploading avatar binary. | `screenshots/prototypes/UC-ACC-02/S03.png` |
| `UC-ACC-02-S04` | Change Password Modal | AF-01 / AF-02 | Secure form with password strength indicator and validation error states. | `screenshots/prototypes/UC-ACC-02/S04.png` |
| `UC-ACC-02-S05` | File Size Warning Dialog | AF-03 | Warning alert for oversized file selections. | `screenshots/prototypes/UC-ACC-02/S05.png` |

---

## 3. UC-ADM-01 — Admin Portal Login & Role-Based Authorization
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

### 3.1. Overview and Preconditions
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Specification |
|---|---|
| **Use-case ID and name** | `UC-ADM-01`: Admin Portal Login & Role-Based Authorization |
| **Primary actor** | System Administrator |
| **Supporting actor(s)** | Spring Security Identity Provider, PostgreSQL User Store |
| **Description / goal** | Authenticates administrative staff via email and password on the Web Admin Portal, strictly verifying `ROLE_ADMIN` authority before issuing an administrative session token. |
| **Trigger** | Administrator accesses the FinSync Web Admin Portal URL (`https://admin.finsync.app/login`). |
| **Related functional requirements** | `FR-ADMIN-01` (Administrative Access Gate), `FR-AUTH-03` (RBAC Authorization) |
| **Related business rules / NFRs** | `BR-SEC-01` (Stateless JWT token with 1-hour expiration), `BR-SEC-02` (Strict role gating: regular users cannot access administrative endpoints under any condition) |
| **Relationships** | Entry point for all administrative use cases (`UC-ADM-02`, `UC-ADM-03`, `UC-ADM-04`) |
| **Preconditions** | 1. Web browser has secure HTTPS access to the Admin Portal domain.<br>2. Administrator holds a registered user account with `ROLE_ADMIN` authority. |

### 3.2. Basic Flow — Main Success Scenario
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Step | Actor action | System response / state change | Prototype screen ID |
|:---:|---|---|:---:|
| 1 | Administrator navigates to Admin Portal login page. | System renders Admin Login View featuring secure branding, Email, Password fields, and "Login to Console" button. | `UC-ADM-01-S01` |
| 2 | Administrator enters administrative credentials and clicks **"Login to Console"**. | System sanitizes input, shows loading state, and sends `POST /api/v1/auth/admin-login`. | `UC-ADM-01-S02` |
| 3 | Backend Spring Boot validates credentials against PostgreSQL, verifies `ROLE_ADMIN` presence in user roles table, and signs JWT with administrative scopes. | System sets secure HTTP-Only cookie, initializes admin state in React store, and redirects to `/admin/dashboard`. | `UC-ADM-01-S03` |
| 4 | Administrator views the Admin Dashboard. | System displays aggregate system health cards (Total Users, Active Sessions, Total Shared Ledgers, System Error Rate). | `UC-ADM-01-S03` |

### 3.3. Alternative and Exception Flows
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Flow ID / name | Branch point / condition | Ordered actions and system responses | Resume step / termination | Resulting state | Screen IDs |
|---|---|---|---|---|---|
| **AF-01: Invalid Credentials** | Step 2: Unmatched email or wrong password | 1. Backend responds with `401 Unauthorized`.<br>2. System displays alert: *"Invalid administrator email or password."*<br>3. Clears password field and retains email. | Resume at Step 2 | Form remains open; zero tokens granted | `UC-ADM-01-S04` |
| **AF-02: Access Denied — Regular User Account Attempt** | Step 2: Valid credentials of a user with only `ROLE_USER` | 1. Backend detects credentials valid, but user lacks `ROLE_ADMIN`.<br>2. Returns `403 Forbidden` with code `INSUFFICIENT_PRIVILEGES`.<br>3. System renders warning: *"Access Denied: Your account does not possess administrative privileges."*<br>4. Logs suspicious administrative access attempt. | Resume at Step 2 | Portal access denied; security audit logged | `UC-ADM-01-S05` |
| **AF-03: Brute-Force Rate Limiting** | Step 2: 5 consecutive failed login attempts within 5 minutes | 1. Spring Security RateLimiter locks the source IP for 15 minutes.<br>2. System disables submit button and shows countdown: *"Too many failed login attempts. Console access locked for 15:00."* | Terminate flow | Submissions blocked until timer expires | `UC-ADM-01-S06` |

### 3.4. Postconditions and Special Requirements
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

* **Success postconditions:** Administrative JWT cookie saved with `SameSite=Strict`, `Secure`, and `HttpOnly` flags; full access granted to navigation sidebar (Users, Ledgers, Settings, Logs).
* **Failure postconditions:** No token issued; security event logged in `admin_security_log` with remote IP, user-agent, and attempted email.
* **Special requirements:** Strict 1-hour session timeout with automatic logout upon 15 minutes of inactivity.

### 3.5. UI Prototype Screenshots
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Screen ID | Screen / State | Flow Reference | Purpose and Expected Behavior | Screenshot File |
|---|---|---|---|---|
| `UC-ADM-01-S01` | Admin Portal Login — Default | Basic Step 1 | Dark-themed administrative login form with security badge. | `screenshots/prototypes/UC-ADM-01/S01.png` |
| `UC-ADM-01-S02` | Admin Portal Login — Authenticating | Basic Step 2 | Loading animation on login action button. | `screenshots/prototypes/UC-ADM-01/S02.png` |
| `UC-ADM-01-S03` | Admin Portal Dashboard Overview | Basic Step 3 & 4 | System telemetry dashboard showing user metrics, server health, and menu tabs. | `screenshots/prototypes/UC-ADM-01/S03.png` |
| `UC-ADM-01-S04` | Admin Login — Invalid Credentials | AF-01 | Red alert banner indicating credential mismatch. | `screenshots/prototypes/UC-ADM-01/S04.png` |
| `UC-ADM-01-S05` | Admin Login — Access Denied 403 | AF-02 | Warning modal alerting non-admin user of forbidden access. | `screenshots/prototypes/UC-ADM-01/S05.png` |
| `UC-ADM-01-S06` | Admin Login — Rate Limit Lockdown | AF-03 | Countdown timer locking login form due to repeated failed attempts. | `screenshots/prototypes/UC-ADM-01/S06.png` |

---

## 4. UC-ADM-02 — Search & Monitor User Account Registry
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

### 4.1. Overview and Preconditions
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Specification |
|---|---|
| **Use-case ID and name** | `UC-ADM-02`: Search & Monitor User Account Registry |
| **Primary actor** | System Administrator |
| **Supporting actor(s)** | PostgreSQL Database Server |
| **Description / goal** | Enables Administrator to inspect the global registered user directory, search by name/email, filter by account status (`ACTIVE`, `LOCKED`), and view account activity telemetry. |
| **Trigger** | Administrator clicks on the **"Users"** link in the Web Admin Portal navigation menu. |
| **Related functional requirements** | `FR-ADMIN-02` (User Account Directory Management) |
| **Related business rules / NFRs** | `NFR-PERF-01` (Paginated user queries must return in under 500ms for up to 100,000 records) |
| **Relationships** | Extends to `UC-ADM-03` (Lock / Unlock User Account) |
| **Preconditions** | Administrator is logged in with an active `ROLE_ADMIN` session. |

### 4.2. Basic Flow — Main Success Scenario
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Step | Actor action | System response / state change | Prototype screen ID |
|:---:|---|---|:---:|
| 1 | Administrator clicks on **"Users"** tab in navigation sidebar. | System issues `GET /api/v1/admin/users?page=0&size=20&sort=createdAt,desc` and renders paginated User Table. | `UC-ADM-02-S01` |
| 2 | System displays columns: User ID, Avatar, Full Name, Email, Registration Date, Status Badge (`ACTIVE` or `LOCKED`), Total Wallets, and Action Controls. | Administrator reviews high-level registry statistics. | `UC-ADM-02-S01` |
| 3 | Administrator enters search keyword (e.g., `"datnguyen"`) in search input and selects Status Filter: `"ACTIVE"`. | System debounces query (300ms) and dispatches `GET /api/v1/admin/users?query=datnguyen&status=ACTIVE`. | `UC-ADM-02-S02` |
| 4 | System updates table with matching records, highlighting search matches. | Administrator views filtered subset. | `UC-ADM-02-S03` |
| 5 | Administrator clicks on a user row to inspect telemetry. | System slides out User Details Drawer showing registration IP, last login timestamp, number of shared groups, and audit log history. | `UC-ADM-02-S04` |

### 4.3. Alternative and Exception Flows
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Flow ID / name | Branch point / condition | Ordered actions and system responses | Resume step / termination | Resulting state | Screen IDs |
|---|---|---|---|---|---|
| **AF-01: Zero Search Results** | Step 3: No user accounts match query criteria | 1. Backend returns empty page payload `[]`.<br>2. Table displays empty state graphic: *"No registered accounts found matching your query."*<br>3. Displays **"Clear Filters"** button. | Resume at Step 3 | Filter controls remain active | `UC-ADM-02-S05` |
| **AF-02: Clear Filters** | AF-01: Administrator clicks "Clear Filters" | 1. Search input cleared; status filter reset to "All".<br>2. System fetches and renders default Page 1. | Resume at Step 1 | Default directory restored | `UC-ADM-02-S01` |
| **AF-03: Session Expiry During Pagination** | Step 1 or 3: Admin JWT token expires | 1. Backend responds `401 Token Expired`.<br>2. System displays notification: *"Your administrative session has expired."*<br>3. Redirects to `/admin/login`. | Terminate flow | Unauthenticated; cached data cleared | `UC-ADM-01-S01` |

### 4.4. Postconditions and Special Requirements
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

* **Success postconditions:** Administrator views accurate, up-to-date user telemetry without exposing sensitive user financial transaction contents (respecting non-custodial privacy guidelines).
* **Failure postconditions:** Query errors fail gracefully without crashing web console; error toasts displayed.
* **Special requirements:** Full compliance with Privacy Guidelines: Administrator can inspect metadata (transaction count, account status) but **cannot** read private transaction descriptions or wallet notes.

### 4.5. UI Prototype Screenshots
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Screen ID | Screen / State | Flow Reference | Purpose and Expected Behavior | Screenshot File |
|---|---|---|---|---|
| `UC-ADM-02-S01` | User Governance Directory — Default | Basic Step 1 & 2 | Data table showing user records, status pills, and pagination footer. | `screenshots/prototypes/UC-ADM-02/S01.png` |
| `UC-ADM-02-S02` | User Governance — Filtering State | Basic Step 3 | Search query entered with status dropdown applied. | `screenshots/prototypes/UC-ADM-02/S02.png` |
| `UC-ADM-02-S03` | User Governance — Filtered Results | Basic Step 4 | Table dynamically displaying matching user records. | `screenshots/prototypes/UC-ADM-02/S03.png` |
| `UC-ADM-02-S04` | User Detail Telemetry Slide-out Drawer | Basic Step 5 | Slide-over drawer detailing account metadata, security status, and group counts. | `screenshots/prototypes/UC-ADM-02/S04.png` |
| `UC-ADM-02-S05` | User Governance — Zero Results State | AF-01 | Empty state illustration with clear-filter CTA button. | `screenshots/prototypes/UC-ADM-02/S05.png` |

---

## 5. UC-ADM-03 — Lock and Unlock User Account
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

### 5.1. Overview and Preconditions
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Specification |
|---|---|
| **Use-case ID and name** | `UC-ADM-03`: Lock and Unlock User Account |
| **Primary actor** | System Administrator |
| **Supporting actor(s)** | Redis Token Blacklist, PostgreSQL DB, Admin Audit Logger |
| **Description / goal** | Allows Administrator to toggle a user's operational status between `ACTIVE` and `LOCKED`. Locking immediately invalidates all active sessions and blocks platform operations with mandatory audit reasoning. |
| **Trigger** | Administrator clicks the **"Lock Account"** or **"Unlock Account"** action button on a user record. |
| **Related functional requirements** | `FR-ADMIN-03` (Account State Governance & Audit Enforcement) |
| **Related business rules / NFRs** | `BR-SEC-03` (Immediate token revocation: Redis blacklist drops active user JWTs within 1 second; locked users cannot create transactions, split bills, or access group ledgers) |
| **Relationships** | Extends `UC-ADM-02` (Search & Monitor User Account Registry) |
| **Preconditions** | 1. Administrator is authenticated with `ROLE_ADMIN`.<br>2. Target user account exists in PostgreSQL user repository. |

### 5.2. Basic Flow — Main Success Scenario (Locking an Account)
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Step | Actor action | System response / state change | Prototype screen ID |
|:---:|---|---|:---:|
| 1 | From User Table, Administrator identifies target user and clicks **"Lock Account"** button. | System renders modal dialog: *"Lock User Account"*, showing target's Name, Email, and a mandatory text area for **"Reason for Administrative Lock"**. | `UC-ADM-03-S01` |
| 2 | Administrator types reason (e.g., *"Reported by multiple members for debt settlement evasion"*) and clicks **"Confirm Lock"**. | System performs client-side validation (ensures length >= 10 characters) and submits `PATCH /api/v1/admin/users/{userId}/status` with `{ "status": "LOCKED", "reason": "..." }`. | `UC-ADM-03-S02` |
| 3 | Backend sets status to `LOCKED` in PostgreSQL, publishes revocation event to Redis Blacklist, and writes record to `admin_audit_logs`. | System closes modal, updates table status badge to red (`LOCKED`), switches button to **"Unlock"**, and displays success toast: *"Account locked successfully."* | `UC-ADM-03-S03` |

### 5.3. Alternative and Exception Flows
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Flow ID / name | Branch point / condition | Ordered actions and system responses | Resume step / termination | Resulting state | Screen IDs |
|---|---|---|---|---|---|
| **AF-01: Empty or Inadequate Reason** | Step 2: Lock reason empty or < 10 characters | 1. System blocks submission.<br>2. Displays validation warning: *"A detailed audit reason of at least 10 characters is mandatory."*<br>3. Highlights text box in red. | Resume at Step 2 | Modal remains open; DB unchanged | `UC-ADM-03-S04` |
| **AF-02: Action Cancellation** | Step 1 or 2: Administrator clicks "Cancel" or closes modal | 1. System closes confirmation modal.<br>2. Clears text buffer without calling backend. | Resume at Step 1 | User account status unchanged | `UC-ADM-02-S01` |
| **AF-03: Self-Locking Prevention** | Step 1: Admin clicks "Lock" on their own account | 1. System compares target user ID with authenticated session ID.<br>2. Disables button and alerts: *"Operation rejected: Administrators cannot lock their own account."* | Resume at Step 1 | Operation disallowed immediately | `UC-ADM-03-S05` |
| **AF-04: Unlocking a Locked Account** | Step 1: Target account is currently `LOCKED` | 1. Button label displays **"Unlock Account"**.<br>2. Admin clicks button; confirms *"Unlock User Account"* modal.<br>3. Backend updates status to `ACTIVE`, removes user from Redis blacklist, and logs audit record.<br>4. Badge turns green (`ACTIVE`). User can log in again. | Terminate flow | Account restored to active status | `UC-ADM-03-S06` |

### 5.4. Postconditions and Special Requirements
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

* **Success postconditions:** PostgreSQL `users.status` set to `LOCKED`; Redis caches user ID with TTL matching maximum token lifetime (24 hours); all ongoing requests from target user return `403 Account Locked`; immutable audit log saved.
* **Failure postconditions:** Account state unchanged; cancellation produces zero database mutations.
* **Special requirements:** Compliance with Academic Auditability standards: every status transition must persist `admin_id`, `target_user_id`, `previous_status`, `new_status`, `reason`, and `timestamp`.

### 5.5. UI Prototype Screenshots
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Screen ID | Screen / State | Flow Reference | Purpose and Expected Behavior | Screenshot File |
|---|---|---|---|---|
| `UC-ADM-03-S01` | Lock Account Confirmation Modal | Basic Step 1 | Modal prompt displaying target user details and reason text area. | `screenshots/prototypes/UC-ADM-03/S01.png` |
| `UC-ADM-03-S02` | Lock Account Submitting State | Basic Step 2 | Loading spinner on confirm button during backend lock execution. | `screenshots/prototypes/UC-ADM-03/S02.png` |
| `UC-ADM-03-S03` | User Table Showing Locked Badge | Basic Step 3 | User row updated with red LOCKED badge and Unlock action button. | `screenshots/prototypes/UC-ADM-03/S03.png` |
| `UC-ADM-03-S04` | Lock Modal — Validation Error | AF-01 | Field validation error highlighting insufficient reason length. | `screenshots/prototypes/UC-ADM-03/S04.png` |
| `UC-ADM-03-S05` | Self-Lock Warning Toast | AF-03 | Warning alert blocking admin from locking themselves. | `screenshots/prototypes/UC-ADM-03/S05.png` |
| `UC-ADM-03-S06` | Unlock Account Modal | AF-04 | Modal confirming account restoration back to Active state. | `screenshots/prototypes/UC-ADM-03/S06.png` |

---

## 6. UC-ADM-04 — Configure Global System Taxonomies & Defaults
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

### 6.1. Overview and Preconditions
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Specification |
|---|---|
| **Use-case ID and name** | `UC-ADM-04`: Configure Global System Taxonomies & Defaults |
| **Primary actor** | System Administrator |
| **Supporting actor(s)** | PostgreSQL System Configuration Table |
| **Description / goal** | Enables Administrator to manage default transaction categories (Food, Transportation, Housing), set system base currency, and configure reference exchange rates. |
| **Trigger** | Administrator selects **"Settings & Taxonomies"** from the navigation sidebar. |
| **Related functional requirements** | `FR-ADMIN-04` (Global Taxonomy & Currency Configuration) |
| **Related business rules / NFRs** | `BR-FIN-01` (Default categories cannot be deleted if referenced by active transactions; deletion sets status to `DEPRECATED`), `BR-FIN-03` (Base currency modification requires explicit recalculation warning) |
| **Relationships** | None |
| **Preconditions** | Administrator is logged in with `ROLE_ADMIN`. |

### 6.2. Basic Flow — Main Success Scenario
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Step | Actor action | System response / state change | Prototype screen ID |
|:---:|---|---|:---:|
| 1 | Administrator navigates to **"Settings & Taxonomies"** screen. | System fetches global settings via `GET /api/v1/admin/taxonomies` and displays Category Management Table and Currency Settings Card. | `UC-ADM-04-S01` |
| 2 | Administrator clicks **"Add Default Category"** button. | System renders modal with Name, Category Type (`EXPENSE` or `INCOME`), Icon Picker, and Color Picker. | `UC-ADM-04-S02` |
| 3 | Administrator inputs name (e.g., *"Electricity"*), selects type `"EXPENSE"`, chooses category color/icon, and clicks **"Save Category"**. | System validates uniqueness, sends `POST /api/v1/admin/categories`, and persists record. | `UC-ADM-04-S03` |
| 4 | Backend returns `201 Created` with saved category object. | System updates category table, shows success alert: *"Category added to default taxonomy"*, and auto-seeds this category for all newly registered accounts. | `UC-ADM-04-S04` |

### 6.3. Alternative and Exception Flows
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Flow ID / name | Branch point / condition | Ordered actions and system responses | Resume step / termination | Resulting state | Screen IDs |
|---|---|---|---|---|---|
| **AF-01: Duplicate Category Name Collision** | Step 3: Category name matches an existing default category | 1. Backend rejects request with `409 Conflict`.<br>2. System displays inline error: *"A default category with this name already exists."* | Resume at Step 3 | Category modal remains open | `UC-ADM-04-S05` |
| **AF-02: Attempting to Delete an Active Referenced Category** | Step 1: Admin clicks "Delete" on a category used in existing transactions | 1. Backend checks transaction references.<br>2. Returns `400 Bad Request` with code `CATEGORY_IN_USE`.<br>3. System displays alert: *"Cannot hard-delete category: Referenced by 2,430 transactions. Would you like to Archive it instead?"* | Resume at Step 1 | Category marked `ARCHIVED`; historical records preserved | `UC-ADM-04-S06` |
| **AF-03: Update Reference Exchange Rate** | Step 1: Admin updates USD/VND reference exchange rate | 1. Admin edits rate input (e.g., from `25,000` to `25,450`).<br>2. Clicks **"Update Rate"**.<br>3. System validates rate > 0 and saves new reference rate timestamped in `exchange_rates_history`. | Normal completion | New conversions use updated rate; historical debts unchanged | `UC-ADM-04-S01` |

### 6.4. Postconditions and Special Requirements
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

* **Success postconditions:** Category taxonomy updated; newly registered accounts automatically receive the updated category list; exchange rate updates logged with timestamp.
* **Failure postconditions:** Existing categories and conversion parameters remain unaltered.
* **Special requirements:** Deletion of default categories must never perform a SQL `DELETE CASCADE` that affects user ledgers; soft-delete pattern (`is_archived = true`) is strictly mandated.

### 6.5. UI Prototype Screenshots
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Screen ID | Screen / State | Flow Reference | Purpose and Expected Behavior | Screenshot File |
|---|---|---|---|---|
| `UC-ADM-04-S01` | System Taxonomies & Currency View | Basic Step 1 | Dashboard tab showing category list, icon badges, and currency exchange rates. | `screenshots/prototypes/UC-ADM-04/S01.png` |
| `UC-ADM-04-S02` | Add Category Modal Dialog | Basic Step 2 | Input modal with name, type toggle (Expense/Income), and icon picker grid. | `screenshots/prototypes/UC-ADM-04/S02.png` |
| `UC-ADM-04-S03` | Category Creation Submitting | Basic Step 3 | Disabled submit button with loader. | `screenshots/prototypes/UC-ADM-04/S03.png` |
| `UC-ADM-04-S04` | Updated Category List State | Basic Step 4 | Table displaying newly added default category with success toast. | `screenshots/prototypes/UC-ADM-04/S04.png` |
| `UC-ADM-04-S05` | Add Category — Duplicate Error | AF-01 | Form error highlighting duplicate category name. | `screenshots/prototypes/UC-ADM-04/S05.png` |
| `UC-ADM-04-S06` | Delete Category In-Use Warning | AF-02 | Warning prompt explaining soft-delete/archive requirement. | `screenshots/prototypes/UC-ADM-04/S06.png` |

