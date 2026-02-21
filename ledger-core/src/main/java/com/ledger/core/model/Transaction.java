package com.ledger.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.transactions.TransactionStatus;
import com.ledger.core.enums.transactions.TransactionType;

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
import jakarta.persistence.Version;

/* creates when an event occurs to describe that event (debit or credit) */
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    /* track requests to avoid duplicates */
    @Column(nullable = false, unique = true, updatable = false)
    private String idempotencyKey;

    /* type of transaction */
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    /* state of transaction */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    /* transaction amount */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /* specify about transaction if needed */
    @Column
    private String description;

    /* who initiated this transaction */
    @Column(nullable = false)
    private UUID initiatedBy;

    /* prevent race conditions */
    @Version
    private Long Version;

    /* transaction reference */
    @Column(nullable = false, unique = true)
    private String transactionReference;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account referenceAccount;

    /* timestamps */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    /* ledger entry written and balance updated */
    private LocalDateTime postedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
