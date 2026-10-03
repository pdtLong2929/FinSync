# REST API Design

> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

## Base URL

```
https://{host}/api/v1
```

## Authentication

All endpoints (except `/auth/**`) require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <jwt_token>
```

## API Endpoints

### 1. Authentication (`/api/v1/auth`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and receive JWT |
| POST | `/auth/forgot-password` | Request password reset email |
| POST | `/auth/reset-password` | Reset password with token |
| PUT | `/auth/change-password` | Change password (authenticated) |

### 2. Profile (`/api/v1/profile`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/profile` | Get current user profile |
| PUT | `/profile` | Update profile info |
| PUT | `/profile/avatar` | Upload/update avatar |

### 3. Wallets (`/api/v1/wallets`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/wallets` | List all wallets |
| POST | `/wallets` | Create a new wallet |
| GET | `/wallets/{id}` | Get wallet details |
| PUT | `/wallets/{id}` | Update wallet |
| DELETE | `/wallets/{id}` | Delete wallet |
| GET | `/wallets/total-assets` | Get total assets summary |

### 4. Transactions (`/api/v1/transactions`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/transactions` | List transactions (with filters) |
| POST | `/transactions` | Create transaction |
| GET | `/transactions/{id}` | Get transaction details |
| PUT | `/transactions/{id}` | Update transaction |
| DELETE | `/transactions/{id}` | Delete transaction |

### 5. Categories (`/api/v1/categories`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/categories` | List all categories (default + custom) |
| POST | `/categories` | Create custom category |
| PUT | `/categories/{id}` | Update custom category |
| DELETE | `/categories/{id}` | Delete custom category |

### 6. Groups (`/api/v1/groups`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/groups` | List user's groups |
| POST | `/groups` | Create new group |
| GET | `/groups/{id}` | Get group details |
| PUT | `/groups/{id}` | Update group info |
| DELETE | `/groups/{id}` | Delete group (owner only) |
| POST | `/groups/{id}/invite` | Generate invite link/code |
| POST | `/groups/join` | Join group via invite code |
| DELETE | `/groups/{id}/members/{userId}` | Remove member |

### 7. Group Expenses & Debts (`/api/v1/groups/{groupId}/expenses`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/groups/{groupId}/expenses` | List group expenses |
| POST | `/groups/{groupId}/expenses` | Add group expense with split |
| GET | `/groups/{groupId}/debts` | Get debt board (who owes whom) |
| PUT | `/groups/{groupId}/debts/{debtId}/settle` | Mark debt as settled |
| POST | `/groups/{groupId}/debts/{debtId}/remind` | Send payment reminder |

### 8. Budgets (`/api/v1/budgets`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/budgets` | List all budgets |
| POST | `/budgets` | Create budget limit |
| PUT | `/budgets/{id}` | Update budget |
| DELETE | `/budgets/{id}` | Delete budget |

### 9. Reports (`/api/v1/reports`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/reports/personal` | Get personal financial report |
| GET | `/reports/groups/{groupId}` | Get group spending report |
| GET | `/reports/export/pdf` | Export report as PDF |
| GET | `/reports/export/excel` | Export report as Excel |

### 10. Savings Goals (`/api/v1/savings-goals`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/savings-goals` | List savings goals |
| POST | `/savings-goals` | Create savings goal |
| PUT | `/savings-goals/{id}` | Update savings goal |
| DELETE | `/savings-goals/{id}` | Delete savings goal |

### 11. AI Advisor (`/api/v1/ai`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/ai/spending-analysis` | Get AI spending analysis for last month |
| GET | `/ai/budget-suggestion` | Get AI budget optimization suggestion |

### 12. Admin (`/api/v1/admin`) — *Admin role required*

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/users` | List all users (with search/filter) |
| GET | `/admin/users/{id}` | Get user details |
| PUT | `/admin/users/{id}/lock` | Lock user account |
| PUT | `/admin/users/{id}/unlock` | Unlock user account |
| GET | `/admin/categories` | List default categories |
| POST | `/admin/categories` | Create default category |
| PUT | `/admin/categories/{id}` | Update default category |
| DELETE | `/admin/categories/{id}` | Delete default category |
| GET | `/admin/dashboard` | Get system-wide statistics |

---

## Standard Response Format

### Success

```json
{
  "status": 200,
  "message": "Success",
  "data": { ... }
}
```

### Error

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    { "field": "email", "message": "Email is required" }
  ]
}
```

### Pagination

```json
{
  "status": 200,
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }
}
```
