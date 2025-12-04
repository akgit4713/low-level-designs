package librarymanagement.models;

import java.util.Objects;

/**
 * Represents a book in the library catalog.
 * This is the template/metadata for books - actual physical copies are BookCopy instances.
 */
public class Book {
    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private String publisher;
    private String genre;
    private String description;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.publicationYear = publicationYear;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    // Setters for mutable fields
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public void setAuthor(String author) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s', year=%d}", 
                isbn, title, author, publicationYear);
    }
}



