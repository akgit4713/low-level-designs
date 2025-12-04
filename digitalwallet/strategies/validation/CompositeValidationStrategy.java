package digitalwallet.strategies.validation;

import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Composite validation strategy that runs multiple validators in order.
 * Follows the Chain of Responsibility pattern.
 */
public class CompositeValidationStrategy implements TransferValidationStrategy {
    
    private final List<TransferValidationStrategy> validators;

    public CompositeValidationStrategy() {
        this.validators = new ArrayList<>();
    }

    public CompositeValidationStrategy(List<TransferValidationStrategy> validators) {
        this.validators = new ArrayList<>(validators);
        sortValidators();
    }

    /**
     * Add a validator to the chain
     */
    public CompositeValidationStrategy addValidator(TransferValidationStrategy validator) {
        validators.add(validator);
        sortValidators();
        return this;
    }

    /**
     * Remove a validator by name
     */
    public CompositeValidationStrategy removeValidator(String name) {
        validators.removeIf(v -> v.getStrategyName().equals(name));
        return this;
    }

    private void sortValidators() {
        validators.sort(Comparator.comparingInt(TransferValidationStrategy::getOrder));
    }

    @Override
    public ValidationResult validate(Transfer transfer, Wallet sourceWallet, Wallet targetWallet) {
        for (TransferValidationStrategy validator : validators) {
            ValidationResult result = validator.validate(transfer, sourceWallet, targetWallet);
            if (!result.isValid()) {
                return result;
            }
        }
        return ValidationResult.success();
    }

    @Override
    public String getStrategyName() {
        return "Composite Validation";
    }

    public List<String> getValidatorNames() {
        return validators.stream()
            .map(TransferValidationStrategy::getStrategyName)
            .toList();
    }

    /**
     * Create a default composite validator with standard rules
     */
    public static CompositeValidationStrategy createDefault(
            digitalwallet.repositories.TransferRepository transferRepository) {
        return new CompositeValidationStrategy()
            .addValidator(new WalletStatusValidationStrategy())
            .addValidator(new BalanceValidationStrategy())
            .addValidator(new LimitValidationStrategy(transferRepository));
    }
}



