# Online Shopping System - Low-Level Design

## Overview

A comprehensive e-commerce system like Amazon that handles product browsing, shopping cart, order management, payments, and inventory. The system is designed following SOLID principles and common design patterns for extensibility and maintainability.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **ShoppingSystem (Facade)** | Single entry point for all operations; wires components together |
| **UserService** | User registration, authentication, profile management |
| **ProductService** | Product CRUD, category management, catalog operations |
| **CartService** | Shopping cart operations, item add/remove/update |
| **OrderService** | Order placement, lifecycle management, tracking |
| **InventoryService** | Stock management, availability checks, low-stock alerts |
| **PaymentService** | Payment processing via multiple payment methods |
| **ShippingService** | Shipping cost calculation, delivery tracking |
| **SearchService** | Product search with filters, sorting, pagination |
| **NotificationService** | Order updates, promotions, alerts |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────┐
│  OrderStatus     - PENDING, CONFIRMED, PROCESSING, SHIPPED,    │
│                    OUT_FOR_DELIVERY, DELIVERED, CANCELLED,      │
│                    RETURNED                                     │
│  PaymentStatus   - PENDING, COMPLETED, FAILED, REFUNDED        │
│  PaymentMethod   - CREDIT_CARD, DEBIT_CARD, UPI, WALLET, COD   │
│  ProductStatus   - ACTIVE, INACTIVE, OUT_OF_STOCK              │
│  UserRole        - CUSTOMER, SELLER, ADMIN                     │
│  ShippingMethod  - STANDARD, EXPRESS, SAME_DAY                 │
└─────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `User` | Customer/seller with profile, addresses, payment methods |
| `Product` | Product details with pricing, category, seller info |
| `Category` | Product category with hierarchical structure |
| `Cart` | Shopping cart containing items for a user |
| `CartItem` | Individual item in cart with quantity |
| `Order` | Customer order with items, payment, shipping info |
| `OrderItem` | Line item in an order with quantity and price |
| `Payment` | Payment transaction with method and status |
| `Address` | Shipping/billing address |
| `Inventory` | Stock levels for products with thresholds |
| `Review` | Product review with rating and comments |
| `Shipment` | Shipping details with tracking information |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `PaymentStrategy` | Process different payment types | CreditCard, DebitCard, UPI, Wallet, COD |
| `PricingStrategy` | Calculate discounts | Percentage, Flat, Tiered, Seasonal |
| `SearchStrategy` | Search products | Keyword, Category, Filter |
| `ShippingStrategy` | Calculate shipping costs | Standard, Express, SameDay |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `OrderObserver` | React to order lifecycle events |
| `InventoryObserver` | React to stock level changes |

---

## 3. Class Diagram

```
                          ┌──────────────────────┐
                          │   ShoppingSystem     │
                          │      (Facade)        │
                          └──────────┬───────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       │             │               │               │             │
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ UserService │ │ProductService│ │ CartService │ │ OrderService│ │Inventory    │
│             │ │             │ │             │ │             │ │ Service     │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│User         │ │Product      │ │Cart         │ │Order        │ │Inventory    │
│Repository   │ │Repository   │ │Repository   │ │Repository   │ │Repository   │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘


                    ┌─────────────┐     ┌─────────────┐
                    │  Payment    │────▶│  Shipping   │
                    │  Service    │     │  Service    │
                    └──────┬──────┘     └──────┬──────┘
                           │                   │
              ┌────────────┴────────┐    ┌─────┴──────────┐
              │                     │    │                │
              ▼                     ▼    ▼                ▼
        ┌───────────┐        ┌───────────┐        ┌───────────┐
        │Payment    │        │Pricing    │        │Shipping   │
        │Strategy   │        │Strategy   │        │Strategy   │
        └───────────┘        └───────────┘        └───────────┘
```

---

## 4. Design Patterns Used

### 1. **Facade Pattern** - `ShoppingSystem` class
- Provides a simplified interface to the complex e-commerce subsystem
- Coordinates between all services
- Entry point for shopping operations

### 2. **Strategy Pattern** - Payment, Pricing, Shipping, Search
- Allows runtime selection of algorithms
- Easy to add new payment methods, discount types, or shipping options
- Follows Open/Closed Principle

```java
// Adding a new payment method:
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTOCURRENCY, 
    new CryptoPaymentStrategy()
);
```

### 3. **Observer Pattern** - Order & Inventory notifications
- Customers receive real-time order updates
- Inventory alerts on low stock
- Decouples event producers from consumers

```java
public interface OrderObserver {
    void onOrderPlaced(Order order);
    void onOrderStatusChanged(Order order, OrderStatus from, OrderStatus to);
    void onOrderDelivered(Order order);
    void onOrderCancelled(Order order);
}
```

### 4. **Builder Pattern** - `Product`, `Order`, `User`
- Complex object construction with many optional parameters
- Fluent API for readability
- Validation during build

