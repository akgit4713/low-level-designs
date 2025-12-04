package onlineshopping.strategies.search;

import onlineshopping.models.Product;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Keyword-based search strategy
 */
public class KeywordSearchStrategy implements SearchStrategy {

    @Override
    public List<Product> search(List<Product> products, SearchCriteria criteria) {
        Stream<Product> stream = products.stream()
            .filter(Product::isAvailable);
        
        // Filter by keyword
        if (criteria.getKeyword().isPresent()) {
            String keyword = criteria.getKeyword().get().toLowerCase();
            stream = stream.filter(p -> matchesKeyword(p, keyword));
        }
        
        // Filter by category
        if (criteria.getCategoryId().isPresent()) {
            String categoryId = criteria.getCategoryId().get();
            stream = stream.filter(p -> 
                p.getCategory().getId().equals(categoryId) ||
                p.getCategory().isDescendantOf(
                    new onlineshopping.models.Category(categoryId, "", "", null)));
        }
        
        // Filter by price range
        if (criteria.getMinPrice().isPresent()) {
            stream = stream.filter(p -> 
                p.getPrice().compareTo(criteria.getMinPrice().get()) >= 0);
        }
        if (criteria.getMaxPrice().isPresent()) {
            stream = stream.filter(p -> 
                p.getPrice().compareTo(criteria.getMaxPrice().get()) <= 0);
        }
        
        // Filter by rating
        if (criteria.getMinRating().isPresent()) {
            stream = stream.filter(p -> 
                p.getAverageRating() >= criteria.getMinRating().get());
        }
        
        // Filter by seller
        if (criteria.getSellerId().isPresent()) {
            stream = stream.filter(p -> 
                p.getSellerId().equals(criteria.getSellerId().get()));
        }
        
        // Sort
        Comparator<Product> comparator = getComparator(criteria);
        if (!criteria.isAscending()) {
            comparator = comparator.reversed();
        }
        stream = stream.sorted(comparator);
        
        // Pagination
        stream = stream
            .skip((long) criteria.getPage() * criteria.getPageSize())
            .limit(criteria.getPageSize());
        
        return stream.collect(Collectors.toList());
    }

    private boolean matchesKeyword(Product product, String keyword) {
        return product.getName().toLowerCase().contains(keyword) ||
               product.getDescription().toLowerCase().contains(keyword) ||
               product.getCategory().getName().toLowerCase().contains(keyword);
    }

    private Comparator<Product> getComparator(SearchCriteria criteria) {
        return switch (criteria.getSortBy()) {
            case PRICE -> Comparator.comparing(Product::getPrice);
            case RATING -> Comparator.comparingDouble(Product::getAverageRating);
            case NEWEST -> Comparator.comparing(Product::getCreatedAt);
            case POPULARITY -> Comparator.comparingInt(Product::getReviewCount);
            default -> Comparator.comparing(Product::getName); // RELEVANCE
        };
    }
}



