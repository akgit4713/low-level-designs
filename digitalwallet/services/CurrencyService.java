package digitalwallet.services;

import digitalwallet.enums.Currency;
import digitalwallet.models.ExchangeRate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for currency operations.
 */
public interface CurrencyService {
    
    /**
     * Convert amount from one currency to another
     */
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
    
    /**
     * Get exchange rate for a currency pair
     */
    ExchangeRate getExchangeRate(Currency from, Currency to);
    
    /**
     * Get all available exchange rates
     */
    List<ExchangeRate> getAllExchangeRates();
    
    /**
     * Get all rates from a specific currency
     */
    List<ExchangeRate> getRatesFrom(Currency from);
    
    /**
     * Check if conversion is supported
     */
    boolean isConversionSupported(Currency from, Currency to);
    
    /**
     * Update an exchange rate
     */
    void updateExchangeRate(Currency from, Currency to, BigDecimal rate);
}



