# Food Delivery System - Low Level Design (Swiggy Clone)

## Overview

A comprehensive food delivery platform that enables customers to browse restaurants, place orders, and track deliveries in real-time. The system supports multiple payment methods, delivery agent management, and real-time notifications to all stakeholders.

---

## Requirements Covered

| Requirement | Implementation |
|-------------|----------------|
| Browse restaurants & menus | `RestaurantService`, `SearchStrategy` |
| Place orders | `OrderService`, `CartService` |
| Restaurant menu management | `RestaurantService.addMenuItem()` |
| Delivery agent fulfillment | `DeliveryService`, `DeliveryAssignmentStrategy` |
| Order tracking & status | `OrderService` lifecycle methods, `Delivery` model |
| Multiple payment methods | `PaymentStrategy` implementations |
| Concurrent order handling | Thread-safe repositories (`ConcurrentHashMap`) |
| Real-time notifications | `OrderEventPublisher`, `OrderObserver` |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         FoodDeliverySystem (Facade)                      │
├─────────────────────────────────────────────────────────────────────────┤
│                              Services Layer                              │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐│
│  │CustomerService│ │RestaurantSvc │ │ OrderService │ │ DeliveryService  ││
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────────┘│
│  ┌──────────────┐ ┌──────────────┐                                      │
│  │ CartService  │ │PaymentService│                                      │
│  └──────────────┘ └──────────────┘                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                            Strategies Layer                              │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────────────┐ │
│  │ PaymentStrategy  │ │  SearchStrategy  │ │ DeliveryAssignmentStrategy││
│  │ • CreditCard     │ │ • NearestFirst   │ │ • NearestAgent            ││
│  │ • UPI            │ │ • HighestRated   │ │ • HighestRatedAgent       ││
│  │ • Wallet         │ │ • FastestDelivery│ │ • LoadBalanced            ││
│  │ • COD            │ │ • ByCuisine      │ └──────────────────────────┘ │
│  └──────────────────┘ └──────────────────┘                              │
├─────────────────────────────────────────────────────────────────────────┤
│                           Observers Layer                                │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                      OrderEventPublisher                            │ │
│  │   ├── CustomerNotificationObserver                                  │ │
│  │   ├── RestaurantNotificationObserver                                │ │
│  │   └── DeliveryAgentNotificationObserver                             │ │
│  └────────────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────┤
│                          Repositories Layer                              │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────────────────┐│
│  │UserRepo    │ │RestaurantRepo│ │OrderRepo   │ │DeliveryAgentRepo     ││
│  └────────────┘ └────────────┘ └────────────┘ └────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. Strategy Pattern
**Purpose:** Enable runtime algorithm selection for payments, search, and delivery assignment.

```
┌─────────────────────┐      ┌─────────────────────────┐
│  PaymentService     │─────▶│   PaymentStrategy       │
└─────────────────────┘      ├─────────────────────────┤
                             │ + processPayment()      │
                             │ + refundPayment()       │
                             └───────────┬─────────────┘
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
         ┌──────────▼──────┐ ┌─────────▼─────────┐ ┌──────▼──────────┐
         │CreditCardStrategy│ │  UPIStrategy     │ │  CODStrategy    │
         └─────────────────┘ └───────────────────┘ └─────────────────┘
```

**SOLID Principle:** Open/Closed Principle - Add new payment methods without modifying existing code.

### 2. Observer Pattern
**Purpose:** Real-time notifications to customers, restaurants, and delivery agents.

```
                    ┌───────────────────────────┐
                    │   OrderEventPublisher     │
                    │   (Subject)               │
                    └─────────────┬─────────────┘
                                  │ notifyObservers()
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
┌─────────▼─────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
│CustomerObserver   │  │RestaurantObserver │  │AgentObserver      │
│• Order updates    │  │• New orders       │  │• Delivery assigned│
│• Delivery tracking│  │• Payment status   │  │• Order ready      │
└───────────────────┘  └───────────────────┘  └───────────────────┘
```

**SOLID Principle:** Single Responsibility - Each observer handles notifications for one type of user.

### 3. Factory Pattern
**Purpose:** Create strategy instances without exposing creation logic.

