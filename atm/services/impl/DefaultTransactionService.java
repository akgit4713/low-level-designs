package atm.services.impl;

import atm.models.Transaction;
import atm.services.TransactionService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default implementation of TransactionService.
 * Uses in-memory storage. In production, would use database.
 */
public class DefaultTransactionService implements TransactionService {
    
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> accountTransactions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> atmTransactions = new ConcurrentHashMap<>();

    @Override
    public void recordTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
        
        // Index by account
        accountTransactions.computeIfAbsent(transaction.getAccountNumber(), 
            k -> Collections.synchronizedList(new ArrayList<>()))
            .add(transaction.getTransactionId());
        
        // Index by ATM
        atmTransactions.computeIfAbsent(transaction.getAtmId(), 
            k -> Collections.synchronizedList(new ArrayList<>()))
            .add(transaction.getTransactionId());
    }

    @Override
    public Transaction getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

    @Override
    public List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        List<String> txnIds = accountTransactions.getOrDefault(accountNumber, Collections.emptyList());
        
        // Get most recent first
        return txnIds.stream()
            .map(transactions::get)
            .filter(Objects::nonNull)
            .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsForDate(String accountNumber, LocalDate date) {
        List<String> txnIds = accountTransactions.getOrDefault(accountNumber, Collections.emptyList());
        
        return txnIds.stream()
            .map(transactions::get)
            .filter(Objects::nonNull)
            .filter(t -> t.getTimestamp().toLocalDate().equals(date))
            .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByAtm(String atmId, LocalDate date) {
        List<String> txnIds = atmTransactions.getOrDefault(atmId, Collections.emptyList());
        
        return txnIds.stream()
            .map(transactions::get)
            .filter(Objects::nonNull)
            .filter(t -> t.getTimestamp().toLocalDate().equals(date))
            .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
            .collect(Collectors.toList());
    }
}



