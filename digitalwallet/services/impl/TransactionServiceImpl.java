package digitalwallet.services.impl;

import digitalwallet.enums.Currency;
import digitalwallet.enums.TransactionType;
import digitalwallet.exceptions.WalletException;
import digitalwallet.models.Transaction;
import digitalwallet.models.TransactionStatement;
import digitalwallet.models.Wallet;
import digitalwallet.observers.TransactionObserver;
import digitalwallet.repositories.TransactionRepository;
import digitalwallet.repositories.WalletRepository;
import digitalwallet.services.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of TransactionService.
 */
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final List<TransactionObserver> observers;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                   WalletRepository walletRepository) {
        this.transactionRepository = Objects.requireNonNull(transactionRepository);
        this.walletRepository = Objects.requireNonNull(walletRepository);
        this.observers = new ArrayList<>();
    }

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Transaction createTransaction(String walletId, TransactionType type, 
                                          BigDecimal amount, Currency currency,
                                          String description, String referenceId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletException("Wallet not found: " + walletId));

        BigDecimal balanceAfter = wallet.getAvailableBalance(currency);
        if (type.isCredit()) {
            balanceAfter = balanceAfter.add(amount);
        } else {
            balanceAfter = balanceAfter.subtract(amount);
        }

        Transaction transaction = Transaction.builder()
            .id(UUID.randomUUID().toString())
            .walletId(walletId)
            .type(type)
            .amount(amount)
            .currency(currency)
            .description(description)
            .referenceId(referenceId)
            .idempotencyKey(UUID.randomUUID().toString())
            .balanceAfter(balanceAfter)
            .build();

        transactionRepository.save(transaction);
        notifyTransactionCreated(transaction);
        
        return transaction;
    }

    @Override
    public Optional<Transaction> getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Override
    public List<Transaction> getTransactions(String walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    @Override
    public List<Transaction> getTransactions(String walletId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByWalletIdAndDateRange(walletId, start, end);
    }

    @Override
    public TransactionStatement generateStatement(String walletId, LocalDateTime start, LocalDateTime end) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletException("Wallet not found: " + walletId));

        List<Transaction> transactions = transactionRepository
            .findByWalletIdAndDateRange(walletId, start, end);

        return TransactionStatement.builder()
            .walletId(walletId)
            .startDate(start)
            .endDate(end)
            .transactions(transactions)
            .primaryCurrency(wallet.getDefaultCurrency())
            .build();
    }

    @Override
    public void completeTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new WalletException("Transaction not found: " + transactionId));

        transaction.markCompleted();
        transactionRepository.save(transaction);
        notifyTransactionCompleted(transaction);
    }

    @Override
    public void failTransaction(String transactionId, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new WalletException("Transaction not found: " + transactionId));

        transaction.markFailed(reason);
        transactionRepository.save(transaction);
        notifyTransactionFailed(transaction, reason);
    }

    @Override
    public Transaction reverseTransaction(String transactionId, String reason) {
        Transaction original = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new WalletException("Transaction not found: " + transactionId));

        if (!original.isSuccessful()) {
            throw new WalletException("Can only reverse completed transactions");
        }

        // Mark original as reversed
        original.markReversed();
        transactionRepository.save(original);
        notifyTransactionReversed(original);

        // Create reversal transaction
        TransactionType reversalType = original.isCredit() ? 
            TransactionType.REVERSAL : TransactionType.REFUND;
        
        Transaction reversal = createTransaction(
            original.getWalletId(),
            reversalType,
            original.getAmount(),
            original.getCurrency(),
            "Reversal: " + reason,
            original.getId()
        );

        reversal.markCompleted();
        transactionRepository.save(reversal);
        notifyTransactionCompleted(reversal);

        return reversal;
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        return transactionRepository.findByIdempotencyKey(idempotencyKey);
    }

    // Observer notifications
    private void notifyTransactionCreated(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionCreated(transaction);
        }
    }

    private void notifyTransactionCompleted(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionCompleted(transaction);
        }
    }

    private void notifyTransactionFailed(Transaction transaction, String reason) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionFailed(transaction, reason);
        }
    }

    private void notifyTransactionReversed(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionReversed(transaction);
        }
    }
}



