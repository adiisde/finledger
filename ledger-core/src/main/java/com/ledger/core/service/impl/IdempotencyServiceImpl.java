package com.ledger.core.service.impl;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.enums.idempotency.IdempotencyStatus;
import com.ledger.core.exception.ConflictException;
import com.ledger.core.model.IdempotencyKey;
import com.ledger.core.model.Transaction;
import com.ledger.core.repository.IdempotencyKeyRepository;
import com.ledger.core.service.IdempotencyService;

@Service
public class IdempotencyServiceImpl implements IdempotencyService {
    private final IdempotencyKeyRepository idempotencyKeyRepo;
    private static final long IN_PROGRESS_TIMEOUT_SECS = 30;

    public IdempotencyServiceImpl(IdempotencyKeyRepository idempotencyKeyRepo) {
        this.idempotencyKeyRepo = idempotencyKeyRepo;
    }

    /* validate or create new key for transaction */

    @Override
    @Transactional
    public IdempotencyKey validateOrCreateKey(String key, String requestHash) {

        // Step 1: Try to fetch WITH LOCK
        IdempotencyKey existing = idempotencyKeyRepo.findByKeyForUpdate(key).orElse(null);

        // Step 2: If not found, try to create
        if (existing == null) {
            try {
                return idempotencyKeyRepo.save(new IdempotencyKey(key, requestHash));
            } catch (DataIntegrityViolationException e) {
                // Another transaction inserted the row → fetch again WITH LOCK
                existing = idempotencyKeyRepo.findByKeyForUpdate(key)
                        .orElseThrow(() -> new IllegalStateException("Key exists but not found"));
            }
        }

        // Step 3: Validate request hash
        if (!existing.getRequestHash().equals(requestHash)) {
            throw new IllegalStateException("Reused idempotency key with different payload");
        }

        // Step 4: Handle state machine
        switch (existing.getStatus()) {

            case COMPLETED:
                return existing;

            case IN_PROGRESS:
                if (isStaleKey(existing)) {
                    existing.markInProgress();
                    return existing;
                }
                return waitForCompletion(existing);

            case FAILED:
                existing.markInProgress();
                return existing;

            default:
                throw new IllegalStateException("Invalid status");
        }
    }

    /* update the key status as completed after transaction completion */

    @Override
    @Transactional
    public void markCompleted(IdempotencyKey key, Transaction transaction) {
        IdempotencyKey lockedKey = idempotencyKeyRepo.findById(key.getKey())
                .orElseThrow(() -> new IllegalStateException("Key not found"));
        if (lockedKey.getStatus() == IdempotencyStatus.COMPLETED) {
            return;
        }

        lockedKey.markCompleted(transaction);
    }

    @Override
    @Transactional
    public void markFailed(IdempotencyKey key) {
        IdempotencyKey lockedKey = idempotencyKeyRepo.findById(key.getKey())
                .orElseThrow(() -> new IllegalStateException("Key not found"));
        if (lockedKey.getStatus() == IdempotencyStatus.COMPLETED) {
            return;
        }

        lockedKey.markFailed();
    }

    /* validate the retry request is older than 30s or not to retry */

    private boolean isStaleKey(IdempotencyKey key) {
        return key.getUpdatedAt().isBefore(LocalDateTime.now().minusSeconds(IN_PROGRESS_TIMEOUT_SECS));
    }

    /* wait for completion */

    private IdempotencyKey waitForCompletion(IdempotencyKey key) {
        int retries = 10;
        long sleepMs = 100;

        for (int i = 0; i < retries; i++) {
            IdempotencyKey refreshedKey = idempotencyKeyRepo.findByKeyForUpdate(key.getKey())
                    .orElseThrow(() -> new IllegalStateException("Key not found"));

            if (refreshedKey.getStatus() == IdempotencyStatus.COMPLETED) {
                return refreshedKey;
            }

            if (refreshedKey.getStatus() == IdempotencyStatus.FAILED) {
                throw new IllegalStateException("Previous request failed");
            }

            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException ignored) {
            }
            ;
        }

        throw new ConflictException("Waiting for request completion");
    }
}
