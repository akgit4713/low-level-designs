package splitwise.services.impl;

import splitwise.models.Balance;
import splitwise.services.BalanceService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementation of BalanceService with thread-safe operations.
 */
public class BalanceServiceImpl implements BalanceService {
    
    // Map of "userId1:userId2" -> Balance (where userId1 < userId2 lexicographically)
    private final Map<String, Balance> balances = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Get the key for a user pair (ensures consistent ordering)
     */
    private String getKey(String userId1, String userId2) {
        if (userId1.compareTo(userId2) <= 0) {
            return userId1 + ":" + userId2;
        } else {
            return userId2 + ":" + userId1;
        }
    }
    
    @Override
    public void updateBalance(String fromUserId, String toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            return; // No self-balance
        }
        
        lock.writeLock().lock();
        try {
            String key = getKey(fromUserId, toUserId);
            Balance balance = balances.computeIfAbsent(key, 
                    k -> new Balance(fromUserId, toUserId));
            
            // If fromUser owes toUser, the amount should increase the balance
            // Balance stores: positive means user1 owes user2
            if (fromUserId.compareTo(toUserId) <= 0) {
                balance.updateBalance(amount);
            } else {
                balance.updateBalance(amount.negate());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public BigDecimal getBalanceBetween(String userId1, String userId2) {
        if (userId1.equals(userId2)) {
            return BigDecimal.ZERO;
        }
        
        lock.readLock().lock();
        try {
            String key = getKey(userId1, userId2);
            Balance balance = balances.get(key);
            
            if (balance == null) {
                return BigDecimal.ZERO;
            }
            
            return balance.getAmountOwed(userId1, userId2);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Map<String, BigDecimal> getUserBalances(String userId) {
        lock.readLock().lock();
        try {
            Map<String, BigDecimal> result = new HashMap<>();
            
            for (Balance balance : balances.values()) {
                String otherUserId = null;
                BigDecimal amount = BigDecimal.ZERO;
                
                if (balance.getUser1Id().equals(userId)) {
                    otherUserId = balance.getUser2Id();
                    amount = balance.getAmount(); // Positive means userId owes otherUserId
                } else if (balance.getUser2Id().equals(userId)) {
                    otherUserId = balance.getUser1Id();
                    amount = balance.getAmount().negate(); // Negate for opposite perspective
                }
                
                if (otherUserId != null && amount.compareTo(BigDecimal.ZERO) != 0) {
                    result.put(otherUserId, amount);
                }
            }
            
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Balance getBalance(String userId1, String userId2) {
        lock.readLock().lock();
        try {
            String key = getKey(userId1, userId2);
            return balances.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isSettled(String userId1, String userId2) {
        return getBalanceBetween(userId1, userId2).compareTo(BigDecimal.ZERO) == 0;
    }
    
    @Override
    public BigDecimal getTotalOwed(String userId) {
        Map<String, BigDecimal> balanceMap = getUserBalances(userId);
        BigDecimal total = BigDecimal.ZERO;
        
        for (BigDecimal amount : balanceMap.values()) {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                total = total.add(amount);
            }
        }
        
        return total;
    }
    
    @Override
    public BigDecimal getTotalOwedToUser(String userId) {
        Map<String, BigDecimal> balanceMap = getUserBalances(userId);
        BigDecimal total = BigDecimal.ZERO;
        
        for (BigDecimal amount : balanceMap.values()) {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                total = total.add(amount.abs());
            }
        }
        
        return total;
    }
}



