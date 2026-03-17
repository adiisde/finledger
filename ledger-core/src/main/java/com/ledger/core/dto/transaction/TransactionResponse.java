package com.ledger.core.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.transactions.TransactionStatus;
import com.ledger.core.enums.transactions.TransactionType;
import com.ledger.core.model.Transaction;

public class TransactionResponse {
    private UUID id;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String reference;
    private LocalDateTime createdAt;
    private LocalDateTime postedAt;

    public TransactionResponse(Transaction txn) {
        this.id = txn.getId();
        this.type = txn.getTransactionType();
        this.status = txn.getTransactionStatus();
        this.amount = txn.getAmount();
        this.reference = txn.getTransactionReference();
        this.createdAt = txn.getCreatedAt();
        this.postedAt = txn.getPostedAt();
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }
}
