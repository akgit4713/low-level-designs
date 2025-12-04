package librarymanagement.strategies.fine;

import librarymanagement.models.BorrowRecord;
import java.math.BigDecimal;

/**
 * Calculates fine using tiered rates based on overdue duration.
 * Encourages quick return by increasing rate over time.
 */
public class TieredFineStrategy implements FineCalculationStrategy {
    
    private final BigDecimal tier1Rate;  // Days 1-7
    private final BigDecimal tier2Rate;  // Days 8-14
    private final BigDecimal tier3Rate;  // Days 15+
    private final BigDecimal maxFine;

    public TieredFineStrategy() {
        this.tier1Rate = BigDecimal.valueOf(0.50);
        this.tier2Rate = BigDecimal.valueOf(1.00);
        this.tier3Rate = BigDecimal.valueOf(2.00);
        this.maxFine = BigDecimal.valueOf(50);
    }

    public TieredFineStrategy(BigDecimal tier1Rate, BigDecimal tier2Rate, 
                               BigDecimal tier3Rate, BigDecimal maxFine) {
        this.tier1Rate = tier1Rate;
        this.tier2Rate = tier2Rate;
        this.tier3Rate = tier3Rate;
        this.maxFine = maxFine;
    }

    @Override
    public BigDecimal calculateFine(BorrowRecord record) {
        long overdueDays = record.getOverdueDays();
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal fine = BigDecimal.ZERO;
        
        // Tier 1: Days 1-7
        long tier1Days = Math.min(overdueDays, 7);
        fine = fine.add(tier1Rate.multiply(BigDecimal.valueOf(tier1Days)));
        
        // Tier 2: Days 8-14
        if (overdueDays > 7) {
            long tier2Days = Math.min(overdueDays - 7, 7);
            fine = fine.add(tier2Rate.multiply(BigDecimal.valueOf(tier2Days)));
        }
        
        // Tier 3: Days 15+
        if (overdueDays > 14) {
            long tier3Days = overdueDays - 14;
            fine = fine.add(tier3Rate.multiply(BigDecimal.valueOf(tier3Days)));
        }
        
        return fine.min(maxFine);
    }

    @Override
    public String getDescription() {
        return String.format("Tiered fine: $%s/day (days 1-7), $%s/day (days 8-14), $%s/day (15+), max $%s",
                tier1Rate, tier2Rate, tier3Rate, maxFine);
    }
}



