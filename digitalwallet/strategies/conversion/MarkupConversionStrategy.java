package digitalwallet.strategies.conversion;

import digitalwallet.enums.Currency;
import digitalwallet.models.ExchangeRate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Currency conversion strategy that applies a markup/spread to base rates.
 * Wraps another conversion strategy and adds a markup percentage.
 */
public class MarkupConversionStrategy implements CurrencyConversionStrategy {
    
    private final CurrencyConversionStrategy baseStrategy;
    private final BigDecimal markupPercentage;

    /**
     * Create with a percentage markup
     * @param baseStrategy The underlying conversion strategy
     * @param markupPercentage Markup as percentage (e.g., 2.5 for 2.5%)
     */
    public MarkupConversionStrategy(CurrencyConversionStrategy baseStrategy, BigDecimal markupPercentage) {
        this.baseStrategy = Objects.requireNonNull(baseStrategy, "Base strategy cannot be null");
        this.markupPercentage = Objects.requireNonNull(markupPercentage, "Markup percentage cannot be null");
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }
        
        // Get base conversion
        BigDecimal converted = baseStrategy.convert(amount, from, to);
        
        // Apply markup (reduce the converted amount)
        BigDecimal markupFactor = BigDecimal.ONE.subtract(
            markupPercentage.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        
        return converted.multiply(markupFactor)
            .setScale(to.getDecimalPlaces(), RoundingMode.HALF_UP);
    }

    @Override
    public ExchangeRate getRate(Currency from, Currency to) {
        if (from == to) {
            return new ExchangeRate(from, to, BigDecimal.ONE);
        }
        
        ExchangeRate baseRate = baseStrategy.getRate(from, to);
        
        // Apply markup to rate
        BigDecimal markupFactor = BigDecimal.ONE.subtract(
            markupPercentage.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        BigDecimal adjustedRate = baseRate.getRate().multiply(markupFactor);
        
        return new ExchangeRate(from, to, adjustedRate);
    }

    @Override
    public boolean supportsConversion(Currency from, Currency to) {
        return baseStrategy.supportsConversion(from, to);
    }

    @Override
    public String getStrategyName() {
        return String.format("Markup Conversion (%.2f%% spread)", markupPercentage);
    }

    public BigDecimal getMarkupPercentage() {
        return markupPercentage;
    }
}



