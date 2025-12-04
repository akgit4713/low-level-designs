package librarymanagement.services.impl;

import librarymanagement.enums.BookStatus;
import librarymanagement.enums.BorrowStatus;
import librarymanagement.exceptions.*;
import librarymanagement.models.*;
import librarymanagement.observers.EventPublisher;
import librarymanagement.observers.LibraryEvent;
import librarymanagement.repositories.BookRepository;
import librarymanagement.repositories.BorrowRecordRepository;
import librarymanagement.repositories.MemberRepository;
import librarymanagement.services.BorrowService;
import librarymanagement.strategies.borrowing.BorrowingRuleEngine;
import librarymanagement.strategies.borrowing.ValidationResult;
import librarymanagement.strategies.fine.DailyFineStrategy;
import librarymanagement.strategies.fine.FineCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of BorrowService with thread-safe operations and rule-based validation.
 */
public class BorrowServiceImpl implements BorrowService {
    
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final EventPublisher eventPublisher;
    private final BorrowingRuleEngine ruleEngine;
    private final FineCalculationStrategy fineStrategy;
    
    // Fine storage (in a real system, this would be in a database)
    private final ConcurrentHashMap<String, Fine> fines = new ConcurrentHashMap<>();
    
    // Lock per book copy for fine-grained concurrency control
    private final ConcurrentHashMap<String, ReentrantLock> bookCopyLocks = new ConcurrentHashMap<>();

    public BorrowServiceImpl(BookRepository bookRepository, 
                             MemberRepository memberRepository,
                             BorrowRecordRepository borrowRecordRepository,
                             EventPublisher eventPublisher) {
        this(bookRepository, memberRepository, borrowRecordRepository, eventPublisher,
                BorrowingRuleEngine.createDefault(), new DailyFineStrategy(BigDecimal.valueOf(0.50)));
    }

