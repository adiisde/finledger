package com.ledger.core.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.ledger.core.model.Transaction;

public interface LedgerService {
    void credit(UUID accountId, BigDecimal amount, Transaction transaction);

    void debit(UUID accountId, BigDecimal amount, Transaction transaction);
}
