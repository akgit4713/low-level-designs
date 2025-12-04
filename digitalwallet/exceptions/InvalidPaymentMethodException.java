package digitalwallet.exceptions;

import digitalwallet.enums.PaymentMethodType;

/**
 * Exception thrown when a payment method is invalid or cannot be used.
 */
public class InvalidPaymentMethodException extends WalletException {
    
    private final String paymentMethodId;
    private final PaymentMethodType type;

    public InvalidPaymentMethodException(String message) {
        super(message, "INVALID_PAYMENT_METHOD");
        this.paymentMethodId = null;
        this.type = null;
    }

    public InvalidPaymentMethodException(String message, String paymentMethodId) {
        super(message, "INVALID_PAYMENT_METHOD");
        this.paymentMethodId = paymentMethodId;
        this.type = null;
    }

    public InvalidPaymentMethodException(String message, String paymentMethodId, PaymentMethodType type) {
        super(message, "INVALID_PAYMENT_METHOD");
        this.paymentMethodId = paymentMethodId;
        this.type = type;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public PaymentMethodType getType() {
        return type;
    }
}



