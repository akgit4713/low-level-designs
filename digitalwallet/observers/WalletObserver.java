package digitalwallet.observers;

import digitalwallet.enums.Currency;
import digitalwallet.models.Wallet;
import java.math.BigDecimal;

/**
 * Observer interface for wallet events.
 */
public interface WalletObserver {
    
    /**
     * Called when a new wallet is created
     */
    void onWalletCreated(Wallet wallet);
    
    /**
     * Called when wallet balance changes
     */
    void onBalanceChanged(Wallet wallet, Currency currency, 
                          BigDecimal oldBalance, BigDecimal newBalance);
    
    /**
     * Called when wallet is deactivated
     */
    void onWalletDeactivated(Wallet wallet);
    
    /**
     * Called when low balance threshold is reached
     */
    void onLowBalance(Wallet wallet, Currency currency, BigDecimal balance);
}



