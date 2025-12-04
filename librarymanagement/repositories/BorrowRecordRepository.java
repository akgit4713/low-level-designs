package librarymanagement.repositories;

import librarymanagement.enums.BorrowStatus;
import librarymanagement.models.BorrowRecord;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for borrow record operations.
 */
public interface BorrowRecordRepository {
    
    void save(BorrowRecord record);
    Optional<BorrowRecord> findById(String recordId);
    List<BorrowRecord> findByMemberId(String memberId);
    List<BorrowRecord> findByBookCopyId(String bookCopyId);
    List<BorrowRecord> findByStatus(BorrowStatus status);
    List<BorrowRecord> findActiveByMemberId(String memberId);
    Optional<BorrowRecord> findActiveByBookCopyId(String bookCopyId);
    List<BorrowRecord> findOverdueRecords();
    List<BorrowRecord> findAll();
    int countActiveByMemberId(String memberId);
}



