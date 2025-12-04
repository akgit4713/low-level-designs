package onlineshopping.strategies.search;

import onlineshopping.models.Product;

import java.util.List;

/**
 * Strategy interface for product search
 */
public interface SearchStrategy {
    
    /**
     * Search products based on criteria
     * @param products list of all products to search in
     * @param criteria search criteria
     * @return filtered list of products
     */
    List<Product> search(List<Product> products, SearchCriteria criteria);
}



