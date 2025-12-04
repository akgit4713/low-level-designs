package hotelmanagement.services;

import hotelmanagement.models.Bill;
import hotelmanagement.models.Reservation;

/**
 * Service interface for check-in and check-out operations
 */
public interface CheckInOutService {
    
    /**
     * Process guest check-in
     * @param reservationId The reservation ID
     * @return The updated reservation
     */
    Reservation checkIn(String reservationId);
    
    /**
     * Process guest check-out and generate bill
     * @param reservationId The reservation ID
     * @return The generated bill
     */
    Bill checkOut(String reservationId);
    
    /**
     * Check if reservation is eligible for check-in
     * @param reservationId The reservation ID
     * @return true if check-in is allowed
     */
    boolean canCheckIn(String reservationId);
    
    /**
     * Check if reservation is eligible for check-out
     * @param reservationId The reservation ID
     * @return true if check-out is allowed
     */
    boolean canCheckOut(String reservationId);
    
    /**
     * Perform early check-in (before standard check-in time)
     * @param reservationId The reservation ID
     * @param earlyCheckInFee Optional early check-in fee
     * @return The updated reservation
     */
    Reservation earlyCheckIn(String reservationId, java.math.BigDecimal earlyCheckInFee);
    
    /**
     * Perform late check-out (after standard check-out time)
     * @param reservationId The reservation ID
     * @param lateCheckOutFee Optional late check-out fee
     * @return The generated bill
     */
    Bill lateCheckOut(String reservationId, java.math.BigDecimal lateCheckOutFee);
}



