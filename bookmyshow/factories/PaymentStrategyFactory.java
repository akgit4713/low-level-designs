package bookmyshow.factories;

import bookmyshow.enums.PaymentMethod;
import bookmyshow.strategies.payment.*;

/**
 * Factory for creating payment strategy instances based on payment method.
 */
public class PaymentStrategyFactory {
    
    /**
     * Get the appropriate payment strategy for the given payment method.
     * @param method The payment method
     * @return PaymentStrategy implementation
     */
    public static PaymentStrategy getStrategy(PaymentMethod method) {
        return switch (method) {
            case CREDIT_CARD, DEBIT_CARD -> new CreditCardPaymentStrategy();
            case UPI -> new UPIPaymentStrategy();
            case NET_BANKING -> new NetBankingPaymentStrategy();
            case WALLET -> new WalletPaymentStrategy();
            case CASH -> throw new UnsupportedOperationException("Cash payments not supported online");
        };
    }
}



