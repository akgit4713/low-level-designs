package librarymanagement;

import librarymanagement.enums.MemberStatus;
import librarymanagement.enums.MemberType;
import librarymanagement.models.*;
import librarymanagement.observers.*;
import librarymanagement.repositories.*;
import librarymanagement.repositories.impl.*;
import librarymanagement.services.*;
import librarymanagement.services.impl.*;
import librarymanagement.strategies.borrowing.BorrowingRuleEngine;
import librarymanagement.strategies.fine.FineCalculationStrategy;
import librarymanagement.strategies.fine.DailyFineStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Façade class that provides a unified interface to the Library Management System.
 * This is the main entry point for interacting with the library system.
 * 
 * Implements the Façade pattern to simplify the complex subsystem of services,
 * repositories, and strategies.
 */
public class LibraryManagementSystem {
    
    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowService borrowService;
    private final EventPublisher eventPublisher;

    /**
     * Creates a new LibraryManagementSystem with default configurations.
     * Uses in-memory repositories and default strategies.
     */
    public LibraryManagementSystem() {
        this.eventPublisher = new EventPublisher();
        
        // Initialize repositories
        BookRepository bookRepository = new InMemoryBookRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository();
        BorrowRecordRepository borrowRecordRepository = new InMemoryBorrowRecordRepository();
        
        // Initialize services with default configurations
        this.bookService = new BookServiceImpl(bookRepository, eventPublisher);
        this.memberService = new MemberServiceImpl(memberRepository, eventPublisher);
        this.borrowService = new BorrowServiceImpl(
                bookRepository, 
                memberRepository, 
                borrowRecordRepository, 
                eventPublisher);
        
        // Add default observers
        eventPublisher.subscribe(new AuditLogObserver());
    }

    /**
     * Creates a new LibraryManagementSystem with custom configurations.
     */
    public LibraryManagementSystem(BookRepository bookRepository,
                                    MemberRepository memberRepository,
                                    BorrowRecordRepository borrowRecordRepository,
                                    BorrowingRuleEngine ruleEngine,
                                    FineCalculationStrategy fineStrategy) {
        this.eventPublisher = new EventPublisher();
        
        this.bookService = new BookServiceImpl(bookRepository, eventPublisher);
        this.memberService = new MemberServiceImpl(memberRepository, eventPublisher);
        this.borrowService = new BorrowServiceImpl(
                bookRepository, 
                memberRepository, 
                borrowRecordRepository, 
                eventPublisher,
                ruleEngine,
                fineStrategy);
    }

    // ============ Book Management ============

    /**
     * Adds a new book to the catalog.
     */
    public Book addBook(String isbn, String title, String author, int publicationYear) {
        return bookService.addBook(isbn, title, author, publicationYear);
    }

    /**
     * Updates an existing book's information.
     */
    public Book updateBook(String isbn, String title, String author, int publicationYear) {
        return bookService.updateBook(isbn, title, author, publicationYear);
    }

    /**
     * Removes a book from the catalog.
     */
    public void removeBook(String isbn) {
        bookService.removeBook(isbn);
    }

    /**
     * Adds a physical copy of a book to the library.
     */
    public BookCopy addBookCopy(String isbn) {
        return bookService.addBookCopy(isbn);
    }

    /**
     * Adds a physical copy of a book with a specific rack location.
     */
    public BookCopy addBookCopy(String isbn, String rackLocation) {
        return bookService.addBookCopy(isbn, rackLocation);
    }

    /**
     * Searches for books by title, author, or ISBN.
     */
    public List<Book> searchBooks(String query) {
        return bookService.searchBooks(query);
    }

