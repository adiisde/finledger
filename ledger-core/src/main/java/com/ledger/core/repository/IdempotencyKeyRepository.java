package com.ledger.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledger.core.model.IdempotencyKey;

/* provides methods for idempotency keys table in db */

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    Optional<IdempotencyKey> findByKey(String key);
    boolean existsByKey(String key);
}
