package digitalwallet.strategies.fee;

import digitalwallet.models.Transfer;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating transaction fees.
 * Allows different fee structures to be plugged in.
 */
public interface FeeCalculationStrategy {
    
    /**
     * Calculate the fee for a transfer
     * @param transfer The transfer to calculate fee for
     * @return Fee amount in the transfer's source currency
     */
    BigDecimal calculateFee(Transfer transfer);
    
    /**
     * Calculate fee for a given amount and currency
     * @param amount Transfer amount
     * @param isExternalTransfer Whether transfer is external
     * @param isCrossCurrency Whether transfer involves currency conversion
     * @return Fee amount
     */
    BigDecimal calculateFee(BigDecimal amount, boolean isExternalTransfer, boolean isCrossCurrency);
    
    /**
     * Get the fee description for display
     */
    String getFeeDescription();
    
    /**
     * Get the name of this fee strategy
     */
    String getStrategyName();
}



