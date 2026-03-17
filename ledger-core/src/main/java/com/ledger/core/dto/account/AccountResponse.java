package com.ledger.core.dto.account;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.accounts.AccountCategory;
import com.ledger.core.enums.accounts.AccountStatus;
import com.ledger.core.enums.accounts.AccountType;
import com.ledger.core.enums.accounts.NormalBalance;
import com.ledger.core.model.Account;

public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private String name;
    private AccountStatus status;
    private AccountType type;
    private NormalBalance normalBalance;
    private AccountCategory category;
    private LocalDateTime createdAt;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.accountNumber = account.getAccountNumber();
        this.name = account.getName();
        this.status = account.getAccountStatus();
        this.category = account.getAccountCategory();
        this.normalBalance = account.getNormalBalance();
        this.type = account.getAccountType();
        this.createdAt = account.getCreatedAt();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountStatus getAccountStatus() {
        return status;
    }

    public AccountType getAccountType() {
        return type;
    }

    public NormalBalance getNormalBalance() {
        return normalBalance;
    }

    public AccountCategory getAccountCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
