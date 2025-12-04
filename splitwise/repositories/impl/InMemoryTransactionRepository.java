package splitwise.repositories.impl;

import splitwise.enums.TransactionType;
import splitwise.models.Transaction;
import splitwise.repositories.TransactionRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TransactionRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryTransactionRepository implements TransactionRepository {
    
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    
    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }
    
    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }
    
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
    
    @Override
    public List<Transaction> findByUserId(String userId) {
        return transactions.values().stream()
                .filter(tx -> tx.getFromUserId().equals(userId) || 
                              tx.getToUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByUserPair(String userId1, String userId2) {
        return transactions.values().stream()
                .filter(tx -> (tx.getFromUserId().equals(userId1) && tx.getToUserId().equals(userId2)) ||
                              (tx.getFromUserId().equals(userId2) && tx.getToUserId().equals(userId1)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByType(TransactionType type) {
        return transactions.values().stream()
                .filter(tx -> tx.getType() == type)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByReferenceId(String referenceId) {
        return transactions.values().stream()
                .filter(tx -> referenceId.equals(tx.getReferenceId()))
                .collect(Collectors.toList());
    }
}



