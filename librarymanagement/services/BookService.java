package librarymanagement.services;

import librarymanagement.models.Book;
import librarymanagement.models.BookCopy;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for book management operations.
 */
public interface BookService {
    
    // Book catalog operations
    Book addBook(String isbn, String title, String author, int publicationYear);
    Book updateBook(String isbn, String title, String author, int publicationYear);
    void removeBook(String isbn);
    Optional<Book> findBookByIsbn(String isbn);
    List<Book> getAllBooks();
    List<Book> searchBooks(String query);
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);

    // Book copy operations
    BookCopy addBookCopy(String isbn);
    BookCopy addBookCopy(String isbn, String rackLocation);
    void removeBookCopy(String copyId);
    Optional<BookCopy> findBookCopyById(String copyId);
    List<BookCopy> getAvailableCopies(String isbn);
    int getAvailableCopyCount(String isbn);
    List<BookCopy> getAllBookCopies();
}



