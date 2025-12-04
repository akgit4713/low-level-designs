package librarymanagement.repositories.impl;

import librarymanagement.enums.BookStatus;
import librarymanagement.models.Book;
import librarymanagement.models.BookCopy;
import librarymanagement.repositories.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of BookRepository.
 * Uses ConcurrentHashMap for thread-safe operations.
 */
public class InMemoryBookRepository implements BookRepository {
    
    private final ConcurrentHashMap<String, Book> books = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BookCopy> bookCopies = new ConcurrentHashMap<>();

    @Override
    public void saveBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    @Override
    public List<Book> findAllBooks() {
        return new ArrayList<>(books.values());
    }

    @Override
    public List<Book> searchByTitle(String title) {
        String lowerTitle = title.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        String lowerAuthor = author.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(lowerAuthor))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBook(String isbn) {
        books.remove(isbn);
        // Also remove all copies of this book
        bookCopies.values().removeIf(copy -> copy.getBook().getIsbn().equals(isbn));
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return books.containsKey(isbn);
    }

    @Override
    public void saveBookCopy(BookCopy bookCopy) {
        bookCopies.put(bookCopy.getCopyId(), bookCopy);
    }

    @Override
    public Optional<BookCopy> findBookCopyById(String copyId) {
        return Optional.ofNullable(bookCopies.get(copyId));
    }

    @Override
    public List<BookCopy> findCopiesByIsbn(String isbn) {
        return bookCopies.values().stream()
                .filter(copy -> copy.getBook().getIsbn().equals(isbn))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookCopy> findAvailableCopiesByIsbn(String isbn) {
        return bookCopies.values().stream()
                .filter(copy -> copy.getBook().getIsbn().equals(isbn))
                .filter(BookCopy::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookCopy> findAllBookCopies() {
        return new ArrayList<>(bookCopies.values());
    }

    @Override
    public void deleteBookCopy(String copyId) {
        bookCopies.remove(copyId);
    }

    @Override
    public int countAvailableCopies(String isbn) {
        return (int) bookCopies.values().stream()
                .filter(copy -> copy.getBook().getIsbn().equals(isbn))
                .filter(copy -> copy.getStatus() == BookStatus.AVAILABLE)
                .count();
    }
}



