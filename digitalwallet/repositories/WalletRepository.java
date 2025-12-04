package digitalwallet.repositories;

import digitalwallet.models.Wallet;
import java.util.Optional;

/**
 * Repository interface for Wallet entity.
 */
public interface WalletRepository extends Repository<Wallet, String> {
    
    /**
     * Find wallet by user ID
     */
    Optional<Wallet> findByUserId(String userId);
    
    /**
     * Check if user has a wallet
     */
    boolean existsByUserId(String userId);
}



