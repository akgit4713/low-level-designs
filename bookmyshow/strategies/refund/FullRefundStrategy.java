package bookmyshow.strategies.refund;

import bookmyshow.models.Booking;
import bookmyshow.models.Show;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Full refund strategy - full refund if cancelled before show time.
 */
public class FullRefundStrategy implements RefundStrategy {
    
    private final long minHoursBeforeShow;

    public FullRefundStrategy(long minHoursBeforeShow) {
        this.minHoursBeforeShow = minHoursBeforeShow;
    }

    @Override
    public BigDecimal calculateRefundAmount(Booking booking, Show show) {
        if (!isRefundAllowed(booking, show)) {
            return BigDecimal.ZERO;
        }
        return booking.getTotalAmount();
    }

    @Override
    public boolean isRefundAllowed(Booking booking, Show show) {
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilShow = ChronoUnit.HOURS.between(now, show.getStartTime());
        return hoursUntilShow >= minHoursBeforeShow;
    }

    @Override
    public String getPolicyDescription() {
        return String.format("Full refund if cancelled at least %d hours before show", minHoursBeforeShow);
    }
}



