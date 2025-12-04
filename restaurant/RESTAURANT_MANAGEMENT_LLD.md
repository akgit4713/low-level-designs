# Restaurant Management System - Low-Level Design

## Overview

A comprehensive restaurant management system that handles customer orders, reservations, inventory, staff management, billing, and reporting. The system is designed following SOLID principles and common design patterns for extensibility and maintainability.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **Restaurant (Facade)** | Single entry point for all operations; wires components together |
| **MenuService** | Manage menu items, categories, availability, pricing |
| **OrderService** | Create, track, and manage orders through their lifecycle |
| **ReservationService** | Handle table reservations with time slot management |
| **InventoryService** | Track ingredients, stock levels, consumption, and alerts |
| **StaffService** | Manage staff, roles, schedules, and performance tracking |
| **BillingService** | Generate bills with discounts and taxes |
| **PaymentService** | Process payments via multiple payment methods |
| **ReportService** | Generate analytics: sales reports, inventory analysis |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────┐
│  OrderStatus    - PLACED, PREPARING, READY, SERVED, BILLED,    │
│                   PAID, COMPLETED, CANCELLED                    │
│  OrderType      - DINE_IN, TAKEOUT, DELIVERY                   │
│  TableStatus    - AVAILABLE, OCCUPIED, RESERVED, CLEANING      │
│  StaffRole      - MANAGER, CHEF, SOUS_CHEF, WAITER, HOST, etc. │
│  MenuCategory   - APPETIZER, MAIN_COURSE, DESSERT, BEVERAGE    │
│  PaymentMethod  - CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT│
└─────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `MenuItem` | Menu item with price, category, ingredients, prep time |
| `Order` | Customer order with items, status, table assignment |
| `OrderItem` | Individual item in an order with quantity and instructions |
| `Reservation` | Table booking with customer info, time, party size |
| `Table` | Restaurant table with capacity, location, status |
| `Staff` | Employee with role, schedule, performance metrics |
| `Ingredient` | Raw material with unit and cost |
| `InventoryItem` | Stock record with quantity and reorder thresholds |
| `Bill` | Invoice with line items, discounts, taxes |
| `Payment` | Payment transaction with method and status |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `PaymentStrategy` | Process different payment types | Cash, Card, Mobile |
| `DiscountStrategy` | Calculate discounts | Percentage, Flat amount |
| `TaxStrategy` | Calculate taxes | GST, VAT, Service Charge |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `OrderObserver` | React to order lifecycle events |
| `InventoryObserver` | React to stock level changes |

---

## 3. Class Diagram

```
                          ┌──────────────────────┐
                          │    Restaurant        │
                          │     (Facade)         │
                          └──────────┬───────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       │             │               │               │             │
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ MenuService │ │ OrderService│ │Reservation  │ │ Inventory   │ │ Staff       │
│             │ │             │ │ Service     │ │ Service     │ │ Service     │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Menu         │ │Order        │ │Reservation  │ │Inventory    │ │Staff        │
│Repository   │ │Repository   │ │Repository   │ │Repository   │ │Repository   │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘


                    ┌─────────────┐     ┌─────────────┐
                    │Billing      │────▶│Payment      │
                    │Service      │     │Service      │
                    └──────┬──────┘     └──────┬──────┘
                           │                   │
              ┌────────────┴────────┐    ┌─────┴──────────┐
              │                     │    │                │
              ▼                     ▼    ▼                ▼
        ┌───────────┐        ┌───────────┐        ┌───────────┐
        │Discount   │        │Tax        │        │Payment    │
        │Strategy   │        │Strategy   │        │Strategy   │
        └───────────┘        └───────────┘        └───────────┘
```

---

## 4. Design Patterns Used

### 1. **Facade Pattern** - `Restaurant` class
- Provides a simplified interface to the complex subsystem
- Coordinates between all services
- Entry point for all restaurant operations

### 2. **Strategy Pattern** - Payment, Discount, Tax
- Allows runtime selection of algorithms
- Easy to add new payment methods, discount types, or tax rules
- Follows Open/Closed Principle

```java
// Adding a new payment method:
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTOCURRENCY, 
    new CryptoPaymentStrategy()
);
```

### 3. **Observer Pattern** - Order & Inventory notifications
- Kitchen receives real-time order updates
- Inventory alerts on low stock or depletion
- Decouples event producers from consumers

