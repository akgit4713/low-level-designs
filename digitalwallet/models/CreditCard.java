package digitalwallet.models;

import digitalwallet.enums.PaymentMethodType;
import java.util.Objects;

/**
 * Represents a credit or debit card payment method.
 * Card numbers are stored masked for security.
 */
public class CreditCard extends PaymentMethod {
    private final String lastFourDigits;
    private final String cardholderName;
    private final String expiryMonth;
    private final String expiryYear;
    private final CardBrand brand;
    
    // Encrypted storage - not the actual CVV
    private final String encryptedToken;

    public enum CardBrand {
        VISA("Visa", "4"),
        MASTERCARD("Mastercard", "5"),
        AMEX("American Express", "3"),
        DISCOVER("Discover", "6"),
        UNKNOWN("Unknown", "");

        private final String displayName;
        private final String prefix;

        CardBrand(String displayName, String prefix) {
            this.displayName = displayName;
            this.prefix = prefix;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static CardBrand fromCardNumber(String cardNumber) {
            if (cardNumber == null || cardNumber.isEmpty()) {
                return UNKNOWN;
            }
            String firstDigit = cardNumber.substring(0, 1);
            for (CardBrand brand : values()) {
                if (brand.prefix.equals(firstDigit)) {
                    return brand;
                }
            }
            return UNKNOWN;
        }
    }

    private CreditCard(Builder builder) {
        super(builder.id, builder.userId, builder.type);
        this.lastFourDigits = builder.lastFourDigits;
        this.cardholderName = builder.cardholderName;
        this.expiryMonth = builder.expiryMonth;
        this.expiryYear = builder.expiryYear;
        this.brand = builder.brand;
        this.encryptedToken = builder.encryptedToken;
        this.nickname = builder.nickname;
    }

    public String getLastFourDigits() { return lastFourDigits; }
    public String getCardholderName() { return cardholderName; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public CardBrand getBrand() { return brand; }
    public String getEncryptedToken() { return encryptedToken; }

    public String getExpiry() {
        return expiryMonth + "/" + expiryYear;
    }

    public boolean isExpired() {
        int currentYear = java.time.LocalDate.now().getYear() % 100;
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int cardYear = Integer.parseInt(expiryYear);
        int cardMonth = Integer.parseInt(expiryMonth);
        
        return cardYear < currentYear || (cardYear == currentYear && cardMonth < currentMonth);
    }

    @Override
    public String getMaskedDisplay() {
        return String.format("**** **** **** %s", lastFourDigits);
    }

    @Override
    protected String getDefaultDisplayName() {
        return String.format("%s ending in %s", brand.getDisplayName(), lastFourDigits);
    }

    @Override
    public boolean canDeposit() {
        return super.canDeposit() && !isExpired();
    }

    @Override
    public boolean canWithdraw() {
        return super.canWithdraw() && !isExpired();
    }

    @Override
    public String toString() {
        return String.format("CreditCard{id='%s', brand=%s, lastFour='%s', expiry='%s', active=%s}",
            id, brand.getDisplayName(), lastFourDigits, getExpiry(), active);
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private PaymentMethodType type = PaymentMethodType.CREDIT_CARD;
        private String lastFourDigits;
        private String cardholderName;
        private String expiryMonth;
        private String expiryYear;
        private CardBrand brand;
        private String encryptedToken;
        private String nickname;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(PaymentMethodType type) {
            this.type = type;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            // Only store last 4 digits
            if (cardNumber != null && cardNumber.length() >= 4) {
                this.lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
                this.brand = CardBrand.fromCardNumber(cardNumber);
            }
            return this;
        }

        public Builder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }

        public Builder cardholderName(String cardholderName) {
            this.cardholderName = cardholderName;
            return this;
        }

        public Builder expiry(String expiry) {
            // Parse MM/YY format
            if (expiry != null && expiry.contains("/")) {
                String[] parts = expiry.split("/");
                this.expiryMonth = parts[0];
                this.expiryYear = parts[1];
            }
            return this;
        }

        public Builder expiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }

        public Builder expiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }

        public Builder brand(CardBrand brand) {
            this.brand = brand;
            return this;
        }

        public Builder encryptedToken(String encryptedToken) {
            this.encryptedToken = encryptedToken;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public CreditCard build() {
            Objects.requireNonNull(id, "Card ID is required");
            Objects.requireNonNull(userId, "User ID is required");
            Objects.requireNonNull(lastFourDigits, "Last four digits are required");
            Objects.requireNonNull(cardholderName, "Cardholder name is required");
            Objects.requireNonNull(expiryMonth, "Expiry month is required");
            Objects.requireNonNull(expiryYear, "Expiry year is required");
            
            if (brand == null) {
                brand = CardBrand.UNKNOWN;
            }
            
            return new CreditCard(this);
        }
    }
}



