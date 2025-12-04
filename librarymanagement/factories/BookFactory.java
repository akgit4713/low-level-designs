package librarymanagement.factories;

import librarymanagement.models.Book;
import librarymanagement.models.BookCopy;

/**
 * Factory for creating Book and BookCopy instances with validation.
 */
public class BookFactory {
    
    private BookFactory() {
        // Private constructor - use static methods
    }

    /**
     * Creates a new Book with validation.
     */
    public static Book createBook(String isbn, String title, String author, int publicationYear) {
        validateIsbn(isbn);
        validateTitle(title);
        validateAuthor(author);
        validatePublicationYear(publicationYear);
        
        return new Book(isbn, title, author, publicationYear);
    }

    /**
     * Creates a new BookCopy for the given book.
     */
    public static BookCopy createBookCopy(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        return new BookCopy(book);
    }

    /**
     * Creates a new BookCopy with a specific location.
     */
    public static BookCopy createBookCopy(Book book, String rackLocation) {
        BookCopy copy = createBookCopy(book);
        if (rackLocation != null && !rackLocation.trim().isEmpty()) {
            copy.setRackLocation(rackLocation);
        }
        return copy;
    }

    private static void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        // Basic ISBN validation (10 or 13 digits, optionally with hyphens)
        String normalized = isbn.replaceAll("-", "");
        if (normalized.length() != 10 && normalized.length() != 13) {
            throw new IllegalArgumentException("ISBN must be 10 or 13 characters (excluding hyphens)");
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (title.length() > 500) {
            throw new IllegalArgumentException("Title cannot exceed 500 characters");
        }
    }

    private static void validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        if (author.length() > 200) {
            throw new IllegalArgumentException("Author name cannot exceed 200 characters");
        }
    }

    private static void validatePublicationYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        if (year < 1000 || year > currentYear + 1) {
            throw new IllegalArgumentException("Publication year must be between 1000 and " + (currentYear + 1));
        }
    }
}



