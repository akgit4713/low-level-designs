package fooddelivery.services;

import fooddelivery.enums.PaymentMethod;
import fooddelivery.models.Order;
import fooddelivery.models.Payment;
import java.util.Optional;

/**
 * Service interface for payment operations.
 */
public interface PaymentService {
    Payment initiatePayment(Order order, PaymentMethod method);
    boolean processPayment(String paymentId);
    boolean refundPayment(String orderId);
    Optional<Payment> getPaymentByOrderId(String orderId);
}



