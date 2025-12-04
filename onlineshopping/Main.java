package onlineshopping;

import onlineshopping.enums.*;
import onlineshopping.models.*;

import java.math.BigDecimal;

/**
 * Demo class showing the Online Shopping System in action
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("\n");
        
        // Initialize the shopping system
        ShoppingSystem shop = new ShoppingSystem("TechMart");
        
        // ==================== Setup Categories ====================
        System.out.println("\n--- Setting up Categories ---");
        
        Category electronics = shop.createCategory("Electronics", null);
        Category laptops = shop.createCategory("Laptops", electronics);
        Category smartphones = shop.createCategory("Smartphones", electronics);
        Category clothing = shop.createCategory("Clothing", null);
        Category mensFashion = shop.createCategory("Men's Fashion", clothing);
        
        System.out.println("Categories created: " + shop.getAllCategories().size());
        
        // ==================== Register Users ====================
        System.out.println("\n--- Registering Users ---");
        
        User seller = shop.registerUser("seller@techmart.com", "Tech Seller", UserRole.SELLER);
        User customer = shop.registerUser("john@email.com", "John Doe", UserRole.CUSTOMER);
        
        // Add address to customer
        Address homeAddress = new Address(
            "123 Main Street", "New York", "NY", "10001", "USA", "555-0123", true
        );
        shop.addUserAddress(customer.getId(), homeAddress);
        
        System.out.println("Seller registered: " + seller.getName() + " (" + seller.getId() + ")");
        System.out.println("Customer registered: " + customer.getName() + " (" + customer.getId() + ")");
        
        // ==================== Add Products ====================
        System.out.println("\n--- Adding Products ---");
        
        Product laptop = shop.addProduct(Product.builder()
            .id("PROD-001")
            .sellerId(seller.getId())
            .name("MacBook Pro 14-inch M3")
            .description("Latest MacBook Pro with M3 chip, 16GB RAM, 512GB SSD")
            .category(laptops)
            .price(new BigDecimal("1999.99"))
            .originalPrice(new BigDecimal("2199.99"))
            .addAttribute("RAM", "16GB")
            .addAttribute("Storage", "512GB SSD")
            .addAttribute("Color", "Space Gray")
            .build());
        
        Product phone = shop.addProduct(Product.builder()
            .id("PROD-002")
            .sellerId(seller.getId())
            .name("iPhone 15 Pro Max")
            .description("Apple iPhone 15 Pro Max with A17 Pro chip")
            .category(smartphones)
            .price(new BigDecimal("1199.99"))
            .addAttribute("Storage", "256GB")
            .addAttribute("Color", "Titanium Blue")
            .build());
        
        Product tshirt = shop.addProduct(Product.builder()
            .id("PROD-003")
            .sellerId(seller.getId())
            .name("Premium Cotton T-Shirt")
            .description("100% organic cotton, comfortable fit")
            .category(mensFashion)
            .price(new BigDecimal("29.99"))
            .originalPrice(new BigDecimal("39.99"))
            .addAttribute("Size", "M")
            .addAttribute("Color", "Navy Blue")
            .build());
        
        System.out.println("Products added:");
        System.out.println("  - " + laptop.getName() + " ($" + laptop.getPrice() + ")");
        System.out.println("  - " + phone.getName() + " ($" + phone.getPrice() + ")");
        System.out.println("  - " + tshirt.getName() + " ($" + tshirt.getPrice() + ")");
        
        // ==================== Set Inventory ====================
        System.out.println("\n--- Setting Inventory ---");
        
        shop.setInventory("PROD-001", 50);
        shop.setInventory("PROD-002", 100);
        shop.setInventory("PROD-003", 200);
        
        System.out.println("Inventory set for all products");
        
        // ==================== Add to Cart ====================
        System.out.println("\n--- Shopping Cart Operations ---");
        
        shop.addToCart(customer.getId(), "PROD-001", 1);
        shop.addToCart(customer.getId(), "PROD-002", 2);
        shop.addToCart(customer.getId(), "PROD-003", 3);
        
        Cart cart = shop.getCart(customer.getId());
        System.out.println("Cart items: " + cart.getItemCount());
        System.out.println("Cart subtotal: $" + cart.getSubtotal());
        System.out.println("Total savings: $" + cart.getTotalSavings());
        
        // Update quantity
        shop.updateCartItemQuantity(customer.getId(), "PROD-002", 1);
        System.out.println("Updated phone quantity to 1");
        System.out.println("New cart subtotal: $" + shop.getCart(customer.getId()).getSubtotal());
        
        // ==================== Search Products ====================
        System.out.println("\n--- Search Products ---");
        
        var searchResults = shop.searchProducts("MacBook");
        System.out.println("Search for 'MacBook': " + searchResults.size() + " results");
        searchResults.forEach(p -> System.out.println("  - " + p.getName()));
        
        var categoryResults = shop.searchByCategory(electronics.getId());
        System.out.println("Products in Electronics: " + categoryResults.size());
        
        // ==================== Place Order ====================
        System.out.println("\n--- Placing Order ---");
        
        Order order = shop.placeOrder(
            customer.getId(),
            homeAddress,
            ShippingMethod.EXPRESS,
            PaymentMethod.CREDIT_CARD
        );
        
        System.out.println("Order placed successfully!");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Subtotal: $" + order.getSubtotal());
        System.out.println("Shipping: $" + order.getShippingCost());
        System.out.println("Tax: $" + order.getTax());
        System.out.println("Total: $" + order.getTotalAmount());
        System.out.println("Estimated Delivery: " + order.getEstimatedDelivery().toLocalDate());
        
        // ==================== Order Lifecycle ====================
        System.out.println("\n--- Order Processing ---");
        
        // Confirm order
        shop.confirmOrder(order.getId());
        System.out.println("Order confirmed. Status: " + shop.getOrderStatus(order.getId()));
        
        // Ship order
        String trackingNumber = "TRACK-" + System.currentTimeMillis();
        shop.shipOrder(order.getId(), trackingNumber);
        System.out.println("Order shipped. Tracking: " + trackingNumber);
        System.out.println("Status: " + shop.getOrderStatus(order.getId()));
        
        // Mark delivered
        shop.markDelivered(order.getId());
        System.out.println("Order delivered. Status: " + shop.getOrderStatus(order.getId()));
        
        // ==================== Add Reviews ====================
        System.out.println("\n--- Adding Reviews ---");
        
        Review laptopReview = shop.addReview(customer.getId(), "PROD-001", 5, 
            "Amazing laptop! The M3 chip is incredibly fast.");
        System.out.println("Review added: " + laptopReview.getStarDisplay() + " - " + laptopReview.getComment());
        
        // Check updated product rating
        Product updatedLaptop = shop.getProduct("PROD-001").get();
        System.out.println("Laptop average rating: " + updatedLaptop.getAverageRating() + 
            " (" + updatedLaptop.getReviewCount() + " reviews)");
        
        // ==================== Order History ====================
        System.out.println("\n--- Order History ---");
        
        var orders = shop.getUserOrders(customer.getId());
        System.out.println("Customer has " + orders.size() + " order(s):");
        for (Order o : orders) {
            System.out.println("  - " + o.getId() + ": $" + o.getTotalAmount() + " - " + o.getStatus());
        }
        
        // ==================== Inventory Check ====================
        System.out.println("\n--- Inventory Status ---");
        System.out.println("Laptop available: " + shop.checkAvailability("PROD-001", 1));
        System.out.println("Low stock items: " + shop.getLowStockItems().size());
        
        // ==================== Track Order ====================
        System.out.println("\n--- Order Tracking ---");
        var trackedOrder = shop.trackOrder(trackingNumber);
        trackedOrder.ifPresent(o -> {
            System.out.println("Tracking " + trackingNumber + ":");
            System.out.println("  Order: " + o.getId());
            System.out.println("  Status: " + o.getStatus());
            System.out.println("  Delivered: " + o.getDeliveredAt().orElse(null));
        });
        
        // ==================== System Status ====================
        shop.printSystemStatus();
        
        // ==================== Demo: Order Cancellation ====================
        System.out.println("--- Demo: New Order with Cancellation ---");
        
        // Add items to cart again
        shop.addToCart(customer.getId(), "PROD-003", 2);
        
        // Place another order
        Order order2 = shop.placeOrder(
            customer.getId(),
            homeAddress,
            ShippingMethod.STANDARD,
            PaymentMethod.UPI
        );
        System.out.println("Second order placed: " + order2.getId());
        
        // Cancel the order
        shop.cancelOrder(order2.getId(), "Changed my mind");
        System.out.println("Order cancelled. Status: " + shop.getOrderStatus(order2.getId()));
        
        System.out.println("\n===========================================");
        System.out.println("  Online Shopping System Demo Complete!");
        System.out.println("===========================================\n");
    }
}



