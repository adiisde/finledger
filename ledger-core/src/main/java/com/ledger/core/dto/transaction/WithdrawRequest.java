package com.ledger.core.dto.transaction;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class WithdrawRequest {
    @NotNull
    private UUID accountId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull
    private String idempotencyKey;

    @NotNull
    private UUID initiatedBy;

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getInitiatedBy() {
        return initiatedBy;
    }
}
