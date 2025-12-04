package librarymanagement.services;

import librarymanagement.models.BorrowRecord;
import librarymanagement.models.Fine;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for borrowing and returning books.
 */
public interface BorrowService {
    
    // Borrowing operations
    BorrowRecord borrowBook(String memberId, String bookCopyId);
    BorrowRecord returnBook(String borrowRecordId);
    BorrowRecord returnBookByCopyId(String bookCopyId);
    
    // Query operations
    Optional<BorrowRecord> findBorrowRecordById(String recordId);
    List<BorrowRecord> getBorrowHistory(String memberId);
    List<BorrowRecord> getActiveBorrows(String memberId);
    List<BorrowRecord> getOverdueRecords();
    int getActiveBorrowCount(String memberId);
    
    // Fine operations
    BigDecimal calculateFine(String borrowRecordId);
    Fine issueFine(String borrowRecordId);
    void payFine(String fineId);
    List<Fine> getUnpaidFines(String memberId);
    BigDecimal getTotalUnpaidFines(String memberId);
}



