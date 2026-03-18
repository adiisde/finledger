package com.ledger.core.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ledger.core.model.Transaction;

public interface TransactionService {
    Transaction deposit(UUID accountId, BigDecimal amount, String idempotencyKey, UUID initiatedBy);
    Transaction withdraw(UUID accountId, BigDecimal amount, String idempotencyKey, UUID initiatedBy);

    Transaction transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String idempotencyKey, UUID initiatedBy);
    Page<Transaction> getAccountTransactions(UUID accountId, Pageable pageable);
}
