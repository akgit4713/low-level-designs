package restaurant.services.impl;

import restaurant.enums.PaymentMethod;
import restaurant.exceptions.PaymentException;
import restaurant.models.Bill;
import restaurant.models.Payment;
import restaurant.services.PaymentService;
import restaurant.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PaymentService using Strategy pattern
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies = new ConcurrentHashMap<>();
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount, PaymentMethod method) {
        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            throw PaymentException.invalidPaymentMethod(method.name());
        }
        
        if (!strategy.canProcess(amount)) {
            throw PaymentException.paymentFailed("Invalid amount for " + method.getDisplayName());
        }
        
        if (amount.compareTo(bill.getTotalAmount()) < 0) {
            throw PaymentException.insufficientAmount(bill.getTotalAmount().doubleValue(), amount.doubleValue());
        }
        
        Payment payment = strategy.processPayment(bill, amount);
        
        if (payment.isSuccessful()) {
            bill.markPaid();
        }
        
        payments.put(payment.getId(), payment);
        return payment;
    }
    
    @Override
    public Optional<Payment> getPayment(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }
    
    @Override
    public boolean processRefund(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            return false;
        }
        
        PaymentStrategy strategy = paymentStrategies.get(payment.getMethod());
        if (strategy == null) {
            return false;
        }
        
        return strategy.processRefund(payment);
    }
    
    @Override
    public void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy) {
        paymentStrategies.put(method, strategy);
    }
    
    @Override
    public PaymentMethod[] getAvailablePaymentMethods() {
        return paymentStrategies.keySet().toArray(new PaymentMethod[0]);
    }
}

