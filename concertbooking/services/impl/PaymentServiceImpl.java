package concertbooking.services.impl;

import concertbooking.enums.PaymentMethod;
import concertbooking.exceptions.PaymentException;
import concertbooking.models.Payment;
import concertbooking.services.PaymentService;
import concertbooking.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PaymentService using Strategy pattern
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies = new ConcurrentHashMap<>();
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    
    @Override
    public Payment processPayment(String bookingId, String userId, BigDecimal amount, PaymentMethod method) {
        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            throw PaymentException.invalidMethod(method);
        }
        
        if (!strategy.canProcess(amount)) {
            throw PaymentException.failed("Amount not acceptable for " + method.getDisplayName());
        }
        
        // Create payment record
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = Payment.builder()
            .id(paymentId)
            .bookingId(bookingId)
            .userId(userId)
            .amount(amount)
            .method(method)
            .build();
        
        // Process payment
        boolean success = strategy.processPayment(payment);
        
        payments.put(payment.getId(), payment);
        
        if (!success) {
            throw PaymentException.failed(payment.getFailureReason());
        }
        
        return payment;
    }
    
    @Override
    public boolean processRefund(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw PaymentException.failed("Payment not found: " + paymentId);
        }
        
        if (!payment.isSuccessful()) {
            throw PaymentException.refundFailed("Cannot refund unsuccessful payment");
        }
        
        PaymentStrategy strategy = paymentStrategies.get(payment.getMethod());
        if (strategy == null) {
            throw PaymentException.refundFailed("Payment method no longer supported");
        }
        
        return strategy.processRefund(payment);
    }
    
    @Override
    public Optional<Payment> getPayment(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }
    
    @Override
    public void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy) {
        paymentStrategies.put(method, strategy);
        System.out.println("[PAYMENT] Registered payment strategy: " + method.getDisplayName());
    }
    
    @Override
    public PaymentMethod[] getAvailablePaymentMethods() {
        return paymentStrategies.keySet().toArray(new PaymentMethod[0]);
    }
    
    @Override
    public BigDecimal getProcessingFee(PaymentMethod method, BigDecimal amount) {
        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            return BigDecimal.ZERO;
        }
        return strategy.getProcessingFee(amount);
    }
}



