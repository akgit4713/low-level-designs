package bookmyshow.strategies.refund;

import bookmyshow.models.Booking;
import bookmyshow.models.Show;
import java.math.BigDecimal;

/**
 * No refund strategy - bookings are non-refundable.
 */
public class NoRefundStrategy implements RefundStrategy {

    @Override
    public BigDecimal calculateRefundAmount(Booking booking, Show show) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean isRefundAllowed(Booking booking, Show show) {
        return false;
    }

    @Override
    public String getPolicyDescription() {
        return "Non-refundable booking";
    }
}



