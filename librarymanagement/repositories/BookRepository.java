package librarymanagement.repositories;

import librarymanagement.models.Book;
import librarymanagement.models.BookCopy;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for book operations.
 * Follows Interface Segregation Principle - focused on book-related operations only.
 */
public interface BookRepository {
    
    // Book (template) operations
    void saveBook(Book book);
    Optional<Book> findBookByIsbn(String isbn);
    List<Book> findAllBooks();
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    void deleteBook(String isbn);
    boolean existsByIsbn(String isbn);

    // Book copy operations
    void saveBookCopy(BookCopy bookCopy);
    Optional<BookCopy> findBookCopyById(String copyId);
    List<BookCopy> findCopiesByIsbn(String isbn);
    List<BookCopy> findAvailableCopiesByIsbn(String isbn);
    List<BookCopy> findAllBookCopies();
    void deleteBookCopy(String copyId);
    int countAvailableCopies(String isbn);
}



