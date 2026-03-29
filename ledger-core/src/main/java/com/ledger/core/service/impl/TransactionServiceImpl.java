package com.ledger.core.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.enums.idempotency.IdempotencyStatus;
import com.ledger.core.enums.logs.AuditAction;
import com.ledger.core.enums.logs.EntityType;
import com.ledger.core.enums.transactions.LinkType;
import com.ledger.core.enums.transactions.TransactionStatus;
import com.ledger.core.enums.transactions.TransactionType;
import com.ledger.core.model.Account;
import com.ledger.core.model.IdempotencyKey;
import com.ledger.core.model.Transaction;
import com.ledger.core.repository.TransactionRepository;
import com.ledger.core.service.AccountService;
import com.ledger.core.service.AuditService;
import com.ledger.core.service.IdempotencyService;
import com.ledger.core.service.LedgerService;
import com.ledger.core.service.TransactionLinkService;
import com.ledger.core.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepo;
    private final AccountService accountService;
    private final LedgerService ledgerService;
    private final AuditService auditService;
    private final TransactionLinkService transactionLinkService;
    private final IdempotencyService idempotencyService;

    public TransactionServiceImpl(TransactionRepository transactionRepo, AccountService accountService,
            LedgerService ledgerService, AuditService auditService, TransactionLinkService transactionLinkService,
            IdempotencyService idempotencyService) {
        this.transactionRepo = transactionRepo;
        this.accountService = accountService;
        this.ledgerService = ledgerService;
        this.auditService = auditService;
        this.idempotencyService = idempotencyService;
        this.transactionLinkService = transactionLinkService;
    }

    private String generateReference() {
        return "TXN-" + UUID.randomUUID().toString();
    }

    /*
     * creation of deposit balance transaction with ledger, audit and account
     * services
     */

    @Override
    @Transactional
    public Transaction deposit(UUID accountId, BigDecimal amount, String idempotencyKey, UUID initiatedBy) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        String requestHash = accountId + "-" + amount + "-DEPOSIT";

        IdempotencyKey txnKeyRecord = idempotencyService.validateOrCreateKey(idempotencyKey, requestHash);
        if (txnKeyRecord.getStatus().equals(IdempotencyStatus.COMPLETED)) {
            return txnKeyRecord.getTransaction();
        }

        Account account = accountService.getAccountById(accountId);
        if (account == null)
            throw new IllegalArgumentException("Account is required");

        Transaction txn = new Transaction(idempotencyKey, TransactionType.DEPOSIT, TransactionStatus.PENDING, amount,
                initiatedBy, generateReference(), account);

        transactionRepo.save(txn);

        try {
            ledgerService.credit(accountId, amount, txn);

            txn.setStatus(TransactionStatus.POSTED);
            txn.setPostedAt(LocalDateTime.now());

            idempotencyService.markCompleted(txnKeyRecord, txn);
        } catch (Exception e) {
            txn.setStatus(TransactionStatus.FAILED);
            transactionRepo.save(txn);

            idempotencyService.markFailed(txnKeyRecord);
            throw e;
        }

        transactionRepo.saveAndFlush(txn);

        auditService.logAction(AuditAction.CREATE, EntityType.TRANSACTION, txn.getId(), initiatedBy.toString(),
                "deposit of " + amount + " to account " + accountId);

        return txn;
    }

    /* creation of withdraw balance transaction */

    @Override
    @Transactional
    public Transaction withdraw(UUID accountId, BigDecimal amount, String idempotencyKey, UUID initiatedBy) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Transaction txn;

        String requestHash = accountId + "-" + amount + "-WITHDRAW";

        IdempotencyKey txnKeyRecord = idempotencyService.validateOrCreateKey(idempotencyKey, requestHash);
        if (txnKeyRecord.getStatus().equals(IdempotencyStatus.COMPLETED)) {
            return txnKeyRecord.getTransaction();
        }

        Account account = accountService.getAccountById(accountId);
        if (account == null)
            throw new IllegalArgumentException("Account is required");

        txn = new Transaction(idempotencyKey, TransactionType.WITHDRAWAL, TransactionStatus.PENDING, amount,
                initiatedBy, generateReference(), account);

        transactionRepo.save(txn);

        try {
            ledgerService.debit(accountId, amount, txn);

            txn.setStatus(TransactionStatus.POSTED);
            txn.setPostedAt(LocalDateTime.now());

            idempotencyService.markCompleted(txnKeyRecord, txn);
        } catch (Exception e) {

            txn.setStatus(TransactionStatus.FAILED);
            transactionRepo.save(txn);

            idempotencyService.markFailed(txnKeyRecord);
            throw e;
        }

        transactionRepo.saveAndFlush(txn);

        auditService.logAction(AuditAction.CREATE, EntityType.TRANSACTION, txn.getId(), initiatedBy.toString(),
                "withdrawal of " + amount + " from account " + accountId);

        return txn;
    }

    /* transfer of balance transaction */

    @Override
    @Transactional
    public Transaction transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String idempotencyKey,
            UUID initiatedBy) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Can't perform transfer to own");
        }

        String requestHash = fromAccountId + "-" + toAccountId + "-" + amount + "-TRANSFER";

        IdempotencyKey txnKeyRecord = idempotencyService.validateOrCreateKey(idempotencyKey, requestHash);

        if (txnKeyRecord.getStatus().equals(IdempotencyStatus.COMPLETED)) {
            return txnKeyRecord.getTransaction();
        }

        UUID first = fromAccountId.compareTo(toAccountId) < 0 ? fromAccountId : toAccountId;
        UUID second = fromAccountId.compareTo(toAccountId) < 0 ? toAccountId : fromAccountId;

        Account firstAccount = accountService.getAccountById(first);
        Account secondAccount = accountService.getAccountById(second);

        Account sender = fromAccountId.equals(first) ? firstAccount : secondAccount;
        Account receiver = fromAccountId.equals(first) ? secondAccount : firstAccount;

        /* transactions */

        Transaction debitTxn = new Transaction(idempotencyKey, TransactionType.TRANSFER, TransactionStatus.PENDING,
                amount, initiatedBy, generateReference(), sender);

        Transaction creditTxn = new Transaction(idempotencyKey, TransactionType.TRANSFER, TransactionStatus.PENDING,
                amount, initiatedBy, generateReference(), receiver);

        transactionRepo.save(debitTxn);
        transactionRepo.save(creditTxn);

        /* critical update */

        try {
            ledgerService.debit(fromAccountId, amount, debitTxn);
            ledgerService.credit(toAccountId, amount, creditTxn);

            LocalDateTime now = LocalDateTime.now();

            debitTxn.setStatus(TransactionStatus.POSTED);
            debitTxn.setPostedAt(now);

            creditTxn.setStatus(TransactionStatus.POSTED);
            creditTxn.setPostedAt(now);

            idempotencyService.markCompleted(txnKeyRecord, debitTxn);
        } catch (Exception e) {
            debitTxn.setStatus(TransactionStatus.FAILED);
            creditTxn.setStatus(TransactionStatus.FAILED);

            transactionRepo.save(debitTxn);
            transactionRepo.save(creditTxn);
            throw e;
        }

        transactionRepo.save(debitTxn);
        transactionRepo.save(creditTxn);

        /* idempotency mark and link transaction */

        transactionLinkService.createTransactionLink(debitTxn, creditTxn, LinkType.TRANSFER);

        transactionRepo.flush();

        auditService.logAction(AuditAction.CREATE, EntityType.TRANSACTION, debitTxn.getId(), initiatedBy.toString(),
                "transfer " + amount + " from " + fromAccountId + " to " + toAccountId);

        return debitTxn;
    }

    /* fetch all transactions */

    public Page<Transaction> getAccountTransactions(UUID accountId, Pageable pageable) {
        return transactionRepo.findByReferenceAccountId(accountId, pageable);
    }
}
