package com.ledger.core.model;

import java.time.LocalDateTime;

import com.ledger.core.enums.idempotency.IdempotencyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/* prevent duplicate request */
@Entity
@Table(name = "idempotency_keys", indexes = {
    @Index(name = "idx_idem_key", columnList = "key")
})
public class IdempotencyKey {
    @Id
    @Column(length = 64, unique = true)
    /* unique key sent by client */
    private String key;

    /* transaction created for the request */
    @OneToOne(optional = true)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;

    /* timestamps */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* request fingerprint */
    @Column(nullable = false)
    private String requestHash;

    /* locking */
    @Version
    private Long version;

    /* key status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdempotencyStatus status;

    /* last update */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /* constructor for creation of key */

    public IdempotencyKey(String key, String requestHash) {
        this.key = key;
        this.requestHash = requestHash;
        this.status = IdempotencyStatus.IN_PROGRESS;
    }

    protected IdempotencyKey() {};

    /* actions used in idempotency service */

    public void markCompleted(Transaction transaction) {
        this.transaction = transaction;
        this.status = IdempotencyStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.transaction = null;
        this.status = IdempotencyStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markInProgress() {
        if (this.status == IdempotencyStatus.COMPLETED) {
           return;
        }
        this.transaction = null;
        this.status = IdempotencyStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    /* getters */

    public String getKey() {
        return key;
    }

    public Transaction getTransaction() {
        if (this.status == IdempotencyStatus.COMPLETED && this.transaction == null) {
            throw new IllegalStateException("Completed key without transaction");
        }
        return transaction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }
}
