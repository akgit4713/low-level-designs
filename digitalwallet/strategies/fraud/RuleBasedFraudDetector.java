package digitalwallet.strategies.fraud;

import digitalwallet.models.Transfer;
import digitalwallet.repositories.TransferRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule-based fraud detection strategy.
 * Uses configurable rules to detect suspicious transactions.
 */
public class RuleBasedFraudDetector implements FraudDetectionStrategy {
    
    private final TransferRepository transferRepository;
    private final List<FraudRule> rules;
    
    // Configurable thresholds
    private BigDecimal singleTransactionLimit = new BigDecimal("10000");
    private BigDecimal hourlyLimit = new BigDecimal("25000");
    private BigDecimal dailyLimit = new BigDecimal("50000");
    private int maxTransactionsPerHour = 20;

    public RuleBasedFraudDetector(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
        this.rules = new ArrayList<>();
        initializeDefaultRules();
    }

    private void initializeDefaultRules() {
        // Rule 1: Single transaction limit
        rules.add(transfer -> {
            if (transfer.getAmount().compareTo(singleTransactionLimit) > 0) {
                return FraudCheckResult.review(
                    "Transaction exceeds single transaction limit: " + 
                    transfer.getSourceCurrency().format(transfer.getAmount()),
                    70
                );
            }
            return FraudCheckResult.allow();
        });

        // Rule 2: Velocity check - too many transactions
        rules.add(transfer -> {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            long recentCount = transferRepository.findByFromWalletId(transfer.getFromWalletId())
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(oneHourAgo))
                .count();
            
            if (recentCount >= maxTransactionsPerHour) {
                return FraudCheckResult.block("Too many transactions in the past hour: " + recentCount);
            } else if (recentCount >= maxTransactionsPerHour * 0.7) {
                return FraudCheckResult.flag("High transaction velocity", 50);
            }
            return FraudCheckResult.allow();
        });

        // Rule 3: Hourly amount limit
        rules.add(transfer -> {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            BigDecimal hourlyTotal = transferRepository.findByFromWalletId(transfer.getFromWalletId())
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(oneHourAgo))
                .filter(t -> t.getStatus().isSuccessful())
                .map(Transfer::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal projected = hourlyTotal.add(transfer.getAmount());
            if (projected.compareTo(hourlyLimit) > 0) {
                return FraudCheckResult.review("Hourly limit would be exceeded", 60);
            }
            return FraudCheckResult.allow();
        });

        // Rule 4: Large transfer to new recipient
        rules.add(transfer -> {
            if (transfer.getToWalletId() != null && 
                transfer.getAmount().compareTo(new BigDecimal("1000")) > 0) {
                
                boolean hasTransferredBefore = transferRepository.findByFromWalletId(transfer.getFromWalletId())
                    .stream()
                    .anyMatch(t -> transfer.getToWalletId().equals(t.getToWalletId()));
                
                if (!hasTransferredBefore) {
                    return FraudCheckResult.flag("Large transfer to new recipient", 40);
                }
            }
            return FraudCheckResult.allow();
        });
    }

    @Override
    public FraudCheckResult check(Transfer transfer) {
        int maxRiskScore = 0;
        FraudCheckResult worstResult = FraudCheckResult.allow();
        
        for (FraudRule rule : rules) {
            FraudCheckResult result = rule.evaluate(transfer);
            
            // If any rule blocks, return immediately
            if (result.isBlocked()) {
                return result;
            }
            
            // Track the worst non-blocking result
            if (result.getRiskScore() > maxRiskScore) {
                maxRiskScore = result.getRiskScore();
                worstResult = result;
            }
        }
        
        // If combined risk score is very high, escalate
        if (maxRiskScore >= 80) {
            return FraudCheckResult.review("Multiple risk factors detected", maxRiskScore);
        }
        
        return worstResult;
    }

    @Override
    public String getStrategyName() {
        return "Rule-Based Fraud Detection";
    }

    // Configuration methods
    public void setSingleTransactionLimit(BigDecimal limit) {
        this.singleTransactionLimit = limit;
    }

    public void setHourlyLimit(BigDecimal limit) {
        this.hourlyLimit = limit;
    }

    public void setDailyLimit(BigDecimal limit) {
        this.dailyLimit = limit;
    }

    public void setMaxTransactionsPerHour(int max) {
        this.maxTransactionsPerHour = max;
    }

    public void addRule(FraudRule rule) {
        rules.add(rule);
    }

    /**
     * Functional interface for individual fraud rules
     */
    @FunctionalInterface
    public interface FraudRule {
        FraudCheckResult evaluate(Transfer transfer);
    }
}



