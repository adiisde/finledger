package com.ledger.core.dto.account;

import com.ledger.core.enums.accounts.AccountCategory;
import com.ledger.core.enums.accounts.AccountType;

import jakarta.validation.constraints.NotNull;

public class CreateAccountRequest {
    @NotNull
    private String name;

    @NotNull
    private AccountType accountType;

    @NotNull
    private AccountCategory accountCategory;

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountCategory getAccountCategory() {
        return accountCategory;
    }
}
