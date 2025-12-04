package digitalwallet.exceptions;

import digitalwallet.enums.Currency;
import java.math.BigDecimal;

/**
 * Exception thrown when a wallet has insufficient funds for a transaction.
 */
public class InsufficientBalanceException extends WalletException {
    
    private final String walletId;
    private final Currency currency;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableBalance;

    public InsufficientBalanceException(String walletId, Currency currency, 
                                        BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format("Insufficient balance in wallet %s. Requested: %s %s, Available: %s %s",
                walletId, currency.getSymbol(), requestedAmount, 
                currency.getSymbol(), availableBalance),
              "INSUFFICIENT_BALANCE");
        this.walletId = walletId;
        this.currency = currency;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public String getWalletId() {
        return walletId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}



