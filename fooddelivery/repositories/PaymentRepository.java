package fooddelivery.repositories;

import fooddelivery.enums.PaymentStatus;
import fooddelivery.models.Payment;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entities.
 */
public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findAll();
    void delete(String id);
    boolean existsById(String id);
}



