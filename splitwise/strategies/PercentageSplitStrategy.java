package splitwise.strategies;

import splitwise.exceptions.InvalidSplitException;
import splitwise.models.Split;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for splitting expenses by percentage.
 * Percentages must sum to 100%.
 */
public class PercentageSplitStrategy implements SplitStrategy {
    
    private static final int SCALE = 2;
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");
    
    @Override
    public List<Split> calculateSplits(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails) {
        
        validate(totalAmount, participantIds, splitDetails);
        
        List<Split> splits = new ArrayList<>();
        BigDecimal totalCalculated = BigDecimal.ZERO;
        
        // Calculate each participant's share based on percentage
        for (int i = 0; i < participantIds.size(); i++) {
            String participantId = participantIds.get(i);
            BigDecimal percentage = splitDetails.get(participantId);
            
            BigDecimal share;
            if (i == participantIds.size() - 1) {
                // Last participant gets the remainder to avoid rounding errors
                share = totalAmount.subtract(totalCalculated);
            } else {
                share = totalAmount.multiply(percentage)
                        .divide(HUNDRED, SCALE, RoundingMode.DOWN);
                totalCalculated = totalCalculated.add(share);
            }
            
            splits.add(new Split(participantId, share, percentage));
        }
        
        return splits;
    }
    
    @Override
    public boolean validate(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails) {
        
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSplitException("Total amount must be positive");
        }
        
        if (participantIds == null || participantIds.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required");
        }
        
        if (splitDetails == null || splitDetails.isEmpty()) {
            throw new InvalidSplitException("Percentage details are required");
        }
        
        // Verify all participants have percentages
        for (String participantId : participantIds) {
            if (!splitDetails.containsKey(participantId)) {
                throw new InvalidSplitException(
                        "Percentage not specified for participant: " + participantId
                );
            }
            BigDecimal percentage = splitDetails.get(participantId);
            if (percentage == null || percentage.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidSplitException(
                        "Percentage must be non-negative for participant: " + participantId
                );
            }
        }
        
        // Verify percentages sum to 100
        BigDecimal totalPercentage = BigDecimal.ZERO;
        for (String participantId : participantIds) {
            totalPercentage = totalPercentage.add(splitDetails.get(participantId));
        }
        
        if (totalPercentage.subtract(HUNDRED).abs().compareTo(TOLERANCE) > 0) {
            throw new InvalidSplitException(
                    "Percentages must sum to 100. Current sum: " + totalPercentage
            );
        }
        
        return true;
    }
}



