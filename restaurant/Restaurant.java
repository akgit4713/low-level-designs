package restaurant;

import restaurant.enums.*;
import restaurant.models.*;
import restaurant.observers.*;
import restaurant.repositories.impl.*;
import restaurant.services.*;
import restaurant.services.impl.*;
import restaurant.strategies.discount.*;
import restaurant.strategies.payment.*;
import restaurant.strategies.tax.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Facade class that provides a simplified interface to the restaurant management system
 * Wires all components together and provides high-level operations
 */
public class Restaurant {
    
    private final String name;
    
    // Repositories
    private final InMemoryOrderRepository orderRepository;
    private final InMemoryMenuRepository menuRepository;
    private final InMemoryTableRepository tableRepository;
    private final InMemoryStaffRepository staffRepository;
    private final InMemoryReservationRepository reservationRepository;
    private final InMemoryInventoryRepository inventoryRepository;
    
    // Services
    private final MenuService menuService;
    private final OrderService orderService;
    private final ReservationService reservationService;
    private final InventoryService inventoryService;
    private final StaffService staffService;
    private final BillingService billingService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    
    public Restaurant(String name) {
        this.name = name;
        
        // Initialize repositories
        this.orderRepository = new InMemoryOrderRepository();
        this.menuRepository = new InMemoryMenuRepository();
        this.tableRepository = new InMemoryTableRepository();
        this.staffRepository = new InMemoryStaffRepository();
        this.reservationRepository = new InMemoryReservationRepository();
        this.inventoryRepository = new InMemoryInventoryRepository();
        
        // Initialize services with dependency injection
        this.menuService = new MenuServiceImpl(menuRepository);
        this.orderService = new OrderServiceImpl(orderRepository);
        this.reservationService = new ReservationServiceImpl(reservationRepository, tableRepository);
        this.inventoryService = new InventoryServiceImpl(inventoryRepository);
        this.staffService = new StaffServiceImpl(staffRepository);
        this.billingService = new BillingServiceImpl();
        this.paymentService = new PaymentServiceImpl();
        this.reportService = new ReportServiceImpl(
            orderRepository, inventoryRepository, staffRepository, reservationRepository
        );
        
        // Set up default observers
        setupObservers();
        
        // Set up default payment strategies
        setupPaymentStrategies();
        
        // Set up default tax strategies
        setupTaxStrategies();
    }
    
    private void setupObservers() {
        // Kitchen display observer for orders
        orderService.addObserver(new KitchenDisplayObserver());
        
        // Inventory alert observer
        inventoryService.addObserver(new InventoryAlertObserver());
    }
    
    private void setupPaymentStrategies() {
        paymentService.registerPaymentStrategy(PaymentMethod.CASH, new CashPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.CREDIT_CARD, 
            new CardPaymentStrategy(PaymentMethod.CREDIT_CARD));
        paymentService.registerPaymentStrategy(PaymentMethod.DEBIT_CARD, 
            new CardPaymentStrategy(PaymentMethod.DEBIT_CARD));
        paymentService.registerPaymentStrategy(PaymentMethod.MOBILE_PAYMENT, 
            new MobilePaymentStrategy());
    }
    
    private void setupTaxStrategies() {
        // Add default GST tax
        ((BillingServiceImpl) billingService).addTaxStrategy(StandardTaxStrategy.gst());
    }
    
    // === Menu Operations ===
    
    public MenuItem addMenuItem(MenuItem item) {
        return menuService.addMenuItem(item);
    }
    
    public List<MenuItem> getMenu() {
        return menuService.getAvailableMenu();
    }
    
    public List<MenuItem> getMenuByCategory(MenuCategory category) {
        return menuService.getMenuByCategory(category);
    }
    
    // === Table Operations ===
    
    public Table addTable(int tableNumber, int capacity, String location) {
        Table table = new Table("TBL-" + tableNumber, tableNumber, capacity, location);
        return tableRepository.save(table);
    }
    
    public List<Table> getAvailableTables() {
        return tableRepository.findAvailable();
    }
    
    public Optional<Table> getTable(int tableNumber) {
        return tableRepository.findByTableNumber(tableNumber);
    }
    
    // === Reservation Operations ===
    
