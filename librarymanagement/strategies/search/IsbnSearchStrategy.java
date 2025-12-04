package librarymanagement.strategies.search;

import librarymanagement.models.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Searches books by ISBN (exact or partial match).
 */
public class IsbnSearchStrategy implements SearchStrategy {
    
    @Override
    public List<Book> search(List<Book> books, String query) {
        if (query == null || query.trim().isEmpty()) {
            return books;
        }
        
        String normalizedQuery = query.replaceAll("-", "").trim();
        return books.stream()
                .filter(book -> book.getIsbn().replaceAll("-", "").contains(normalizedQuery))
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "ISBN";
    }
}



