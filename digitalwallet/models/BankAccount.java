package digitalwallet.models;

import digitalwallet.enums.PaymentMethodType;
import java.util.Objects;

/**
 * Represents a bank account payment method.
 * Account numbers are stored masked for security.
 */
public class BankAccount extends PaymentMethod {
    private final String bankName;
    private final String routingNumber;
    private final String lastFourDigits;
    private final AccountType accountType;
    private final String accountHolderName;
    
    // Encrypted storage
    private final String encryptedAccountNumber;

    public enum AccountType {
        CHECKING("Checking"),
        SAVINGS("Savings");

        private final String displayName;

        AccountType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static AccountType fromString(String type) {
            if (type == null) return CHECKING;
            return switch (type.toUpperCase()) {
                case "SAVINGS", "SAVING" -> SAVINGS;
                default -> CHECKING;
            };
        }
    }

    private BankAccount(Builder builder) {
        super(builder.id, builder.userId, PaymentMethodType.BANK_ACCOUNT);
        this.bankName = builder.bankName;
        this.routingNumber = builder.routingNumber;
        this.lastFourDigits = builder.lastFourDigits;
        this.accountType = builder.accountType;
        this.accountHolderName = builder.accountHolderName;
        this.encryptedAccountNumber = builder.encryptedAccountNumber;
        this.nickname = builder.nickname;
    }

    public String getBankName() { return bankName; }
    public String getRoutingNumber() { return routingNumber; }
    public String getLastFourDigits() { return lastFourDigits; }
    public AccountType getAccountType() { return accountType; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getEncryptedAccountNumber() { return encryptedAccountNumber; }

    @Override
    public String getMaskedDisplay() {
        return String.format("****%s", lastFourDigits);
    }

    @Override
    protected String getDefaultDisplayName() {
        return String.format("%s %s ****%s", bankName, accountType.getDisplayName(), lastFourDigits);
    }

    @Override
    public String toString() {
        return String.format("BankAccount{id='%s', bank='%s', type=%s, lastFour='%s', active=%s}",
            id, bankName, accountType.getDisplayName(), lastFourDigits, active);
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private String bankName;
        private String routingNumber;
        private String lastFourDigits;
        private AccountType accountType = AccountType.CHECKING;
        private String accountHolderName;
        private String encryptedAccountNumber;
        private String nickname;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public Builder routingNumber(String routingNumber) {
            this.routingNumber = routingNumber;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            // Only store last 4 digits
            if (accountNumber != null && accountNumber.length() >= 4) {
                this.lastFourDigits = accountNumber.substring(accountNumber.length() - 4);
            }
            return this;
        }

        public Builder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }

        public Builder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder accountType(String accountType) {
            this.accountType = AccountType.fromString(accountType);
            return this;
        }

        public Builder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public Builder encryptedAccountNumber(String encryptedAccountNumber) {
            this.encryptedAccountNumber = encryptedAccountNumber;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public BankAccount build() {
            Objects.requireNonNull(id, "Bank account ID is required");
            Objects.requireNonNull(userId, "User ID is required");
            Objects.requireNonNull(bankName, "Bank name is required");
            Objects.requireNonNull(routingNumber, "Routing number is required");
            Objects.requireNonNull(lastFourDigits, "Last four digits are required");
            Objects.requireNonNull(accountHolderName, "Account holder name is required");
            
            return new BankAccount(this);
        }
    }
}



