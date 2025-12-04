package digitalwallet.models;

import digitalwallet.enums.Currency;
import digitalwallet.exceptions.InsufficientBalanceException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a user's digital wallet with multi-currency support.
 * Thread-safe for concurrent access.
 */
public class Wallet {
    private final String id;
    private final String userId;
    private final LocalDateTime createdAt;
    private final Map<Currency, WalletBalance> balances;
    private final ReentrantLock lock = new ReentrantLock();
    
    private volatile boolean active;
    private volatile BigDecimal dailyTransferLimit;
    private volatile BigDecimal dailyWithdrawalLimit;
    private volatile Currency defaultCurrency;

    public Wallet(String id, String userId, Currency defaultCurrency) {
        this.id = Objects.requireNonNull(id, "Wallet ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.defaultCurrency = Objects.requireNonNull(defaultCurrency, "Default currency cannot be null");
        this.createdAt = LocalDateTime.now();
        this.balances = new ConcurrentHashMap<>();
        this.active = true;
        this.dailyTransferLimit = new BigDecimal("10000.00");
        this.dailyWithdrawalLimit = new BigDecimal("5000.00");
        
        // Initialize default currency balance
        this.balances.put(defaultCurrency, new WalletBalance(defaultCurrency));
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }
    public BigDecimal getDailyTransferLimit() { return dailyTransferLimit; }
    public BigDecimal getDailyWithdrawalLimit() { return dailyWithdrawalLimit; }
    public Currency getDefaultCurrency() { return defaultCurrency; }

    /**
     * Get balance for a specific currency
     */
    public WalletBalance getBalance(Currency currency) {
        return balances.computeIfAbsent(currency, WalletBalance::new);
    }

    /**
     * Get available balance for a currency
     */
    public BigDecimal getAvailableBalance(Currency currency) {
        WalletBalance balance = balances.get(currency);
        return balance != null ? balance.getAvailableBalance() : BigDecimal.ZERO;
    }

    /**
     * Get all balances
     */
    public Map<Currency, WalletBalance> getAllBalances() {
        return Collections.unmodifiableMap(new HashMap<>(balances));
    }

    /**
     * Credit funds to wallet
     */
    public void credit(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        
        if (!active) {
            throw new IllegalStateException("Cannot credit inactive wallet");
        }
        
        getBalance(currency).credit(amount);
    }

    /**
     * Debit funds from wallet
     * @throws InsufficientBalanceException if insufficient funds
     */
    public void debit(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        
        if (!active) {
            throw new IllegalStateException("Cannot debit inactive wallet");
        }
        
        WalletBalance balance = getBalance(currency);
        if (!balance.debit(amount)) {
            throw new InsufficientBalanceException(id, currency, amount, balance.getAvailableBalance());
        }
    }

    /**
     * Hold funds for pending transaction
     * @return true if hold successful
     */
    public boolean holdFunds(BigDecimal amount, Currency currency) {
        if (!active) return false;
        return getBalance(currency).holdPending(amount);
    }

    /**
     * Release held funds
     */
    public void releaseFunds(BigDecimal amount, Currency currency) {
        getBalance(currency).releasePending(amount);
    }

    /**
     * Check if wallet has sufficient balance
     */
    public boolean hasSufficientBalance(BigDecimal amount, Currency currency) {
        WalletBalance balance = balances.get(currency);
        return balance != null && balance.getAvailableBalance().compareTo(amount) >= 0;
    }

    // Configuration methods
    public void setDailyTransferLimit(BigDecimal limit) {
        lock.lock();
        try {
            this.dailyTransferLimit = limit;
        } finally {
            lock.unlock();
        }
    }

    public void setDailyWithdrawalLimit(BigDecimal limit) {
        lock.lock();
        try {
            this.dailyWithdrawalLimit = limit;
        } finally {
            lock.unlock();
        }
    }

    public void setDefaultCurrency(Currency currency) {
        lock.lock();
        try {
            this.defaultCurrency = currency;
            getBalance(currency); // Ensure balance exists
        } finally {
            lock.unlock();
        }
    }

    public void deactivate() {
        lock.lock();
        try {
            this.active = false;
        } finally {
            lock.unlock();
        }
    }

    public void activate() {
        lock.lock();
        try {
            this.active = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Wallet{id='%s', userId='%s', active=%s, balances=[", id, userId, active));
        balances.forEach((currency, balance) -> 
            sb.append(String.format("%s: %s, ", currency.name(), currency.format(balance.getAmount()))));
        if (!balances.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");
        return sb.toString();
    }
}



