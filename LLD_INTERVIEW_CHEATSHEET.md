# 🎯 Low-Level Design (LLD) Interview Cheat Sheet

> **Ultimate revision guide for FAANG & Startups with SOLID principles, design patterns, concurrency, and code examples**

---

## 📋 Table of Contents

1. [Interview Approach Framework](#-interview-approach-framework)
2. [SOLID Principles](#-solid-principles-with-code)
3. [Design Patterns Cheat Sheet](#-design-patterns-cheat-sheet)
4. [Concurrency & Multithreading](#-concurrency--multithreading)
5. [Common LLD Problems & Key Patterns](#-common-lld-problems--key-patterns)
6. [Code Structure Template](#-code-structure-template)
7. [Testing Strategies](#-testing-strategies)
8. [Pro Tips to Shine](#-pro-tips-to-shine)
9. [Quick Reference Cards](#-quick-reference-cards)

---

## 🚀 Interview Approach Framework

### The 7-Step LLD Interview Process (45-60 mins)

```
┌─────────────────────────────────────────────────────────────────┐
│  Step 1: CLARIFY (3-5 min)                                      │
│  ─────────────────────────────                                  │
│  • Ask clarifying questions                                     │
│  • Define scope boundaries                                      │
│  • Identify core use cases vs nice-to-haves                     │
│  • Clarify scale: single server or distributed?                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 2: REQUIREMENTS (5 min)                                   │
│  ─────────────────────────────                                  │
│  • List functional requirements                                 │
│  • List non-functional requirements                             │
│  • Prioritize: Must-have vs Optional                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 3: CORE ENTITIES (5 min)                                  │
│  ─────────────────────────────                                  │
│  • Identify main objects/classes                                │
│  • Define relationships (1:1, 1:N, N:N)                         │
│  • Create simple class diagram                                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 4: API/INTERFACE DESIGN (5-10 min)                        │
│  ───────────────────────────────────────                        │
│  • Define public interfaces/contracts                           │
│  • Identify key methods                                         │
│  • Think about extensibility points                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 5: DESIGN PATTERNS & SOLID (5 min)                        │
│  ─────────────────────────────────────────                      │
│  • Identify applicable patterns                                 │
│  • Justify pattern choices                                      │
│  • Ensure SOLID compliance                                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 6: IMPLEMENTATION (20-25 min)                             │
│  ─────────────────────────────────                              │
│  • Start with core classes                                      │
│  • Implement key flows                                          │
│  • Handle edge cases                                            │
│  • Add concurrency if needed                                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 7: TESTING & EXTENSIONS (5 min)                           │
│  ─────────────────────────────────────                          │
│  • Discuss test approach                                        │
│  • Mention future extensions                                    │
│  • Highlight trade-offs made                                    │
└─────────────────────────────────────────────────────────────────┘
```

### ⭐ Key Clarifying Questions Template

```
📍 SCOPE
- Is this single-user or multi-user?
- Single server or distributed system?
- Do we need to persist data or in-memory is fine?

📍 SCALE
- Expected number of concurrent users?
- Read-heavy or write-heavy?
- Any rate limits?

📍 FEATURES
- What are the must-have features?
- Any payment/notification integrations?
- Admin functionalities needed?

📍 CONSTRAINTS
- Any specific tech stack preferences?
- Real-time requirements?
- Consistency vs availability trade-offs?
```

---

## 📐 SOLID Principles with Code

### 1. Single Responsibility Principle (SRP)

> **"A class should have only one reason to change"**

```java
// ❌ BAD: Multiple responsibilities
public class User {
    public void saveToDatabase() { /* DB logic */ }
    public void sendEmail() { /* Email logic */ }
    public void generateReport() { /* Report logic */ }
}

// ✅ GOOD: Single responsibility each
public class User {
    private String id, name, email;
    // Only user data and behavior
}

public class UserRepository {
    public void save(User user) { /* DB logic */ }
}

public class EmailService {
    public void sendWelcomeEmail(User user) { /* Email logic */ }
}

public class UserReportGenerator {
    public Report generate(User user) { /* Report logic */ }
}
```

**Interview Tip:** When you split classes, explain the "reason to change" for each.

---

### 2. Open/Closed Principle (OCP)

> **"Open for extension, closed for modification"**

```java
// ❌ BAD: Requires modification for new payment types
public class PaymentProcessor {
    public void process(String type, double amount) {
        if (type.equals("CREDIT_CARD")) {
            // credit card logic
        } else if (type.equals("UPI")) {
            // UPI logic
        }
        // Adding new payment type requires modifying this class!
    }
}

// ✅ GOOD: Open for extension via Strategy pattern
public interface PaymentStrategy {
    void process(double amount);
}

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void process(double amount) { /* Credit card logic */ }
}

public class UPIPayment implements PaymentStrategy {
    @Override
    public void process(double amount) { /* UPI logic */ }
}

// Adding new payment: just create new class implementing PaymentStrategy
public class CryptoPayment implements PaymentStrategy {
    @Override
    public void process(double amount) { /* Crypto logic */ }
}

public class PaymentProcessor {
    public void process(PaymentStrategy strategy, double amount) {
        strategy.process(amount);  // No modification needed!
    }
}
```

---

### 3. Liskov Substitution Principle (LSP)

> **"Subtypes must be substitutable for their base types"**

```java
// ❌ BAD: Violates LSP - Square can't substitute Rectangle
public class Rectangle {
    protected int width, height;
    
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int w) { 
        this.width = w; 
        this.height = w;  // Side effect! Breaks expectations
    }
}

// ✅ GOOD: Use composition or separate hierarchy
public interface Shape {
    int area();
}

public class Rectangle implements Shape {
    private final int width, height;
    public Rectangle(int w, int h) { this.width = w; this.height = h; }
    public int area() { return width * height; }
}

public class Square implements Shape {
    private final int side;
    public Square(int side) { this.side = side; }
    public int area() { return side * side; }
}
```

---

### 4. Interface Segregation Principle (ISP)

> **"Clients should not be forced to depend on interfaces they don't use"**

```java
// ❌ BAD: Fat interface forces unnecessary implementations
public interface Vehicle {
    void drive();
    void fly();
    void sail();
}

public class Car implements Vehicle {
    public void drive() { /* OK */ }
    public void fly() { throw new UnsupportedOperationException(); }  // Forced!
    public void sail() { throw new UnsupportedOperationException(); } // Forced!
}

// ✅ GOOD: Segregated interfaces
public interface Drivable {
    void drive();
}

public interface Flyable {
    void fly();
}

public interface Sailable {
    void sail();
}

public class Car implements Drivable {
    public void drive() { /* Only what it needs */ }
}

public class AmphibiousVehicle implements Drivable, Sailable {
    public void drive() { /* ... */ }
    public void sail() { /* ... */ }
}
```

---

### 5. Dependency Inversion Principle (DIP)

> **"Depend on abstractions, not concretions"**

```java
// ❌ BAD: High-level module depends on low-level module
public class OrderService {
    private MySQLDatabase database = new MySQLDatabase();  // Tight coupling!
    
    public void save(Order order) {
        database.insert(order);  // Can't switch databases easily
    }
}

// ✅ GOOD: Both depend on abstraction
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
}

public class MySQLOrderRepository implements OrderRepository {
    @Override
    public void save(Order order) { /* MySQL specific */ }
    @Override
    public Optional<Order> findById(String id) { /* ... */ }
}

public class MongoOrderRepository implements OrderRepository {
    @Override
    public void save(Order order) { /* MongoDB specific */ }
    @Override
    public Optional<Order> findById(String id) { /* ... */ }
}

public class OrderService {
    private final OrderRepository repository;  // Abstraction!
    
    // Dependency Injection
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
    
    public void save(Order order) {
        repository.save(order);  // Works with any implementation
    }
}
```

---

## 🎨 Design Patterns Cheat Sheet

### Creational Patterns

#### 1. Singleton Pattern
**Use when:** Only one instance should exist (Config, Logger, Connection Pool)

```java
public class DatabaseConnectionPool {
    private static volatile DatabaseConnectionPool instance;
    private final List<Connection> pool;
    
    private DatabaseConnectionPool() {
        pool = new ArrayList<>();
        // Initialize pool
    }
    
    // Thread-safe double-checked locking
    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionPool();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() { /* ... */ }
}
```

**Enum Singleton (Preferred in Java):**
```java
public enum ConfigManager {
    INSTANCE;
    
    private final Properties config;
    
    ConfigManager() {
        config = loadConfig();
    }
    
    public String get(String key) {
        return config.getProperty(key);
    }
}
```

---

#### 2. Factory Pattern
**Use when:** Object creation logic is complex or needs to be centralized

```java
// Simple Factory
public class VehicleFactory {
    public static Vehicle create(VehicleType type) {
        return switch (type) {
            case CAR -> new Car();
            case MOTORCYCLE -> new Motorcycle();
            case TRUCK -> new Truck();
        };
    }
}

// Abstract Factory - Family of related objects
public interface UIFactory {
    Button createButton();
    TextBox createTextBox();
}

public class WindowsUIFactory implements UIFactory {
    public Button createButton() { return new WindowsButton(); }
    public TextBox createTextBox() { return new WindowsTextBox(); }
}

public class MacUIFactory implements UIFactory {
    public Button createButton() { return new MacButton(); }
    public TextBox createTextBox() { return new MacTextBox(); }
}
```

---

#### 3. Builder Pattern
**Use when:** Object has many optional parameters or complex construction

```java
public class Order {
    private final String orderId;
    private final String customerId;
    private final List<Item> items;
    private final Address shippingAddress;
    private final Address billingAddress;
    private final PaymentMethod paymentMethod;
    private final String couponCode;
    
    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.items = builder.items;
        this.shippingAddress = builder.shippingAddress;
        this.billingAddress = builder.billingAddress;
        this.paymentMethod = builder.paymentMethod;
        this.couponCode = builder.couponCode;
    }
    
    public static class Builder {
        // Required
        private final String orderId;
        private final String customerId;
        private final List<Item> items = new ArrayList<>();
        
        // Optional
        private Address shippingAddress;
        private Address billingAddress;
        private PaymentMethod paymentMethod;
        private String couponCode;
        
        public Builder(String orderId, String customerId) {
            this.orderId = orderId;
            this.customerId = customerId;
        }
        
        public Builder addItem(Item item) {
            this.items.add(item);
            return this;
        }
        
        public Builder shippingAddress(Address addr) {
            this.shippingAddress = addr;
            return this;
        }
        
        public Builder billingAddress(Address addr) {
            this.billingAddress = addr;
            return this;
        }
        
        public Builder paymentMethod(PaymentMethod method) {
            this.paymentMethod = method;
            return this;
        }
        
        public Builder couponCode(String code) {
            this.couponCode = code;
            return this;
        }
        
        public Order build() {
            // Validation
            if (items.isEmpty()) {
                throw new IllegalStateException("Order must have at least one item");
            }
            return new Order(this);
        }
    }
}

// Usage
Order order = new Order.Builder("ORD-001", "CUST-123")
    .addItem(new Item("iPhone", 999.99))
    .addItem(new Item("Case", 29.99))
    .shippingAddress(homeAddress)
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .couponCode("SAVE10")
    .build();
```

---

### Structural Patterns

#### 4. Decorator Pattern
**Use when:** Add behavior dynamically without modifying original class

```java
// Base interface
public interface Coffee {
    String getDescription();
    double getCost();
}

// Concrete component
public class SimpleCoffee implements Coffee {
    public String getDescription() { return "Simple Coffee"; }
    public double getCost() { return 2.0; }
}

// Decorator base
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    
    public String getDescription() { 
        return coffee.getDescription() + ", Milk"; 
    }
    public double getCost() { 
        return coffee.getCost() + 0.5; 
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) { super(coffee); }
    
    public String getDescription() { 
        return coffee.getDescription() + ", Sugar"; 
    }
    public double getCost() { 
        return coffee.getCost() + 0.2; 
    }
}

// Usage - stack decorators
Coffee order = new SugarDecorator(
                 new MilkDecorator(
                   new SimpleCoffee()));
// "Simple Coffee, Milk, Sugar" - $2.70
```

---

#### 5. Adapter Pattern
**Use when:** Make incompatible interfaces work together

```java
// Existing interface your code expects
public interface PaymentGateway {
    boolean pay(String userId, double amount);
}

// Third-party library with different interface
public class StripeAPI {
    public StripeResult charge(StripeCustomer customer, StripeMoney money) {
        // Stripe-specific implementation
    }
}

// Adapter to bridge the gap
public class StripeAdapter implements PaymentGateway {
    private final StripeAPI stripeAPI;
    
    public StripeAdapter(StripeAPI stripeAPI) {
        this.stripeAPI = stripeAPI;
    }
    
    @Override
    public boolean pay(String userId, double amount) {
        // Convert to Stripe's format
        StripeCustomer customer = new StripeCustomer(userId);
        StripeMoney money = new StripeMoney(amount, "USD");
        
        StripeResult result = stripeAPI.charge(customer, money);
        return result.isSuccessful();
    }
}

// Usage - your code works with the common interface
PaymentGateway gateway = new StripeAdapter(new StripeAPI());
gateway.pay("user123", 99.99);
```

---

### Behavioral Patterns

#### 6. Strategy Pattern ⭐ (Most Important!)
**Use when:** Multiple algorithms/behaviors that can be swapped at runtime

```java
// Strategy interface
public interface PricingStrategy {
    BigDecimal calculatePrice(Show show, List<Seat> seats);
}

// Concrete strategies
public class BasePricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Show show, List<Seat> seats) {
        return seats.stream()
            .map(seat -> show.getBasePrice().multiply(seat.getType().getMultiplier()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

public class WeekendPricingStrategy implements PricingStrategy {
    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.25");
    
    @Override
    public BigDecimal calculatePrice(Show show, List<Seat> seats) {
        BigDecimal baseTotal = new BasePricingStrategy().calculatePrice(show, seats);
        return baseTotal.multiply(WEEKEND_MULTIPLIER);
    }
}

public class DynamicPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Show show, List<Seat> seats) {
        // Price increases as seats fill up
        double occupancyRate = show.getOccupancyRate();
        BigDecimal multiplier = BigDecimal.valueOf(1 + occupancyRate * 0.5);
        return new BasePricingStrategy().calculatePrice(show, seats)
                   .multiply(multiplier);
    }
}

// Context
public class BookingService {
    private PricingStrategy pricingStrategy;
    
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }
    
    public BigDecimal calculateTotal(Show show, List<Seat> seats) {
        return pricingStrategy.calculatePrice(show, seats);
    }
}

// Usage - swap strategies at runtime
BookingService service = new BookingService();
if (isWeekend()) {
    service.setPricingStrategy(new WeekendPricingStrategy());
} else {
    service.setPricingStrategy(new BasePricingStrategy());
}
```

---

#### 7. Observer Pattern ⭐ (Very Common!)
**Use when:** Objects need to be notified of state changes

```java
// Observer interface
public interface OrderObserver {
    void onOrderCreated(Order order);
    void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}

// Concrete observers
public class EmailNotificationObserver implements OrderObserver {
    private final EmailService emailService;
    
    @Override
    public void onOrderCreated(Order order) {
        emailService.send(order.getCustomerEmail(), 
            "Order Confirmed", 
            "Your order " + order.getId() + " has been placed!");
    }
    
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        emailService.send(order.getCustomerEmail(),
            "Order Update",
            "Your order is now: " + newStatus);
    }
}

public class InventoryObserver implements OrderObserver {
    private final InventoryService inventoryService;
    
    @Override
    public void onOrderCreated(Order order) {
        for (Item item : order.getItems()) {
            inventoryService.reserve(item.getProductId(), item.getQuantity());
        }
    }
    
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus old, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            inventoryService.release(order);
        }
    }
}

public class AnalyticsObserver implements OrderObserver {
    @Override
    public void onOrderCreated(Order order) {
        Analytics.track("order_created", Map.of(
            "orderId", order.getId(),
            "amount", order.getTotal()
        ));
    }
    // ...
}

// Subject
public class OrderService {
    private final List<OrderObserver> observers = new ArrayList<>();
    
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyOrderCreated(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderCreated(order);
        }
    }
    
    public Order createOrder(OrderRequest request) {
        Order order = // create order
        notifyOrderCreated(order);
        return order;
    }
}
```

---

#### 8. State Pattern
**Use when:** Object behavior changes based on internal state

```java
// State interface
public interface ATMState {
    void insertCard(ATM atm);
    void ejectCard(ATM atm);
    void enterPin(ATM atm, String pin);
    void withdraw(ATM atm, int amount);
}

// Concrete states
public class NoCardState implements ATMState {
    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card inserted");
        atm.setState(new HasCardState());
    }
    
    @Override
    public void ejectCard(ATM atm) {
        System.out.println("No card to eject");
    }
    
    @Override
    public void enterPin(ATM atm, String pin) {
        System.out.println("Insert card first");
    }
    
    @Override
    public void withdraw(ATM atm, int amount) {
        System.out.println("Insert card first");
    }
}

public class HasCardState implements ATMState {
    @Override
    public void insertCard(ATM atm) {
        System.out.println("Card already inserted");
    }
    
    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Card ejected");
        atm.setState(new NoCardState());
    }
    
    @Override
    public void enterPin(ATM atm, String pin) {
        if (atm.validatePin(pin)) {
            atm.setState(new AuthenticatedState());
        } else {
            System.out.println("Invalid PIN");
        }
    }
    
    @Override
    public void withdraw(ATM atm, int amount) {
        System.out.println("Enter PIN first");
    }
}

// Context
public class ATM {
    private ATMState state;
    private int balance;
    
    public ATM() {
        this.state = new NoCardState();
        this.balance = 100000;
    }
    
    public void setState(ATMState state) {
        this.state = state;
    }
    
    public void insertCard() { state.insertCard(this); }
    public void ejectCard() { state.ejectCard(this); }
    public void enterPin(String pin) { state.enterPin(this, pin); }
    public void withdraw(int amount) { state.withdraw(this, amount); }
}
```

---

#### 9. Chain of Responsibility Pattern
**Use when:** Multiple handlers can process a request

```java
// Handler interface
public abstract class ValidationHandler {
    protected ValidationHandler next;
    
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }
    
    public abstract ValidationResult validate(TransferRequest request);
    
    protected ValidationResult validateNext(TransferRequest request) {
        if (next != null) {
            return next.validate(request);
        }
        return ValidationResult.success();
    }
}

// Concrete handlers
public class BalanceValidationHandler extends ValidationHandler {
    @Override
    public ValidationResult validate(TransferRequest request) {
        if (request.getFromWallet().getBalance() < request.getAmount()) {
            return ValidationResult.failure("Insufficient balance");
        }
        return validateNext(request);
    }
}

public class DailyLimitHandler extends ValidationHandler {
    @Override
    public ValidationResult validate(TransferRequest request) {
        double todayTotal = getTodayTransferTotal(request.getFromWallet());
        if (todayTotal + request.getAmount() > DAILY_LIMIT) {
            return ValidationResult.failure("Daily limit exceeded");
        }
        return validateNext(request);
    }
}

public class FraudDetectionHandler extends ValidationHandler {
    @Override
    public ValidationResult validate(TransferRequest request) {
        if (fraudDetector.isSuspicious(request)) {
            return ValidationResult.failure("Transaction flagged for review");
        }
        return validateNext(request);
    }
}

// Usage - build the chain
ValidationHandler validationChain = new BalanceValidationHandler();
validationChain
    .setNext(new DailyLimitHandler())
    .setNext(new FraudDetectionHandler());

ValidationResult result = validationChain.validate(transferRequest);
```

---

#### 10. Command Pattern
**Use when:** Encapsulate requests as objects (undo/redo, queuing)

```java
// Command interface
public interface Command {
    void execute();
    void undo();
}

// Concrete commands
public class AddToCartCommand implements Command {
    private final Cart cart;
    private final Product product;
    private final int quantity;
    
    public AddToCartCommand(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }
    
    @Override
    public void execute() {
        cart.addItem(product, quantity);
    }
    
    @Override
    public void undo() {
        cart.removeItem(product, quantity);
    }
}

// Invoker with history
public class CommandHistory {
    private final Stack<Command> history = new Stack<>();
    
    public void execute(Command command) {
        command.execute();
        history.push(command);
    }
    
    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }
}
```

---

## 🔒 Concurrency & Multithreading

### Thread Safety Patterns

#### 1. Synchronized Methods/Blocks

```java
public class Counter {
    private int count = 0;
    
    // Method-level synchronization
    public synchronized void increment() {
        count++;
    }
    
    // Block-level synchronization (more granular)
    public void incrementV2() {
        synchronized (this) {
            count++;
        }
    }
}
```

#### 2. ReentrantLock (Preferred for Complex Cases)

```java
public class SeatBookingService {
    private final ReentrantLock bookingLock = new ReentrantLock();
    private final Map<String, ShowSeat> seats;
    
    public BookingResult bookSeats(String userId, List<String> seatIds) {
        // Try to acquire lock with timeout
        try {
            if (bookingLock.tryLock(5, TimeUnit.SECONDS)) {
                try {
                    // Critical section - only one thread at a time
                    if (!areSeatsAvailable(seatIds)) {
                        return BookingResult.failure("Seats not available");
                    }
                    
                    lockSeats(seatIds, userId);
                    return BookingResult.success();
                } finally {
                    bookingLock.unlock();  // ALWAYS unlock in finally!
                }
            } else {
                return BookingResult.failure("System busy, try again");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return BookingResult.failure("Booking interrupted");
        }
    }
}
```

#### 3. ReadWriteLock (Multiple Readers, Single Writer)

```java
public class Cache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public V get(K key) {
        lock.readLock().lock();  // Multiple threads can read
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        lock.writeLock().lock();  // Only one thread can write
        try {
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

#### 4. ConcurrentHashMap (Thread-Safe Map)

```java
public class SessionManager {
    // Thread-safe without explicit synchronization
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    
    public void createSession(String token, Session session) {
        sessions.put(token, session);
    }
    
    public Session getSession(String token) {
        return sessions.get(token);
    }
    
    // Atomic operations
    public Session getOrCreate(String token, User user) {
        return sessions.computeIfAbsent(token, k -> new Session(user));
    }
    
    public boolean invalidateIfExpired(String token) {
        return sessions.computeIfPresent(token, (k, session) -> 
            session.isExpired() ? null : session) == null;
    }
}
```

#### 5. AtomicInteger/AtomicReference (Lock-Free)

```java
public class TicketCounter {
    private final AtomicInteger nextTicketNumber = new AtomicInteger(1);
    
    public int getNextTicketNumber() {
        return nextTicketNumber.getAndIncrement();  // Atomic!
    }
}

public class AtomicReferenceExample {
    private final AtomicReference<ImmutableConfig> config = new AtomicReference<>();
    
    public void updateConfig(ImmutableConfig newConfig) {
        config.set(newConfig);
    }
    
    // Compare-and-swap for conditional updates
    public boolean updateIfMatch(ImmutableConfig expected, ImmutableConfig newConfig) {
        return config.compareAndSet(expected, newConfig);
    }
}
```

#### 6. Volatile Keyword

```java
public class FeatureFlag {
    // Ensures visibility across threads
    private volatile boolean enabled = false;
    
    public void enable() {
        enabled = true;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
```

#### 7. Producer-Consumer with BlockingQueue

```java
public class OrderProcessor {
    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>(1000);
    
    // Producer
    public void submitOrder(Order order) throws InterruptedException {
        orderQueue.put(order);  // Blocks if queue is full
    }
    
    // Consumer (runs in separate thread)
    public void startProcessing() {
        new Thread(() -> {
            while (true) {
                try {
                    Order order = orderQueue.take();  // Blocks if queue is empty
                    processOrder(order);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
```

#### 8. Semaphore (Rate Limiting/Connection Pooling)

```java
public class ConnectionPool {
    private final Semaphore semaphore;
    private final Queue<Connection> pool;
    
    public ConnectionPool(int maxConnections) {
        this.semaphore = new Semaphore(maxConnections);
        this.pool = new ConcurrentLinkedQueue<>();
        // Initialize pool with connections
    }
    
    public Connection acquire() throws InterruptedException {
        semaphore.acquire();  // Blocks if no permits available
        return pool.poll();
    }
    
    public void release(Connection conn) {
        pool.offer(conn);
        semaphore.release();  // Return permit
    }
}
```

#### 9. CountDownLatch (Wait for Multiple Operations)

```java
public class ParallelTaskExecutor {
    public void executeInParallel(List<Runnable> tasks) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(tasks.size());
        
        for (Runnable task : tasks) {
            new Thread(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        latch.await();  // Wait for all tasks to complete
        System.out.println("All tasks completed!");
    }
}
```

### Concurrency Best Practices Cheat Sheet

| Scenario | Use This |
|----------|----------|
| Simple counter | `AtomicInteger` |
| Thread-safe map | `ConcurrentHashMap` |
| Multiple readers, single writer | `ReadWriteLock` |
| Critical section with timeout | `ReentrantLock.tryLock()` |
| Connection pool / rate limiting | `Semaphore` |
| Wait for multiple tasks | `CountDownLatch` |
| Producer-consumer | `BlockingQueue` |
| Publish config/flags | `volatile` or `AtomicReference` |
| Double-checked locking | `volatile` + `synchronized` |

---

## 🏗️ Common LLD Problems & Key Patterns

### Quick Reference Table

| System | Must-Have Patterns | Key Concurrency | Critical Classes |
|--------|-------------------|-----------------|------------------|
| **Parking Lot** | Singleton, Strategy, Factory, Observer | Lock on spot allocation | `ParkingLot`, `Level`, `ParkingSpot`, `Ticket` |
| **BookMyShow** | Strategy, Observer, Factory, Builder | Lock on seat booking | `Show`, `Seat`, `Booking`, `Theater` |
| **LRU Cache** | - | ReadWriteLock | `Node`, `DoublyLinkedList`, `Cache` |
| **Elevator** | State, Strategy, Observer | Lock per elevator | `Elevator`, `Request`, `Controller` |
| **Chess** | Strategy, Factory, Command | - | `Board`, `Piece`, `Move`, `Game` |
| **Splitwise** | Strategy, Observer | Lock on balances | `User`, `Expense`, `Split`, `Group` |
| **Digital Wallet** | Strategy, Chain of Responsibility | Lock on transactions | `Wallet`, `Transaction`, `Transfer` |
| **Food Delivery** | State, Strategy, Observer, Factory | Lock on order state | `Order`, `Restaurant`, `DeliveryAgent` |
| **Ride Sharing** | Strategy, Observer, State | Lock on driver assignment | `Ride`, `Driver`, `Rider`, `Trip` |
| **Vending Machine** | State, Strategy | Lock on dispensing | `VendingMachine`, `Product`, `Slot` |

### System-Specific Key Points

#### Parking Lot
```java
// Key design decisions:
// 1. Singleton for ParkingLot
// 2. Strategy for spot allocation (nearest, spread, etc.)
// 3. Factory for vehicle creation
// 4. Observer for display board updates

// Critical concurrent operation
public ParkingTicket parkVehicle(Vehicle vehicle) {
    synchronized (this) {
        ParkingSpot spot = allocationStrategy.findSpot(levels, vehicle);
        if (spot == null) throw new NoSpotAvailableException();
        
        spot.parkVehicle(vehicle);
        ParkingTicket ticket = new ParkingTicket(vehicle, spot);
        activeTickets.put(ticket.getId(), ticket);
        notifyObservers(ticket);
        return ticket;
    }
}
```

#### BookMyShow / Ticket Booking
```java
// Key: Seat locking with expiry
public class ShowSeat {
    private SeatStatus status;
    private String lockedByUserId;
    private LocalDateTime lockExpiry;
    private final ReentrantLock seatLock = new ReentrantLock();
    
    public boolean lock(String userId, int minutes) {
        seatLock.lock();
        try {
            if (!isAvailable()) return false;
            
            this.status = SeatStatus.LOCKED;
            this.lockedByUserId = userId;
            this.lockExpiry = LocalDateTime.now().plusMinutes(minutes);
            return true;
        } finally {
            seatLock.unlock();
        }
    }
    
    public boolean isAvailable() {
        // Auto-release expired locks
        if (status == SeatStatus.LOCKED && 
            LocalDateTime.now().isAfter(lockExpiry)) {
            unlock();
        }
        return status == SeatStatus.AVAILABLE;
    }
}
```

#### LRU Cache
```java
public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> list;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public V get(K key) {
        lock.writeLock().lock();  // Need write lock to move node
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) return null;
            
            list.moveToHead(node);  // Mark as recently used
            return node.value;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (cache.containsKey(key)) {
                Node<K, V> node = cache.get(key);
                node.value = value;
                list.moveToHead(node);
            } else {
                if (cache.size() >= capacity) {
                    Node<K, V> lru = list.removeTail();
                    cache.remove(lru.key);
                }
                Node<K, V> newNode = new Node<>(key, value);
                list.addToHead(newNode);
                cache.put(key, newNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

#### Splitwise
```java
// Key: Different split strategies
public interface SplitStrategy {
    Map<User, BigDecimal> split(BigDecimal amount, List<User> participants, 
                                 Map<User, Object> splitDetails);
}

public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> split(BigDecimal amount, List<User> participants,
                                        Map<User, Object> details) {
        BigDecimal share = amount.divide(
            BigDecimal.valueOf(participants.size()), 
            2, RoundingMode.HALF_UP);
        
        return participants.stream()
            .collect(Collectors.toMap(u -> u, u -> share));
    }
}

public class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> split(BigDecimal amount, List<User> participants,
                                        Map<User, Object> details) {
        return participants.stream()
            .collect(Collectors.toMap(
                u -> u,
                u -> amount.multiply((BigDecimal) details.get(u))
                          .divide(BigDecimal.valueOf(100))
            ));
    }
}
```

---

## 📁 Code Structure Template

### Standard Package Structure

```
system/
├── Main.java                    # Entry point, demo
├── SystemFacade.java            # Main facade (Singleton optional)
│
├── enums/                       # All enumerations
│   ├── Status.java
│   ├── Type.java
│   └── ...
│
├── models/                      # Domain models/entities
│   ├── User.java
│   ├── Order.java
│   └── ...
│
├── exceptions/                  # Custom exceptions
│   ├── NotFoundException.java
│   ├── ValidationException.java
│   └── ...
│
├── repositories/                # Data access layer
│   ├── Repository.java          # Base interface
│   ├── UserRepository.java      # Specific interface
│   └── impl/
│       └── InMemoryUserRepository.java
│
├── services/                    # Business logic layer
│   ├── UserService.java         # Interface
│   └── impl/
│       └── UserServiceImpl.java
│
├── strategies/                  # Strategy implementations
│   ├── pricing/
│   │   ├── PricingStrategy.java
│   │   └── implementations...
│   ├── payment/
│   │   ├── PaymentStrategy.java
│   │   └── implementations...
│   └── ...
│
├── factories/                   # Factory classes
│   └── VehicleFactory.java
│
├── observers/                   # Observer implementations
│   ├── EventObserver.java       # Interface
│   ├── EmailObserver.java
│   └── AnalyticsObserver.java
│
└── validators/                  # Validation logic
    └── RequestValidator.java
```

### Base Repository Interface

```java
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    boolean exists(ID id);
}

public interface UserRepository extends Repository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByCity(String city);
}

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> store = new ConcurrentHashMap<>();
    
    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
    
    // ... other methods
}
```

### Service Template

```java
public interface OrderService {
    Order createOrder(CreateOrderRequest request);
    Order getOrder(String orderId);
    Order updateStatus(String orderId, OrderStatus status);
    void cancelOrder(String orderId);
}

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final List<OrderObserver> observers;
    
    // Constructor injection
    public OrderServiceImpl(OrderRepository orderRepository,
                           PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.observers = new ArrayList<>();
    }
    
    @Override
    public Order createOrder(CreateOrderRequest request) {
        // 1. Validate
        validateRequest(request);
        
        // 2. Create order
        Order order = Order.builder()
            .id(generateId())
            .customerId(request.getCustomerId())
            .items(request.getItems())
            .status(OrderStatus.CREATED)
            .createdAt(LocalDateTime.now())
            .build();
        
        // 3. Persist
        orderRepository.save(order);
        
        // 4. Notify observers
        notifyObservers(order);
        
        return order;
    }
    
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }
    
    private void notifyObservers(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderCreated(order);
        }
    }
}
```

---

## 🧪 Testing Strategies

### 1. Unit Tests for Core Logic

```java
public class PricingStrategyTest {
    
    @Test
    void testBasePricing() {
        PricingStrategy strategy = new BasePricingStrategy();
        Show show = createShow(100.0);  // Base price $100
        Seat regularSeat = createSeat(SeatType.REGULAR);  // 1x multiplier
        Seat premiumSeat = createSeat(SeatType.PREMIUM);  // 1.5x multiplier
        
        assertEquals(new BigDecimal("100.00"), 
                     strategy.calculateSeatPrice(show, regularSeat));
        assertEquals(new BigDecimal("150.00"), 
                     strategy.calculateSeatPrice(show, premiumSeat));
    }
    
    @Test
    void testWeekendPricing_applies25PercentSurcharge() {
        PricingStrategy strategy = new WeekendPricingStrategy();
        Show show = createShow(100.0);
        Seat seat = createSeat(SeatType.REGULAR);
        
        // Weekend should be 25% more
        assertEquals(new BigDecimal("125.00"), 
                     strategy.calculateSeatPrice(show, seat));
    }
}
```

### 2. Concurrency Tests

```java
public class SeatBookingConcurrencyTest {
    
    @Test
    void testConcurrentBookingOfSameSeat_onlyOneSucceeds() throws InterruptedException {
        ShowSeat seat = new ShowSeat("SEAT-001", SeatType.REGULAR);
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            final String userId = "USER-" + i;
            new Thread(() -> {
                try {
                    startLatch.await();  // Wait for signal to start
                    if (seat.lock(userId, 5)) {
                        successCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }
        
        startLatch.countDown();  // Start all threads simultaneously
        doneLatch.await();       // Wait for all to complete
        
        assertEquals(1, successCount.get(), 
            "Only one thread should successfully lock the seat");
    }
    
    @Test
    void testConcurrentReadsAndWrites_noDataCorruption() throws InterruptedException {
        LRUCache<String, Integer> cache = new LRUCache<>(100);
        int operations = 1000;
        CountDownLatch latch = new CountDownLatch(operations * 2);
        
        // Writers
        for (int i = 0; i < operations; i++) {
            final int key = i;
            new Thread(() -> {
                cache.put("key-" + key, key);
                latch.countDown();
            }).start();
        }
        
        // Readers
        for (int i = 0; i < operations; i++) {
            final int key = i;
            new Thread(() -> {
                cache.get("key-" + key);  // Should not throw
                latch.countDown();
            }).start();
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}
```

### 3. Integration/Flow Tests

```java
public class BookingFlowTest {
    private BookMyShow bookMyShow;
    
    @BeforeEach
    void setup() {
        bookMyShow = BookMyShow.getInstance();
        // Setup test data
    }
    
    @Test
    void testCompleteBookingFlow() {
        // 1. Setup
        User user = bookMyShow.registerUser("John", "john@email.com");
        Movie movie = bookMyShow.addMovie(new Movie("Inception", 148));
        Theater theater = bookMyShow.addTheater(new Theater("PVR", "Mumbai"));
        Show show = bookMyShow.createShow(movie, theater, LocalDateTime.now().plusHours(2));
        
        // 2. Search
        List<Show> shows = bookMyShow.getShowsByMovie(movie.getId());
        assertFalse(shows.isEmpty());
        
        // 3. Get available seats
        List<Seat> available = bookMyShow.getAvailableSeats(show.getId());
        assertTrue(available.size() > 0);
        
        // 4. Book
        List<String> seatIds = available.stream()
            .limit(2)
            .map(Seat::getId)
            .collect(Collectors.toList());
        
        Booking booking = bookMyShow.initiateBooking(user.getId(), show.getId(), seatIds);
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        
        // 5. Confirm with payment
        Booking confirmed = bookMyShow.confirmBooking(booking.getId(), PaymentMethod.CREDIT_CARD);
        assertEquals(BookingStatus.CONFIRMED, confirmed.getStatus());
        
        // 6. Verify seats are no longer available
        List<Seat> availableAfter = bookMyShow.getAvailableSeats(show.getId());
        assertEquals(available.size() - 2, availableAfter.size());
    }
    
    @Test
    void testBookingCancellation_seatsReleased() {
        // Setup booking...
        Booking booking = createConfirmedBooking();
        int seatsBooked = booking.getSeatIds().size();
        int availableBefore = bookMyShow.getAvailableSeats(booking.getShowId()).size();
        
        // Cancel
        bookMyShow.cancelBooking(booking.getId());
        
        // Verify seats released
        int availableAfter = bookMyShow.getAvailableSeats(booking.getShowId()).size();
        assertEquals(availableBefore + seatsBooked, availableAfter);
    }
}
```

### 4. Edge Case Tests

```java
public class EdgeCaseTests {
    
    @Test
    void testBookingExpiredSeats_shouldFail() {
        // Lock seats, wait for expiry, try to book
        ShowSeat seat = new ShowSeat("SEAT-001", SeatType.REGULAR);
        seat.lock("USER-1", 0);  // 0 minutes = immediate expiry
        
        Thread.sleep(100);  // Let it expire
        
        // Another user should be able to book now
        assertTrue(seat.lock("USER-2", 5));
    }
    
    @Test
    void testParkingLotFull_throwsException() {
        ParkingLot lot = createSmallParkingLot(2);  // Only 2 spots
        
        lot.parkVehicle(new Car("ABC-123"));
        lot.parkVehicle(new Car("XYZ-789"));
        
        assertThrows(ParkingFullException.class, 
            () -> lot.parkVehicle(new Car("NEW-001")));
    }
    
    @Test
    void testNegativeTransferAmount_throwsException() {
        WalletService walletService = new WalletServiceImpl();
        
        assertThrows(InvalidAmountException.class,
            () -> walletService.transfer("wallet1", "wallet2", -100.0));
    }
    
    @Test
    void testInsufficientBalance_throwsException() {
        Wallet wallet = new Wallet("W-001", 50.0);
        
        assertThrows(InsufficientBalanceException.class,
            () -> wallet.debit(100.0));
    }
}
```

---

## 💡 Pro Tips to Shine

### 🎯 During the Interview

#### 1. Start Strong
```
✅ "Before I start coding, let me confirm my understanding of the requirements..."
✅ "I'll identify the core entities first, then design the interfaces..."
✅ "Let me draw a quick class diagram to visualize the relationships..."
```

#### 2. Think Aloud
```
✅ "I'm using Strategy pattern here because we might have different pricing algorithms..."
✅ "This violates SRP, so let me separate this into two classes..."
✅ "I'm making this synchronized because multiple threads could access it..."
```

#### 3. Acknowledge Trade-offs
```
✅ "I'm using a simple lock here. For production, we'd want a distributed lock..."
✅ "This is O(n) lookup. If scale is a concern, we could use a HashMap..."
✅ "I'm keeping it simple for now, but we could make this more flexible with..."
```

### 🚫 Common Mistakes to Avoid

| ❌ Don't | ✅ Do Instead |
|---------|---------------|
| Jump straight to coding | Spend 5-10 min on requirements & design |
| Over-engineer from start | Start simple, mention extensions |
| Ignore concurrency | Address thread-safety for shared state |
| Forget edge cases | Handle nulls, empty lists, invalid input |
| Use generic names | Use domain-specific, clear names |
| Skip interfaces | Program to interfaces, not implementations |
| Hardcode values | Use enums and constants |
| Giant methods | Keep methods small, single responsibility |

### 📝 Code Quality Checklist

```
Before submitting, verify:
□ All classes follow SRP
□ Using interfaces for abstraction
□ Thread-safety addressed for shared state
□ Proper exception handling
□ Null checks where appropriate
□ Meaningful variable/method names
□ No magic numbers (use constants/enums)
□ Dependencies injected, not instantiated
□ At least one extensibility point mentioned
```

### 🌟 Bonus Points

1. **Mention Real Patterns**
   - "This is similar to how Stripe handles webhooks with observers..."
   - "Netflix uses this circuit breaker pattern for resilience..."

2. **Discuss Scaling**
   - "For horizontal scaling, we'd need distributed locking via Redis..."
   - "We could shard by user ID for better distribution..."

3. **Testing Mindset**
   - "This design makes it easy to mock dependencies for testing..."
   - "We could verify this with a concurrent test using CountDownLatch..."

4. **Production Considerations**
   - "In production, we'd add logging and metrics here..."
   - "We'd want idempotency keys for payment retries..."

---

## 📋 Quick Reference Cards

### Pattern Selection Guide

```
Need single instance?              → Singleton
Creating objects with many params? → Builder
Creating family of related objects?→ Abstract Factory
Algorithm varies by context?       → Strategy ⭐
Need to notify multiple systems?   → Observer ⭐
Behavior changes with state?       → State
Add behavior without modification? → Decorator
Make incompatible interfaces work? → Adapter
Chain of validation/processing?    → Chain of Responsibility
Undo/redo operations?              → Command
Access to collection elements?     → Iterator
```

### Thread-Safety Quick Guide

```
Simple counter      → AtomicInteger
Shared map          → ConcurrentHashMap
Single resource     → synchronized or ReentrantLock
Read-heavy          → ReadWriteLock
Limit concurrency   → Semaphore
Wait for completion → CountDownLatch
Producer-consumer   → BlockingQueue
Flag/config updates → volatile
```

### Exception Naming Convention

```
{Domain}{Problem}Exception

Examples:
- SeatNotAvailableException
- InsufficientBalanceException
- PaymentFailedException
- UserNotFoundException
- BookingExpiredException
- DailyLimitExceededException
```

### Common Enum Patterns

```java
// Status enums (lifecycle)
enum OrderStatus { CREATED, PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
enum BookingStatus { INITIATED, PENDING, CONFIRMED, EXPIRED, CANCELLED, REFUNDED }
enum PaymentStatus { PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED }

// Type enums (with properties)
enum SeatType {
    REGULAR(1.0), PREMIUM(1.5), RECLINER(2.0), VIP(3.0);
    
    private final double multiplier;
    SeatType(double multiplier) { this.multiplier = multiplier; }
    public double getMultiplier() { return multiplier; }
}

enum VehicleType {
    MOTORCYCLE(1), CAR(2), TRUCK(4);
    
    private final int spotsRequired;
    VehicleType(int spots) { this.spotsRequired = spots; }
    public int getSpotsRequired() { return spotsRequired; }
}
```

---

## 📚 15-Minute Pre-Interview Revision

### 🔴 Must Remember

1. **SOLID** - Single Responsibility, Open/Closed, Liskov, Interface Segregation, Dependency Inversion

2. **Top 5 Patterns**:
   - Strategy (different algorithms)
   - Observer (notifications)
   - Factory (object creation)
   - Singleton (single instance)
   - Builder (complex objects)

3. **Concurrency Essentials**:
   - `synchronized` for simple cases
   - `ReentrantLock` for timeouts/try-lock
   - `ConcurrentHashMap` for thread-safe maps
   - `AtomicInteger` for counters

4. **Interview Structure**:
   - 5 min: Clarify & Requirements
   - 5 min: Core Entities & Diagram
   - 5 min: Interfaces & Patterns
   - 25 min: Implementation
   - 5 min: Testing & Extensions

5. **Always Show**:
   - Interface-based design
   - Proper exception handling
   - Thread-safety awareness
   - Extension points

---

**Good luck with your interviews! 🚀**

*Remember: The goal is to demonstrate clear thinking, good design principles, and clean code - not perfection.*

