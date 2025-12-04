package digitalwallet.repositories.impl;

import digitalwallet.enums.TransactionStatus;
import digitalwallet.enums.TransactionType;
import digitalwallet.models.Transaction;
import digitalwallet.repositories.TransactionRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TransactionRepository.
 * Uses ConcurrentHashMap for thread-safety.
 */
public class InMemoryTransactionRepository implements TransactionRepository {
    
    private final ConcurrentHashMap<String, Transaction> transactions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> walletIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> idempotencyIndex = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        
        // Update wallet index
        walletIndex.computeIfAbsent(transaction.getWalletId(), k -> 
            Collections.synchronizedList(new ArrayList<>())).add(transaction.getId());
        
        // Update idempotency index
        if (transaction.getIdempotencyKey() != null) {
            idempotencyIndex.put(transaction.getIdempotencyKey(), transaction.getId());
        }
        
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public boolean deleteById(String id) {
        Transaction tx = transactions.remove(id);
        if (tx != null) {
            List<String> walletTxs = walletIndex.get(tx.getWalletId());
            if (walletTxs != null) {
                walletTxs.remove(id);
            }
            if (tx.getIdempotencyKey() != null) {
                idempotencyIndex.remove(tx.getIdempotencyKey());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return transactions.containsKey(id);
    }

    @Override
    public long count() {
        return transactions.size();
    }

    @Override
    public List<Transaction> findByWalletId(String walletId) {
        List<String> txIds = walletIndex.get(walletId);
        if (txIds == null) {
            return new ArrayList<>();
        }
        return txIds.stream()
            .map(transactions::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Transaction::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByWalletIdAndDateRange(String walletId, LocalDateTime start, LocalDateTime end) {
        return findByWalletId(walletId).stream()
            .filter(tx -> !tx.getCreatedAt().isBefore(start) && !tx.getCreatedAt().isAfter(end))
            .sorted(Comparator.comparing(Transaction::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByReferenceId(String referenceId) {
        return transactions.values().stream()
            .filter(tx -> referenceId.equals(tx.getReferenceId()))
            .sorted(Comparator.comparing(Transaction::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        String txId = idempotencyIndex.get(idempotencyKey);
        return txId != null ? findById(txId) : Optional.empty();
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        return transactions.values().stream()
            .filter(tx -> tx.getStatus() == status)
            .sorted(Comparator.comparing(Transaction::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByWalletIdAndType(String walletId, TransactionType type) {
        return findByWalletId(walletId).stream()
            .filter(tx -> tx.getType() == type)
            .collect(Collectors.toList());
    }

    @Override
    public long countByWalletIdAndDate(String walletId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return findByWalletIdAndDateRange(walletId, startOfDay, endOfDay).size();
    }
}



