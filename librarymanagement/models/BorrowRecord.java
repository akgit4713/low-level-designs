package librarymanagement.models;

import librarymanagement.enums.BorrowStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a borrowing transaction between a member and a book copy.
 */
public class BorrowRecord {
    private final String recordId;
    private final String memberId;
    private final String bookCopyId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private BigDecimal fineAmount;
    private boolean finePaid;

    public BorrowRecord(String memberId, String bookCopyId, int loanDurationDays) {
        this.recordId = "BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.bookCopyId = Objects.requireNonNull(bookCopyId, "Book copy ID cannot be null");
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(loanDurationDays);
        this.status = BorrowStatus.ACTIVE;
        this.fineAmount = BigDecimal.ZERO;
        this.finePaid = false;
    }

    public BorrowRecord(String recordId, String memberId, String bookCopyId, 
                        LocalDate borrowDate, LocalDate dueDate) {
        this.recordId = Objects.requireNonNull(recordId, "Record ID cannot be null");
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.bookCopyId = Objects.requireNonNull(bookCopyId, "Book copy ID cannot be null");
        this.borrowDate = Objects.requireNonNull(borrowDate, "Borrow date cannot be null");
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
        this.status = BorrowStatus.ACTIVE;
        this.fineAmount = BigDecimal.ZERO;
        this.finePaid = false;
    }

    // Getters
    public String getRecordId() {
        return recordId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBookCopyId() {
        return bookCopyId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public boolean isFinePaid() {
        return finePaid;
    }

    // Setters
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setStatus(BorrowStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount != null ? fineAmount : BigDecimal.ZERO;
    }

    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    public boolean isOverdue() {
        if (status == BorrowStatus.RETURNED) {
            return returnDate != null && returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getOverdueDays() {
        LocalDate checkDate = (returnDate != null) ? returnDate : LocalDate.now();
        if (checkDate.isAfter(dueDate)) {
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecord that = (BorrowRecord) o;
        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }

    @Override
    public String toString() {
        return String.format("BorrowRecord{id='%s', member='%s', book='%s', borrowed=%s, due=%s, status=%s}", 
                recordId, memberId, bookCopyId, borrowDate, dueDate, status);
    }
}



