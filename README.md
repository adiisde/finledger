## FinLedger - Payment Ledger Engine

### Overview
FinLedger is fault-tolerant, ACID-compliant(usage) double-entry accounting engine designed for high concurrency environments.

It guarantees **exactly-once transaction processing**, **consistent balances** in legder and transaction tables and provide **idempotent APIs** for safe retries under concurrency.

---

### Highlights
- Double-entry ledger for accurate accounting.
- Idempotent and thread-safe APIs.
- Optimistic locking and transactional consistency.
- Audit logging and transaction linking.

---

### Features
- **Accounts & Balances:** Create new account, fetch that account information, check balance for that account.
- **Transaction & Ledger Entries:** Main sources for money movement from A account to B account or on own account, it records debits & credits in double-entry format.
- **Idempotent Protection:** Prevents the duplicate requests processing.
- **Audit Logs:** Helps to track entire system activity and maintain history of actions within system.
- **Controllers & DTOs:** API layer with clean data abstraction (for request & response) for performing operations.
- **Global Exception Handling:** Meaningful error responses.

---

### Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3
- **Database:** PostgreSQL
- **ORM:** JPA, Hibernate
- **Testing:** JUnit 5, k6 (load testing, most used)
- **Build Tool:** Maven

---

### Key Principles
- **Double-Entry Accounting:** Every transaction updates (for transfer method) two sides (debit/credit) for money movement consistency.
- **Idempotent:** Prevent duplicate transactions during retries or network issues.
- **Optimistic Locking:** Ensure no lost updates during concurrent operations.
- **ACID Compaliance:** Guarantees consistency under high load.

---

### Installation

1. **Prerequisites**
- Java 17
- PostgreSQL 15+
- Maven 3+

2. **Setup**
- Clone this repository

```bash
git clone https://github.com/adiisde/fin-ledger.git

cd ledger-core
```

- Configure database in ``application.yaml``
- Build and run

```bash
mvn clean install (run tests and build project)
mvn spring-boot:run
```

- Access APIs at: ``http://localhost:8066``

---

### APIs Routes

- Transfer: ``POST /account/transaction/transfer``
- Deposit: ``POST /account/transaction/deposit``
- Withdraw: ``POST /account/transaction/withdraw``

- Request body sample for transfer:

```json
{
    "fromAccountId": "<UUID who sending money>",
    "toAccountId": "<UUID who will receive money>",
    "amount": 100,
    "idempotencyKey": "<Unique_Key for every request of transaction>",
    "initiatedBy": "<UUID who sending money>"
}
```
---

### Testing and Load Testing

- Unit tests: ``mvn test``
- Load Testing (with k6 scripts and run when server is running) in `cd /tests` and run `k6 run stress.js`
