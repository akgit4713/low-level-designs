package airline.strategies.refund;

import airline.models.Booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Time-based refund policy:
 * - More than 7 days before: 100% refund
 * - 3-7 days before: 75% refund
 * - 1-3 days before: 50% refund
 * - Less than 24 hours: 25% refund
 */
public class TimeBasedRefundStrategy implements RefundStrategy {

    @Override
    public BigDecimal calculateRefund(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = booking.getFlight().getDepartureTime();
        
        long hoursUntilDeparture = ChronoUnit.HOURS.between(now, departure);
        
        double refundPercentage;
        if (hoursUntilDeparture > 168) { // > 7 days
            refundPercentage = 1.0;
        } else if (hoursUntilDeparture > 72) { // 3-7 days
            refundPercentage = 0.75;
        } else if (hoursUntilDeparture > 24) { // 1-3 days
            refundPercentage = 0.50;
        } else if (hoursUntilDeparture > 0) { // < 24 hours
            refundPercentage = 0.25;
        } else { // Past departure
            refundPercentage = 0.0;
        }
        
        return booking.getTotalAmount()
                .multiply(BigDecimal.valueOf(refundPercentage))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return "Time-based refund (100%/>7d, 75%/3-7d, 50%/1-3d, 25%/<24h)";
    }
}



