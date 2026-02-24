package com.ledger.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.transactions.LinkType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/* transaction links have link to old transaction to new transaction */

@Entity
@Table(name = "transaction_links")
public class TransactionLink {
    @Id
    @GeneratedValue
    private UUID id;

    /* original or first transaction for that operation */
    @ManyToOne
    @JoinColumn(name = "original_transaction_id", nullable = false)
    private Transaction originalTransaction;

    /* new transaction for that old transaction */
    @ManyToOne
    @JoinColumn(name = "new_transaction_id", nullable = false)
    private Transaction newTransaction;

    /* link type */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkType linkType;

    /* timestamps */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /* getters */

    public UUID getId() {
        return id;
    }

    public Transaction getOriginalTransaction() {
        return originalTransaction;
    }

    public Transaction getNewTransaction() {
        return newTransaction;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
