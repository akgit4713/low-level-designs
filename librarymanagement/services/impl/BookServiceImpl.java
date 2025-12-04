package librarymanagement.services.impl;

import librarymanagement.exceptions.BookNotFoundException;
import librarymanagement.exceptions.LibraryException;
import librarymanagement.models.Book;
import librarymanagement.models.BookCopy;
import librarymanagement.observers.EventPublisher;
import librarymanagement.observers.LibraryEvent;
import librarymanagement.repositories.BookRepository;
import librarymanagement.services.BookService;
import librarymanagement.strategies.search.CompositeSearchStrategy;
import librarymanagement.strategies.search.SearchStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of BookService with thread-safe operations.
 */
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;
    private final SearchStrategy searchStrategy;
    private final Object bookLock = new Object();

    public BookServiceImpl(BookRepository bookRepository, EventPublisher eventPublisher) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
        this.searchStrategy = new CompositeSearchStrategy();
    }

    public BookServiceImpl(BookRepository bookRepository, EventPublisher eventPublisher, 
                           SearchStrategy searchStrategy) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
        this.searchStrategy = searchStrategy;
    }

    @Override
    public Book addBook(String isbn, String title, String author, int publicationYear) {
        synchronized (bookLock) {
            if (bookRepository.existsByIsbn(isbn)) {
                throw new LibraryException("Book with ISBN " + isbn + " already exists");
            }
            
            Book book = new Book(isbn, title, author, publicationYear);
            bookRepository.saveBook(book);
            
            eventPublisher.publish(new LibraryEvent(
                    LibraryEvent.EventType.BOOK_ADDED,
                    isbn,
                    "New book added: " + title + " by " + author));
            
            return book;
        }
    }

    @Override
    public Book updateBook(String isbn, String title, String author, int publicationYear) {
        synchronized (bookLock) {
            Book book = bookRepository.findBookByIsbn(isbn)
                    .orElseThrow(() -> new BookNotFoundException(isbn));
            
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublicationYear(publicationYear);
            bookRepository.saveBook(book);
            
            return book;
        }
    }

    @Override
    public void removeBook(String isbn) {
        synchronized (bookLock) {
            Book book = bookRepository.findBookByIsbn(isbn)
                    .orElseThrow(() -> new BookNotFoundException(isbn));
            
            bookRepository.deleteBook(isbn);
            
            eventPublisher.publish(new LibraryEvent(
                    LibraryEvent.EventType.BOOK_REMOVED,
                    isbn,
                    "Book removed: " + book.getTitle()));
        }
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findBookByIsbn(isbn);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAllBooks();
    }

    @Override
    public List<Book> searchBooks(String query) {
        return searchStrategy.search(bookRepository.findAllBooks(), query);
    }

    @Override
    public List<Book> searchByTitle(String title) {
        return bookRepository.searchByTitle(title);
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        return bookRepository.searchByAuthor(author);
    }

    @Override
    public BookCopy addBookCopy(String isbn) {
        return addBookCopy(isbn, null);
    }

    @Override
    public BookCopy addBookCopy(String isbn, String rackLocation) {
        synchronized (bookLock) {
            Book book = bookRepository.findBookByIsbn(isbn)
                    .orElseThrow(() -> new BookNotFoundException(isbn));
            
            BookCopy copy = new BookCopy(book);
            if (rackLocation != null) {
                copy.setRackLocation(rackLocation);
            }
            
            bookRepository.saveBookCopy(copy);
            return copy;
        }
    }

    @Override
    public void removeBookCopy(String copyId) {
        synchronized (bookLock) {
            BookCopy copy = bookRepository.findBookCopyById(copyId)
                    .orElseThrow(() -> new BookNotFoundException("Book copy not found: " + copyId));
            
            bookRepository.deleteBookCopy(copyId);
        }
    }

    @Override
    public Optional<BookCopy> findBookCopyById(String copyId) {
        return bookRepository.findBookCopyById(copyId);
    }

    @Override
    public List<BookCopy> getAvailableCopies(String isbn) {
        return bookRepository.findAvailableCopiesByIsbn(isbn);
    }

    @Override
    public int getAvailableCopyCount(String isbn) {
        return bookRepository.countAvailableCopies(isbn);
    }

    @Override
    public List<BookCopy> getAllBookCopies() {
        return bookRepository.findAllBookCopies();
    }
}



