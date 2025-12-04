# Library Management System - Low-Level Design

## Overview

A comprehensive Library Management System that allows librarians to manage books, members, and borrowing activities with support for concurrent access, extensible design, and clean architecture principles.

## Requirements Summary

- Manage books, members, and borrowing activities
- Add, update, and remove books from the catalog
- Track book details: title, author, ISBN, publication year, availability
- Allow members to borrow and return books
- Track member details: name, ID, contact info, borrowing history
- Enforce borrowing rules (max books, loan duration)
- Handle concurrent access
- Extensible for future enhancements

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        LibraryManagementSystem (Façade)                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                              Services Layer                              ││
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐         ││
│  │  │   BookService   │  │  MemberService  │  │  BorrowService  │         ││
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘         ││
│  └───────────┼─────────────────────┼─────────────────────┼──────────────────┘│
│              │                     │                     │                   │
│  ┌───────────┼─────────────────────┼─────────────────────┼──────────────────┐│
│  │           ▼                     ▼                     ▼                  ││
│  │  ┌─────────────────────────────────────────────────────────────────────┐││
│  │  │                        Repository Layer                              │││
│  │  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐  │││
│  │  │  │ BookRepository  │  │ MemberRepository│  │BorrowRecordRepository│ │││
│  │  │  └─────────────────┘  └─────────────────┘  └─────────────────────┘  │││
│  │  └─────────────────────────────────────────────────────────────────────┘││
│  └──────────────────────────────────────────────────────────────────────────┘│
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────────┐│
│  │                           Cross-Cutting Concerns                          ││
│  │  ┌────────────────────────────────┐  ┌────────────────────────────────┐  ││
│  │  │        Event Publisher         │  │         Strategies             │  ││
│  │  │  (Observer Pattern)            │  │  - Fine Calculation            │  ││
│  │  │                                │  │  - Borrowing Rules             │  ││
│  │  │  - EmailNotificationObserver   │  │  - Search Strategies           │  ││
│  │  │  - AuditLogObserver            │  │                                │  ││
│  │  └────────────────────────────────┘  └────────────────────────────────┘  ││
│  └──────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                   MODELS                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌───────────────────────┐         ┌───────────────────────┐                │
│  │         Book          │         │       BookCopy        │                │
│  ├───────────────────────┤         ├───────────────────────┤                │
│  │ - isbn: String        │ 1    * │ - copyId: String      │                │
│  │ - title: String       │◄───────┤ - book: Book          │                │
│  │ - author: String      │         │ - status: BookStatus  │                │
│  │ - publicationYear: int│         │ - rackLocation: String│                │
│  │ - publisher: String   │         ├───────────────────────┤                │
│  │ - genre: String       │         │ + isAvailable(): bool │                │
│  └───────────────────────┘         └───────────────────────┘                │
│                                                                              │
│  ┌───────────────────────┐         ┌───────────────────────┐                │
│  │        Member         │         │     BorrowRecord      │                │
│  ├───────────────────────┤         ├───────────────────────┤                │
│  │ - memberId: String    │ 1    * │ - recordId: String    │                │
│  │ - name: String        │◄───────┤ - memberId: String    │                │
│  │ - email: String       │         │ - bookCopyId: String  │                │
│  │ - phone: String       │         │ - borrowDate: Date    │                │
│  │ - memberType: enum    │         │ - dueDate: Date       │                │
│  │ - status: enum        │         │ - returnDate: Date    │                │
│  │ - joinDate: Date      │         │ - status: BorrowStatus│                │
│  │ - expiryDate: Date    │         │ - fineAmount: BigDec  │                │
│  ├───────────────────────┤         ├───────────────────────┤                │
│  │ + getMaxBooksAllowed()│         │ + isOverdue(): bool   │                │
│  │ + getLoanDuration()   │         │ + getOverdueDays(): l │                │
│  │ + isActive(): bool    │         └───────────────────────┘                │
│  │ + isMembershipValid() │                                                  │
│  └───────────────────────┘         ┌───────────────────────┐                │
│                                    │         Fine          │                │
│                                    ├───────────────────────┤                │
│                                    │ - fineId: String      │                │
│                                    │ - memberId: String    │                │
│                                    │ - amount: BigDecimal  │                │
│                                    │ - paid: boolean       │                │
│                                    └───────────────────────┘                │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. **Façade Pattern** - `LibraryManagementSystem`
Provides a simplified interface to the complex subsystem of services, repositories, and strategies.

```java
// Client code only interacts with the façade
LibraryManagementSystem library = new LibraryManagementSystem();
library.addBook("978-...", "Clean Code", "Robert Martin", 2008);
library.registerMember("John", "john@example.com", MemberType.STANDARD);
library.borrowBook(memberId, bookCopyId);
```

### 2. **Strategy Pattern** - Fine Calculation & Search
Allows different algorithms to be plugged in without modifying client code.

