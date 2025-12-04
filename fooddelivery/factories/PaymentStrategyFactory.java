package fooddelivery.factories;

import fooddelivery.enums.PaymentMethod;
import fooddelivery.strategies.payment.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for creating payment strategy instances.
 * Implements Factory Pattern for payment method selection.
 */
public class PaymentStrategyFactory {
    
    private final Map<PaymentMethod, PaymentStrategy> strategies;
    
    public PaymentStrategyFactory() {
        strategies = new EnumMap<>(PaymentMethod.class);
        
        // Register default strategies
        CreditCardPaymentStrategy cardStrategy = new CreditCardPaymentStrategy();
        strategies.put(PaymentMethod.CREDIT_CARD, cardStrategy);
        strategies.put(PaymentMethod.DEBIT_CARD, cardStrategy);
        strategies.put(PaymentMethod.UPI, new UPIPaymentStrategy());
        strategies.put(PaymentMethod.WALLET, new WalletPaymentStrategy());
        strategies.put(PaymentMethod.CASH_ON_DELIVERY, new CashOnDeliveryStrategy());
    }
    
    public PaymentStrategy getStrategy(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for: " + method);
        }
        return strategy;
    }
    
    public Map<PaymentMethod, PaymentStrategy> getAllStrategies() {
        return new EnumMap<>(strategies);
    }
    
    public void registerStrategy(PaymentMethod method, PaymentStrategy strategy) {
        strategies.put(method, strategy);
    }
}



