package digitalwallet.services.impl;

import digitalwallet.enums.Currency;
import digitalwallet.exceptions.CurrencyConversionException;
import digitalwallet.models.ExchangeRate;
import digitalwallet.repositories.ExchangeRateRepository;
import digitalwallet.services.CurrencyService;
import digitalwallet.strategies.conversion.CurrencyConversionStrategy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of CurrencyService.
 */
public class CurrencyServiceImpl implements CurrencyService {
    
    private final ExchangeRateRepository exchangeRateRepository;
    private CurrencyConversionStrategy conversionStrategy;

    public CurrencyServiceImpl(ExchangeRateRepository exchangeRateRepository,
                                CurrencyConversionStrategy conversionStrategy) {
        this.exchangeRateRepository = Objects.requireNonNull(exchangeRateRepository);
        this.conversionStrategy = Objects.requireNonNull(conversionStrategy);
    }

    public void setConversionStrategy(CurrencyConversionStrategy strategy) {
        this.conversionStrategy = strategy;
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }
        return conversionStrategy.convert(amount, from, to);
    }

    @Override
    public ExchangeRate getExchangeRate(Currency from, Currency to) {
        return conversionStrategy.getRate(from, to);
    }

    @Override
    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    @Override
    public List<ExchangeRate> getRatesFrom(Currency from) {
        return exchangeRateRepository.findByFromCurrency(from);
    }

    @Override
    public boolean isConversionSupported(Currency from, Currency to) {
        return conversionStrategy.supportsConversion(from, to);
    }

    @Override
    public void updateExchangeRate(Currency from, Currency to, BigDecimal rate) {
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CurrencyConversionException("Exchange rate must be positive");
        }
        exchangeRateRepository.save(new ExchangeRate(from, to, rate));
    }
}



