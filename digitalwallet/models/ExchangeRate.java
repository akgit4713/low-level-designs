package digitalwallet.models;

import digitalwallet.enums.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an exchange rate between two currencies.
 * Immutable for thread-safety.
 */
public class ExchangeRate {
    private final Currency fromCurrency;
    private final Currency toCurrency;
    private final BigDecimal rate;
    private final LocalDateTime timestamp;
    private final String source;

    public ExchangeRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate) {
        this(fromCurrency, toCurrency, rate, LocalDateTime.now(), "SYSTEM");
    }

    public ExchangeRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate, 
                        LocalDateTime timestamp, String source) {
        this.fromCurrency = Objects.requireNonNull(fromCurrency, "From currency cannot be null");
        this.toCurrency = Objects.requireNonNull(toCurrency, "To currency cannot be null");
        this.rate = Objects.requireNonNull(rate, "Rate cannot be null");
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.source = source != null ? source : "SYSTEM";
        
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
    }

    public Currency getFromCurrency() { return fromCurrency; }
    public Currency getToCurrency() { return toCurrency; }
    public BigDecimal getRate() { return rate; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSource() { return source; }

    /**
     * Get the currency pair string (e.g., "USD/EUR")
     */
    public String getCurrencyPair() {
        return fromCurrency.name() + "/" + toCurrency.name();
    }

    /**
     * Convert an amount using this exchange rate
     */
    public BigDecimal convert(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        int targetScale = toCurrency.getDecimalPlaces();
        return amount.multiply(rate).setScale(targetScale, RoundingMode.HALF_UP);
    }

    /**
     * Get the inverse exchange rate
     */
    public ExchangeRate getInverse() {
        BigDecimal inverseRate = BigDecimal.ONE.divide(rate, 10, RoundingMode.HALF_UP);
        return new ExchangeRate(toCurrency, fromCurrency, inverseRate, timestamp, source);
    }

    /**
     * Check if this rate is stale (older than specified minutes)
     */
    public boolean isStale(int maxAgeMinutes) {
        return timestamp.plusMinutes(maxAgeMinutes).isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return fromCurrency == that.fromCurrency && 
               toCurrency == that.toCurrency && 
               Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, toCurrency, rate);
    }

    @Override
    public String toString() {
        return String.format("ExchangeRate{%s/%s = %s, timestamp=%s}",
            fromCurrency.name(), toCurrency.name(), rate, timestamp);
    }
}



