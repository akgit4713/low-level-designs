package airline.strategies.refund;

import airline.models.Booking;

import java.math.BigDecimal;

/**
 * No refund policy - returns 0.
 */
public class NoRefundStrategy implements RefundStrategy {

    @Override
    public BigDecimal calculateRefund(Booking booking) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getDescription() {
        return "Non-refundable";
    }
}



