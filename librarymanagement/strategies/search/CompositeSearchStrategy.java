package librarymanagement.strategies.search;

import librarymanagement.models.Book;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Searches across multiple fields (title, author, ISBN) and returns combined results.
 */
public class CompositeSearchStrategy implements SearchStrategy {
    
    private final List<SearchStrategy> strategies;

    public CompositeSearchStrategy() {
        this.strategies = List.of(
                new TitleSearchStrategy(),
                new AuthorSearchStrategy(),
                new IsbnSearchStrategy()
        );
    }

    @Override
    public List<Book> search(List<Book> books, String query) {
        if (query == null || query.trim().isEmpty()) {
            return books;
        }
        
        Set<Book> results = new HashSet<>();
        for (SearchStrategy strategy : strategies) {
            results.addAll(strategy.search(books, query));
        }
        
        return results.stream().collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "COMPOSITE";
    }
}



