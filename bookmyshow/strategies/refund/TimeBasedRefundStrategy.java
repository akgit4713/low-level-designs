package bookmyshow.strategies.refund;

import bookmyshow.models.Booking;
import bookmyshow.models.Show;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Time-based refund strategy with sliding scale based on cancellation time.
 * - 24+ hours before: 100% refund
 * - 12-24 hours before: 75% refund
 * - 2-12 hours before: 50% refund
 * - Less than 2 hours: No refund
 */
public class TimeBasedRefundStrategy implements RefundStrategy {

    @Override
    public BigDecimal calculateRefundAmount(Booking booking, Show show) {
        BigDecimal refundPercentage = getRefundPercentage(show);
        return booking.getTotalAmount().multiply(refundPercentage);
    }

    @Override
    public boolean isRefundAllowed(Booking booking, Show show) {
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilShow = ChronoUnit.HOURS.between(now, show.getStartTime());
        return hoursUntilShow >= 2;  // No refund within 2 hours of show
    }

    private BigDecimal getRefundPercentage(Show show) {
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilShow = ChronoUnit.HOURS.between(now, show.getStartTime());

        if (hoursUntilShow >= 24) {
            return BigDecimal.ONE;  // 100%
        } else if (hoursUntilShow >= 12) {
            return BigDecimal.valueOf(0.75);  // 75%
        } else if (hoursUntilShow >= 2) {
            return BigDecimal.valueOf(0.50);  // 50%
        } else {
            return BigDecimal.ZERO;  // 0%
        }
    }

    @Override
    public String getPolicyDescription() {
        return "Time-based refund: 100% (24h+), 75% (12-24h), 50% (2-12h), 0% (<2h)";
    }
}



