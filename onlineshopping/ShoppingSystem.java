package onlineshopping;

import onlineshopping.enums.*;
import onlineshopping.models.*;
import onlineshopping.observers.*;
import onlineshopping.repositories.impl.*;
import onlineshopping.services.*;
import onlineshopping.services.impl.*;
import onlineshopping.strategies.search.SearchCriteria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Facade class for the Online Shopping System
 * Provides a simplified interface to all subsystems
 */
public class ShoppingSystem {
    
    private final String name;
    
    // Repositories
    private final InMemoryUserRepository userRepository;
    private final InMemoryProductRepository productRepository;
    private final InMemoryCartRepository cartRepository;
    private final InMemoryOrderRepository orderRepository;
    private final InMemoryInventoryRepository inventoryRepository;
    private final InMemoryReviewRepository reviewRepository;
    
    // Services
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final SearchService searchService;

    public ShoppingSystem(String name) {
        this.name = name;
        
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.productRepository = new InMemoryProductRepository();
        this.cartRepository = new InMemoryCartRepository();
        this.orderRepository = new InMemoryOrderRepository();
        this.inventoryRepository = new InMemoryInventoryRepository();
        this.reviewRepository = new InMemoryReviewRepository();
        
        // Initialize services with dependencies
        this.inventoryService = new InventoryServiceImpl(inventoryRepository);
        this.paymentService = new PaymentServiceImpl();
        this.userService = new UserServiceImpl(userRepository);
        this.productService = new ProductServiceImpl(productRepository, reviewRepository);
        this.cartService = new CartServiceImpl(cartRepository, productRepository, inventoryService);
        this.orderService = new OrderServiceImpl(orderRepository, cartService, inventoryService, paymentService);
        this.searchService = new SearchServiceImpl(productRepository);
        
        // Register default observers
        orderService.addObserver(new EmailNotificationObserver());
        inventoryService.addObserver(new InventoryAlertObserver());
        
        System.out.println("===========================================");
        System.out.println("  " + name + " - Online Shopping System");
        System.out.println("===========================================");
    }

    public String getName() {
        return name;
    }

    // ==================== User Operations ====================
    
    public User registerUser(String email, String name, UserRole role) {
        return userService.register(email, name, "password123", role);
    }

    public User registerUser(String email, String name, String password, UserRole role) {
        return userService.register(email, name, password, role);
    }

    public Optional<User> authenticateUser(String email, String password) {
        return userService.authenticate(email, password);
    }

    public Optional<User> getUser(String userId) {
        return userService.getUser(userId);
    }

    public void addUserAddress(String userId, Address address) {
        userService.addAddress(userId, address);
    }

    // ==================== Category Operations ====================
    
    public Category createCategory(String name, Category parent) {
        String id = "CAT-" + name.toUpperCase().replace(" ", "_");
        return productService.createCategory(id, name, "", parent);
    }

    public Optional<Category> getCategory(String categoryId) {
        return productService.getCategory(categoryId);
    }

    public List<Category> getAllCategories() {
        return productService.getAllCategories();
    }

    // ==================== Product Operations ====================
    
    public Product addProduct(Product product) {
        Product saved = productService.addProduct(product);
        // Initialize inventory with 0 quantity
        inventoryService.setInventory(product.getId(), 0);
        return saved;
    }

    public Optional<Product> getProduct(String productId) {
        return productService.getProduct(productId);
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    public List<Product> getProductsBySeller(String sellerId) {
        return productService.getProductsBySeller(sellerId);
    }

    // ==================== Inventory Operations ====================
    
    public void setInventory(String productId, int quantity) {
        inventoryService.setInventory(productId, quantity);
    }

    public void addStock(String productId, int quantity) {
        inventoryService.addStock(productId, quantity);
    }

    public boolean checkAvailability(String productId, int quantity) {
        return inventoryService.checkAvailability(productId, quantity);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    // ==================== Cart Operations ====================
    
    public void addToCart(String userId, String productId, int quantity) {
        Product product = productService.getProduct(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        cartService.addToCart(userId, product, quantity);
    }

    public void updateCartItemQuantity(String userId, String productId, int quantity) {
        cartService.updateQuantity(userId, productId, quantity);
    }

    public void removeFromCart(String userId, String productId) {
        cartService.removeFromCart(userId, productId);
    }

    public Cart getCart(String userId) {
        return cartService.getCart(userId);
    }

    public List<CartItem> getCartItems(String userId) {
        return cartService.getCartItems(userId);
    }

    public void clearCart(String userId) {
        cartService.clearCart(userId);
    }

    // ==================== Order Operations ====================
    
    public Order placeOrder(String userId, Address shippingAddress, 
                            ShippingMethod shippingMethod, PaymentMethod paymentMethod) {
        return orderService.placeOrder(userId, shippingAddress, shippingMethod, paymentMethod);
    }

    public Optional<Order> getOrder(String orderId) {
        return orderService.getOrder(orderId);
    }

    public List<Order> getUserOrders(String userId) {
        return orderService.getUserOrders(userId);
    }

    public OrderStatus getOrderStatus(String orderId) {
        return orderService.getOrder(orderId)
            .map(Order::getStatus)
            .orElse(null);
    }

    public void confirmOrder(String orderId) {
        orderService.confirmOrder(orderId);
    }

    public void shipOrder(String orderId, String trackingNumber) {
        orderService.shipOrder(orderId, trackingNumber);
    }

    public void markDelivered(String orderId) {
        orderService.markDelivered(orderId);
    }

    public void cancelOrder(String orderId, String reason) {
        orderService.cancelOrder(orderId, reason);
    }

    public Optional<Order> trackOrder(String trackingNumber) {
        return orderService.getOrderByTracking(trackingNumber);
    }

    // ==================== Search Operations ====================
    
    public List<Product> searchProducts(String keyword) {
        return searchService.searchByKeyword(keyword);
    }

    public List<Product> searchProducts(SearchCriteria criteria) {
        return searchService.search(criteria);
    }

    public List<Product> searchByCategory(String categoryId) {
        return searchService.searchByCategory(categoryId);
    }

    public List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return searchService.searchByPriceRange(minPrice, maxPrice);
    }

    public List<Product> getFeaturedProducts(int limit) {
        return searchService.getFeaturedProducts(limit);
    }

    public List<Product> getNewArrivals(int limit) {
        return searchService.getNewArrivals(limit);
    }

    // ==================== Review Operations ====================
    
    public Review addReview(String userId, String productId, int rating, String comment) {
        User user = userService.getUser(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return productService.addReview(productId, userId, user.getName(), rating, "", comment);
    }

    public List<Review> getProductReviews(String productId) {
        return productService.getProductReviews(productId);
    }

    // ==================== Observer Registration ====================
    
    public void addOrderObserver(OrderObserver observer) {
        orderService.addObserver(observer);
    }

    public void addInventoryObserver(InventoryObserver observer) {
        inventoryService.addObserver(observer);
    }

    // ==================== Reporting ====================
    
    public void printSystemStatus() {
        System.out.println("\n--- System Status ---");
        System.out.println("Users: " + userRepository.count());
        System.out.println("Products: " + productRepository.count());
        System.out.println("Active Carts: " + cartRepository.count());
        System.out.println("Orders: " + orderRepository.count());
        System.out.println("Low Stock Items: " + inventoryService.getLowStockItems().size());
        System.out.println("Out of Stock Items: " + inventoryService.getOutOfStockItems().size());
        System.out.println("--------------------\n");
    }
}



