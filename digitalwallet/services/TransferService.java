package digitalwallet.services;

import digitalwallet.enums.Currency;
import digitalwallet.models.Transfer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for fund transfers.
 */
public interface TransferService {
    
    /**
     * Transfer funds between wallets (P2P)
     */
    Transfer transfer(String fromWalletId, String toWalletId, BigDecimal amount,
                      Currency currency, String description);
    
    /**
     * Transfer with currency conversion
     */
    Transfer transferWithConversion(String fromWalletId, String toWalletId,
                                    BigDecimal amount, Currency fromCurrency,
                                    Currency toCurrency, String description);
    
    /**
     * Transfer to external bank account
     */
    Transfer transferToExternal(String fromWalletId, String paymentMethodId,
                                BigDecimal amount, Currency currency, String description);
    
    /**
     * Get transfer by ID
     */
    Optional<Transfer> getTransfer(String transferId);
    
    /**
     * Get transfers for a wallet (sent and received)
     */
    List<Transfer> getTransfers(String walletId);
    
    /**
     * Get outgoing transfers for a wallet
     */
    List<Transfer> getOutgoingTransfers(String walletId);
    
    /**
     * Get incoming transfers for a wallet
     */
    List<Transfer> getIncomingTransfers(String walletId);
    
    /**
     * Cancel a pending transfer
     */
    void cancelTransfer(String transferId);
    
    /**
     * Check for duplicate transfer using idempotency key
     */
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * Get pending transfers that need attention
     */
    List<Transfer> getPendingTransfers();
}



