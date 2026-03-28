package com.ledger.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.ledger.core.model.IdempotencyKey;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

/* provides methods for idempotency keys table in db */

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("SELECT k FROM IdempotencyKey k WHERE k.key = :key")
    Optional<IdempotencyKey> findByKeyForUpdate(String key);
}
