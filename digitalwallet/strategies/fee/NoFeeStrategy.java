package digitalwallet.strategies.fee;

import digitalwallet.models.Transfer;
import java.math.BigDecimal;

/**
 * Fee strategy that charges no fees.
 * Useful for premium users or promotional periods.
 */
public class NoFeeStrategy implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculateFee(Transfer transfer) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal amount, boolean isExternalTransfer, boolean isCrossCurrency) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getFeeDescription() {
        return "No fees";
    }

    @Override
    public String getStrategyName() {
        return "No Fee";
    }
}