```java
Product laptop = Product.builder()
    .id("PROD-001")
    .name("MacBook Pro 14")
    .price(new BigDecimal("1999.99"))
    .category(electronics)
    .sellerId("SELLER-001")
    .build();
```

### 5. **Repository Pattern** - Data Access
- Abstracts data storage from business logic
- Easy to swap implementations (in-memory → database)
- Follows Dependency Inversion Principle

### 6. **Factory Pattern** - Payment Strategy Creation
- Creates appropriate payment strategy based on payment method
- Encapsulates object creation logic

### 7. **State Pattern** - Order Lifecycle
- `OrderStatus` enum with valid transition rules
- Prevents invalid state transitions

```java
public boolean canTransitionTo(OrderStatus newStatus) {
    return switch (this) {
        case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
        case CONFIRMED -> newStatus == PROCESSING || newStatus == CANCELLED;
        case PROCESSING -> newStatus == SHIPPED;
        case SHIPPED -> newStatus == OUT_FOR_DELIVERY;
        case OUT_FOR_DELIVERY -> newStatus == DELIVERED;
        case DELIVERED -> newStatus == RETURNED;
        default -> false;
    };
}
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each service handles one domain concern
- `CartService` only manages shopping carts
- `InventoryService` only manages stock
- Models are pure data holders

### Open/Closed Principle (OCP)
- New payment methods via `PaymentStrategy` without modifying `PaymentService`
- New shipping options via `ShippingStrategy`
- New search filters via `SearchStrategy`

### Liskov Substitution Principle (LSP)
- All `PaymentStrategy` implementations are interchangeable
- All `Repository` implementations fulfill the same contract

### Interface Segregation Principle (ISP)
- `OrderObserver` is separate from `InventoryObserver`
- Clients only depend on interfaces they need

### Dependency Inversion Principle (DIP)
- Services depend on interfaces, not concrete implementations
- `OrderService` depends on `OrderRepository` interface
- `PaymentService` depends on `PaymentStrategy` interface

---

## 6. Concurrency Handling

### Thread-Safe Components

```java
// Cart uses ReentrantLock for atomic operations
public class Cart {
    private final ReentrantLock lock = new ReentrantLock();
    
    public void addItem(Product product, int quantity) {
        lock.lock();
        try {
            // thread-safe add operation
        } finally {
            lock.unlock();
        }
    }
}

