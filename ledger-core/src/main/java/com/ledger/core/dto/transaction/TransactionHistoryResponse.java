package com.ledger.core.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.transactions.TransactionStatus;
import com.ledger.core.enums.transactions.TransactionType;
import com.ledger.core.model.Transaction;

public class TransactionHistoryResponse {
    private UUID transactionId;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String description;
    private String transactionReference;
    private LocalDateTime createdAt;

    public TransactionHistoryResponse(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.type = transaction.getTransactionType();
        this.status = transaction.getTransactionStatus();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.transactionReference = transaction.getTransactionReference();
        this.createdAt = transaction.getCreatedAt();
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public TransactionType getTransactionType() {
        return type;
    }

    public TransactionStatus getTransactionStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
