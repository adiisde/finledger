package com.ledger.core.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.core.enums.transactions.LinkType;
import com.ledger.core.model.Transaction;
import com.ledger.core.model.TransactionLink;
import com.ledger.core.repository.TransactionLinkRepository;
import com.ledger.core.service.TransactionLinkService;

@Service
public class TransactionLinkServiceImpl implements TransactionLinkService {
    private final TransactionLinkRepository transactionLinkRepo;

    public TransactionLinkServiceImpl(TransactionLinkRepository transactionLinkRepo) {
        this.transactionLinkRepo = transactionLinkRepo;
    }

    /* create new transaction link to old transaction */

    @Override
    @Transactional
    public TransactionLink createTransactionLink(Transaction originalTransaction, Transaction newTransaction,
            LinkType linkType) {
        if (originalTransaction.getId().equals(newTransaction.getId()))
            throw new IllegalArgumentException("Cannot link a transaction to itself");

        TransactionLink newLink = new TransactionLink(originalTransaction, newTransaction, linkType);
        return transactionLinkRepo.save(newLink);
    }

    /* fetch transaction links by transaction id */

    @Override
    public List<TransactionLink> findLinksForTransaction(UUID transactionId) {
        return transactionLinkRepo.findByOriginalTransactionId(transactionId);
    }
}
