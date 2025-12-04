package digitalwallet.services;

import digitalwallet.enums.Currency;
import digitalwallet.models.Wallet;
import digitalwallet.models.WalletBalance;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for wallet management.
 */
public interface WalletService {
    
    /**
     * Create a wallet for a user
     */
    Wallet createWallet(String userId, Currency defaultCurrency);
    
    /**
     * Get wallet by ID
     */
    Optional<Wallet> getWallet(String walletId);
    
    /**
     * Get wallet by user ID
     */
    Optional<Wallet> getWalletByUserId(String userId);
    
    /**
     * Get available balance for a currency
     */
    BigDecimal getBalance(String walletId, Currency currency);
    
    /**
     * Get all balances for a wallet
     */
    Map<Currency, WalletBalance> getAllBalances(String walletId);
    
    /**
     * Credit funds to wallet (internal use)
     */
    void credit(String walletId, BigDecimal amount, Currency currency);
    
    /**
     * Debit funds from wallet (internal use)
     */
    void debit(String walletId, BigDecimal amount, Currency currency);
    
    /**
     * Set daily transfer limit
     */
    void setDailyTransferLimit(String walletId, BigDecimal limit);
    
    /**
     * Set daily withdrawal limit
     */
    void setDailyWithdrawalLimit(String walletId, BigDecimal limit);
    
    /**
     * Deactivate a wallet
     */
    void deactivateWallet(String walletId);
    
    /**
     * Activate a wallet
     */
    void activateWallet(String walletId);
}