    public BorrowServiceImpl(BookRepository bookRepository,
                             MemberRepository memberRepository,
                             BorrowRecordRepository borrowRecordRepository,
                             EventPublisher eventPublisher,
                             BorrowingRuleEngine ruleEngine,
                             FineCalculationStrategy fineStrategy) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.eventPublisher = eventPublisher;
        this.ruleEngine = ruleEngine;
        this.fineStrategy = fineStrategy;
    }

    private ReentrantLock getLockForBookCopy(String bookCopyId) {
        return bookCopyLocks.computeIfAbsent(bookCopyId, k -> new ReentrantLock());
    }

    @Override
    public BorrowRecord borrowBook(String memberId, String bookCopyId) {
        ReentrantLock lock = getLockForBookCopy(bookCopyId);
        lock.lock();
        try {
            // Fetch member
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId, true));
            
            // Fetch book copy
            BookCopy bookCopy = bookRepository.findBookCopyById(bookCopyId)
                    .orElseThrow(() -> new BookNotFoundException("Book copy not found: " + bookCopyId));
            
            // Get current borrow count
            int currentBorrowCount = borrowRecordRepository.countActiveByMemberId(memberId);
            
            // Validate borrowing rules
            ValidationResult result = ruleEngine.validate(member, bookCopy, currentBorrowCount);
            if (!result.isValid()) {
                throw new BorrowingException(result.getMessage());
            }
            
            // Create borrow record
            BorrowRecord record = new BorrowRecord(memberId, bookCopyId, member.getLoanDurationDays());
            borrowRecordRepository.save(record);
            
            // Update book copy status
            bookCopy.setStatus(BookStatus.BORROWED);
            bookRepository.saveBookCopy(bookCopy);
            
            // Publish event
            eventPublisher.publish(new LibraryEvent(
                    LibraryEvent.EventType.BOOK_BORROWED,
                    record.getRecordId(),
                    String.format("Book '%s' borrowed by %s. Due: %s",
                            bookCopy.getBook().getTitle(), member.getName(), record.getDueDate()),
                    record));
            
            return record;
            
        } finally {
            lock.unlock();
        }
    }

    @Override
    public BorrowRecord returnBook(String borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BorrowingException("Borrow record not found: " + borrowRecordId));
        
        return processReturn(record);
    }

    @Override
    public BorrowRecord returnBookByCopyId(String bookCopyId) {
        BorrowRecord record = borrowRecordRepository.findActiveByBookCopyId(bookCopyId)
                .orElseThrow(() -> new BorrowingException("No active borrow found for book copy: " + bookCopyId));
        
        return processReturn(record);
    }

    private BorrowRecord processReturn(BorrowRecord record) {
        ReentrantLock lock = getLockForBookCopy(record.getBookCopyId());
        lock.lock();
        try {
            if (record.getStatus() == BorrowStatus.RETURNED) {
                throw new BorrowingException("Book has already been returned");
            }
            
            // Update record
            record.setReturnDate(LocalDate.now());
            record.setStatus(BorrowStatus.RETURNED);
            
            // Calculate and set fine if overdue
            if (record.isOverdue()) {
                BigDecimal fine = fineStrategy.calculateFine(record);
                record.setFineAmount(fine);
            }
            
            borrowRecordRepository.save(record);
            
            // Update book copy status
            BookCopy bookCopy = bookRepository.findBookCopyById(record.getBookCopyId())
                    .orElseThrow(() -> new BookNotFoundException("Book copy not found: " + record.getBookCopyId()));
            bookCopy.setStatus(BookStatus.AVAILABLE);
            bookRepository.saveBookCopy(bookCopy);
            
            // Publish event
            String message = record.isOverdue() 
                    ? String.format("Book returned (overdue by %d days). Fine: $%s", 
                            record.getOverdueDays(), record.getFineAmount())
                    : "Book returned on time";
            
            eventPublisher.publish(new LibraryEvent(
                    LibraryEvent.EventType.BOOK_RETURNED,
                    record.getRecordId(),
                    message,
                    record));
            
            return record;
            
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<BorrowRecord> findBorrowRecordById(String recordId) {
        return borrowRecordRepository.findById(recordId);
    }

    @Override
    public List<BorrowRecord> getBorrowHistory(String memberId) {
        return borrowRecordRepository.findByMemberId(memberId);
    }

    @Override
    public List<BorrowRecord> getActiveBorrows(String memberId) {
        return borrowRecordRepository.findActiveByMemberId(memberId);
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords();
    }

    @Override
    public int getActiveBorrowCount(String memberId) {
        return borrowRecordRepository.countActiveByMemberId(memberId);
    }

    @Override
    public BigDecimal calculateFine(String borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BorrowingException("Borrow record not found: " + borrowRecordId));
        
        return fineStrategy.calculateFine(record);
    }

    @Override
    public Fine issueFine(String borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BorrowingException("Borrow record not found: " + borrowRecordId));
        
        if (!record.isOverdue()) {
            throw new BorrowingException("Cannot issue fine for non-overdue record");
        }
        
        BigDecimal amount = fineStrategy.calculateFine(record);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BorrowingException("Fine amount must be greater than zero");
        }
        
        Fine fine = new Fine(record.getMemberId(), borrowRecordId, amount);
        fines.put(fine.getFineId(), fine);
        
        record.setFineAmount(amount);
        borrowRecordRepository.save(record);
        
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.FINE_ISSUED,
                fine.getFineId(),
                String.format("Fine of $%s issued for overdue book", amount),
                fine));
        
        return fine;
    }

    @Override
    public void payFine(String fineId) {
        Fine fine = fines.get(fineId);
        if (fine == null) {
            throw new BorrowingException("Fine not found: " + fineId);
        }
        
        if (fine.isPaid()) {
            throw new BorrowingException("Fine has already been paid");
        }
        
        fine.markAsPaid();
        
        // Update borrow record
        borrowRecordRepository.findById(fine.getBorrowRecordId())
                .ifPresent(record -> {
                    record.setFinePaid(true);
                    borrowRecordRepository.save(record);
                });
        
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.FINE_PAID,
                fineId,
                String.format("Fine of $%s paid", fine.getAmount()),
                fine));
    }

    @Override
    public List<Fine> getUnpaidFines(String memberId) {
        List<Fine> unpaidFines = new ArrayList<>();
        for (Fine fine : fines.values()) {
            if (fine.getMemberId().equals(memberId) && !fine.isPaid()) {
                unpaidFines.add(fine);
            }
        }
        return unpaidFines;
    }

    @Override
    public BigDecimal getTotalUnpaidFines(String memberId) {
        return getUnpaidFines(memberId).stream()
                .map(Fine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}



