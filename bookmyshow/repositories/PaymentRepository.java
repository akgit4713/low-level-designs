package bookmyshow.repositories;

import bookmyshow.models.Payment;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations.
 */
public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(String id);
    Optional<Payment> findByBookingId(String bookingId);
    List<Payment> findAll();
    void delete(String id);
    boolean exists(String id);
}