```java
// Fine calculation strategies
FineCalculationStrategy dailyFine = new DailyFineStrategy(BigDecimal.valueOf(0.50));
FineCalculationStrategy tieredFine = new TieredFineStrategy();
FineCalculationStrategy noFine = new NoFineStrategy();

// Search strategies
SearchStrategy titleSearch = new TitleSearchStrategy();
SearchStrategy authorSearch = new AuthorSearchStrategy();
SearchStrategy compositeSearch = new CompositeSearchStrategy();
```

### 3. **Observer Pattern** - Event Notifications
Decouples event producers from consumers for notifications and logging.

```java
// Subscribe to library events
library.subscribeToEvents(new EmailNotificationObserver());
library.subscribeToEvents(new AuditLogObserver());
library.subscribeToEvents(new SMSNotificationObserver()); // Future extension
```

### 4. **Chain of Responsibility** - Borrowing Rules
Multiple rules can validate borrowing requests in sequence.

```java
BorrowingRuleEngine ruleEngine = new BorrowingRuleEngine(
    new MemberStatusRule(),
    new MembershipValidityRule(),
    new MaxBooksRule(),
    new BookAvailabilityRule()
);

// Custom rule can be added
ruleEngine.addRule(new NoOverdueBooksRule());
```

### 5. **Repository Pattern** - Data Access
Abstracts data access behind interfaces for testability and flexibility.

```java
// Can swap implementations
BookRepository inMemory = new InMemoryBookRepository();
BookRepository database = new JdbcBookRepository(); // Future extension
BookRepository cache = new CachedBookRepository(database); // Future extension
```

### 6. **Factory Pattern** - Object Creation
Centralizes object creation with validation.

```java
Book book = BookFactory.createBook(isbn, title, author, year);
Member member = MemberFactory.createStudentMember(name, email);
```

### 7. **Builder Pattern** - Complex Object Construction
Provides fluent API for configuring the library system.

```java
LibraryManagementSystem library = LibraryManagementSystem.builder()
    .withBookRepository(new InMemoryBookRepository())
    .withFineStrategy(new TieredFineStrategy())
    .withBorrowingRuleEngine(customRuleEngine)
    .build();
```

---

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
Each class has one reason to change:
- `BookService` - only handles book operations
- `MemberService` - only handles member operations
- `BorrowService` - only handles borrowing operations
- `FineCalculationStrategy` - only calculates fines
- `BorrowingRule` - only validates one borrowing condition

### Open/Closed Principle (OCP)
System is open for extension, closed for modification:
- Add new `FineCalculationStrategy` implementations without changing existing code
- Add new `BorrowingRule` implementations without changing existing code
- Add new `SearchStrategy` implementations without changing existing code
- Add new `LibraryEventObserver` implementations without changing existing code

### Liskov Substitution Principle (LSP)
All implementations are substitutable for their interfaces:
- Any `BookRepository` implementation can replace another
- Any `FineCalculationStrategy` can be used interchangeably
- Any `BorrowingRule` can be added to the rule engine

### Interface Segregation Principle (ISP)
Interfaces are focused and specific:
- `BookRepository` - book-specific operations
- `MemberRepository` - member-specific operations
- `BorrowRecordRepository` - borrow record-specific operations

### Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:
- Services depend on repository interfaces, not implementations
- `BorrowService` depends on `FineCalculationStrategy` interface
- `BorrowService` depends on `BorrowingRuleEngine` (composed of `BorrowingRule` interfaces)

---

## Concurrency Handling

### Thread-Safe Repositories
```java
// Using ConcurrentHashMap for thread-safe storage
private final ConcurrentHashMap<String, Book> books = new ConcurrentHashMap<>();
private final ConcurrentHashMap<String, Member> members = new ConcurrentHashMap<>();
```

### Fine-Grained Locking
```java
// Lock per book copy for concurrent borrow/return operations
private final ConcurrentHashMap<String, ReentrantLock> bookCopyLocks = new ConcurrentHashMap<>();

public BorrowRecord borrowBook(String memberId, String bookCopyId) {
    ReentrantLock lock = getLockForBookCopy(bookCopyId);
    lock.lock();
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
}
```

### Thread-Safe Event Publishing
```java
// Using CopyOnWriteArrayList for observers
private final List<LibraryEventObserver> observers = new CopyOnWriteArrayList<>();
```

---

## Extension Points

### 1. New Member Types
Add new values to `MemberType` enum with different limits:
```java
public enum MemberType {
    STANDARD(5, 14),
    PREMIUM(10, 21),
    STUDENT(3, 14),
    FACULTY(15, 30),
    RESEARCHER(20, 60),
    VIP(50, 90);  // New type
}
```

### 2. New Fine Strategies
Implement `FineCalculationStrategy` interface:
```java
public class HolidayAwareFineStrategy implements FineCalculationStrategy {
    @Override
    public BigDecimal calculateFine(BorrowRecord record) {
        // Skip holidays in calculation
    }
}
```

### 3. New Borrowing Rules
Implement `BorrowingRule` interface:
```java
public class NoOverdueBooksRule implements BorrowingRule {
    @Override
    public ValidationResult validate(Member member, BookCopy bookCopy, int count) {
        // Check if member has overdue books
    }
}
```

