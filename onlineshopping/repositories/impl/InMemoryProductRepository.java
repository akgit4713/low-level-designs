package onlineshopping.repositories.impl;

import onlineshopping.enums.ProductStatus;
import onlineshopping.models.Category;
import onlineshopping.models.Product;
import onlineshopping.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of product repository
 */
public class InMemoryProductRepository implements Repository<Product, String> {
    
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final Map<String, Category> categories = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public boolean deleteById(String id) {
        return products.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    @Override
    public long count() {
        return products.size();
    }

    /**
     * Find products by category
     */
    public List<Product> findByCategory(String categoryId) {
        return products.values().stream()
            .filter(p -> p.getCategory().getId().equals(categoryId))
            .collect(Collectors.toList());
    }

    /**
     * Find products by seller
     */
    public List<Product> findBySeller(String sellerId) {
        return products.values().stream()
            .filter(p -> p.getSellerId().equals(sellerId))
            .collect(Collectors.toList());
    }

    /**
     * Find active products
     */
    public List<Product> findActive() {
        return products.values().stream()
            .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    /**
     * Search products by name
     */
    public List<Product> searchByName(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return products.values().stream()
            .filter(p -> p.getName().toLowerCase().contains(lowerKeyword) ||
                        p.getDescription().toLowerCase().contains(lowerKeyword))
            .collect(Collectors.toList());
    }

    // Category management
    
    public Category saveCategory(Category category) {
        categories.put(category.getId(), category);
        return category;
    }

    public Optional<Category> findCategoryById(String id) {
        return Optional.ofNullable(categories.get(id));
    }

    public List<Category> findAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public List<Category> findRootCategories() {
        return categories.values().stream()
            .filter(c -> c.getParent().isEmpty())
            .collect(Collectors.toList());
    }

    public boolean deleteCategoryById(String id) {
        return categories.remove(id) != null;
    }
}



