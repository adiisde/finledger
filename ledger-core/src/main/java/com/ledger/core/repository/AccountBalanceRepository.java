package com.ledger.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledger.core.model.AccountBalance;

/* provides methods for account balance table in db */

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, UUID> {
    Optional<AccountBalance> findByAccountId(UUID accountId);
}