```java
PaymentStrategyFactory factory = new PaymentStrategyFactory();
PaymentStrategy strategy = factory.getStrategy(PaymentMethod.UPI);
```

### 4. Repository Pattern
**Purpose:** Abstract data access layer for easy swapping between in-memory and database implementations.

**SOLID Principle:** Dependency Inversion - Services depend on repository interfaces, not implementations.

### 5. Facade Pattern
**Purpose:** `FoodDeliverySystem` provides a simplified interface to the complex subsystem.

---

## Class Diagram

### Core Models

```
┌───────────────────┐     ┌───────────────────┐     ┌───────────────────┐
│      User         │     │    Restaurant     │     │     MenuItem      │
├───────────────────┤     ├───────────────────┤     ├───────────────────┤
│ - id: String      │     │ - id: String      │     │ - id: String      │
│ - name: String    │     │ - name: String    │     │ - name: String    │
│ - email: String   │     │ - location: Loc   │     │ - price: BigDec   │
│ - phone: String   │     │ - status: Enum    │     │ - cuisineType     │
│ - role: UserRole  │     │ - menu: Map       │     │ - status: Enum    │
│ - addresses: List │     │ - rating: double  │     │ - vegetarian: bool│
└───────────────────┘     └───────────────────┘     └───────────────────┘
         │                         │                         │
         │                         │                         │
         ▼                         ▼                         ▼
┌───────────────────┐     ┌───────────────────┐     ┌───────────────────┐
│  DeliveryAgent    │     │      Order        │     │    OrderItem      │
├───────────────────┤     ├───────────────────┤     ├───────────────────┤
│ - currentLocation │     │ - id: String      │     │ - menuItemId      │
│ - status: Enum    │     │ - customerId      │     │ - quantity: int   │
│ - vehicleNumber   │     │ - restaurantId    │     │ - unitPrice       │
│ - rating: double  │     │ - items: List     │     │ - specialInstr    │
│ - totalDeliveries │     │ - status: Enum    │     └───────────────────┘
└───────────────────┘     │ - totalAmount     │
                          │ - paymentMethod   │
                          └───────────────────┘
```

### Service Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                      OrderServiceImpl                            │
├─────────────────────────────────────────────────────────────────┤
│ - orderRepository: OrderRepository                               │
│ - cartRepository: CartRepository                                 │
│ - restaurantRepository: RestaurantRepository                     │
│ - paymentService: PaymentService                                 │
│ - deliveryService: DeliveryService                               │
│ - deliveryFeeStrategy: DeliveryFeeStrategy                       │
│ - eventPublisher: OrderEventPublisher                            │
├─────────────────────────────────────────────────────────────────┤
│ + placeOrder(customerId, address, paymentMethod): Order          │
│ + confirmOrder(orderId): void                                    │
│ + startPreparing(orderId): void                                  │
│ + markReady(orderId): void                                       │
│ + markOutForDelivery(orderId): void                              │
│ + markDelivered(orderId): void                                   │
│ + cancelOrder(orderId, reason): void                             │
└─────────────────────────────────────────────────────────────────┘
```

---

## Order State Machine

```
                    ┌──────────┐
                    │  PLACED  │
                    └────┬─────┘
                         │ confirmOrder()
                         ▼
                    ┌──────────┐
              ┌────▶│CONFIRMED │
              │     └────┬─────┘
              │          │ startPreparing()
              │          ▼
              │     ┌──────────┐
   cancel()   │     │PREPARING │
              │     └────┬─────┘
              │          │ markReady()
              │          ▼
              │     ┌────────────────┐
              └────▶│READY_FOR_PICKUP│
                    └────┬───────────┘
                         │ markOutForDelivery()
                         ▼
                    ┌────────────────┐
                    │OUT_FOR_DELIVERY│──────────────────┐
                    └────┬───────────┘                  │
                         │ markDelivered()              │ (cannot cancel)
                         ▼                              │
                    ┌──────────┐                        │
                    │DELIVERED │◀───────────────────────┘
                    └──────────┘
                    
                    ┌──────────┐
                    │CANCELLED │ (from PLACED, CONFIRMED, PREPARING, READY)
                    └──────────┘
