package splitwise.services.impl;

import splitwise.enums.TransactionType;
import splitwise.exceptions.InvalidSettlementException;
import splitwise.exceptions.SplitwiseException;
import splitwise.models.Transaction;
import splitwise.observers.SettlementObserver;
import splitwise.repositories.TransactionRepository;
import splitwise.services.BalanceService;
import splitwise.services.TransactionService;
import splitwise.services.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionService.
 */
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final UserService userService;
    private final List<SettlementObserver> observers;
    
    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            BalanceService balanceService,
            UserService userService) {
        this.transactionRepository = transactionRepository;
        this.balanceService = balanceService;
        this.userService = userService;
        this.observers = new ArrayList<>();
    }
    
    /**
     * Add an observer for settlement events.
     */
    public void addObserver(SettlementObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Remove an observer.
     */
    public void removeObserver(SettlementObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public Transaction settleBalance(String fromUserId, String toUserId, BigDecimal amount) {
        // Validate users exist
        userService.getUser(fromUserId);
        userService.getUser(toUserId);
        
        if (fromUserId.equals(toUserId)) {
            throw new InvalidSettlementException("Cannot settle with yourself");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSettlementException("Settlement amount must be positive");
        }
        
        // Check current balance
        BigDecimal currentBalance = balanceService.getBalanceBetween(fromUserId, toUserId);
        
        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSettlementException(
                    "No outstanding balance from " + fromUserId + " to " + toUserId
            );
        }
        
        if (amount.compareTo(currentBalance) > 0) {
            throw new InvalidSettlementException(
                    "Settlement amount exceeds outstanding balance. Max: " + currentBalance
            );
        }
        
        // Update balance (reduce the debt)
        balanceService.updateBalance(fromUserId, toUserId, amount.negate());
        
        // Create settlement transaction
        Transaction settlement = new Transaction.Builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .type(TransactionType.SETTLEMENT)
                .description("Settlement payment")
                .build();
        
        settlement = transactionRepository.save(settlement);
        
        // Notify observers
        for (SettlementObserver observer : observers) {
            observer.onSettlement(settlement);
        }
        
        return settlement;
    }
    
    @Override
    public List<Transaction> getUserTransactions(String userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    @Override
    public List<Transaction> getTransactionsBetween(String userId1, String userId2) {
        return transactionRepository.findByUserPair(userId1, userId2);
    }
    
    @Override
    public Transaction getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new SplitwiseException("Transaction not found: " + transactionId));
    }
    
    @Override
    public List<Transaction> getAllSettlements() {
        return transactionRepository.findByType(TransactionType.SETTLEMENT);
    }
}



