package com.ledger.core.model;

import java.time.LocalDateTime;

import com.ledger.core.enums.idempotency.IdempotencyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/* prevent duplicate request */
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {
    @Id
    @Column(length = 64)
    /* unique key sent by client */
    private String key;

    /* transaction created for the request */
    @OneToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = true, unique = true)
    private Transaction transaction;

    /* timestamps */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* request fingerprint */
    @Column(nullable = false)
    private String requestHash;

    /* key status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdempotencyStatus status;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
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
    }

    public void markFailed() {
        this.status = IdempotencyStatus.FAILED;
    }

    /* getters */

    public String getKey() {
        return key;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }
}
