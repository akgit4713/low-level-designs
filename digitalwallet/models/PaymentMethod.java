package digitalwallet.models;

import digitalwallet.enums.PaymentMethodType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for payment methods.
 * Extended by CreditCard and BankAccount.
 */
public abstract class PaymentMethod {
    protected final String id;
    protected final String userId;
    protected final PaymentMethodType type;
    protected final LocalDateTime addedAt;
    
    protected volatile boolean verified;
    protected volatile boolean active;
    protected volatile String nickname;

    protected PaymentMethod(String id, String userId, PaymentMethodType type) {
        this.id = Objects.requireNonNull(id, "Payment method ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.type = Objects.requireNonNull(type, "Payment method type cannot be null");
        this.addedAt = LocalDateTime.now();
        this.verified = false;
        this.active = true;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public PaymentMethodType getType() { return type; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public boolean isVerified() { return verified; }
    public boolean isActive() { return active; }
    public String getNickname() { return nickname; }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void markVerified() {
        this.verified = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    /**
     * Check if this payment method can be used for deposits
     */
    public boolean canDeposit() {
        return active && verified && type.supportsDeposit();
    }

    /**
     * Check if this payment method can be used for withdrawals
     */
    public boolean canWithdraw() {
        return active && verified && type.supportsWithdrawal();
    }

    /**
     * Get a masked representation for display
     */
    public abstract String getMaskedDisplay();

    /**
     * Get the display name (nickname or default)
     */
    public String getDisplayName() {
        return nickname != null ? nickname : getDefaultDisplayName();
    }

    protected abstract String getDefaultDisplayName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentMethod that = (PaymentMethod) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}



