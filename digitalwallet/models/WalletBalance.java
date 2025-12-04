package digitalwallet.models;

import digitalwallet.enums.Currency;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a balance in a specific currency within a wallet.
 * Thread-safe for concurrent access.
 */
public class WalletBalance {
    private final Currency currency;
    private final ReentrantLock lock = new ReentrantLock();
    
    private volatile BigDecimal amount;
    private volatile BigDecimal pendingAmount;

    public WalletBalance(Currency currency) {
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.amount = BigDecimal.ZERO;
        this.pendingAmount = BigDecimal.ZERO;
    }

    public WalletBalance(Currency currency, BigDecimal initialAmount) {
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.amount = initialAmount != null ? initialAmount : BigDecimal.ZERO;
        this.pendingAmount = BigDecimal.ZERO;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    /**
     * Get available balance (total minus pending)
     */
    public BigDecimal getAvailableBalance() {
        lock.lock();
        try {
            return amount.subtract(pendingAmount);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Credit funds to this balance
     */
    public void credit(BigDecimal creditAmount) {
        Objects.requireNonNull(creditAmount, "Credit amount cannot be null");
        if (creditAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        
        lock.lock();
        try {
            this.amount = this.amount.add(creditAmount);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Debit funds from this balance
     * @return true if successful, false if insufficient funds
     */
    public boolean debit(BigDecimal debitAmount) {
        Objects.requireNonNull(debitAmount, "Debit amount cannot be null");
        if (debitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        
        lock.lock();
        try {
            if (getAvailableBalance().compareTo(debitAmount) < 0) {
                return false;
            }
            this.amount = this.amount.subtract(debitAmount);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Hold funds as pending (e.g., for in-progress transactions)
     * @return true if funds can be held, false if insufficient
     */
    public boolean holdPending(BigDecimal holdAmount) {
        Objects.requireNonNull(holdAmount, "Hold amount cannot be null");
        if (holdAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hold amount must be positive");
        }
        
        lock.lock();
        try {
            BigDecimal availableAfterHold = amount.subtract(pendingAmount).subtract(holdAmount);
            if (availableAfterHold.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
            this.pendingAmount = this.pendingAmount.add(holdAmount);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Release pending hold (e.g., transaction failed or completed)
     */
    public void releasePending(BigDecimal releaseAmount) {
        Objects.requireNonNull(releaseAmount, "Release amount cannot be null");
        
        lock.lock();
        try {
            this.pendingAmount = this.pendingAmount.subtract(releaseAmount);
            if (this.pendingAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.pendingAmount = BigDecimal.ZERO;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Complete a pending transaction - release hold and debit
     * @return true if successful
     */
    public boolean completePending(BigDecimal completedAmount) {
        lock.lock();
        try {
            releasePending(completedAmount);
            return debit(completedAmount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("WalletBalance{currency=%s, amount=%s, pending=%s, available=%s}",
            currency.name(), currency.format(amount), 
            currency.format(pendingAmount), currency.format(getAvailableBalance()));
    }
}



