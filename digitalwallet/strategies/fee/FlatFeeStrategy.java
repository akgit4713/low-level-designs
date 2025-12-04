package digitalwallet.strategies.fee;

import digitalwallet.models.Transfer;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Fee strategy that charges a flat fee regardless of transfer amount.
 */
public class FlatFeeStrategy implements FeeCalculationStrategy {
    
    private final BigDecimal flatFee;
    private final BigDecimal externalTransferFee;
    private final BigDecimal crossCurrencyFee;

    public FlatFeeStrategy(BigDecimal flatFee) {
        this(flatFee, flatFee.multiply(new BigDecimal("2")), flatFee);
    }

    public FlatFeeStrategy(BigDecimal flatFee, BigDecimal externalTransferFee, BigDecimal crossCurrencyFee) {
        this.flatFee = Objects.requireNonNull(flatFee, "Flat fee cannot be null");
        this.externalTransferFee = Objects.requireNonNull(externalTransferFee);
        this.crossCurrencyFee = Objects.requireNonNull(crossCurrencyFee);
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
        BigDecimal totalFee = flatFee;
        
        if (isExternalTransfer) {
            totalFee = totalFee.add(externalTransferFee);
        }
        
        if (isCrossCurrency) {
            totalFee = totalFee.add(crossCurrencyFee);
        }
        
        return totalFee;
    }

    @Override
    public String getFeeDescription() {
        return String.format("Flat fee: $%.2f + $%.2f (external) + $%.2f (currency conversion)",
            flatFee, externalTransferFee, crossCurrencyFee);
    }

    @Override
    public String getStrategyName() {
        return "Flat Fee";
    }
}



