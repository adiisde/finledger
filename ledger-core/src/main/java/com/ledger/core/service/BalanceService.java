package com.ledger.core.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.ledger.core.model.AccountBalance;

public interface BalanceService {
    AccountBalance getBalance(UUID accountId);

    void applyLedgerImpact(UUID accountId, BigDecimal delta, UUID ledgerEntryId);
}
