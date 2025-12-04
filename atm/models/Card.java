package atm.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a bank card used for ATM authentication.
 */
public class Card {
    
    private final String cardNumber;
    private final String customerName;
    private final LocalDate expiryDate;
    private final String bankCode;
    private boolean blocked;
    private int failedAttempts;

    public Card(String cardNumber, String customerName, LocalDate expiryDate, String bankCode) {
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number cannot be null");
        this.customerName = Objects.requireNonNull(customerName, "Customer name cannot be null");
        this.expiryDate = Objects.requireNonNull(expiryDate, "Expiry date cannot be null");
        this.bankCode = Objects.requireNonNull(bankCode, "Bank code cannot be null");
        this.blocked = false;
        this.failedAttempts = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMaskedCardNumber() {
        if (cardNumber.length() <= 4) {
            return cardNumber;
        }
        return "XXXX-XXXX-XXXX-" + cardNumber.substring(cardNumber.length() - 4);
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getBankCode() {
        return bankCode;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
        this.failedAttempts = 0;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
        if (failedAttempts >= 3) {
            block();
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardNumber.equals(card.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return "Card{" +
               "cardNumber='" + getMaskedCardNumber() + '\'' +
               ", customerName='" + customerName + '\'' +
               ", expiryDate=" + expiryDate +
               ", blocked=" + blocked +
               '}';
    }
}



