package com.ledger.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.accounts.AccountCategory;
import com.ledger.core.enums.accounts.AccountStatus;
import com.ledger.core.enums.accounts.AccountType;
import com.ledger.core.enums.accounts.NormalBalance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue
    private UUID id;

    /* unique account number */
    @Column(nullable = false, unique = true)
    private String accountNumber;

    /* account holder name */
    @Column(nullable = false)
    private String name;

    /* account current status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    /* account type */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    /* normal balance */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NormalBalance normalBalance;

    /* account category */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountCategory accountCategory;

    /* timestamps */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
