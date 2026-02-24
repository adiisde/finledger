package com.ledger.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.ledger.EntryType;

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

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {
    @Id
    @GeneratedValue
    private UUID id;

    /* operation that causes this ledger entry */
    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    /* hold account entity for owner of funds */
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /* entry type */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType entryType;

    /* how much funds moved */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

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

    public Transaction getTransaction() {
        return transaction;
    }

    public Account getAccount() {
        return account;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
