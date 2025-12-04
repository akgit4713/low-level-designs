package librarymanagement.strategies.fine;

import librarymanagement.models.BorrowRecord;
import java.math.BigDecimal;

/**
 * A no-fine strategy for special cases (e.g., grace periods, special members).
 */
public class NoFineStrategy implements FineCalculationStrategy {
    
    @Override
    public BigDecimal calculateFine(BorrowRecord record) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getDescription() {
        return "No fines applied";
    }
}



