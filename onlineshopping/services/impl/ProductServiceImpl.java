package onlineshopping.services.impl;

import onlineshopping.exceptions.ProductException;
import onlineshopping.models.Category;
import onlineshopping.models.Product;
import onlineshopping.models.Review;
import onlineshopping.repositories.impl.InMemoryProductRepository;
import onlineshopping.repositories.impl.InMemoryReviewRepository;
import onlineshopping.services.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ProductService
 */
public class ProductServiceImpl implements ProductService {
    
    private final InMemoryProductRepository productRepository;
    private final InMemoryReviewRepository reviewRepository;

    public ProductServiceImpl(InMemoryProductRepository productRepository,
                              InMemoryReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Product addProduct(Product product) {
        if (productRepository.existsById(product.getId())) {
            throw ProductException.duplicateProduct(product.getId());
        }
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProduct(String productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw ProductException.notFound(product.getId());
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String productId) {
        if (!productRepository.deleteById(productId)) {
            throw ProductException.notFound(productId);
        }
    }

    @Override
    public List<Product> getActiveProducts() {
        return productRepository.findActive();
    }

    @Override
    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    @Override
    public List<Product> getProductsBySeller(String sellerId) {
        return productRepository.findBySeller(sellerId);
    }

    @Override
    public Category createCategory(String id, String name, String description, Category parent) {
        Category category = new Category(id, name, description, parent);
        return productRepository.saveCategory(category);
    }

    @Override
    public Optional<Category> getCategory(String categoryId) {
        return productRepository.findCategoryById(categoryId);
    }

    @Override
    public List<Category> getRootCategories() {
        return productRepository.findRootCategories();
    }

    @Override
    public List<Category> getAllCategories() {
        return productRepository.findAllCategories();
    }

    @Override
    public Review addReview(String productId, String userId, String userName, 
                            int rating, String title, String comment) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> ProductException.notFound(productId));
        
        // Check if user already reviewed
        if (reviewRepository.hasUserReviewedProduct(userId, productId)) {
            throw new IllegalStateException("User has already reviewed this product");
        }
        
        String reviewId = "REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Review review = new Review(reviewId, productId, userId, userName, rating, title, comment);
        
        // Update product rating
        product.updateRating(rating);
        productRepository.save(product);
        
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getProductReviews(String productId) {
        return reviewRepository.findByProduct(productId);
    }
}



