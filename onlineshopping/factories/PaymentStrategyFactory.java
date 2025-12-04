package onlineshopping.factories;

import onlineshopping.enums.PaymentMethod;
import onlineshopping.exceptions.PaymentException;
import onlineshopping.strategies.payment.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating payment strategies
 * Follows Factory Pattern
 */
public class PaymentStrategyFactory {
    
    private static final Map<PaymentMethod, Supplier<PaymentStrategy>> strategies = new EnumMap<>(PaymentMethod.class);

    static {
        // Register default strategies
        strategies.put(PaymentMethod.CREDIT_CARD, CreditCardPaymentStrategy::new);
        strategies.put(PaymentMethod.DEBIT_CARD, DebitCardPaymentStrategy::new);
        strategies.put(PaymentMethod.UPI, UPIPaymentStrategy::new);
        strategies.put(PaymentMethod.WALLET, WalletPaymentStrategy::new);
        strategies.put(PaymentMethod.COD, CODPaymentStrategy::new);
    }

    private PaymentStrategyFactory() {
        // Prevent instantiation
    }

    /**
     * Get payment strategy for the given method
     */
    public static PaymentStrategy getStrategy(PaymentMethod method) {
        Supplier<PaymentStrategy> supplier = strategies.get(method);
        if (supplier == null) {
            throw PaymentException.unsupportedMethod(method);
        }
        return supplier.get();
    }

    /**
     * Register a new payment strategy
     */
    public static void register(PaymentMethod method, Supplier<PaymentStrategy> strategySupplier) {
        strategies.put(method, strategySupplier);
    }

    /**
     * Check if a payment method is supported
     */
    public static boolean isSupported(PaymentMethod method) {
        return strategies.containsKey(method);
    }
}



