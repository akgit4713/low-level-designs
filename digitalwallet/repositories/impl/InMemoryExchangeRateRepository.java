package digitalwallet.repositories.impl;

import digitalwallet.enums.Currency;
import digitalwallet.models.ExchangeRate;
import digitalwallet.repositories.ExchangeRateRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ExchangeRateRepository.
 * Uses ConcurrentHashMap for thread-safety.
 * Pre-populated with sample exchange rates.
 */
public class InMemoryExchangeRateRepository implements ExchangeRateRepository {
    
    private final ConcurrentHashMap<String, ExchangeRate> rates = new ConcurrentHashMap<>();

    public InMemoryExchangeRateRepository() {
        initializeDefaultRates();
    }

    /**
     * Initialize with default exchange rates (USD as base)
     */
    private void initializeDefaultRates() {
        // USD base rates
        save(new ExchangeRate(Currency.USD, Currency.EUR, new BigDecimal("0.92")));
        save(new ExchangeRate(Currency.USD, Currency.GBP, new BigDecimal("0.79")));
        save(new ExchangeRate(Currency.USD, Currency.INR, new BigDecimal("83.12")));
        save(new ExchangeRate(Currency.USD, Currency.JPY, new BigDecimal("149.50")));
        save(new ExchangeRate(Currency.USD, Currency.CAD, new BigDecimal("1.36")));
        save(new ExchangeRate(Currency.USD, Currency.AUD, new BigDecimal("1.53")));
        save(new ExchangeRate(Currency.USD, Currency.CHF, new BigDecimal("0.88")));
        save(new ExchangeRate(Currency.USD, Currency.CNY, new BigDecimal("7.24")));
        save(new ExchangeRate(Currency.USD, Currency.SGD, new BigDecimal("1.34")));
        
        // EUR base rates
        save(new ExchangeRate(Currency.EUR, Currency.USD, new BigDecimal("1.09")));
        save(new ExchangeRate(Currency.EUR, Currency.GBP, new BigDecimal("0.86")));
        save(new ExchangeRate(Currency.EUR, Currency.INR, new BigDecimal("90.35")));
        
        // GBP base rates
        save(new ExchangeRate(Currency.GBP, Currency.USD, new BigDecimal("1.27")));
        save(new ExchangeRate(Currency.GBP, Currency.EUR, new BigDecimal("1.16")));
        save(new ExchangeRate(Currency.GBP, Currency.INR, new BigDecimal("105.20")));
        
        // INR base rates
        save(new ExchangeRate(Currency.INR, Currency.USD, new BigDecimal("0.012")));
        save(new ExchangeRate(Currency.INR, Currency.EUR, new BigDecimal("0.011")));
        save(new ExchangeRate(Currency.INR, Currency.GBP, new BigDecimal("0.0095")));
    }

    private String getKey(Currency from, Currency to) {
        return from.name() + "_" + to.name();
    }

    @Override
    public Optional<ExchangeRate> findRate(Currency from, Currency to) {
        if (from == to) {
            return Optional.of(new ExchangeRate(from, to, BigDecimal.ONE));
        }
        return Optional.ofNullable(rates.get(getKey(from, to)));
    }

    @Override
    public ExchangeRate save(ExchangeRate rate) {
        rates.put(getKey(rate.getFromCurrency(), rate.getToCurrency()), rate);
        return rate;
    }

    @Override
    public List<ExchangeRate> findByFromCurrency(Currency from) {
        return rates.values().stream()
            .filter(r -> r.getFromCurrency() == from)
            .collect(Collectors.toList());
    }

    @Override
    public List<ExchangeRate> findAll() {
        return new ArrayList<>(rates.values());
    }

    @Override
    public boolean delete(Currency from, Currency to) {
        return rates.remove(getKey(from, to)) != null;
    }

    @Override
    public boolean exists(Currency from, Currency to) {
        if (from == to) return true;
        return rates.containsKey(getKey(from, to));
    }
}



