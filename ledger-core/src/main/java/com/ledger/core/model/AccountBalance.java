package com.ledger.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "account_balances")
/* updates when ledger entries are written */
public class AccountBalance {
    @Id
    private UUID accountId;

    /* holds account entity */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    /* usable account balance */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    /* total balance current position includes also pending */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalBalance;

    /* last ledger used to calculate the balance */
    @Column(nullable = false)
    private UUID lastLedgerEntryId;

    /* prevents against race conditions */
    @Version
    private Long version;

    /* timestamps */
    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    /* timestamps methods */
    @PrePersist
    void onCreate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /* getters */

    public UUID getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public UUID getLastLedgerEntryId() {
        return lastLedgerEntryId;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /* setters */

    public void setLedgerUpdate(BigDecimal availableNewBalance, BigDecimal totalNewBalance, UUID ledgerEntryId) {
        this.availableBalance = availableNewBalance;
        this.totalBalance = totalNewBalance;
        this.lastLedgerEntryId = ledgerEntryId;
    }


}
