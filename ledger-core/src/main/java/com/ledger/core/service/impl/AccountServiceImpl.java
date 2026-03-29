package com.ledger.core.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.enums.accounts.AccountCategory;
import com.ledger.core.enums.accounts.AccountStatus;
import com.ledger.core.enums.accounts.AccountType;
import com.ledger.core.enums.accounts.NormalBalance;
import com.ledger.core.enums.logs.AuditAction;
import com.ledger.core.enums.logs.EntityType;
import com.ledger.core.exception.AccountNotFoundException;
import com.ledger.core.model.Account;
import com.ledger.core.model.AccountBalance;
import com.ledger.core.repository.AccountBalanceRepository;
import com.ledger.core.repository.AccountRepository;
import com.ledger.core.service.AccountService;
import com.ledger.core.service.AuditService;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepo;
    private final AuditService auditService;
    private final AccountBalanceRepository accountBalanceRepo;

    public AccountServiceImpl(AccountRepository accountRepository, AuditService auditService, AccountBalanceRepository accountBalanceRepo) {
        this.accountRepo = accountRepository;
        this.auditService = auditService;
        this.accountBalanceRepo = accountBalanceRepo;
    }

    /* gets account with id */

    @Override
    public Account getAccountById(UUID accountId) {
        return accountRepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    /* validate account status */

    @Override
    public void validateAccountActive(Account account) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
    }

    /* create account */

    @Transactional
    @Override
    public Account createAccount(String name, AccountType type, AccountCategory category) {
        String accountNumber = generateAccountNumber();

        NormalBalance normalBalance = NormalBalance.DEBIT;
        Account account = new Account(accountNumber, name, type, category, normalBalance);

        account = accountRepo.save(account);
        auditService.logAction(AuditAction.CREATE, EntityType.ACCOUNT, account.getId(), account.getId().toString(), "account created");

        AccountBalance balance = new AccountBalance(account, BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        accountBalanceRepo.save(balance);

        return account;
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 10);
    }
}