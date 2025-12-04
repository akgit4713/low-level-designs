package librarymanagement.models;

import librarymanagement.enums.BookStatus;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a physical copy of a book in the library.
 * Multiple BookCopy instances can reference the same Book (ISBN).
 */
public class BookCopy {
    private final String copyId;
    private final Book book;
    private BookStatus status;
    private String rackLocation;

    public BookCopy(Book book) {
        this.copyId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.book = Objects.requireNonNull(book, "Book cannot be null");
        this.status = BookStatus.AVAILABLE;
    }

    public BookCopy(String copyId, Book book) {
        this.copyId = Objects.requireNonNull(copyId, "Copy ID cannot be null");
        this.book = Objects.requireNonNull(book, "Book cannot be null");
        this.status = BookStatus.AVAILABLE;
    }

    // Getters
    public String getCopyId() {
        return copyId;
    }

    public Book getBook() {
        return book;
    }

    public BookStatus getStatus() {
        return status;
    }

    public String getRackLocation() {
        return rackLocation;
    }

    // Setters
    public void setStatus(BookStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public void setRackLocation(String rackLocation) {
        this.rackLocation = rackLocation;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCopy bookCopy = (BookCopy) o;
        return copyId.equals(bookCopy.copyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(copyId);
    }

    @Override
    public String toString() {
        return String.format("BookCopy{copyId='%s', book=%s, status=%s, location='%s'}", 
                copyId, book.getTitle(), status, rackLocation);
    }
}



