package digitalwallet.strategies.fraud;

import digitalwallet.models.Transfer;

/**
 * Strategy interface for fraud detection.
 * Allows different fraud detection algorithms to be plugged in.
 */
public interface FraudDetectionStrategy {
    
    /**
     * Check a transfer for potential fraud
     * @param transfer The transfer to check
     * @return FraudCheckResult with the decision and details
     */
    FraudCheckResult check(Transfer transfer);
    
    /**
     * Get the name of this fraud detection strategy
     */
    String getStrategyName();

    /**
     * Result of a fraud check
     */
    class FraudCheckResult {
        private final Decision decision;
        private final String reason;
        private final int riskScore; // 0-100

        public enum Decision {
            ALLOW,      // Transaction is safe
            FLAG,       // Transaction is suspicious but allowed
            REVIEW,     // Transaction needs manual review
            BLOCK       // Transaction should be blocked
        }

        private FraudCheckResult(Decision decision, String reason, int riskScore) {
            this.decision = decision;
            this.reason = reason;
            this.riskScore = Math.max(0, Math.min(100, riskScore));
        }

        public static FraudCheckResult allow() {
            return new FraudCheckResult(Decision.ALLOW, "No issues detected", 0);
        }

        public static FraudCheckResult allow(int riskScore) {
            return new FraudCheckResult(Decision.ALLOW, "Low risk", riskScore);
        }

        public static FraudCheckResult flag(String reason, int riskScore) {
            return new FraudCheckResult(Decision.FLAG, reason, riskScore);
        }

        public static FraudCheckResult review(String reason, int riskScore) {
            return new FraudCheckResult(Decision.REVIEW, reason, riskScore);
        }

        public static FraudCheckResult block(String reason) {
            return new FraudCheckResult(Decision.BLOCK, reason, 100);
        }

        public Decision getDecision() { return decision; }
        public String getReason() { return reason; }
        public int getRiskScore() { return riskScore; }

        public boolean isAllowed() {
            return decision == Decision.ALLOW || decision == Decision.FLAG;
        }

        public boolean needsReview() {
            return decision == Decision.REVIEW;
        }

        public boolean isBlocked() {
            return decision == Decision.BLOCK;
        }

        @Override
        public String toString() {
            return String.format("FraudCheckResult{decision=%s, riskScore=%d, reason='%s'}",
                decision, riskScore, reason);
        }
    }
}



