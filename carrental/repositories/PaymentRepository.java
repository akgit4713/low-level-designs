package carrental.repositories;

import carrental.enums.PaymentStatus;
import carrental.models.Payment;

import java.util.List;

/**
 * Repository interface for Payment entities.
 */
public interface PaymentRepository extends Repository<Payment, String> {
    
    /**
     * Finds all payments for a specific reservation.
     */
    List<Payment> findByReservationId(String reservationId);
    
    /**
     * Finds all payments with a specific status.
     */
    List<Payment> findByStatus(PaymentStatus status);
    
    /**
     * Finds a payment by its transaction reference.
     */
    Payment findByTransactionReference(String transactionReference);
}