```

---

## SOLID Principles Application

### 1. Single Responsibility Principle (SRP)
Each class has one reason to change:
- `CustomerService` - Customer management only
- `RestaurantService` - Restaurant & menu management only
- `OrderService` - Order lifecycle only
- `PaymentService` - Payment processing only
- `DeliveryService` - Delivery management only

### 2. Open/Closed Principle (OCP)
System is open for extension, closed for modification:
- Add new `PaymentStrategy` implementations without changing `PaymentService`
- Add new `RestaurantSearchStrategy` without modifying search logic
- Add new `DeliveryAssignmentStrategy` for different assignment algorithms

### 3. Liskov Substitution Principle (LSP)
Subtypes are substitutable:
- Any `PaymentStrategy` implementation can be used interchangeably
- `DeliveryAgent` extends `User` and can be used where `User` is expected

### 4. Interface Segregation Principle (ISP)
Clients depend only on methods they use:
- `PaymentStrategy` interface has only payment-related methods
- `OrderObserver` has single `onOrderEvent()` method
- Repository interfaces are focused on specific entity operations

### 5. Dependency Inversion Principle (DIP)
High-level modules don't depend on low-level modules:
- Services depend on repository interfaces, not implementations
- `OrderService` depends on `PaymentService` interface
- Strategies are injected via constructor

---

## Extension Points

### Adding a New Payment Method
1. Create new class implementing `PaymentStrategy`
2. Register in `PaymentStrategyFactory`
3. Add enum value to `PaymentMethod`

```java
public class NetBankingStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Implementation
    }
    // ...
}

// Register
factory.registerStrategy(PaymentMethod.NET_BANKING, new NetBankingStrategy());
```

### Adding a New Search Algorithm
1. Create new class implementing `RestaurantSearchStrategy`
2. Use via `SearchStrategyFactory`

```java
public class PopularityStrategy implements RestaurantSearchStrategy {
    @Override
    public List<Restaurant> search(List<Restaurant> restaurants, Location location) {
        // Sort by order count
    }
}
```

### Adding a New Notification Channel
1. Create new `OrderObserver` implementation
2. Register with `OrderEventPublisher`

```java
public class SMSNotificationObserver implements OrderObserver {
    @Override
    public void onOrderEvent(OrderEvent event) {
        // Send SMS
    }
}
```

### Switching to Database Storage
1. Create new repository implementations (e.g., `JpaUserRepository`)
2. Replace in `FoodDeliverySystem` constructor

```java
// Replace:
this.userRepository = new InMemoryUserRepository();
// With:
this.userRepository = new JpaUserRepository(entityManager);
```

---

## Concurrency Handling

1. **Thread-safe Collections:** All repositories use `ConcurrentHashMap`
2. **CopyOnWriteArrayList:** Observer list in `OrderEventPublisher`
3. **Atomic Operations:** ID generation using `UUID`
4. **Async Notifications:** Optional async event publishing with `ExecutorService`

---

## File Structure

```
fooddelivery/
├── enums/
│   ├── UserRole.java
│   ├── OrderStatus.java
│   ├── PaymentStatus.java
│   ├── PaymentMethod.java
│   ├── DeliveryStatus.java
│   ├── RestaurantStatus.java
│   ├── MenuItemStatus.java
│   ├── CuisineType.java
│   ├── AgentStatus.java
│   └── NotificationType.java
├── exceptions/
│   ├── FoodDeliveryException.java
│   ├── RestaurantException.java
│   ├── OrderException.java
│   ├── PaymentException.java
│   ├── DeliveryException.java
│   ├── CartException.java
│   ├── UserException.java
│   └── MenuException.java
├── models/
│   ├── Location.java
│   ├── User.java
│   ├── DeliveryAgent.java
│   ├── MenuItem.java
│   ├── Restaurant.java
│   ├── OrderItem.java
│   ├── Cart.java
│   ├── Payment.java
│   ├── Delivery.java
│   ├── Order.java
│   └── Review.java
├── repositories/
│   ├── UserRepository.java
│   ├── RestaurantRepository.java
│   ├── MenuItemRepository.java
│   ├── OrderRepository.java
│   ├── CartRepository.java
│   ├── PaymentRepository.java
│   ├── DeliveryRepository.java
│   ├── DeliveryAgentRepository.java
│   └── impl/
│       └── InMemory*.java (8 files)
├── strategies/
│   ├── payment/
│   │   ├── PaymentStrategy.java
│   │   ├── CreditCardPaymentStrategy.java
│   │   ├── UPIPaymentStrategy.java
│   │   ├── WalletPaymentStrategy.java
│   │   └── CashOnDeliveryStrategy.java
│   ├── search/
│   │   ├── RestaurantSearchStrategy.java
│   │   ├── NearestRestaurantStrategy.java
│   │   ├── HighestRatedStrategy.java
│   │   ├── FastestDeliveryStrategy.java
│   │   └── CuisineFilterStrategy.java
│   ├── delivery/
│   │   ├── DeliveryAssignmentStrategy.java
│   │   ├── NearestAgentStrategy.java
│   │   ├── HighestRatedAgentStrategy.java
│   │   └── LoadBalancedAgentStrategy.java
│   └── pricing/
│       ├── DeliveryFeeStrategy.java
│       ├── DistanceBasedFeeStrategy.java
│       └── SurgePricingStrategy.java
├── observers/
│   ├── OrderEvent.java
│   ├── OrderObserver.java
│   ├── OrderSubject.java
│   ├── OrderEventPublisher.java
│   ├── CustomerNotificationObserver.java
│   ├── RestaurantNotificationObserver.java
│   └── DeliveryAgentNotificationObserver.java
├── services/
│   ├── CustomerService.java
│   ├── RestaurantService.java
│   ├── CartService.java
│   ├── OrderService.java
│   ├── PaymentService.java
│   ├── DeliveryService.java
│   └── impl/
│       └── *ServiceImpl.java (6 files)
├── factories/
│   ├── PaymentStrategyFactory.java
│   ├── SearchStrategyFactory.java
│   └── DeliveryStrategyFactory.java
├── FoodDeliverySystem.java
└── Main.java
```

---

## Usage Example

```java
// Initialize system
FoodDeliverySystem system = new FoodDeliverySystem();

