package com.ledger.core.controller;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.core.dto.account.CreateAccountRequest;
import com.ledger.core.dto.api.ApiResponse;
import com.ledger.core.model.Account;
import com.ledger.core.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /* create account */

    @PostMapping("/create")
    public ApiResponse<Account> createAccount(@Validated @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request.getName(), request.getAccountType(),
                request.getAccountCategory());
        return new ApiResponse<>(true, "Account created", account);
    }

    /* fetch account by id */

    @GetMapping("/{accountId}")
    public ApiResponse<Account> getAccount(@PathVariable UUID accountId) {
        Account account = accountService.getAccountById(accountId);
        return new ApiResponse<>(true, "Account fetched", account);
    }

}
