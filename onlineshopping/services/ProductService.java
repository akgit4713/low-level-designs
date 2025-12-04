package onlineshopping.services;

import onlineshopping.models.Category;
import onlineshopping.models.Product;
import onlineshopping.models.Review;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for product management
 */
public interface ProductService {
    
    /**
     * Add a new product
     */
    Product addProduct(Product product);
    
    /**
     * Get product by ID
     */
    Optional<Product> getProduct(String productId);
    
    /**
     * Update product
     */
    Product updateProduct(Product product);
    
    /**
     * Delete product
     */
    void deleteProduct(String productId);
    
    /**
     * Get all active products
     */
    List<Product> getActiveProducts();
    
    /**
     * Get products by category
     */
    List<Product> getProductsByCategory(String categoryId);
    
    /**
     * Get products by seller
     */
    List<Product> getProductsBySeller(String sellerId);
    
    // Category operations
    
    /**
     * Create category
     */
    Category createCategory(String id, String name, String description, Category parent);
    
    /**
     * Get category by ID
     */
    Optional<Category> getCategory(String categoryId);
    
    /**
     * Get all root categories
     */
    List<Category> getRootCategories();
    
    /**
     * Get all categories
     */
    List<Category> getAllCategories();
    
    // Review operations
    
    /**
     * Add review
     */
    Review addReview(String productId, String userId, String userName, int rating, String title, String comment);
    
    /**
     * Get reviews for product
     */
    List<Review> getProductReviews(String productId);
}



