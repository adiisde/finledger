package com.ledger.core.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.model.IdempotencyKey;
import com.ledger.core.model.Transaction;
import com.ledger.core.repository.IdempotencyKeyRepository;
import com.ledger.core.service.IdempotencyService;

@Service
public class IdempotencyServiceImpl implements IdempotencyService {
    private final IdempotencyKeyRepository idempotencyKeyRepo;

    public IdempotencyServiceImpl(IdempotencyKeyRepository idempotencyKeyRepo) {
        this.idempotencyKeyRepo = idempotencyKeyRepo;
    }

    /* validate or create new key for transaction */

    @Override
    @Transactional
    public IdempotencyKey validateOrCreateKey(String key, String requestHash) {
        IdempotencyKey existKey = idempotencyKeyRepo.findByKey(key).orElse(null);

        if (existKey == null) {
            try {
                IdempotencyKey newKey = new IdempotencyKey(key, requestHash);
                return idempotencyKeyRepo.save(newKey);
            } catch (DataIntegrityViolationException e) {
                return idempotencyKeyRepo.findByKey(key).orElseThrow(() -> e);
            }
        }

        if (!existKey.getRequestHash().equals(requestHash)) {
            throw new IllegalStateException("Idempotency key reused with different request payload");
        }

        return existKey;
    }

    /* update the key status as completed after transaction completion */

    @Override
    @Transactional
    public void markCompleted(IdempotencyKey key, Transaction transaction) {
        key.markCompleted(transaction);
        idempotencyKeyRepo.save(key);
    }
}