// Inventory uses ReentrantReadWriteLock for stock operations
public class Inventory {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    public boolean reserveStock(int quantity) {
        lock.writeLock().lock();
        try {
            if (availableQuantity >= quantity) {
                availableQuantity -= quantity;
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

- All repositories use `ConcurrentHashMap`
- Volatile fields for visibility guarantees
- Atomic operations for stock reservation

---

## 7. Extension Points

### Adding a New Payment Method

```java
// 1. Create new strategy
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment processPayment(Order order, BigDecimal amount) {
        // Implementation
    }
}

// 2. Register with factory
PaymentStrategyFactory.register(PaymentMethod.CRYPTO, CryptoPaymentStrategy::new);
```

### Adding a New Shipping Option

```java
// 1. Create new strategy
public class DroneDeliveryStrategy implements ShippingStrategy {
    @Override
    public Shipment calculateShipping(Address destination, BigDecimal orderValue) {
        // Calculate drone delivery cost and time
    }
}

// 2. Register with service
shippingService.registerShippingStrategy(ShippingMethod.DRONE, new DroneDeliveryStrategy());
```

### Adding New Search Filters

```java
// 1. Create new strategy
public class PriceRangeSearchStrategy implements SearchStrategy {
    @Override
    public List<Product> search(SearchCriteria criteria) {
        // Filter by price range
    }
}
```

---

## 8. Order Lifecycle Flow

```
[Customer Places Order]
       │
       ▼
   ┌───────┐     ┌────────────┐     ┌────────────┐
   │PENDING │────▶│ CONFIRMED  │────▶│ PROCESSING │
   └───────┘     └────────────┘     └─────┬──────┘
       │                                  │
       │ cancel                           ▼
       │                           ┌────────────┐
       ▼                           │  SHIPPED   │
  ┌─────────┐                      └─────┬──────┘
  │CANCELLED│                            │
  └─────────┘                            ▼
                                  ┌──────────────┐
                                  │OUT_FOR_DELIVERY│
                                  └──────┬───────┘
                                         │
                                         ▼
                                  ┌────────────┐
                                  │ DELIVERED  │
                                  └─────┬──────┘
                                        │
                                        ▼ (if requested)
                                  ┌────────────┐
                                  │  RETURNED  │
                                  └────────────┘
```

---

## 9. File Structure

```
onlineshopping/
├── enums/
│   ├── OrderStatus.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   ├── ProductStatus.java
│   ├── ShippingMethod.java
│   └── UserRole.java
├── exceptions/
│   ├── ShoppingException.java
│   ├── ProductException.java
│   ├── OrderException.java
│   ├── PaymentException.java
│   ├── InventoryException.java
│   └── CartException.java
├── models/
│   ├── User.java
│   ├── Product.java
│   ├── Category.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   ├── Address.java
│   ├── Inventory.java
│   ├── Review.java
│   └── Shipment.java
├── observers/
│   ├── OrderObserver.java
│   ├── InventoryObserver.java
│   ├── EmailNotificationObserver.java
│   └── InventoryAlertObserver.java
├── repositories/
│   ├── Repository.java
│   └── impl/
│       ├── InMemoryUserRepository.java
│       ├── InMemoryProductRepository.java
│       ├── InMemoryCartRepository.java
│       ├── InMemoryOrderRepository.java
│       └── InMemoryInventoryRepository.java
├── services/
│   ├── UserService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   ├── InventoryService.java
│   ├── PaymentService.java
│   ├── ShippingService.java
│   ├── SearchService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── ProductServiceImpl.java
│       ├── CartServiceImpl.java
│       ├── OrderServiceImpl.java
│       ├── InventoryServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── ShippingServiceImpl.java
│       └── SearchServiceImpl.java
├── strategies/
│   ├── payment/
│   │   ├── PaymentStrategy.java
│   │   ├── CreditCardPaymentStrategy.java
│   │   ├── DebitCardPaymentStrategy.java
│   │   ├── UPIPaymentStrategy.java
│   │   ├── WalletPaymentStrategy.java
│   │   └── CODPaymentStrategy.java
│   ├── pricing/
│   │   ├── PricingStrategy.java
│   │   ├── PercentageDiscountStrategy.java
│   │   ├── FlatDiscountStrategy.java
│   │   └── TieredPricingStrategy.java
│   ├── search/
│   │   ├── SearchStrategy.java
│   │   ├── KeywordSearchStrategy.java
│   │   └── FilterSearchStrategy.java
│   └── shipping/
│       ├── ShippingStrategy.java
│       ├── StandardShippingStrategy.java
│       ├── ExpressShippingStrategy.java
│       └── SameDayShippingStrategy.java
├── factories/
│   └── PaymentStrategyFactory.java
├── ShoppingSystem.java
└── Main.java
```

---

## 10. Usage Example

```java
// Initialize shopping system
ShoppingSystem shop = new ShoppingSystem("MyShop");

// Register a user
User customer = shop.registerUser("john@example.com", "John Doe", UserRole.CUSTOMER);
customer.addAddress(new Address("123 Main St", "NYC", "NY", "10001", "USA"));

// Add products (by seller)
Category electronics = shop.createCategory("Electronics", null);
Product laptop = shop.addProduct(Product.builder()
    .id("PROD-001")
    .name("MacBook Pro 14")
    .price(new BigDecimal("1999.99"))
    .category(electronics)
    .sellerId("SELLER-001")
    .description("Latest MacBook with M3 chip")
    .build());

// Set inventory
shop.setInventory("PROD-001", 100);

// Customer browses and adds to cart
shop.addToCart(customer.getId(), laptop.getId(), 1);

// Search products
List<Product> results = shop.searchProducts("MacBook", electronics.getId(), null, null);

// Place order
Order order = shop.placeOrder(
    customer.getId(),
    customer.getDefaultAddress(),
    ShippingMethod.EXPRESS,
    PaymentMethod.CREDIT_CARD
);

// Track order
OrderStatus status = shop.getOrderStatus(order.getId());

// Process order lifecycle
shop.confirmOrder(order.getId());
shop.shipOrder(order.getId(), "TRACK-123456");
shop.markDelivered(order.getId());

// Customer leaves review
shop.addReview(customer.getId(), laptop.getId(), 5, "Excellent laptop!");
```

---

## 11. Key Design Decisions

1. **In-Memory Storage**: Used `ConcurrentHashMap` for thread-safe in-memory storage. Can be easily replaced with database repositories.

2. **Immutable Collections**: `Order` and `Cart` return unmodifiable lists to prevent external modification.

3. **Builder Pattern for Complex Objects**: `Product`, `Order`, and `User` use builders for clean construction with validation.

4. **Explicit State Machine**: `OrderStatus` enum defines valid transitions, preventing illegal state changes.

5. **Facade for Simplicity**: `ShoppingSystem` class hides complexity and provides a clean API for common operations.

6. **Strategy for Flexibility**: Payment, shipping, and search algorithms can be easily extended without modifying core logic.

7. **Observer for Decoupling**: Email notifications and inventory alerts are decoupled from order/inventory processing.

8. **Optimistic Locking**: Inventory uses versioning to handle concurrent stock updates.

---

## 12. Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer (Spring Boot)
- [ ] Elasticsearch for advanced search
- [ ] Redis for cart caching
- [ ] Message queue for order processing
- [ ] Recommendation engine
- [ ] Multi-currency support
- [ ] Wishlist feature
- [ ] Coupon/promo code system
- [ ] Seller analytics dashboard



