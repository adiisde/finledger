package com.ledger.core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledger.core.model.TransactionLink;

/* provides methods for transaction links table in db */

public interface TransactionLinkRepository extends JpaRepository<TransactionLink, UUID> {
    List<TransactionLink> findByOriginalTransactionId(UUID transactionId);
    List<TransactionLink> findByNewTransactionId(UUID transactionId);
}
