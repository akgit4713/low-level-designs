package hotelmanagement.services.impl;

import hotelmanagement.enums.PaymentMethod;
import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.exceptions.PaymentException;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Payment;
import hotelmanagement.services.PaymentService;
import hotelmanagement.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PaymentService
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies = new ConcurrentHashMap<>();
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount, PaymentMethod method) {
        // Validate bill is not already paid
        if (bill.isPaid()) {
            throw PaymentException.billAlreadyPaid(bill.getId());
        }
        
        // Validate amount
        if (amount.compareTo(bill.getTotalAmount()) < 0) {
            throw PaymentException.insufficientAmount(
                bill.getId(),
                bill.getTotalAmount().toString(),
                amount.toString()
            );
        }
        
        // Get payment strategy
        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            throw PaymentException.unsupportedPaymentMethod(method);
        }
        
        // Validate payment can be processed
        if (!strategy.canProcess(amount)) {
            throw PaymentException.paymentFailed("Payment validation failed for amount: " + amount);
        }
        
        // Process payment
        Payment payment = strategy.processPayment(bill, amount);
        
        // Store payment
        payments.put(payment.getId(), payment);
        
        // Update bill status
        if (payment.isSuccessful()) {
            bill.setPaymentStatus(PaymentStatus.COMPLETED);
            bill.setPaymentId(payment.getId());
        }
        
        return payment;
    }
    
    @Override
    public Optional<Payment> getPayment(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }
    
    @Override
    public boolean processRefund(String paymentId, BigDecimal amount) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw PaymentException.refundFailed(paymentId, "Payment not found");
        }
        
        if (!payment.isSuccessful()) {
            throw PaymentException.refundFailed(paymentId, "Can only refund successful payments");
        }
        
        // Get the strategy for this payment's method
        PaymentStrategy strategy = paymentStrategies.get(payment.getMethod());
        if (strategy == null) {
            throw PaymentException.refundFailed(paymentId, "Payment method no longer supported");
        }
        
        // Process refund
        boolean success = strategy.processRefund(payment);
        if (success) {
            payment.refund(amount);
        }
        
        return success;
    }
    
    @Override
    public void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy) {
        paymentStrategies.put(method, strategy);
    }
    
    @Override
    public boolean isPaymentMethodSupported(PaymentMethod method) {
        return paymentStrategies.containsKey(method);
    }
}



