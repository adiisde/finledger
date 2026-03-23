package com.ledger.core.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.core.dto.api.ApiResponse;
import com.ledger.core.dto.transaction.DepositRequest;
import com.ledger.core.dto.transaction.TransferRequest;
import com.ledger.core.dto.transaction.WithdrawRequest;
import com.ledger.core.model.Transaction;
import com.ledger.core.service.TransactionService;

@RestController
@RequestMapping("/account/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /* deposit */

    @PostMapping("/deposit")
    public ApiResponse<Transaction> deposit(@Validated @RequestBody DepositRequest request) {
        Transaction txn = transactionService.deposit(request.getAccountId(), request.getAmount(),
                request.getIdempotencyKey(), request.getInitiatedBy());
        return new ApiResponse<Transaction>(true, "Deposit successful", txn);
    }

    /* withdraw */

    @PostMapping("/withdraw")
    public ApiResponse<Transaction> withdraw(@Validated @RequestBody WithdrawRequest request) {
        Transaction txn = transactionService.withdraw(request.getAccountId(), request.getAmount(),
                request.getIdempotencyKey(), request.getInitiatedBy());
        return new ApiResponse<Transaction>(true, "Withdraw successful", txn);
    }

    /* transfer */

    @PostMapping("/transfer")
    public ApiResponse<Transaction> transfer(@Validated @RequestBody TransferRequest request) {
        Transaction txn = transactionService.transfer(request.getFromAccountId(), request.getToAccountId(),
                request.getAmount(), request.getIdempotencyKey(), request.getInitiatedBy());
        return new ApiResponse<Transaction>(true, "Transfer successful", txn);
    }

    /* get transaction history by account id */

    @GetMapping("/{accountId}")
    public ApiResponse<Page<Transaction>> transactionHistory(@PathVariable UUID accountId, Pageable pageable) {
        Page<Transaction> transactions = transactionService.getAccountTransactions(accountId, pageable);
        return new ApiResponse<Page<Transaction>>(true, "Transactions fetched", transactions);
    }
}