```java
// Order observers get notified
public interface OrderObserver {
    void onOrderCreated(Order order);
    void onOrderStatusChanged(Order order, OrderStatus from, OrderStatus to);
    void onOrderCompleted(Order order);
    void onOrderCancelled(Order order);
}
```

### 4. **Builder Pattern** - `Order`, `MenuItem`, `Bill`
- Complex object construction with many optional parameters
- Fluent API for readability
- Validation during build

```java
MenuItem burger = MenuItem.builder()
    .id("MENU-001")
    .name("Classic Burger")
    .price(new BigDecimal("18.99"))
    .category(MenuCategory.MAIN_COURSE)
    .preparationTimeMinutes(20)
    .vegetarian(false)
    .build();
```

### 5. **Repository Pattern** - Data Access
- Abstracts data storage from business logic
- Easy to swap implementations (in-memory → database)
- Follows Dependency Inversion Principle

### 6. **State Pattern** - Order Lifecycle
- `OrderStatus` enum with valid transition rules
- Encapsulates state-specific behavior
- Prevents invalid state transitions

```java
public boolean canTransitionTo(OrderStatus newStatus) {
    return switch (this) {
        case PLACED -> newStatus == PREPARING || newStatus == CANCELLED;
        case PREPARING -> newStatus == READY || newStatus == CANCELLED;
        // ...
    };
}
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each service handles one domain concern
- `OrderService` only manages orders
- `InventoryService` only manages stock
- Models are pure data holders

### Open/Closed Principle (OCP)
- New payment methods via `PaymentStrategy` without modifying `PaymentService`
- New discount types via `DiscountStrategy`
- New tax rules via `TaxStrategy`

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
// Table uses ReentrantLock for atomic operations
public class Table {
    private final ReentrantLock lock = new ReentrantLock();
    
    public boolean tryReserve() {
        lock.lock();
        try {
            if (status == TableStatus.AVAILABLE) {
                status = TableStatus.RESERVED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
```

- `Order` uses `ReentrantReadWriteLock` for status transitions
- `InventoryItem` uses `ReentrantLock` for stock operations
- All repositories use `ConcurrentHashMap`
- Volatile fields for visibility guarantees

---

## 7. Extension Points

### Adding a New Payment Method

```java
// 1. Create new strategy
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        // Implementation
    }
}

// 2. Register with service
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTO, 
    new CryptoPaymentStrategy()
);
```

### Adding a New Discount Type

```java
// 1. Create new strategy
public class LoyaltyDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal) {
        // Check loyalty points and apply discount
    }
}

// 2. Add to billing service
billingService.addDiscountStrategy(new LoyaltyDiscountStrategy());
```

### Adding New Order Type

```java
// 1. Add to enum
public enum OrderType {
    DINE_IN, TAKEOUT, DELIVERY, CATERING  // New type
}

// 2. Handle in Order builder validation
```

### Adding New Report Type

```java
// 1. Add method to ReportService interface
CustomerReport generateCustomerReport(String customerId);

// 2. Implement in ReportServiceImpl
```

---

## 8. Order Lifecycle Flow

```
[Customer Orders]
       │
       ▼
   ┌───────┐     ┌────────────┐
   │PLACED │────▶│ PREPARING  │
   └───────┘     └─────┬──────┘
       │               │
       │ cancel        ▼
       │         ┌───────────┐
       ▼         │   READY   │
  ┌─────────┐    └─────┬─────┘
  │CANCELLED│          │
  └─────────┘          ▼
                 ┌───────────┐
                 │  SERVED   │
                 └─────┬─────┘
                       │
                       ▼
                 ┌───────────┐
                 │  BILLED   │
                 └─────┬─────┘
                       │
                       ▼
                 ┌───────────┐
                 │   PAID    │
                 └─────┬─────┘
                       │
                       ▼
                 ┌───────────┐
                 │ COMPLETED │
                 └───────────┘
```

---

## 9. File Structure

