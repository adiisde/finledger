package com.ledger.core.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.core.dto.api.ApiResponse;
import com.ledger.core.dto.balance.BalanceResponse;
import com.ledger.core.model.AccountBalance;
import com.ledger.core.service.BalanceService;

@RestController
@RequestMapping("/account/balance")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /* fetch balance by account id */

    @GetMapping("/{accountId}")
    public ApiResponse<BalanceResponse> getBalance(@PathVariable UUID accountId) {
        AccountBalance balance = balanceService.getBalance(accountId);

        BalanceResponse response = new BalanceResponse(balance);

        return new ApiResponse<>(true, "Balance fetched", response);
    }
}
