package restaurant;

import restaurant.enums.*;
import restaurant.models.*;
import restaurant.strategies.discount.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Main demo class showcasing the Restaurant Management System
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     RESTAURANT MANAGEMENT SYSTEM - DEMO                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Initialize the restaurant
        Restaurant restaurant = new Restaurant("The Gourmet Kitchen");
        System.out.println("✓ Restaurant initialized: " + restaurant.getName());
        System.out.println();

        // Setup demo data
        setupInventory(restaurant);
        setupMenu(restaurant);
        setupTables(restaurant);
        setupStaff(restaurant);

        // Add promotional discount
        restaurant.addDiscountStrategy(
            new PercentageDiscountStrategy("Happy Hour", new BigDecimal("10"), new BigDecimal("500"))
        );

        System.out.println("\n" + "═".repeat(65));
        System.out.println("SCENARIO 1: Making a Reservation");
        System.out.println("═".repeat(65));

        // Make a reservation
        LocalDateTime reservationTime = LocalDateTime.now().plusHours(2);
        Reservation reservation = restaurant.makeReservation(
            "John Smith", "+1-555-0123", 1, reservationTime, 4
        );
        System.out.println("✓ Reservation created: " + reservation);

        System.out.println("\n" + "═".repeat(65));
        System.out.println("SCENARIO 2: Processing a Dine-in Order");
        System.out.println("═".repeat(65));

        // Get a table and menu items
        Table table = restaurant.getTable(1).orElseThrow();
        List<MenuItem> menu = restaurant.getMenu();
        System.out.println("\n📋 Available Menu Items:");
        menu.forEach(item -> System.out.println("   - " + item.getName() + " ($" + item.getPrice() + ")"));

        // Create order items
        MenuItem burger = menu.stream().filter(m -> m.getName().contains("Burger")).findFirst().orElseThrow();
        MenuItem pasta = menu.stream().filter(m -> m.getName().contains("Pasta")).findFirst().orElseThrow();
        MenuItem soda = menu.stream().filter(m -> m.getName().contains("Soda")).findFirst().orElseThrow();

        List<OrderItem> orderItems = Arrays.asList(
            new OrderItem(burger, 2, "Medium rare, no onions"),
            new OrderItem(pasta, 1, "Extra cheese"),
            new OrderItem(soda, 3)
        );

        // Place order
        Order order = restaurant.placeOrder("CUST-001", table, orderItems);
        System.out.println("\n✓ Order placed: " + order.getId());
        System.out.println("   Subtotal: $" + order.calculateSubtotal());

        // Process order through kitchen
        System.out.println("\n🔄 Processing order through kitchen...");
        restaurant.startPreparing(order.getId());

        // Simulate preparation
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        restaurant.markOrderReady(order.getId());

        // Serve order
        restaurant.serveOrder(order.getId());

        System.out.println("\n" + "═".repeat(65));
        System.out.println("SCENARIO 3: Billing and Payment");
        System.out.println("═".repeat(65));

        // Generate bill
        Bill bill = restaurant.generateBill(order.getId());
        System.out.println("\n" + bill);

        // Process payment
        Payment payment = restaurant.processPayment(
            bill.getId(),
            bill.getTotalAmount(),
            PaymentMethod.CREDIT_CARD
        );
        System.out.println("✓ Payment processed: " + payment);
        System.out.println("   Status: " + payment.getStatus());

        System.out.println("\n" + "═".repeat(65));
        System.out.println("SCENARIO 4: Takeout Order");
        System.out.println("═".repeat(65));

        // Place a takeout order
        MenuItem salad = menu.stream().filter(m -> m.getName().contains("Salad")).findFirst().orElseThrow();
        Order takeoutOrder = restaurant.placeTakeoutOrder(
            "CUST-002",
            Arrays.asList(new OrderItem(salad, 2))
        );
        System.out.println("\n✓ Takeout order placed: " + takeoutOrder.getId());

        System.out.println("\n" + "═".repeat(65));
        System.out.println("SCENARIO 5: Inventory Check");
        System.out.println("═".repeat(65));

        List<InventoryItem> lowStock = restaurant.getLowStockItems();
        System.out.println("\n📦 Low Stock Items: " + (lowStock.isEmpty() ? "None" : lowStock.size() + " items"));

        // Restock an ingredient
        System.out.println("\n🔄 Restocking tomatoes...");
        restaurant.restockIngredient("ING-002", 20);

        System.out.println("\n" + "═".repeat(65));
        System.out.println("SCENARIO 6: Reports");
        System.out.println("═".repeat(65));

        // Generate sales report
        var salesReport = restaurant.getSalesReport(
            LocalDateTime.now().minusHours(24),
            LocalDateTime.now().plusHours(1)
        );
        System.out.println("\n📊 Sales Report:");
        System.out.println("   Total Orders: " + salesReport.totalOrders());
        System.out.println("   Total Revenue: $" + salesReport.totalRevenue());
        System.out.println("   Avg Order Value: $" + salesReport.averageOrderValue());

        // Inventory report
        var inventoryReport = restaurant.getInventoryReport();
        System.out.println("\n📦 Inventory Report:");
        System.out.println("   Total Items: " + inventoryReport.totalItems());
        System.out.println("   Low Stock: " + inventoryReport.lowStockItems().size());
        System.out.println("   Total Value: $" + inventoryReport.totalInventoryValue());

        System.out.println("\n" + "═".repeat(65));
        System.out.println("                    DEMO COMPLETED SUCCESSFULLY                 ");
        System.out.println("═".repeat(65));
    }

    private static void setupInventory(Restaurant restaurant) {
        System.out.println("📦 Setting up inventory...");

        Ingredient beef = new Ingredient("ING-001", "Beef Patty", "pieces", new BigDecimal("5.00"));
        Ingredient tomato = new Ingredient("ING-002", "Tomato", "kg", new BigDecimal("2.50"));
        Ingredient lettuce = new Ingredient("ING-003", "Lettuce", "kg", new BigDecimal("1.50"));
        Ingredient cheese = new Ingredient("ING-004", "Cheese", "kg", new BigDecimal("8.00"));
        Ingredient pasta = new Ingredient("ING-005", "Pasta", "kg", new BigDecimal("3.00"));
        Ingredient chicken = new Ingredient("ING-006", "Chicken", "kg", new BigDecimal("10.00"));

        restaurant.addIngredientToInventory(beef, 50, 10, 100);
        restaurant.addIngredientToInventory(tomato, 30, 5, 50);
        restaurant.addIngredientToInventory(lettuce, 20, 5, 40);
        restaurant.addIngredientToInventory(cheese, 15, 3, 30);
        restaurant.addIngredientToInventory(pasta, 25, 5, 50);
        restaurant.addIngredientToInventory(chicken, 40, 10, 80);

        System.out.println("   ✓ 6 ingredients added to inventory");
    }

    private static void setupMenu(Restaurant restaurant) {
        System.out.println("📋 Setting up menu...");

        // Appetizers
        MenuItem caesarSalad = MenuItem.builder()
            .id("MENU-001")
            .name("Caesar Salad")
            .description("Fresh romaine lettuce with caesar dressing")
            .price(new BigDecimal("12.99"))
            .category(MenuCategory.APPETIZER)
            .preparationTimeMinutes(10)
            .vegetarian(true)
            .build();

        // Main courses
        MenuItem classicBurger = MenuItem.builder()
            .id("MENU-002")
            .name("Classic Burger")
            .description("Juicy beef patty with fresh vegetables")
            .price(new BigDecimal("18.99"))
            .category(MenuCategory.MAIN_COURSE)
            .preparationTimeMinutes(20)
            .build();

        MenuItem pastaAlfredo = MenuItem.builder()
            .id("MENU-003")
            .name("Pasta Alfredo")
            .description("Creamy alfredo pasta with parmesan")
            .price(new BigDecimal("16.99"))
            .category(MenuCategory.MAIN_COURSE)
            .preparationTimeMinutes(15)
            .vegetarian(true)
            .build();

        MenuItem grilledChicken = MenuItem.builder()
            .id("MENU-004")
            .name("Grilled Chicken")
            .description("Herb-marinated grilled chicken breast")
            .price(new BigDecimal("22.99"))
            .category(MenuCategory.MAIN_COURSE)
            .preparationTimeMinutes(25)
            .build();

        // Desserts
        MenuItem chocolateCake = MenuItem.builder()
            .id("MENU-005")
            .name("Chocolate Cake")
            .description("Rich chocolate layer cake")
            .price(new BigDecimal("8.99"))
            .category(MenuCategory.DESSERT)
            .preparationTimeMinutes(5)
            .vegetarian(true)
            .build();

        // Beverages
        MenuItem soda = MenuItem.builder()
            .id("MENU-006")
            .name("Soda")
            .description("Refreshing carbonated drink")
            .price(new BigDecimal("3.99"))
            .category(MenuCategory.BEVERAGE)
            .preparationTimeMinutes(1)
            .vegetarian(true)
            .build();

        restaurant.addMenuItem(caesarSalad);
        restaurant.addMenuItem(classicBurger);
        restaurant.addMenuItem(pastaAlfredo);
        restaurant.addMenuItem(grilledChicken);
        restaurant.addMenuItem(chocolateCake);
        restaurant.addMenuItem(soda);

        System.out.println("   ✓ 6 menu items added");
    }

    private static void setupTables(Restaurant restaurant) {
        System.out.println("🪑 Setting up tables...");

        restaurant.addTable(1, 4, "Indoor - Window");
        restaurant.addTable(2, 4, "Indoor - Center");
        restaurant.addTable(3, 6, "Indoor - Corner");
        restaurant.addTable(4, 2, "Outdoor - Patio");
        restaurant.addTable(5, 8, "Private Room");

        System.out.println("   ✓ 5 tables added");
    }

    private static void setupStaff(Restaurant restaurant) {
        System.out.println("👥 Setting up staff...");

        Staff manager = restaurant.addStaff(
            "Alice Johnson", "alice@restaurant.com", "+1-555-0101",
            StaffRole.MANAGER, new BigDecimal("25.00")
        );
        
        Staff chef = restaurant.addStaff(
            "Bob Williams", "bob@restaurant.com", "+1-555-0102",
            StaffRole.CHEF, new BigDecimal("22.00")
        );
        
        Staff waiter1 = restaurant.addStaff(
            "Carol Davis", "carol@restaurant.com", "+1-555-0103",
            StaffRole.WAITER, new BigDecimal("15.00")
        );
        
        Staff waiter2 = restaurant.addStaff(
            "David Brown", "david@restaurant.com", "+1-555-0104",
            StaffRole.WAITER, new BigDecimal("15.00")
        );
        
        Staff host = restaurant.addStaff(
            "Eve Miller", "eve@restaurant.com", "+1-555-0105",
            StaffRole.HOST, new BigDecimal("14.00")
        );

        // Set schedules for today
        String today = java.time.LocalDate.now().getDayOfWeek().toString();
        restaurant.setStaffSchedule(manager.getId(), today, LocalTime.of(9, 0), LocalTime.of(18, 0));
        restaurant.setStaffSchedule(chef.getId(), today, LocalTime.of(10, 0), LocalTime.of(22, 0));
        restaurant.setStaffSchedule(waiter1.getId(), today, LocalTime.of(11, 0), LocalTime.of(19, 0));
        restaurant.setStaffSchedule(waiter2.getId(), today, LocalTime.of(15, 0), LocalTime.of(23, 0));
        restaurant.setStaffSchedule(host.getId(), today, LocalTime.of(11, 0), LocalTime.of(21, 0));

        System.out.println("   ✓ 5 staff members added with schedules");
    }
}

