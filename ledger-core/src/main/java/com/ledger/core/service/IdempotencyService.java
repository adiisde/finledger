package com.ledger.core.service;

import com.ledger.core.model.IdempotencyKey;
import com.ledger.core.model.Transaction;

public interface IdempotencyService {
    IdempotencyKey validateOrCreateKey(String key, String requestHash);

    void markCompleted(IdempotencyKey key, Transaction transaction);
}
