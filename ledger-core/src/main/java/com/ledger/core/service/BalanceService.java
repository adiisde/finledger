package com.ledger.core.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.ledger.core.enums.ledger.EntryType;
import com.ledger.core.model.Account;
import com.ledger.core.model.AccountBalance;

public interface BalanceService {
    AccountBalance getBalance(UUID accountId);

    void applyLedgerImpact(Account account, BigDecimal amount, EntryType entryType, UUID ledgerEntryId);
}
