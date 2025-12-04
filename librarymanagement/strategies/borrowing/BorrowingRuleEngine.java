package librarymanagement.strategies.borrowing;

import librarymanagement.models.Member;
import librarymanagement.models.BookCopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Engine that combines multiple borrowing rules using Chain of Responsibility pattern.
 * All rules must pass for borrowing to be allowed.
 */
public class BorrowingRuleEngine {
    
    private final List<BorrowingRule> rules;

    public BorrowingRuleEngine() {
        this.rules = new ArrayList<>();
    }

    public BorrowingRuleEngine(BorrowingRule... rules) {
        this.rules = new ArrayList<>(Arrays.asList(rules));
    }

    public BorrowingRuleEngine addRule(BorrowingRule rule) {
        rules.add(rule);
        return this;
    }

    public BorrowingRuleEngine removeRule(Class<? extends BorrowingRule> ruleClass) {
        rules.removeIf(rule -> rule.getClass().equals(ruleClass));
        return this;
    }

    /**
     * Validates all rules and returns the first failure, or success if all pass.
     */
    public ValidationResult validate(Member member, BookCopy bookCopy, int currentBorrowCount) {
        for (BorrowingRule rule : rules) {
            ValidationResult result = rule.validate(member, bookCopy, currentBorrowCount);
            if (!result.isValid()) {
                return result;
            }
        }
        return ValidationResult.success();
    }

    /**
     * Validates all rules and returns all failures.
     */
    public List<ValidationResult> validateAll(Member member, BookCopy bookCopy, int currentBorrowCount) {
        List<ValidationResult> failures = new ArrayList<>();
        for (BorrowingRule rule : rules) {
            ValidationResult result = rule.validate(member, bookCopy, currentBorrowCount);
            if (!result.isValid()) {
                failures.add(result);
            }
        }
        return failures;
    }

    /**
     * Creates a default rule engine with standard borrowing rules.
     */
    public static BorrowingRuleEngine createDefault() {
        return new BorrowingRuleEngine(
                new MemberStatusRule(),
                new MembershipValidityRule(),
                new MaxBooksRule(),
                new BookAvailabilityRule()
        );
    }

    public List<BorrowingRule> getRules() {
        return new ArrayList<>(rules);
    }
}



