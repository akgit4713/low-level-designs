package bookmyshow.services.impl;

import bookmyshow.enums.PaymentMethod;
import bookmyshow.enums.PaymentStatus;
import bookmyshow.exceptions.EntityNotFoundException;
import bookmyshow.exceptions.InvalidOperationException;
import bookmyshow.exceptions.PaymentFailedException;
import bookmyshow.factories.PaymentStrategyFactory;
import bookmyshow.models.Payment;
import bookmyshow.repositories.PaymentRepository;
import bookmyshow.services.PaymentService;
import bookmyshow.strategies.payment.PaymentStrategy;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementation of PaymentService.
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment processPayment(String bookingId, BigDecimal amount, PaymentMethod method) {
        // Create payment record
        Payment payment = new Payment(bookingId, amount, method);
        payment.markProcessing();
        paymentRepository.save(payment);
        
        try {
            // Get appropriate payment strategy
            PaymentStrategy strategy = PaymentStrategyFactory.getStrategy(method);
            
            // Process payment
            boolean success = strategy.processPayment(payment);
            
            if (!success) {
                payment.markFailed("Payment processing failed");
            }
            
            paymentRepository.save(payment);
            return payment;
            
        } catch (Exception e) {
            payment.markFailed(e.getMessage());
            paymentRepository.save(payment);
            throw new PaymentFailedException("Payment failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Payment processRefund(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new EntityNotFoundException("Payment", paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidOperationException(
                "Cannot refund payment in status: " + payment.getStatus());
        }
        
        try {
            PaymentStrategy strategy = PaymentStrategyFactory.getStrategy(payment.getMethod());
            boolean success = strategy.processRefund(payment);
            
            if (!success) {
                throw new PaymentFailedException("Refund processing failed");
            }
            
            paymentRepository.save(payment);
            return payment;
            
        } catch (Exception e) {
            throw new PaymentFailedException("Refund failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Payment> getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Optional<Payment> getPaymentByBooking(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}



