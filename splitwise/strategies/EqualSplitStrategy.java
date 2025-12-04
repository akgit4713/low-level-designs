package splitwise.strategies;

import splitwise.exceptions.InvalidSplitException;
import splitwise.models.Split;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for splitting expenses equally among all participants.
 */
public class EqualSplitStrategy implements SplitStrategy {
    
    private static final int SCALE = 2;
    
    @Override
    public List<Split> calculateSplits(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails) {
        
        validate(totalAmount, participantIds, splitDetails);
        
        List<Split> splits = new ArrayList<>();
        int participantCount = participantIds.size();
        
        // Calculate equal share
        BigDecimal equalShare = totalAmount.divide(
                BigDecimal.valueOf(participantCount),
                SCALE,
                RoundingMode.DOWN
        );
        
        // Calculate remainder to distribute
        BigDecimal totalDistributed = equalShare.multiply(BigDecimal.valueOf(participantCount));
        BigDecimal remainder = totalAmount.subtract(totalDistributed);
        BigDecimal pennies = remainder.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.DOWN);
        int penniesInt = pennies.intValue();
        
        // Distribute shares, adding extra penny to first few participants
        for (int i = 0; i < participantCount; i++) {
            BigDecimal share = equalShare;
            if (i < penniesInt) {
                share = share.add(new BigDecimal("0.01"));
            }
            splits.add(new Split(participantIds.get(i), share));
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
        
        return true;
    }
}



