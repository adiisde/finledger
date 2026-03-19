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

        Account account = accountService.getAccountById(accountId);
        Transaction txn = new Transaction(idempotencyKey, TransactionType.DEPOSIT, TransactionStatus.PENDING, amount,
                initiatedBy, generateReference(), account);

        IdempotencyKey txnKeyRecord = idempotencyService.validateOrCreateKey(idempotencyKey, requestHash);
        if (txnKeyRecord.getStatus().equals(IdempotencyStatus.COMPLETED)) {
            return txnKeyRecord.getTransaction();
        }

        transactionRepo.save(txn);
        ledgerService.credit(accountId, amount, txn);

        txn.setStatus(TransactionStatus.POSTED);
        txn.setPostedAt(LocalDateTime.now());

        transactionRepo.save(txn);
        idempotencyService.markCompleted(txnKeyRecord, txn);

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

        String requestHash = accountId + "-" + amount + "-WITHDRAW";

        Account account = accountService.getAccountById(accountId);
        Transaction txn = new Transaction(idempotencyKey, TransactionType.WITHDRAWAL, TransactionStatus.PENDING, amount,
                initiatedBy, generateReference(), account);

        IdempotencyKey txnKeyRecord = idempotencyService.validateOrCreateKey(idempotencyKey, requestHash);
        if (txnKeyRecord.getStatus().equals(IdempotencyStatus.COMPLETED)) {
            return txnKeyRecord.getTransaction();
        }

        transactionRepo.save(txn);
        ledgerService.debit(accountId, amount, txn);

        txn.setStatus(TransactionStatus.POSTED);
        txn.setPostedAt(LocalDateTime.now());

        transactionRepo.save(txn);
        idempotencyService.markCompleted(txnKeyRecord, txn);

        auditService.logAction(AuditAction.CREATE, EntityType.TRANSACTION, txn.getId(), initiatedBy.toString(),
                "Withdrawal of " + amount + " from account " + accountId);

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

        String requestHash = fromAccountId + "-" + toAccountId + "-" + amount + "-TRANSFER";

        Account sender = accountService.getAccountById(fromAccountId);
        Account receiver = accountService.getAccountById(toAccountId);

        /* debit transaction */

        Transaction debitTxn = new Transaction(idempotencyKey, TransactionType.TRANSFER, TransactionStatus.PENDING,
                amount, initiatedBy, generateReference(), sender);

        IdempotencyKey txnKeyRecord = idempotencyService.validateOrCreateKey(idempotencyKey, requestHash);
        if (txnKeyRecord.getStatus().equals(IdempotencyStatus.COMPLETED)) {
            return txnKeyRecord.getTransaction();
        }

        transactionRepo.save(debitTxn);

        ledgerService.debit(fromAccountId, amount, debitTxn);
        debitTxn.setStatus(TransactionStatus.POSTED);
        debitTxn.setPostedAt(LocalDateTime.now());
        transactionRepo.save(debitTxn);

        /* credit transaction */

        Transaction creditTxn = new Transaction(idempotencyKey, TransactionType.TRANSFER, TransactionStatus.PENDING,
                amount, initiatedBy, generateReference(), receiver);

        transactionRepo.save(creditTxn);
        ledgerService.credit(toAccountId, amount, creditTxn);
        creditTxn.setStatus(TransactionStatus.POSTED);
        creditTxn.setPostedAt(LocalDateTime.now());
        transactionRepo.save(creditTxn);

        /* link transaction */

        idempotencyService.markCompleted(txnKeyRecord, debitTxn);
        transactionLinkService.createTransactionLink(debitTxn, creditTxn, LinkType.TRANSFER);

        auditService.logAction(AuditAction.CREATE, EntityType.TRANSACTION, debitTxn.getId(), initiatedBy.toString(),
                "transfer transaction");
        return debitTxn;
    }

    /* fetch all transactions */

    public Page<Transaction> getAccountTransactions(UUID accountId, Pageable pageable) {
        return transactionRepo.findByReferenceAccountId(accountId, pageable);
    }
}
