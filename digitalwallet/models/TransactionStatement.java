package digitalwallet.models;

import digitalwallet.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a statement of transactions for a period.
 * Immutable once created.
 */
public class TransactionStatement {
    private final String walletId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime generatedAt;
    private final List<Transaction> transactions;
    private final Currency primaryCurrency;

    private TransactionStatement(Builder builder) {
        this.walletId = builder.walletId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.generatedAt = LocalDateTime.now();
        this.transactions = Collections.unmodifiableList(new ArrayList<>(builder.transactions));
        this.primaryCurrency = builder.primaryCurrency;
    }

    public String getWalletId() { return walletId; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public List<Transaction> getTransactions() { return transactions; }
    public Currency getPrimaryCurrency() { return primaryCurrency; }

    /**
     * Get total number of transactions
     */
    public int getTransactionCount() {
        return transactions.size();
    }

    /**
     * Get total credits (money in)
     */
    public BigDecimal getTotalCredits() {
        return transactions.stream()
            .filter(t -> t.isCredit() && t.isSuccessful())
            .filter(t -> t.getCurrency() == primaryCurrency)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total debits (money out)
     */
    public BigDecimal getTotalDebits() {
        return transactions.stream()
            .filter(t -> t.isDebit() && t.isSuccessful())
            .filter(t -> t.getCurrency() == primaryCurrency)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get net change (credits - debits)
     */
    public BigDecimal getNetChange() {
        return getTotalCredits().subtract(getTotalDebits());
    }

    /**
     * Get opening balance (first transaction's balance - its amount for credit, + amount for debit)
     */
    public BigDecimal getOpeningBalance() {
        if (transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Transaction first = transactions.get(0);
        if (first.getBalanceAfter() == null) {
            return BigDecimal.ZERO;
        }
        if (first.isCredit()) {
            return first.getBalanceAfter().subtract(first.getAmount());
        } else {
            return first.getBalanceAfter().add(first.getAmount());
        }
    }

    /**
     * Get closing balance (last transaction's balance after)
     */
    public BigDecimal getClosingBalance() {
        if (transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Transaction last = transactions.get(transactions.size() - 1);
        return last.getBalanceAfter() != null ? last.getBalanceAfter() : BigDecimal.ZERO;
    }

    /**
     * Generate a text summary of the statement
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("                    TRANSACTION STATEMENT\n");
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append(String.format("Wallet ID: %s\n", walletId));
        sb.append(String.format("Period: %s to %s\n", 
            startDate.toLocalDate(), endDate.toLocalDate()));
        sb.append(String.format("Generated: %s\n", generatedAt));
        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append(String.format("Opening Balance: %s\n", primaryCurrency.format(getOpeningBalance())));
        sb.append(String.format("Total Credits:   %s\n", primaryCurrency.format(getTotalCredits())));
        sb.append(String.format("Total Debits:    %s\n", primaryCurrency.format(getTotalDebits())));
        sb.append(String.format("Net Change:      %s\n", primaryCurrency.format(getNetChange())));
        sb.append(String.format("Closing Balance: %s\n", primaryCurrency.format(getClosingBalance())));
        sb.append("───────────────────────────────────────────────────────────\n");
        sb.append(String.format("Total Transactions: %d\n", getTransactionCount()));
        sb.append("═══════════════════════════════════════════════════════════\n");
        
        if (!transactions.isEmpty()) {
            sb.append("\nTransaction Details:\n");
            sb.append("───────────────────────────────────────────────────────────\n");
            for (Transaction tx : transactions) {
                String sign = tx.isCredit() ? "+" : "-";
                sb.append(String.format("%s | %s%s | %s | %s\n",
                    tx.getCreatedAt().toLocalDate(),
                    sign, tx.getCurrency().format(tx.getAmount()),
                    tx.getType().getDisplayName(),
                    tx.getDescription() != null ? tx.getDescription() : ""));
            }
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("TransactionStatement{wallet='%s', period=%s to %s, txCount=%d}",
            walletId, startDate.toLocalDate(), endDate.toLocalDate(), transactions.size());
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String walletId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<Transaction> transactions = new ArrayList<>();
        private Currency primaryCurrency = Currency.USD;

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder transactions(List<Transaction> transactions) {
            this.transactions = transactions != null ? transactions : new ArrayList<>();
            return this;
        }

        public Builder addTransaction(Transaction transaction) {
            this.transactions.add(transaction);
            return this;
        }

        public Builder primaryCurrency(Currency currency) {
            this.primaryCurrency = currency;
            return this;
        }

        public TransactionStatement build() {
            Objects.requireNonNull(walletId, "Wallet ID is required");
            Objects.requireNonNull(startDate, "Start date is required");
            Objects.requireNonNull(endDate, "End date is required");
            
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }
            
            return new TransactionStatement(this);
        }
    }
}



