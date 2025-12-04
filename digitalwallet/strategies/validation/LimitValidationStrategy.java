package digitalwallet.strategies.validation;

import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;
import digitalwallet.repositories.TransferRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Validates that the transfer doesn't exceed daily limits.
 */
public class LimitValidationStrategy implements TransferValidationStrategy {
    
    private final TransferRepository transferRepository;

    public LimitValidationStrategy(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    public ValidationResult validate(Transfer transfer, Wallet sourceWallet, Wallet targetWallet) {
        if (sourceWallet == null) {
            return ValidationResult.failure("Source wallet not found", "WALLET_NOT_FOUND");
        }

        // Calculate today's transfers
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        BigDecimal todayTotal = transferRepository
            .findByFromWalletId(sourceWallet.getId())
            .stream()
            .filter(t -> t.getCreatedAt().isAfter(startOfDay) && t.getCreatedAt().isBefore(endOfDay))
            .filter(t -> t.getStatus().isSuccessful() || t.getStatus() == digitalwallet.enums.TransactionStatus.PENDING)
            .map(Transfer::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal projectedTotal = todayTotal.add(transfer.getAmount());
        BigDecimal dailyLimit = sourceWallet.getDailyTransferLimit();

        if (projectedTotal.compareTo(dailyLimit) > 0) {
            return ValidationResult.failure(
                String.format("Daily transfer limit exceeded. Limit: %s, Already Used: %s, Requested: %s",
                    transfer.getSourceCurrency().format(dailyLimit),
                    transfer.getSourceCurrency().format(todayTotal),
                    transfer.getSourceCurrency().format(transfer.getAmount())),
                "DAILY_LIMIT_EXCEEDED"
            );
        }

        return ValidationResult.success();
    }

    @Override
    public String getStrategyName() {
        return "Daily Limit Validation";
    }

    @Override
    public int getOrder() {
        return 20;
    }
}



