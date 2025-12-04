package hotelmanagement.repositories;

import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.models.Bill;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Bill entity with domain-specific queries
 */
public interface BillRepository extends Repository<Bill, String> {
    
    /**
     * Find bill by reservation ID
     */
    Optional<Bill> findByReservationId(String reservationId);
    
    /**
     * Find bills by payment status
     */
    List<Bill> findByPaymentStatus(PaymentStatus status);
    
    /**
     * Find unpaid bills
     */
    List<Bill> findUnpaidBills();
    
    /**
     * Find bills generated on a specific date
     */
    List<Bill> findByGeneratedDate(LocalDate date);
    
    /**
     * Find bills generated in date range
     */
    List<Bill> findByDateRange(LocalDate start, LocalDate end);
}



