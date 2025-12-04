package librarymanagement.enums;

/**
 * Represents the availability status of a book copy in the library.
 */
public enum BookStatus {
    AVAILABLE,      // Book is available for borrowing
    BORROWED,       // Book is currently borrowed
    RESERVED,       // Book is reserved by a member
    LOST,           // Book is reported lost
    DAMAGED,        // Book is damaged and not available
    MAINTENANCE     // Book is under maintenance/repair
}