```
restaurant/
├── enums/
│   ├── MenuCategory.java
│   ├── OrderStatus.java
│   ├── OrderType.java
│   ├── PaymentMethod.java
│   ├── StaffRole.java
│   └── TableStatus.java
├── exceptions/
│   ├── RestaurantException.java
│   ├── OrderException.java
│   ├── InventoryException.java
│   ├── PaymentException.java
│   └── ReservationException.java
├── models/
│   ├── Bill.java
│   ├── Ingredient.java
│   ├── IngredientRequirement.java
│   ├── InventoryItem.java
│   ├── MenuItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   ├── Reservation.java
│   ├── Staff.java
│   └── Table.java
├── observers/
│   ├── InventoryObserver.java
│   ├── OrderObserver.java
│   ├── InventoryAlertObserver.java
│   └── KitchenDisplayObserver.java
├── repositories/
│   ├── Repository.java
│   ├── OrderRepository.java
│   └── impl/
│       ├── InMemoryInventoryRepository.java
│       ├── InMemoryMenuRepository.java
│       ├── InMemoryOrderRepository.java
│       ├── InMemoryReservationRepository.java
│       ├── InMemoryStaffRepository.java
│       └── InMemoryTableRepository.java
├── services/
│   ├── BillingService.java
│   ├── InventoryService.java
│   ├── MenuService.java
│   ├── OrderService.java
│   ├── PaymentService.java
│   ├── ReportService.java
│   ├── ReservationService.java
│   ├── StaffService.java
│   └── impl/
│       ├── BillingServiceImpl.java
│       ├── InventoryServiceImpl.java
│       ├── MenuServiceImpl.java
│       ├── OrderServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── ReportServiceImpl.java
│       ├── ReservationServiceImpl.java
│       └── StaffServiceImpl.java
├── strategies/
│   ├── discount/
│   │   ├── DiscountStrategy.java
│   │   ├── FlatDiscountStrategy.java
│   │   └── PercentageDiscountStrategy.java
│   ├── payment/
│   │   ├── PaymentStrategy.java
│   │   ├── CardPaymentStrategy.java
│   │   ├── CashPaymentStrategy.java
│   │   └── MobilePaymentStrategy.java
│   └── tax/
│       ├── TaxStrategy.java
│       ├── ServiceChargeStrategy.java
│       └── StandardTaxStrategy.java
├── factories/           # (Extension point for future)
├── Restaurant.java      # Facade
└── Main.java           # Demo
```

---

## 10. Usage Example

```java
// Initialize restaurant
Restaurant restaurant = new Restaurant("The Gourmet Kitchen");

// Add menu item
MenuItem burger = MenuItem.builder()
    .id("MENU-001")
    .name("Classic Burger")
    .price(new BigDecimal("18.99"))
    .category(MenuCategory.MAIN_COURSE)
    .build();
restaurant.addMenuItem(burger);

// Add table
restaurant.addTable(1, 4, "Indoor");

// Make reservation
Reservation res = restaurant.makeReservation(
    "John Doe", "555-0123", 1, 
    LocalDateTime.now().plusHours(2), 4
);

// Place order
Table table = restaurant.getTable(1).get();
List<OrderItem> items = List.of(new OrderItem(burger, 2));
Order order = restaurant.placeOrder("CUST-001", table, items);

// Process order
restaurant.startPreparing(order.getId());
restaurant.markOrderReady(order.getId());
restaurant.serveOrder(order.getId());

// Generate bill and pay
Bill bill = restaurant.generateBill(order.getId());
Payment payment = restaurant.processPayment(
    bill.getId(), 
    bill.getTotalAmount(), 
    PaymentMethod.CREDIT_CARD
);

// Generate reports
var salesReport = restaurant.getSalesReport(
    LocalDateTime.now().minusDays(1), 
    LocalDateTime.now()
);
```

---

## 11. Key Design Decisions

1. **In-Memory Storage**: Used `ConcurrentHashMap` for thread-safe in-memory storage. Can be easily replaced with database repositories.

2. **Immutable Collections**: `Order` and `Bill` return unmodifiable lists to prevent external modification.

3. **Builder Pattern for Complex Objects**: `MenuItem`, `Order`, and `Bill` use builders for clean construction with validation.

4. **Explicit State Machine**: `OrderStatus` enum defines valid transitions, preventing illegal state changes.

5. **Facade for Simplicity**: `Restaurant` class hides complexity and provides a clean API for common operations.

6. **Strategy for Flexibility**: Payment, discount, and tax calculations can be easily extended without modifying core logic.

7. **Observer for Decoupling**: Kitchen display and inventory alerts are decoupled from order/inventory processing.

---

## 12. Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer
- [ ] Customer authentication and loyalty program
- [ ] Kitchen queue management with priorities
- [ ] Multi-restaurant support
- [ ] Real-time notifications (WebSocket)
- [ ] Analytics dashboard
- [ ] Integration with POS systems