// Register customer
User customer = system.getCustomerService().registerCustomer(
    "John Doe", "john@example.com", "+91-9876543210");

// Register restaurant and add menu items
Restaurant restaurant = system.getRestaurantService().registerRestaurant(
    "Pizza Palace", "owner-001", location);
MenuItem pizza = system.getRestaurantService().addMenuItem(
    restaurant.getId(), "Margherita", "Classic pizza", 
    new BigDecimal("299"), CuisineType.ITALIAN);

// Add to cart
system.getCartService().addToCart(customer.getId(), pizza, 2);

// Place order
Order order = system.getOrderService().placeOrder(
    customer.getId(), deliveryAddress, PaymentMethod.UPI);

// Order lifecycle
system.getOrderService().confirmOrder(order.getId());
system.getOrderService().startPreparing(order.getId());
system.getOrderService().markReady(order.getId());
system.getOrderService().markOutForDelivery(order.getId());
system.getOrderService().markDelivered(order.getId());

// Rate
system.getOrderService().rateOrder(order.getId(), 5.0, "Excellent!");
```

---

## Key Design Decisions

1. **Value Objects for Location:** Immutable `Location` class with Haversine distance calculation
2. **Cart tied to single restaurant:** Prevents complexity of multi-restaurant orders
3. **Event-driven notifications:** Decouples order processing from notification logic
4. **Strategy injection:** Allows runtime algorithm selection
5. **In-memory repositories:** Easy testing and demo; easily swappable for production databases
6. **BigDecimal for money:** Precise financial calculations

---

## Future Enhancements

1. **Multi-restaurant orders:** Support cart items from multiple restaurants
2. **Coupon system:** Discount code validation and application
3. **Subscription plans:** Premium delivery memberships
4. **Restaurant analytics:** Order statistics and insights
5. **Delivery batching:** Multiple orders per delivery trip
6. **Real-time tracking:** WebSocket-based live location updates
7. **Scheduled orders:** Future delivery time selection
8. **Review moderation:** Comment filtering and approval workflow



