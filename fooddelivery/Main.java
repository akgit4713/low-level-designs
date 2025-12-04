package fooddelivery;

import fooddelivery.enums.*;
import fooddelivery.models.*;
import fooddelivery.services.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Main class demonstrating the Food Delivery System usage.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        FOOD DELIVERY SYSTEM - SWIGGY CLONE DEMO              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        // Initialize the system
        FoodDeliverySystem system = new FoodDeliverySystem();
        
        try {
            runDemo(system);
        } finally {
            system.shutdown();
        }
    }
    
    private static void runDemo(FoodDeliverySystem system) {
        // Get services
        CustomerService customerService = system.getCustomerService();
        RestaurantService restaurantService = system.getRestaurantService();
        CartService cartService = system.getCartService();
        OrderService orderService = system.getOrderService();
        DeliveryService deliveryService = system.getDeliveryService();
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 1: SETUP - Register customers, restaurants, and delivery agents
        // ═══════════════════════════════════════════════════════════════════
        printSection("1. SYSTEM SETUP");
        
        // Register customers
        User customer1 = customerService.registerCustomer(
            "Rahul Sharma", "rahul@example.com", "+91-9876543210");
        System.out.println("✓ Customer registered: " + customer1);
        
        Location customerLocation = new Location(
            12.9716, 77.5946, "123 Koramangala", "Bangalore", "560034");
        customerService.addDeliveryAddress(customer1.getId(), customerLocation);
        
        // Register restaurant owner and create restaurant
        Location restaurantLocation = new Location(
            12.9766, 77.5993, "HSR Layout Main Road", "Bangalore", "560102");
        Restaurant restaurant = restaurantService.registerRestaurant(
            "Biryani Blues", "owner-001", restaurantLocation);
        System.out.println("✓ Restaurant registered: " + restaurant);
        
        // Register observers for notifications
        system.registerCustomerObserver(customer1.getId());
        system.registerRestaurantObserver(restaurant.getId());
        
        // Add menu items
        MenuItem biryani = restaurantService.addMenuItem(
            restaurant.getId(), "Chicken Biryani", "Aromatic basmati rice with tender chicken",
            new BigDecimal("299.00"), CuisineType.INDIAN);
        
        MenuItem kebab = restaurantService.addMenuItem(
            restaurant.getId(), "Seekh Kebab", "Juicy minced meat kebabs",
            new BigDecimal("199.00"), CuisineType.INDIAN);
        
        MenuItem naan = restaurantService.addMenuItem(
            restaurant.getId(), "Butter Naan", "Soft tandoor-baked bread",
            new BigDecimal("49.00"), CuisineType.INDIAN);
        
        MenuItem raita = restaurantService.addMenuItem(
            restaurant.getId(), "Raita", "Cool yogurt with cucumber",
            new BigDecimal("59.00"), CuisineType.INDIAN);
        
        System.out.println("✓ Menu items added: " + restaurantService.getMenu(restaurant.getId()).size() + " items");
        
        // Open restaurant
        restaurantService.updateRestaurantStatus(restaurant.getId(), RestaurantStatus.OPEN);
        System.out.println("✓ Restaurant is now OPEN");
        
        // Register delivery agents
        DeliveryAgent agent1 = deliveryService.registerAgent(
            "Suresh Kumar", "suresh@delivery.com", "+91-9999888877", "KA-01-AB-1234");
        deliveryService.updateAgentStatus(agent1.getId(), AgentStatus.AVAILABLE);
        deliveryService.updateAgentLocation(agent1.getId(), new Location(
            12.9746, 77.5970, "Near HSR Layout", "Bangalore", "560102"));
        System.out.println("✓ Delivery agent registered: " + agent1);
        system.registerDeliveryAgentObserver(agent1.getId());
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 2: CUSTOMER BROWSING AND CART
        // ═══════════════════════════════════════════════════════════════════
        printSection("2. BROWSING & CART");
        
        // Search restaurants
        List<Restaurant> nearbyRestaurants = restaurantService.searchRestaurants(customerLocation);
        System.out.println("Found " + nearbyRestaurants.size() + " nearby restaurants");
        
        // View menu
        List<MenuItem> menu = restaurantService.getAvailableMenu(restaurant.getId());
        System.out.println("\n📋 Menu at " + restaurant.getName() + ":");
        menu.forEach(item -> System.out.println("   - " + item.getName() + " : ₹" + item.getPrice()));
        
        // Add items to cart
        System.out.println("\n🛒 Adding items to cart...");
        cartService.addToCart(customer1.getId(), biryani, 2);
        cartService.addToCart(customer1.getId(), kebab, 1);
        cartService.addToCart(customer1.getId(), naan, 3);
        cartService.addToCart(customer1.getId(), raita, 2);
        
        Cart cart = cartService.getOrCreateCart(customer1.getId());
        System.out.println("Cart contents:");
        cart.getItems().forEach(item -> 
            System.out.println("   - " + item.getMenuItemName() + " x" + item.getQuantity() + 
                             " = ₹" + item.getSubtotal()));
        System.out.println("   Subtotal: ₹" + cart.getSubtotal());
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 3: PLACE ORDER
        // ═══════════════════════════════════════════════════════════════════
        printSection("3. ORDER PLACEMENT");
        
        System.out.println("Placing order with UPI payment...\n");
        Order order = orderService.placeOrder(
            customer1.getId(), customerLocation, PaymentMethod.UPI);
        
        System.out.println("\n📦 Order Details:");
        System.out.println("   Order ID: " + order.getId());
        System.out.println("   Status: " + order.getStatus());
        System.out.println("   Subtotal: ₹" + order.getSubtotal());
        System.out.println("   Delivery Fee: ₹" + order.getDeliveryFee());
        System.out.println("   Tax: ₹" + order.getTaxAmount());
        System.out.println("   Total: ₹" + order.getTotalAmount());
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 4: ORDER LIFECYCLE
        // ═══════════════════════════════════════════════════════════════════
        printSection("4. ORDER LIFECYCLE");
        
        // Restaurant confirms order
        System.out.println("\n[Restaurant Action] Confirming order...");
        orderService.confirmOrder(order.getId());
        
        // Restaurant starts preparing
        System.out.println("\n[Restaurant Action] Starting preparation...");
        orderService.startPreparing(order.getId());
        
        // Simulate preparation time
        sleep(500);
        
        // Order ready for pickup
        System.out.println("\n[Restaurant Action] Order ready for pickup...");
        orderService.markReady(order.getId());
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 5: DELIVERY
        // ═══════════════════════════════════════════════════════════════════
        printSection("5. DELIVERY TRACKING");
        
        // Delivery agent picks up order
        System.out.println("\n[Delivery Action] Picking up order...");
        orderService.markOutForDelivery(order.getId());
        
        // Track delivery with location updates
        Delivery delivery = deliveryService.getDeliveryByOrderId(order.getId()).orElse(null);
        if (delivery != null) {
            System.out.println("\n🚴 Delivery Tracking:");
            System.out.println("   Delivery ID: " + delivery.getId());
            System.out.println("   Agent: " + delivery.getAgentId());
            System.out.println("   Distance: " + String.format("%.2f km", delivery.getEstimatedDistanceKm()));
            System.out.println("   ETA: " + delivery.getEstimatedTimeMinutes() + " minutes");
            
            // Simulate delivery agent movement
            System.out.println("\n   Simulating delivery progress...");
            deliveryService.updateDeliveryLocation(delivery.getId(), 
                new Location(12.9730, 77.5960, "On the way", "Bangalore", "560034"));
            System.out.println("   📍 Agent en route...");
            
            sleep(300);
        }
        
        // Complete delivery
        System.out.println("\n[Delivery Action] Completing delivery...");
        orderService.markDelivered(order.getId());
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 6: RATING & REVIEW
        // ═══════════════════════════════════════════════════════════════════
        printSection("6. RATING & REVIEW");
        
        // Rate the order
        orderService.rateOrder(order.getId(), 4.5, "Great biryani! Delivery was quick.");
        restaurantService.addRating(restaurant.getId(), 4.5);
        deliveryService.rateAgent(agent1.getId(), 5.0);
        
        Order completedOrder = orderService.getOrderById(order.getId()).orElse(null);
        if (completedOrder != null) {
            System.out.println("✓ Order rated: " + completedOrder.getCustomerRating() + "/5");
            System.out.println("  Review: \"" + completedOrder.getCustomerReview() + "\"");
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // STEP 7: ORDER HISTORY
        // ═══════════════════════════════════════════════════════════════════
        printSection("7. ORDER HISTORY");
        
        List<Order> customerOrders = orderService.getOrdersByCustomer(customer1.getId());
        System.out.println("Customer order history: " + customerOrders.size() + " order(s)");
        customerOrders.forEach(o -> 
            System.out.println("   - Order " + o.getId() + " | " + o.getStatus() + " | ₹" + o.getTotalAmount()));
        
        // ═══════════════════════════════════════════════════════════════════
        // DEMO COMPLETE
        // ═══════════════════════════════════════════════════════════════════
        printSection("DEMO COMPLETE");
        System.out.println("✅ Food Delivery System demonstration completed successfully!");
        System.out.println("\nKey features demonstrated:");
        System.out.println("   • Customer registration & address management");
        System.out.println("   • Restaurant registration & menu management");
        System.out.println("   • Cart operations");
        System.out.println("   • Order placement with payment processing");
        System.out.println("   • Real-time notifications (Observer pattern)");
        System.out.println("   • Delivery agent assignment (Strategy pattern)");
        System.out.println("   • Order lifecycle management");
        System.out.println("   • Delivery tracking");
        System.out.println("   • Rating system");
    }
    
    private static void printSection(String title) {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println(" " + title);
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}



