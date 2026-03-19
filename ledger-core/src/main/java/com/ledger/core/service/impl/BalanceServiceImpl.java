package com.ledger.core.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.exception.InsufficientBalanceException;
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
    public AccountBalance getBalance(UUID accountId) {
        return balanceRepo.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("Balance not found"));
    }

    /* updates balance based on ledger entries */

    @Override
    @Transactional
    public void applyLedgerImpact(UUID accountId, BigDecimal delta, UUID ledgerEntryId) {
        AccountBalance balance = balanceRepo.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("Account balance not found"));

        BigDecimal newAvailable = balance.getAvailableBalance().add(delta);
        BigDecimal newTotal = balance.getTotalBalance().add(delta);

        if (newAvailable.compareTo(BigDecimal.ZERO) < 0) throw new InsufficientBalanceException("Insufficient balance");

        balance.setLedgerUpdate(newAvailable, newTotal, ledgerEntryId);

        balanceRepo.save(balance);
    }
}
