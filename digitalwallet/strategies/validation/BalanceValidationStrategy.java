package digitalwallet.strategies.validation;

import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;
import java.math.BigDecimal;

/**
 * Validates that the source wallet has sufficient balance for the transfer.
 */
public class BalanceValidationStrategy implements TransferValidationStrategy {

    @Override
    public ValidationResult validate(Transfer transfer, Wallet sourceWallet, Wallet targetWallet) {
        if (sourceWallet == null) {
            return ValidationResult.failure("Source wallet not found", "WALLET_NOT_FOUND");
        }

        BigDecimal totalRequired = transfer.getTotalDebitAmount();
        BigDecimal available = sourceWallet.getAvailableBalance(transfer.getSourceCurrency());

        if (available.compareTo(totalRequired) < 0) {
            return ValidationResult.failure(
                String.format("Insufficient balance. Required: %s, Available: %s",
                    transfer.getSourceCurrency().format(totalRequired),
                    transfer.getSourceCurrency().format(available)),
                "INSUFFICIENT_BALANCE"
            );
        }

        return ValidationResult.success();
    }

    @Override
    public String getStrategyName() {
        return "Balance Validation";
    }

    @Override
    public int getOrder() {
        return 10; // Run early
    }
}



