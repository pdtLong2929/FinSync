# Database Design

> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

## 1. Entity-Relationship Diagram

<!-- Add ER diagram using Mermaid erDiagram syntax -->

```mermaid
erDiagram
    USER ||--o{ WALLET : owns
    USER ||--o{ TRANSACTION : records
    USER ||--o{ GROUP_MEMBER : joins
    USER ||--o{ SAVINGS_GOAL : creates
    USER ||--o{ BUDGET : sets

    WALLET ||--o{ TRANSACTION : contains

    CATEGORY ||--o{ TRANSACTION : categorizes
    CATEGORY ||--o{ BUDGET : limits

    GROUP ||--o{ GROUP_MEMBER : has
    GROUP ||--|| WALLET : "has shared"
    GROUP ||--o{ GROUP_EXPENSE : tracks

    GROUP_EXPENSE ||--o{ DEBT : generates

    USER {
        bigint id PK
        string email UK
        string password_hash
        string display_name
        string avatar_url
        string default_currency
        string role
        boolean is_active
        timestamp created_at
    }

    WALLET {
        bigint id PK
        bigint user_id FK
        string name
        string type
        decimal balance
        string currency
        timestamp created_at
    }

    TRANSACTION {
        bigint id PK
        bigint wallet_id FK
        bigint category_id FK
        bigint user_id FK
        string type
        decimal amount
        string note
        string receipt_url
        date transaction_date
        timestamp created_at
    }

    CATEGORY {
        bigint id PK
        string name
        string type
        string icon
        boolean is_default
        bigint user_id FK
    }
```

## 2. Table Descriptions

<!-- Add detailed table schemas here -->

## 3. Indexing Strategy

<!-- Add index definitions for performance -->
