package librarymanagement.strategies.search;

import librarymanagement.models.Book;
import java.util.List;

/**
 * Strategy interface for searching books in the catalog.
 * Allows different search implementations to be plugged in.
 */
public interface SearchStrategy {
    
    /**
     * Searches for books matching the given query.
     * 
     * @param books The list of books to search in
     * @param query The search query
     * @return List of matching books
     */
    List<Book> search(List<Book> books, String query);
    
    /**
     * Returns the name/type of this search strategy.
     */
    String getSearchType();
}



