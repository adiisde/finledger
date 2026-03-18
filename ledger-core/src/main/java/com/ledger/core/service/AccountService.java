package com.ledger.core.service;

import java.util.UUID;

import com.ledger.core.enums.accounts.AccountCategory;
import com.ledger.core.enums.accounts.AccountType;
import com.ledger.core.model.Account;

public interface AccountService {
    Account getAccountById(UUID accountId);

    void validateAccountActive(Account account);

    Account createAccount(String name, AccountType accountType, AccountCategory accountCategory);
}
