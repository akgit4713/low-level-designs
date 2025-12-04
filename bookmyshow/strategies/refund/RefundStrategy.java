package bookmyshow.strategies.refund;

import bookmyshow.models.Booking;
import bookmyshow.models.Show;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating refund amounts.
 * Different policies can be implemented based on cancellation time.
 */
public interface RefundStrategy {
    
    /**
     * Calculate refund amount for a booking.
     * @param booking The booking being cancelled
     * @param show The show associated with the booking
     * @return Refund amount
     */
    BigDecimal calculateRefundAmount(Booking booking, Show show);
    
    /**
     * Check if refund is allowed for this booking.
     * @param booking The booking being cancelled
     * @param show The show associated with the booking
     * @return true if refund is allowed
     */
    boolean isRefundAllowed(Booking booking, Show show);
    
    /**
     * Get the refund policy description.
     * @return Policy description
     */
    String getPolicyDescription();
}



