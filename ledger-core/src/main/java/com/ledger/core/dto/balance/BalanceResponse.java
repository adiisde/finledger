package com.ledger.core.dto.balance;

import java.math.BigDecimal;
import java.util.UUID;

import com.ledger.core.model.AccountBalance;

public class BalanceResponse {
    private UUID accountId;
    private BigDecimal availableBalance;
    private BigDecimal totalBalance;

    public BalanceResponse(AccountBalance balance) {
        this.accountId = balance.getAccountId();
        this.availableBalance = balance.getAvailableBalance();
        this.totalBalance = balance.getTotalBalance();
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
}
