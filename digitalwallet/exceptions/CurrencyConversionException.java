package digitalwallet.exceptions;

import digitalwallet.enums.Currency;

/**
 * Exception thrown when currency conversion fails.
 */
public class CurrencyConversionException extends WalletException {
    
    private final Currency fromCurrency;
    private final Currency toCurrency;

    public CurrencyConversionException(String message) {
        super(message, "CURRENCY_CONVERSION_ERROR");
        this.fromCurrency = null;
        this.toCurrency = null;
    }

    public CurrencyConversionException(Currency fromCurrency, Currency toCurrency, String reason) {
        super(String.format("Cannot convert from %s to %s: %s", 
                fromCurrency.name(), toCurrency.name(), reason),
              "CURRENCY_CONVERSION_ERROR");
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public CurrencyConversionException(Currency fromCurrency, Currency toCurrency, Throwable cause) {
        super(String.format("Currency conversion failed from %s to %s", 
                fromCurrency.name(), toCurrency.name()), 
              "CURRENCY_CONVERSION_ERROR", cause);
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }
}



