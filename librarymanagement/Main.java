package librarymanagement;

import librarymanagement.enums.MemberType;
import librarymanagement.models.*;
import librarymanagement.observers.*;
import librarymanagement.strategies.borrowing.*;
import librarymanagement.strategies.fine.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Main class demonstrating the Library Management System functionality.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("           LIBRARY MANAGEMENT SYSTEM - DEMONSTRATION");
        System.out.println("=".repeat(70));
        System.out.println();

        // Create the library system with custom configuration
        LibraryManagementSystem library = LibraryManagementSystem.builder()
                .withFineStrategy(new TieredFineStrategy())
                .withBorrowingRuleEngine(BorrowingRuleEngine.createDefault())
                .build();

        // Subscribe to notifications
        library.subscribeToEvents(new EmailNotificationObserver());

        try {
            // ============ 1. Add Books to Catalog ============
            System.out.println("1. ADDING BOOKS TO CATALOG");
            System.out.println("-".repeat(50));

            Book book1 = library.addBook("978-0-13-468599-1", "Clean Code", "Robert C. Martin", 2008);
            System.out.println("Added: " + book1);

            Book book2 = library.addBook("978-0-201-63361-0", "Design Patterns", "Gang of Four", 1994);
            System.out.println("Added: " + book2);

            Book book3 = library.addBook("978-0-596-51774-8", "JavaScript: The Good Parts", "Douglas Crockford", 2008);
            System.out.println("Added: " + book3);

            System.out.println();

            // ============ 2. Add Book Copies ============
            System.out.println("2. ADDING PHYSICAL COPIES");
            System.out.println("-".repeat(50));

            BookCopy copy1 = library.addBookCopy("978-0-13-468599-1", "Shelf A-1");
            System.out.println("Added copy: " + copy1);

            BookCopy copy2 = library.addBookCopy("978-0-13-468599-1", "Shelf A-2");
            System.out.println("Added copy: " + copy2);

            BookCopy copy3 = library.addBookCopy("978-0-201-63361-0", "Shelf B-1");
            System.out.println("Added copy: " + copy3);

            BookCopy copy4 = library.addBookCopy("978-0-596-51774-8", "Shelf C-1");
            System.out.println("Added copy: " + copy4);

            System.out.println();

            // ============ 3. Register Members ============
            System.out.println("3. REGISTERING MEMBERS");
            System.out.println("-".repeat(50));

            Member member1 = library.registerMember("Alice Johnson", "alice@example.com", MemberType.STANDARD);
            System.out.println("Registered: " + member1);

            Member member2 = library.registerMember("Bob Smith", "bob@example.com", MemberType.PREMIUM);
            System.out.println("Registered: " + member2);

            Member member3 = library.registerMember("Charlie Brown", "charlie@example.com", MemberType.STUDENT);
            System.out.println("Registered: " + member3);

            System.out.println();

            // ============ 4. Borrow Books ============
            System.out.println("4. BORROWING BOOKS");
            System.out.println("-".repeat(50));

            BorrowRecord borrow1 = library.borrowBook(member1.getMemberId(), copy1.getCopyId());
            System.out.println("Borrow created: " + borrow1);

            BorrowRecord borrow2 = library.borrowBook(member1.getMemberId(), copy3.getCopyId());
            System.out.println("Borrow created: " + borrow2);

            BorrowRecord borrow3 = library.borrowBook(member2.getMemberId(), copy4.getCopyId());
            System.out.println("Borrow created: " + borrow3);

            System.out.println();

            // ============ 5. Check Active Borrows ============
            System.out.println("5. CHECKING ACTIVE BORROWS");
            System.out.println("-".repeat(50));

            System.out.println("Alice's active borrows: " + library.getActiveBorrowCount(member1.getMemberId()));
            List<BorrowRecord> aliceBorrows = library.getActiveBorrows(member1.getMemberId());
            for (BorrowRecord record : aliceBorrows) {
                System.out.println("  - " + record);
            }

            System.out.println("Bob's active borrows: " + library.getActiveBorrowCount(member2.getMemberId()));

            System.out.println();

            // ============ 6. Search Books ============
            System.out.println("6. SEARCHING BOOKS");
            System.out.println("-".repeat(50));

            System.out.println("Search for 'Clean':");
            List<Book> searchResults = library.searchBooks("Clean");
            for (Book book : searchResults) {
                System.out.println("  - " + book);
            }

            System.out.println("Search for 'Martin':");
            searchResults = library.searchBooks("Martin");
            for (Book book : searchResults) {
                System.out.println("  - " + book);
            }

            System.out.println();

            // ============ 7. Check Available Copies ============
            System.out.println("7. CHECKING AVAILABLE COPIES");
            System.out.println("-".repeat(50));

            String cleanCodeIsbn = "978-0-13-468599-1";
            List<BookCopy> availableCopies = library.getAvailableCopies(cleanCodeIsbn);
            System.out.println("Available copies of 'Clean Code': " + availableCopies.size());
            for (BookCopy copy : availableCopies) {
                System.out.println("  - " + copy);
            }

            System.out.println();

            // ============ 8. Return Books ============
            System.out.println("8. RETURNING BOOKS");
            System.out.println("-".repeat(50));

            BorrowRecord returned = library.returnBook(borrow1.getRecordId());
            System.out.println("Returned: " + returned);
            System.out.println("Return date: " + returned.getReturnDate());
            System.out.println("Was overdue: " + returned.isOverdue());
            System.out.println("Fine amount: $" + returned.getFineAmount());

            System.out.println();

            // ============ 9. Verify Updated Availability ============
            System.out.println("9. VERIFYING UPDATED AVAILABILITY");
            System.out.println("-".repeat(50));

            availableCopies = library.getAvailableCopies(cleanCodeIsbn);
            System.out.println("Available copies of 'Clean Code' after return: " + availableCopies.size());

            System.out.println("Alice's active borrows after return: " + library.getActiveBorrowCount(member1.getMemberId()));

            System.out.println();

            // ============ 10. Test Borrowing Limits ============
            System.out.println("10. TESTING BORROWING LIMITS");
            System.out.println("-".repeat(50));

            System.out.println("Charlie is a STUDENT member with max " + member3.getMaxBooksAllowed() + " books");

            // Borrow up to limit for student
            library.borrowBook(member3.getMemberId(), copy1.getCopyId());
            System.out.println("Charlie borrowed book 1");

            library.borrowBook(member3.getMemberId(), copy2.getCopyId());
            System.out.println("Charlie borrowed book 2");

            // Try to return and borrow again
            BorrowRecord charlieBorrow3 = library.borrowBook(member3.getMemberId(), copy3.getCopyId());
            System.out.println("Charlie borrowed book 3");

            System.out.println("Charlie's current borrows: " + library.getActiveBorrowCount(member3.getMemberId()));

            // Try to exceed limit
            try {
                library.borrowBook(member3.getMemberId(), copy4.getCopyId());
                System.out.println("ERROR: Should not have been able to borrow!");
            } catch (Exception e) {
                System.out.println("Correctly blocked: " + e.getMessage());
            }

            System.out.println();

            // ============ 11. Member Types and Limits ============
            System.out.println("11. MEMBER TYPES AND LIMITS");
            System.out.println("-".repeat(50));

            for (MemberType type : MemberType.values()) {
                System.out.printf("%-12s: Max Books = %2d, Loan Duration = %2d days%n",
                        type.name(), type.getMaxBooks(), type.getLoanDurationDays());
            }

            System.out.println();

            // ============ 12. Summary ============
            System.out.println("12. SYSTEM SUMMARY");
            System.out.println("-".repeat(50));

            System.out.println("Total books in catalog: " + library.getAllBooks().size());
            System.out.println("Total members: " + library.getAllMembers().size());
            System.out.println("Overdue records: " + library.getOverdueRecords().size());

            System.out.println();
            System.out.println("=".repeat(70));
            System.out.println("                    DEMONSTRATION COMPLETE");
            System.out.println("=".repeat(70));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



