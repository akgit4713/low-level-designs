package digitalwallet.exceptions;

import digitalwallet.enums.Currency;
import java.math.BigDecimal;

/**
 * Exception thrown when a transaction exceeds daily limits.
 */
public class DailyLimitExceededException extends WalletException {
    
    private final String userId;
    private final BigDecimal requestedAmount;
    private final BigDecimal dailyLimit;
    private final BigDecimal usedToday;
    private final Currency currency;

    public DailyLimitExceededException(String userId, BigDecimal requestedAmount, 
                                       BigDecimal dailyLimit, BigDecimal usedToday, Currency currency) {
        super(String.format(
            "Daily limit exceeded for user %s. Requested: %s %s, Daily Limit: %s %s, Already Used Today: %s %s",
            userId, currency.getSymbol(), requestedAmount, 
            currency.getSymbol(), dailyLimit, currency.getSymbol(), usedToday),
            "DAILY_LIMIT_EXCEEDED");
        this.userId = userId;
        this.requestedAmount = requestedAmount;
        this.dailyLimit = dailyLimit;
        this.usedToday = usedToday;
        this.currency = currency;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BigDecimal getUsedToday() {
        return usedToday;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getRemainingLimit() {
        return dailyLimit.subtract(usedToday);
    }
}



