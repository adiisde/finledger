package com.ledger.core.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.enums.ledger.EntryType;
import com.ledger.core.model.Account;
import com.ledger.core.model.LedgerEntry;
import com.ledger.core.model.Transaction;
import com.ledger.core.repository.LedgerEntryRepository;
import com.ledger.core.service.AccountService;
import com.ledger.core.service.BalanceService;
import com.ledger.core.service.LedgerService;

@Service
public class LedgerServiceImpl implements LedgerService {
    private final LedgerEntryRepository ledgerEntryRepo;
    private final AccountService accountService;
    private final BalanceService balanceService;

    public LedgerServiceImpl(LedgerEntryRepository ledgerEntryRepo, AccountService accountService, BalanceService balanceService) {
        this.accountService = accountService;
        this.ledgerEntryRepo = ledgerEntryRepo;
        this.balanceService = balanceService;
    }

    /* balance credit entry with balance update */

    @Override
    @Transactional
    public void credit(UUID accountId, BigDecimal amount, Transaction transaction) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");

        Account account = accountService.getAccountById(accountId);
        LedgerEntry entry = new LedgerEntry(transaction, account, EntryType.CREDIT, amount);
        
        ledgerEntryRepo.save(entry);
        balanceService.applyLedgerImpact(accountId, amount, entry.getId());
    }

    @Override
    @Transactional
    public void debit(UUID accountId, BigDecimal amount, Transaction transaction) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");

        Account account = accountService.getAccountById(accountId);
        LedgerEntry entry = new LedgerEntry(transaction, account, EntryType.DEBIT, amount);
    
        ledgerEntryRepo.save(entry);
        balanceService.applyLedgerImpact(accountId, amount, entry.getId());
    }
}
