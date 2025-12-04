package digitalwallet.strategies.validation;

import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;

/**
 * Validates that both wallets are active and can transact.
 */
public class WalletStatusValidationStrategy implements TransferValidationStrategy {

    @Override
    public ValidationResult validate(Transfer transfer, Wallet sourceWallet, Wallet targetWallet) {
        // Validate source wallet
        if (sourceWallet == null) {
            return ValidationResult.failure("Source wallet not found", "SOURCE_WALLET_NOT_FOUND");
        }

        if (!sourceWallet.isActive()) {
            return ValidationResult.failure("Source wallet is not active", "SOURCE_WALLET_INACTIVE");
        }

        // Validate target wallet for P2P transfers
        if (transfer.getToWalletId() != null) {
            if (targetWallet == null) {
                return ValidationResult.failure("Target wallet not found", "TARGET_WALLET_NOT_FOUND");
            }

            if (!targetWallet.isActive()) {
                return ValidationResult.failure("Target wallet is not active", "TARGET_WALLET_INACTIVE");
            }

            // Prevent self-transfer (unless it's a currency exchange)
            if (sourceWallet.getId().equals(targetWallet.getId()) && 
                !transfer.isCrossCurrency()) {
                return ValidationResult.failure(
                    "Cannot transfer to the same wallet without currency conversion",
                    "SELF_TRANSFER_NOT_ALLOWED"
                );
            }
        }

        return ValidationResult.success();
    }

    @Override
    public String getStrategyName() {
        return "Wallet Status Validation";
    }

    @Override
    public int getOrder() {
        return 5; // Run first
    }
}



