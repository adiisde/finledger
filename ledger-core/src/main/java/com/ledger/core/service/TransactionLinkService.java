package com.ledger.core.service;

import java.util.List;
import java.util.UUID;

import com.ledger.core.enums.transactions.LinkType;
import com.ledger.core.model.Transaction;
import com.ledger.core.model.TransactionLink;

public interface TransactionLinkService {
    TransactionLink createTransactionLink(Transaction originalTransaction, Transaction newTransaction, LinkType linkType);

    List<TransactionLink> findLinksForTransaction(UUID transactionId);
}
