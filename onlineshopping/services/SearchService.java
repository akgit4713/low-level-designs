package onlineshopping.services;

import onlineshopping.models.Product;
import onlineshopping.strategies.search.SearchCriteria;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for product search
 */
public interface SearchService {
    
    /**
     * Search products by keyword
     */
    List<Product> searchByKeyword(String keyword);
    
    /**
     * Search with full criteria
     */
    List<Product> search(SearchCriteria criteria);
    
    /**
     * Search by category
     */
    List<Product> searchByCategory(String categoryId);
    
    /**
     * Search by price range
     */
    List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get featured products
     */
    List<Product> getFeaturedProducts(int limit);
    
    /**
     * Get top rated products
     */
    List<Product> getTopRatedProducts(int limit);
    
    /**
     * Get new arrivals
     */
    List<Product> getNewArrivals(int limit);
}



