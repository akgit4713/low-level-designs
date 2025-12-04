package digitalwallet.strategies.fee;

import digitalwallet.models.Transfer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Fee strategy that uses tiered pricing based on transfer amount.
 * Lower fees for higher amounts.
 */
public class TieredFeeStrategy implements FeeCalculationStrategy {
    
    private final List<Tier> tiers;
    private final BigDecimal externalFeeMultiplier;
    private final BigDecimal crossCurrencyFeeMultiplier;

    public TieredFeeStrategy() {
        this.tiers = new ArrayList<>();
        this.externalFeeMultiplier = new BigDecimal("1.5");
        this.crossCurrencyFeeMultiplier = new BigDecimal("1.25");
        initializeDefaultTiers();
    }

    public TieredFeeStrategy(List<Tier> tiers, BigDecimal externalFeeMultiplier, 
                             BigDecimal crossCurrencyFeeMultiplier) {
        this.tiers = new ArrayList<>(tiers);
        this.tiers.sort(Comparator.comparing(Tier::getMinAmount));
        this.externalFeeMultiplier = externalFeeMultiplier;
        this.crossCurrencyFeeMultiplier = crossCurrencyFeeMultiplier;
    }

    private void initializeDefaultTiers() {
        // Tier structure: amount threshold, percentage fee
        tiers.add(new Tier(BigDecimal.ZERO, new BigDecimal("100"), new BigDecimal("2.5")));
        tiers.add(new Tier(new BigDecimal("100"), new BigDecimal("500"), new BigDecimal("2.0")));
        tiers.add(new Tier(new BigDecimal("500"), new BigDecimal("1000"), new BigDecimal("1.5")));
        tiers.add(new Tier(new BigDecimal("1000"), new BigDecimal("5000"), new BigDecimal("1.0")));
        tiers.add(new Tier(new BigDecimal("5000"), null, new BigDecimal("0.5")));
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
        Tier applicableTier = findApplicableTier(amount);
        
        BigDecimal fee = amount.multiply(applicableTier.getPercentage())
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        if (isExternalTransfer) {
            fee = fee.multiply(externalFeeMultiplier).setScale(2, RoundingMode.HALF_UP);
        }
        
        if (isCrossCurrency) {
            fee = fee.multiply(crossCurrencyFeeMultiplier).setScale(2, RoundingMode.HALF_UP);
        }
        
        return fee;
    }

    private Tier findApplicableTier(BigDecimal amount) {
        for (int i = tiers.size() - 1; i >= 0; i--) {
            Tier tier = tiers.get(i);
            if (amount.compareTo(tier.getMinAmount()) >= 0) {
                return tier;
            }
        }
        return tiers.get(0);
    }

    @Override
    public String getFeeDescription() {
        StringBuilder sb = new StringBuilder("Tiered pricing:\n");
        for (Tier tier : tiers) {
            if (tier.getMaxAmount() != null) {
                sb.append(String.format("  $%.0f - $%.0f: %.2f%%\n", 
                    tier.getMinAmount(), tier.getMaxAmount(), tier.getPercentage()));
            } else {
                sb.append(String.format("  $%.0f+: %.2f%%\n", 
                    tier.getMinAmount(), tier.getPercentage()));
            }
        }
        return sb.toString();
    }

    @Override
    public String getStrategyName() {
        return "Tiered Fee";
    }

    /**
     * Represents a fee tier
     */
    public static class Tier {
        private final BigDecimal minAmount;
        private final BigDecimal maxAmount; // null for unlimited
        private final BigDecimal percentage;

        public Tier(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal percentage) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.percentage = percentage;
        }

        public BigDecimal getMinAmount() { return minAmount; }
        public BigDecimal getMaxAmount() { return maxAmount; }
        public BigDecimal getPercentage() { return percentage; }
    }
}



