package atm.models;

import atm.enums.AccountType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a bank account with balance and transaction limits.
 * Thread-safe for concurrent access.
 */
public class Account {
    
    private final String accountNumber;
    private final String cardNumber;
    private final AccountType accountType;
    private BigDecimal balance;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal withdrawnToday;
    private final ReentrantLock lock = new ReentrantLock();

    public Account(String accountNumber, String cardNumber, AccountType accountType, 
                   BigDecimal balance, BigDecimal dailyWithdrawalLimit) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number cannot be null");
        this.accountType = Objects.requireNonNull(accountType, "Account type cannot be null");
        this.balance = Objects.requireNonNull(balance, "Balance cannot be null");
        this.dailyWithdrawalLimit = Objects.requireNonNull(dailyWithdrawalLimit, "Daily limit cannot be null");
        this.withdrawnToday = BigDecimal.ZERO;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public BigDecimal getWithdrawnToday() {
        lock.lock();
        try {
            return withdrawnToday;
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getRemainingDailyLimit() {
        lock.lock();
        try {
            return dailyWithdrawalLimit.subtract(withdrawnToday);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Debit amount from account for withdrawal.
     * @param amount Amount to debit
     * @return true if successful, false if insufficient funds or limit exceeded
     */
    public boolean debit(BigDecimal amount) {
        lock.lock();
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            if (balance.compareTo(amount) < 0) {
                return false; // Insufficient funds
            }
            if (withdrawnToday.add(amount).compareTo(dailyWithdrawalLimit) > 0) {
                return false; // Daily limit exceeded
            }
            balance = balance.subtract(amount);
            withdrawnToday = withdrawnToday.add(amount);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Credit amount to account for deposit.
     * @param amount Amount to credit
     * @return true if successful
     */
    public boolean credit(BigDecimal amount) {
        lock.lock();
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            balance = balance.add(amount);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reset daily withdrawal counter (called at midnight).
     */
    public void resetDailyLimit() {
        lock.lock();
        try {
            withdrawnToday = BigDecimal.ZERO;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if withdrawal is allowed for given amount.
     */
    public boolean canWithdraw(BigDecimal amount) {
        lock.lock();
        try {
            return balance.compareTo(amount) >= 0 &&
                   withdrawnToday.add(amount).compareTo(dailyWithdrawalLimit) <= 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "Account{" +
               "accountNumber='" + accountNumber + '\'' +
               ", type=" + accountType +
               ", balance=" + balance +
               '}';
    }
}



