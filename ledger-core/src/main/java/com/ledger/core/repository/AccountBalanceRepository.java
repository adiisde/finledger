package com.ledger.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.ledger.core.model.AccountBalance;

import jakarta.persistence.LockModeType;

/* provides methods for account balance table in db */

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountBalance> findByAccountId(UUID accountId);
}
