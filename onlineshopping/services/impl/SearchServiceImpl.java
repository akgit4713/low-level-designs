package onlineshopping.services.impl;

import onlineshopping.models.Product;
import onlineshopping.repositories.impl.InMemoryProductRepository;
import onlineshopping.services.SearchService;
import onlineshopping.strategies.search.KeywordSearchStrategy;
import onlineshopping.strategies.search.SearchCriteria;
import onlineshopping.strategies.search.SearchStrategy;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SearchService
 */
public class SearchServiceImpl implements SearchService {
    
    private final InMemoryProductRepository productRepository;
    private final SearchStrategy searchStrategy;

    public SearchServiceImpl(InMemoryProductRepository productRepository) {
        this.productRepository = productRepository;
        this.searchStrategy = new KeywordSearchStrategy();
    }

    @Override
    public List<Product> searchByKeyword(String keyword) {
        SearchCriteria criteria = SearchCriteria.builder()
            .keyword(keyword)
            .build();
        return search(criteria);
    }

    @Override
    public List<Product> search(SearchCriteria criteria) {
        List<Product> allProducts = productRepository.findActive();
        return searchStrategy.search(allProducts, criteria);
    }

    @Override
    public List<Product> searchByCategory(String categoryId) {
        SearchCriteria criteria = SearchCriteria.builder()
            .categoryId(categoryId)
            .build();
        return search(criteria);
    }

    @Override
    public List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        SearchCriteria criteria = SearchCriteria.builder()
            .priceRange(minPrice, maxPrice)
            .sortBy(SearchCriteria.SortBy.PRICE)
            .ascending(true)
            .build();
        return search(criteria);
    }

    @Override
    public List<Product> getFeaturedProducts(int limit) {
        // Featured products: top rated with at least some reviews
        return productRepository.findActive().stream()
            .filter(p -> p.getReviewCount() > 0)
            .sorted(Comparator.comparingDouble(Product::getAverageRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<Product> getTopRatedProducts(int limit) {
        SearchCriteria criteria = SearchCriteria.builder()
            .sortBy(SearchCriteria.SortBy.RATING)
            .ascending(false)
            .pageSize(limit)
            .build();
        return search(criteria);
    }

    @Override
    public List<Product> getNewArrivals(int limit) {
        SearchCriteria criteria = SearchCriteria.builder()
            .sortBy(SearchCriteria.SortBy.NEWEST)
            .ascending(false)
            .pageSize(limit)
            .build();
        return search(criteria);
    }
}



