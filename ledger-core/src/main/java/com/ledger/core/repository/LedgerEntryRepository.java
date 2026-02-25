package com.ledger.core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledger.core.model.LedgerEntry;

/* provides methods for ledger entries table in db */

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByAccountId(UUID accountId);
    List<LedgerEntry> findByTransactionId(UUID transactionId);
}
