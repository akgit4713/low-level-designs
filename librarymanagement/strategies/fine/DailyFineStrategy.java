package librarymanagement.strategies.fine;

import librarymanagement.models.BorrowRecord;
import java.math.BigDecimal;

/**
 * Calculates fine based on a daily rate for overdue books.
 */
public class DailyFineStrategy implements FineCalculationStrategy {
    
    private final BigDecimal dailyRate;
    private final BigDecimal maxFine;

    public DailyFineStrategy(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
        this.maxFine = BigDecimal.valueOf(100); // Default max fine
    }

    public DailyFineStrategy(BigDecimal dailyRate, BigDecimal maxFine) {
        this.dailyRate = dailyRate;
        this.maxFine = maxFine;
    }

    @Override
    public BigDecimal calculateFine(BorrowRecord record) {
        long overdueDays = record.getOverdueDays();
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal fine = dailyRate.multiply(BigDecimal.valueOf(overdueDays));
        return fine.min(maxFine);
    }

    @Override
    public String getDescription() {
        return String.format("Daily fine of $%s per day, maximum $%s", dailyRate, maxFine);
    }
}



