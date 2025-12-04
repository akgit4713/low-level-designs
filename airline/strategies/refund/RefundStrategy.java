package airline.strategies.refund;

import airline.models.Booking;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating refund amounts.
 */
public interface RefundStrategy {
    
    /**
     * Calculates the refund amount for a cancelled booking.
     * 
     * @param booking The cancelled booking
     * @return The refund amount
     */
    BigDecimal calculateRefund(Booking booking);
    
    /**
     * Gets the description of this refund policy.
     */
    String getDescription();
}



