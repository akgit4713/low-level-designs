package fooddelivery.services.impl;

import fooddelivery.enums.PaymentMethod;
import fooddelivery.enums.PaymentStatus;
import fooddelivery.exceptions.PaymentException;
import fooddelivery.models.Order;
import fooddelivery.models.Payment;
import fooddelivery.repositories.PaymentRepository;
import fooddelivery.services.PaymentService;
import fooddelivery.strategies.payment.PaymentStrategy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PaymentService with strategy selection.
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies;
    
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               Map<PaymentMethod, PaymentStrategy> paymentStrategies) {
        this.paymentRepository = paymentRepository;
        this.paymentStrategies = paymentStrategies;
    }

    @Override
    public Payment initiatePayment(Order order, PaymentMethod method) {
        if (!paymentStrategies.containsKey(method)) {
            throw new PaymentException("Unsupported payment method: " + method);
        }
        
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
        Payment payment = new Payment(paymentId, order.getId(), order.getTotalAmount(), method);
        
        return paymentRepository.save(payment);
    }

    @Override
    public boolean processPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Payment already processed: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        
        PaymentStrategy strategy = paymentStrategies.get(payment.getMethod());
        boolean success = strategy.processPayment(payment);
        
        paymentRepository.save(payment);
        return success;
    }

    @Override
    public boolean refundPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException("Payment not found for order: " + orderId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false; // Nothing to refund
        }
        
        PaymentStrategy strategy = paymentStrategies.get(payment.getMethod());
        boolean success = strategy.refundPayment(payment);
        
        paymentRepository.save(payment);
        return success;
    }

    @Override
    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}



