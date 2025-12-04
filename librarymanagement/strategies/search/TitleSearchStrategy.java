package librarymanagement.strategies.search;

import librarymanagement.models.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Searches books by title (case-insensitive, partial match).
 */
public class TitleSearchStrategy implements SearchStrategy {
    
    @Override
    public List<Book> search(List<Book> books, String query) {
        if (query == null || query.trim().isEmpty()) {
            return books;
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "TITLE";
    }
}



