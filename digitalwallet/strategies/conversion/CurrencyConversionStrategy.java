package digitalwallet.strategies.conversion;

import digitalwallet.enums.Currency;
import digitalwallet.models.ExchangeRate;
import java.math.BigDecimal;

/**
 * Strategy interface for currency conversion.
 * Allows different conversion providers to be plugged in.
 */
public interface CurrencyConversionStrategy {
    
    /**
     * Convert an amount from one currency to another
     * @param amount The amount to convert
     * @param from Source currency
     * @param to Target currency
     * @return Converted amount in target currency
     */
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
    
    /**
     * Get the exchange rate for a currency pair
     * @param from Source currency
     * @param to Target currency
     * @return ExchangeRate object
     */
    ExchangeRate getRate(Currency from, Currency to);
    
    /**
     * Check if conversion is supported for a currency pair
     */
    boolean supportsConversion(Currency from, Currency to);
    
    /**
     * Get the name of this conversion strategy
     */
    String getStrategyName();
}



