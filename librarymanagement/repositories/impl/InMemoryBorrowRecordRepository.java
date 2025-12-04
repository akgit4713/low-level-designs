package librarymanagement.repositories.impl;

import librarymanagement.enums.BorrowStatus;
import librarymanagement.models.BorrowRecord;
import librarymanagement.repositories.BorrowRecordRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of BorrowRecordRepository.
 */
public class InMemoryBorrowRecordRepository implements BorrowRecordRepository {
    
    private final ConcurrentHashMap<String, BorrowRecord> records = new ConcurrentHashMap<>();

    @Override
    public void save(BorrowRecord record) {
        records.put(record.getRecordId(), record);
    }

    @Override
    public Optional<BorrowRecord> findById(String recordId) {
        return Optional.ofNullable(records.get(recordId));
    }

    @Override
    public List<BorrowRecord> findByMemberId(String memberId) {
        return records.values().stream()
                .filter(record -> record.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecord> findByBookCopyId(String bookCopyId) {
        return records.values().stream()
                .filter(record -> record.getBookCopyId().equals(bookCopyId))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecord> findByStatus(BorrowStatus status) {
        return records.values().stream()
                .filter(record -> record.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecord> findActiveByMemberId(String memberId) {
        return records.values().stream()
                .filter(record -> record.getMemberId().equals(memberId))
                .filter(record -> record.getStatus() == BorrowStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BorrowRecord> findActiveByBookCopyId(String bookCopyId) {
        return records.values().stream()
                .filter(record -> record.getBookCopyId().equals(bookCopyId))
                .filter(record -> record.getStatus() == BorrowStatus.ACTIVE)
                .findFirst();
    }

    @Override
    public List<BorrowRecord> findOverdueRecords() {
        LocalDate today = LocalDate.now();
        return records.values().stream()
                .filter(record -> record.getStatus() == BorrowStatus.ACTIVE)
                .filter(record -> record.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    @Override
    public int countActiveByMemberId(String memberId) {
        return (int) records.values().stream()
                .filter(record -> record.getMemberId().equals(memberId))
                .filter(record -> record.getStatus() == BorrowStatus.ACTIVE)
                .count();
    }
}



