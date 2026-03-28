package com.ledger.core.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.enums.ledger.EntryType;
import com.ledger.core.exception.InsufficientBalanceException;
import com.ledger.core.model.Account;
import com.ledger.core.model.AccountBalance;
import com.ledger.core.repository.AccountBalanceRepository;
import com.ledger.core.service.BalanceService;

@Service
public class BalanceServiceImpl implements BalanceService {
    private final AccountBalanceRepository balanceRepo;

    public BalanceServiceImpl(AccountBalanceRepository accountBalanceRepository) {
        this.balanceRepo = accountBalanceRepository;
    }

    /* get balance */

    @Override
    @Transactional
    public AccountBalance getBalance(UUID accountId) {
        return balanceRepo.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("Balance not found"));
    }

    /* updates balance based on ledger entries */

    @Override
    @Transactional
    public void applyLedgerImpact(Account account, BigDecimal amount, EntryType entryType, UUID ledgerEntryId) {

        AccountBalance balance = balanceRepo.findByAccountId(account.getId())
                .orElseThrow(() -> new IllegalStateException("Balance not found for account: " + account.getId()));

        BigDecimal newAvailable;
        BigDecimal newTotal;

        if (entryType == EntryType.DEBIT) {
            if (balance.getAvailableBalance().compareTo(amount) < 0)
                throw new InsufficientBalanceException("Insufficient balance");

            newAvailable = balance.getAvailableBalance().subtract(amount);
            newTotal = balance.getTotalBalance().subtract(amount);
        } else if (entryType == EntryType.CREDIT) {
            newAvailable = balance.getAvailableBalance().add(amount);
            newTotal = balance.getTotalBalance().add(amount);
        } else {
            throw new IllegalStateException("Invalid entry type");
        }

        balance.setLedgerUpdate(newAvailable, newTotal, ledgerEntryId);
        balanceRepo.save(balance);
        return;
    }
}
