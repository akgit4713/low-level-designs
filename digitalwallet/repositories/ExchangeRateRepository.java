package digitalwallet.repositories;

import digitalwallet.enums.Currency;
import digitalwallet.models.ExchangeRate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExchangeRate entity.
 */
public interface ExchangeRateRepository {
    
    /**
     * Get exchange rate for a currency pair
     */
    Optional<ExchangeRate> findRate(Currency from, Currency to);
    
    /**
     * Save or update an exchange rate
     */
    ExchangeRate save(ExchangeRate rate);
    
    /**
     * Get all exchange rates for a source currency
     */
    List<ExchangeRate> findByFromCurrency(Currency from);
    
    /**
     * Get all exchange rates
     */
    List<ExchangeRate> findAll();
    
    /**
     * Delete exchange rate for a currency pair
     */
    boolean delete(Currency from, Currency to);
    
    /**
     * Check if rate exists for a currency pair
     */
    boolean exists(Currency from, Currency to);
}



