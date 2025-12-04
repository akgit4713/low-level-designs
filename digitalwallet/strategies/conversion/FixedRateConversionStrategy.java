package digitalwallet.strategies.conversion;

import digitalwallet.enums.Currency;
import digitalwallet.exceptions.CurrencyConversionException;
import digitalwallet.models.ExchangeRate;
import digitalwallet.repositories.ExchangeRateRepository;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Currency conversion strategy using fixed rates from repository.
 * Suitable for stable rate requirements or when real-time rates are not needed.
 */
public class FixedRateConversionStrategy implements CurrencyConversionStrategy {
    
    private final ExchangeRateRepository exchangeRateRepository;

    public FixedRateConversionStrategy(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = Objects.requireNonNull(exchangeRateRepository, 
            "Exchange rate repository cannot be null");
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(from, "Source currency cannot be null");
        Objects.requireNonNull(to, "Target currency cannot be null");
        
        if (from == to) {
            return amount;
        }
        
        ExchangeRate rate = getRate(from, to);
        return rate.convert(amount);
    }

    @Override
    public ExchangeRate getRate(Currency from, Currency to) {
        if (from == to) {
            return new ExchangeRate(from, to, BigDecimal.ONE);
        }
        
        return exchangeRateRepository.findRate(from, to)
            .orElseThrow(() -> new CurrencyConversionException(from, to, 
                "Exchange rate not available"));
    }

    @Override
    public boolean supportsConversion(Currency from, Currency to) {
        if (from == to) return true;
        return exchangeRateRepository.exists(from, to);
    }

    @Override
    public String getStrategyName() {
        return "Fixed Rate Conversion";
    }
}



