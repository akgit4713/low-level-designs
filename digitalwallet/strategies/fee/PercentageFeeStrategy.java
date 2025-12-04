package digitalwallet.strategies.fee;

import digitalwallet.models.Transfer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Fee strategy that charges a percentage of the transfer amount.
 */
public class PercentageFeeStrategy implements FeeCalculationStrategy {
    
    private final BigDecimal percentageFee;
    private final BigDecimal externalFeeAdditional;
    private final BigDecimal crossCurrencyFeeAdditional;
    private final BigDecimal minimumFee;
    private final BigDecimal maximumFee;

    public PercentageFeeStrategy(BigDecimal percentageFee) {
        this(percentageFee, new BigDecimal("0.5"), new BigDecimal("0.5"), 
             new BigDecimal("0.50"), new BigDecimal("50.00"));
    }

    public PercentageFeeStrategy(BigDecimal percentageFee, BigDecimal externalFeeAdditional,
                                  BigDecimal crossCurrencyFeeAdditional,
                                  BigDecimal minimumFee, BigDecimal maximumFee) {
        this.percentageFee = Objects.requireNonNull(percentageFee);
        this.externalFeeAdditional = Objects.requireNonNull(externalFeeAdditional);
        this.crossCurrencyFeeAdditional = Objects.requireNonNull(crossCurrencyFeeAdditional);
        this.minimumFee = Objects.requireNonNull(minimumFee);
        this.maximumFee = Objects.requireNonNull(maximumFee);
    }

    @Override
    public BigDecimal calculateFee(Transfer transfer) {
        return calculateFee(
            transfer.getAmount(),
            transfer.isExternalTransfer(),
            transfer.isCrossCurrency()
        );
    }

    @Override
    public BigDecimal calculateFee(BigDecimal amount, boolean isExternalTransfer, boolean isCrossCurrency) {
        BigDecimal effectivePercentage = percentageFee;
        
        if (isExternalTransfer) {
            effectivePercentage = effectivePercentage.add(externalFeeAdditional);
        }
        
        if (isCrossCurrency) {
            effectivePercentage = effectivePercentage.add(crossCurrencyFeeAdditional);
        }
        
        BigDecimal fee = amount.multiply(effectivePercentage)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Apply min/max bounds
        if (fee.compareTo(minimumFee) < 0) {
            fee = minimumFee;
        } else if (fee.compareTo(maximumFee) > 0) {
            fee = maximumFee;
        }
        
        return fee;
    }

    @Override
    public String getFeeDescription() {
        return String.format("%.2f%% (min $%.2f, max $%.2f) + %.2f%% (external) + %.2f%% (currency)",
            percentageFee, minimumFee, maximumFee, externalFeeAdditional, crossCurrencyFeeAdditional);
    }

    @Override
    public String getStrategyName() {
        return "Percentage Fee";
    }
}



