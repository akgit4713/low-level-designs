package ridesharing.repositories;

import ridesharing.enums.PaymentStatus;
import ridesharing.models.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity.
 */
public interface PaymentRepository {
    
    Payment save(Payment payment);
    
    Optional<Payment> findById(String paymentId);
    
    Optional<Payment> findByRideId(String rideId);
    
    List<Payment> findByPassengerId(String passengerId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findAll();
}