### 4. New Notification Channels
Implement `LibraryEventObserver` interface:
```java
public class SMSNotificationObserver implements LibraryEventObserver {
    @Override
    public void onEvent(LibraryEvent event) {
        // Send SMS notification
    }
}
```

### 5. Database Persistence
Implement repository interfaces:
```java
public class JdbcBookRepository implements BookRepository {
    // JDBC-based implementation
}
```

---

## File Structure

```
librarymanagement/
├── enums/
│   ├── BookStatus.java
│   ├── MemberStatus.java
│   ├── MemberType.java
│   └── BorrowStatus.java
├── exceptions/
│   ├── LibraryException.java
│   ├── BookNotFoundException.java
│   ├── MemberNotFoundException.java
│   ├── BorrowingException.java
│   ├── MaxBooksExceededException.java
│   ├── BookNotAvailableException.java
│   └── MemberNotEligibleException.java
├── models/
│   ├── Book.java
│   ├── BookCopy.java
│   ├── Member.java
│   ├── BorrowRecord.java
│   └── Fine.java
├── repositories/
│   ├── BookRepository.java
│   ├── MemberRepository.java
│   ├── BorrowRecordRepository.java
│   └── impl/
│       ├── InMemoryBookRepository.java
│       ├── InMemoryMemberRepository.java
│       └── InMemoryBorrowRecordRepository.java
├── services/
│   ├── BookService.java
│   ├── MemberService.java
│   ├── BorrowService.java
│   └── impl/
│       ├── BookServiceImpl.java
│       ├── MemberServiceImpl.java
│       └── BorrowServiceImpl.java
├── strategies/
│   ├── fine/
│   │   ├── FineCalculationStrategy.java
│   │   ├── DailyFineStrategy.java
│   │   ├── TieredFineStrategy.java
│   │   └── NoFineStrategy.java
│   ├── borrowing/
│   │   ├── BorrowingRule.java
│   │   ├── ValidationResult.java
│   │   ├── MaxBooksRule.java
│   │   ├── MemberStatusRule.java
│   │   ├── MembershipValidityRule.java
│   │   ├── BookAvailabilityRule.java
│   │   └── BorrowingRuleEngine.java
│   └── search/
│       ├── SearchStrategy.java
│       ├── TitleSearchStrategy.java
│       ├── AuthorSearchStrategy.java
│       ├── IsbnSearchStrategy.java
│       └── CompositeSearchStrategy.java
├── observers/
│   ├── LibraryEvent.java
│   ├── LibraryEventObserver.java
│   ├── EmailNotificationObserver.java
│   ├── AuditLogObserver.java
│   └── EventPublisher.java
├── factories/
│   ├── BookFactory.java
│   └── MemberFactory.java
├── LibraryManagementSystem.java
└── Main.java
```

---

## Usage Example

```java
public class Main {
    public static void main(String[] args) {
        // Create library with custom configuration
        LibraryManagementSystem library = LibraryManagementSystem.builder()
            .withFineStrategy(new TieredFineStrategy())
            .build();
        
        // Subscribe to events
        library.subscribeToEvents(new EmailNotificationObserver());
        library.subscribeToEvents(new AuditLogObserver());
        
        // Add books
        Book book = library.addBook("978-0-13-468599-1", "Clean Code", "Robert Martin", 2008);
        BookCopy copy = library.addBookCopy(book.getIsbn(), "Shelf A-1");
        
        // Register member
        Member member = library.registerMember("Alice", "alice@example.com", MemberType.STANDARD);
        
        // Borrow book
        BorrowRecord record = library.borrowBook(member.getMemberId(), copy.getCopyId());
        
        // Return book
        BorrowRecord returned = library.returnBook(record.getRecordId());
        
        // Check fines
        BigDecimal fine = library.getTotalUnpaidFines(member.getMemberId());
    }
}
```

---

## Key Design Decisions

1. **Book vs BookCopy Separation**: Separating book metadata from physical copies allows multiple copies of the same book and independent tracking of each copy's status.

2. **MemberType Enum with Limits**: Encapsulating borrowing limits in the enum makes it easy to add new member types with different privileges.

3. **Rule Engine for Validation**: Using a composable rule engine allows flexible configuration of borrowing policies without code changes.

4. **Event-Driven Notifications**: Decoupling notifications through the observer pattern allows adding new notification channels without modifying core logic.

5. **Fine-Grained Locking**: Locking at the book copy level allows concurrent borrowing of different books while preventing race conditions on the same book.

6. **Strategy Pattern for Algorithms**: Using strategies for fine calculation and search allows different algorithms to be configured at runtime.

---

## Running the Demo

```bash
# Compile
javac -d out librarymanagement/**/*.java

# Run
java -cp out librarymanagement.Main
```

The demo will show:
- Adding books and copies
- Registering members
- Borrowing and returning books
- Search functionality
- Borrowing limit enforcement
- Member type differences