    /**
     * Gets all books in the catalog.
     */
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * Finds a book by ISBN.
     */
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookService.findBookByIsbn(isbn);
    }

    /**
     * Gets available copies of a book.
     */
    public List<BookCopy> getAvailableCopies(String isbn) {
        return bookService.getAvailableCopies(isbn);
    }

    // ============ Member Management ============

    /**
     * Registers a new library member.
     */
    public Member registerMember(String name, String email, MemberType memberType) {
        return memberService.registerMember(name, email, memberType);
    }

    /**
     * Registers a new standard member.
     */
    public Member registerMember(String name, String email) {
        return memberService.registerMember(name, email, MemberType.STANDARD);
    }

    /**
     * Updates member information.
     */
    public Member updateMember(String memberId, String name, String email, String phone, String address) {
        return memberService.updateMember(memberId, name, email, phone, address);
    }

    /**
     * Updates member status.
     */
    public void updateMemberStatus(String memberId, MemberStatus status) {
        memberService.updateMemberStatus(memberId, status);
    }

    /**
     * Finds a member by ID.
     */
    public Optional<Member> findMemberById(String memberId) {
        return memberService.findById(memberId);
    }

    /**
     * Gets all members.
     */
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    /**
     * Searches members by name.
     */
    public List<Member> searchMembersByName(String name) {
        return memberService.searchByName(name);
    }

    // ============ Borrowing Operations ============

    /**
     * Borrows a book for a member.
     */
    public BorrowRecord borrowBook(String memberId, String bookCopyId) {
        return borrowService.borrowBook(memberId, bookCopyId);
    }

    /**
     * Returns a borrowed book.
     */
    public BorrowRecord returnBook(String borrowRecordId) {
        return borrowService.returnBook(borrowRecordId);
    }

    /**
     * Returns a book by its copy ID.
     */
    public BorrowRecord returnBookByCopyId(String bookCopyId) {
        return borrowService.returnBookByCopyId(bookCopyId);
    }

    /**
     * Gets the borrowing history for a member.
     */
    public List<BorrowRecord> getBorrowHistory(String memberId) {
        return borrowService.getBorrowHistory(memberId);
    }

    /**
     * Gets active borrows for a member.
     */
    public List<BorrowRecord> getActiveBorrows(String memberId) {
        return borrowService.getActiveBorrows(memberId);
    }

    /**
     * Gets all overdue records.
     */
    public List<BorrowRecord> getOverdueRecords() {
        return borrowService.getOverdueRecords();
    }

    /**
     * Gets the number of books currently borrowed by a member.
     */
    public int getActiveBorrowCount(String memberId) {
        return borrowService.getActiveBorrowCount(memberId);
    }

    // ============ Fine Operations ============

    /**
     * Calculates the fine for a borrow record.
     */
    public BigDecimal calculateFine(String borrowRecordId) {
        return borrowService.calculateFine(borrowRecordId);
    }

    /**
     * Gets total unpaid fines for a member.
     */
    public BigDecimal getTotalUnpaidFines(String memberId) {
        return borrowService.getTotalUnpaidFines(memberId);
    }

    // ============ Event Management ============

    /**
     * Subscribes an observer to library events.
     */
    public void subscribeToEvents(LibraryEventObserver observer) {
        eventPublisher.subscribe(observer);
    }

    /**
     * Unsubscribes an observer from library events.
     */
    public void unsubscribeFromEvents(LibraryEventObserver observer) {
        eventPublisher.unsubscribe(observer);
    }

    // ============ Direct Service Access ============

    /**
     * Gets the BookService for advanced operations.
     */
    public BookService getBookService() {
        return bookService;
    }

    /**
     * Gets the MemberService for advanced operations.
     */
    public MemberService getMemberService() {
        return memberService;
    }

    /**
     * Gets the BorrowService for advanced operations.
     */
    public BorrowService getBorrowService() {
        return borrowService;
    }

    /**
     * Gets the EventPublisher for event management.
     */
    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    /**
     * Creates a builder for customized LibraryManagementSystem configuration.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating customized LibraryManagementSystem instances.
     */
    public static class Builder {
        private BookRepository bookRepository;
        private MemberRepository memberRepository;
        private BorrowRecordRepository borrowRecordRepository;
        private BorrowingRuleEngine ruleEngine;
        private FineCalculationStrategy fineStrategy;

        public Builder withBookRepository(BookRepository repository) {
            this.bookRepository = repository;
            return this;
        }

        public Builder withMemberRepository(MemberRepository repository) {
            this.memberRepository = repository;
            return this;
        }

        public Builder withBorrowRecordRepository(BorrowRecordRepository repository) {
            this.borrowRecordRepository = repository;
            return this;
        }

        public Builder withBorrowingRuleEngine(BorrowingRuleEngine engine) {
            this.ruleEngine = engine;
            return this;
        }

        public Builder withFineStrategy(FineCalculationStrategy strategy) {
            this.fineStrategy = strategy;
            return this;
        }

        public LibraryManagementSystem build() {
            // Use defaults if not specified
            if (bookRepository == null) bookRepository = new InMemoryBookRepository();
            if (memberRepository == null) memberRepository = new InMemoryMemberRepository();
            if (borrowRecordRepository == null) borrowRecordRepository = new InMemoryBorrowRecordRepository();
            if (ruleEngine == null) ruleEngine = BorrowingRuleEngine.createDefault();
            if (fineStrategy == null) fineStrategy = new DailyFineStrategy(BigDecimal.valueOf(0.50));

            return new LibraryManagementSystem(
                    bookRepository,
                    memberRepository,
                    borrowRecordRepository,
                    ruleEngine,
                    fineStrategy);
        }
    }
}



