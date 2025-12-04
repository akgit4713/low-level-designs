package onlineshopping.services.impl;

import onlineshopping.enums.PaymentMethod;
import onlineshopping.exceptions.PaymentException;
import onlineshopping.factories.PaymentStrategyFactory;
import onlineshopping.models.Order;
import onlineshopping.models.Payment;
import onlineshopping.services.PaymentService;
import onlineshopping.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PaymentService
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, String> orderPaymentIndex = new ConcurrentHashMap<>(); // orderId -> paymentId

    @Override
    public Payment processPayment(Order order, PaymentMethod method) {
        // Validate payment method
        if (!validatePaymentMethod(order, method)) {
            throw PaymentException.failed("Payment method validation failed");
        }
        
        // Create payment record
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = new Payment(paymentId, order.getId(), order.getUserId(), 
                                       order.getTotalAmount(), method);
        
        // Get appropriate strategy
        PaymentStrategy strategy = PaymentStrategyFactory.getStrategy(method);
        
        // Process payment
        boolean success = strategy.processPayment(order, payment);
        
        // Save payment
        payments.put(paymentId, payment);
        orderPaymentIndex.put(order.getId(), paymentId);
        
        if (!success && method.isPrepaid()) {
            System.out.printf("[PAYMENT] Payment failed for order %s: %s%n", 
                order.getId(), payment.getFailureReason().orElse("Unknown error"));
        }
        
        return payment;
    }

    @Override
    public Optional<Payment> getPayment(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public Optional<Payment> getPaymentByOrder(String orderId) {
        String paymentId = orderPaymentIndex.get(orderId);
        if (paymentId != null) {
            return Optional.ofNullable(payments.get(paymentId));
        }
        return Optional.empty();
    }

    @Override
    public boolean processRefund(String paymentId, BigDecimal amount) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw PaymentException.failed("Payment not found: " + paymentId);
        }
        
        if (!payment.isSuccessful()) {
            throw PaymentException.refundFailed(paymentId, "Payment was not successful");
        }
        
        if (amount.compareTo(payment.getNetAmount()) > 0) {
            throw PaymentException.refundFailed(paymentId, "Refund amount exceeds payment amount");
        }
        
        PaymentStrategy strategy = PaymentStrategyFactory.getStrategy(payment.getMethod());
        
        // Create a temporary payment for refund processing
        Payment refundPayment = new Payment(
            "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            payment.getOrderId(),
            payment.getUserId(),
            amount,
            payment.getMethod()
        );
        refundPayment.complete(payment.getTransactionId().orElse(""));
        
        boolean success = strategy.processRefund(refundPayment);
        
        if (success) {
            payment.refund(amount);
            System.out.printf("[REFUND] Refunded $%s for payment %s%n", amount, paymentId);
        }
        
        return success;
    }

    @Override
    public boolean processFullRefund(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw PaymentException.failed("Payment not found: " + paymentId);
        }
        return processRefund(paymentId, payment.getNetAmount());
    }

    @Override
    public boolean validatePaymentMethod(Order order, PaymentMethod method) {
        if (!PaymentStrategyFactory.isSupported(method)) {
            return false;
        }
        
        PaymentStrategy strategy = PaymentStrategyFactory.getStrategy(method);
        return strategy.validate(order);
    }
}



