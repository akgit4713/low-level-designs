package airline.strategies.refund;

import airline.models.Booking;

import java.math.BigDecimal;

/**
 * Full refund policy - returns 100% of the booking amount.
 */
public class FullRefundStrategy implements RefundStrategy {

    @Override
    public BigDecimal calculateRefund(Booking booking) {
        return booking.getTotalAmount();
    }

    @Override
    public String getDescription() {
        return "Full refund (100%)";
    }
}



