package librarymanagement.enums;

/**
 * Represents the status of a borrow record.
 */
public enum BorrowStatus {
    ACTIVE,         // Book is currently borrowed
    RETURNED,       // Book has been returned
    OVERDUE,        // Book is overdue
    LOST            // Book reported lost by member
}