    public Reservation makeReservation(String customerName, String phone, 
                                        int tableNumber, LocalDateTime dateTime, 
                                        int partySize) {
        Table table = tableRepository.findByTableNumber(tableNumber)
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableNumber));
        return reservationService.makeReservation(customerName, phone, table, dateTime, partySize, null);
    }
    
    public List<Reservation> getTodayReservations() {
        return reservationService.getReservationsForDate(LocalDate.now());
    }
    
    public void checkInReservation(String reservationId) {
        reservationService.checkInReservation(reservationId);
    }
    
    // === Order Operations ===
    
    public Order placeOrder(String customerId, Table table, List<OrderItem> items) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        Order order = Order.builder()
            .id(orderId)
            .customerId(customerId)
            .table(table)
            .orderType(OrderType.DINE_IN)
            .items(items)
            .build();
        
        return orderService.placeOrder(order);
    }
    
    public Order placeTakeoutOrder(String customerId, List<OrderItem> items) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        Order order = Order.builder()
            .id(orderId)
            .customerId(customerId)
            .orderType(OrderType.TAKEOUT)
            .items(items)
            .build();
        
        return orderService.placeOrder(order);
    }
    
    public void startPreparing(String orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.PREPARING);
    }
    
    public void markOrderReady(String orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.READY);
    }
    
    public void serveOrder(String orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.SERVED);
    }
    
    public List<Order> getActiveOrders() {
        return orderService.getActiveOrders();
    }
    
    // === Billing & Payment Operations ===
    
    public Bill generateBill(String orderId) {
        Order order = orderService.getOrder(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        orderService.updateOrderStatus(orderId, OrderStatus.BILLED);
        return billingService.generateBill(order);
    }
    
    public Payment processPayment(String billId, BigDecimal amount, PaymentMethod method) {
        Bill bill = billingService.getBill(billId)
            .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));
        
        Payment payment = paymentService.processPayment(bill, amount, method);
        
        if (payment.isSuccessful()) {
            orderService.updateOrderStatus(bill.getOrder().getId(), OrderStatus.PAID);
        }
        
        return payment;
    }
    
    // === Inventory Operations ===
    
    public InventoryItem addIngredientToInventory(Ingredient ingredient, double quantity,
                                                   double reorderLevel, double reorderQuantity) {
        return inventoryService.addInventoryItem(ingredient, quantity, reorderLevel, reorderQuantity);
    }
    
    public void restockIngredient(String ingredientId, double quantity) {
        inventoryService.restock(ingredientId, quantity);
    }
    
    public List<InventoryItem> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }
    
    // === Staff Operations ===
    
    public Staff addStaff(String name, String email, String phone, 
                          StaffRole role, BigDecimal hourlyRate) {
        String staffId = "STF-" + UUID.randomUUID().toString().substring(0, 8);
        Staff staff = new Staff(staffId, name, email, phone, role, hourlyRate);
        return staffService.addStaff(staff);
    }
    
    public void setStaffSchedule(String staffId, String dayOfWeek, 
                                  LocalTime start, LocalTime end) {
        staffService.setSchedule(staffId, dayOfWeek, start, end);
    }
    
    public List<Staff> getAvailableWaiters() {
        String today = LocalDate.now().getDayOfWeek().toString();
        return staffService.getAvailableWaiters(today, LocalTime.now());
    }
    
    // === Reporting Operations ===
    
    public ReportService.SalesReport getSalesReport(LocalDateTime start, LocalDateTime end) {
        return reportService.generateSalesReport(start, end);
    }
    
    public ReportService.InventoryReport getInventoryReport() {
        return reportService.generateInventoryReport();
    }
    
    public ReportService.DailySummary getTodaySummary() {
        return reportService.getDailySummary(LocalDate.now());
    }
    
    // === Configuration ===
    
    public void addDiscountStrategy(DiscountStrategy discount) {
        billingService.addDiscountStrategy(discount);
    }
    
    public void addOrderObserver(OrderObserver observer) {
        orderService.addObserver(observer);
    }
    
    public void addInventoryObserver(InventoryObserver observer) {
        inventoryService.addObserver(observer);
    }
    
    // === Getters for Services (for advanced use) ===
    
    public MenuService getMenuService() {
        return menuService;
    }
    
    public OrderService getOrderService() {
        return orderService;
    }
    
    public ReservationService getReservationService() {
        return reservationService;
    }
    
    public InventoryService getInventoryService() {
        return inventoryService;
    }
    
    public StaffService getStaffService() {
        return staffService;
    }
    
    public BillingService getBillingService() {
        return billingService;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }
    
    public ReportService getReportService() {
        return reportService;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Restaurant{name='" + name + "'}";
    }
}

