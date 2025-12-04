package librarymanagement.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a fine issued to a member for overdue books.
 */
public class Fine {
    private final String fineId;
    private final String memberId;
    private final String borrowRecordId;
    private final BigDecimal amount;
    private final LocalDateTime issuedAt;
    private boolean paid;
    private LocalDateTime paidAt;

    public Fine(String memberId, String borrowRecordId, BigDecimal amount) {
        this.fineId = "FINE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.borrowRecordId = Objects.requireNonNull(borrowRecordId, "Borrow record ID cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.issuedAt = LocalDateTime.now();
        this.paid = false;
    }

    // Getters
    public String getFineId() {
        return fineId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBorrowRecordId() {
        return borrowRecordId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public boolean isPaid() {
        return paid;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    // Methods
    public void markAsPaid() {
        this.paid = true;
        this.paidAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fine fine = (Fine) o;
        return fineId.equals(fine.fineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fineId);
    }

    @Override
    public String toString() {
        return String.format("Fine{id='%s', member='%s', amount=%s, paid=%s}", 
                fineId, memberId, amount, paid);
    }
}



