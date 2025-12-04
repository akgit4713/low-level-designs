# Car Rental System - Low-Level Design

## Overview

A comprehensive car rental system that allows customers to browse, search, and reserve cars for specific dates. The system handles the complete rental lifecycle including reservations, payments, and notifications.

## Requirements Covered

1. ✅ Browse and reserve available cars for specific dates
2. ✅ Car details (make, model, year, license plate, rental price)
3. ✅ Search by car type, price range, and availability
4. ✅ Reservation CRUD (create, modify, cancel)
5. ✅ Car availability tracking and status updates
6. ✅ Customer management with driver's license info
7. ✅ Payment processing
8. ✅ Concurrent reservation handling with data consistency

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CarRentalSystem (Facade)                        │
│   Provides simplified API for all operations                            │
├─────────────────────────────────────────────────────────────────────────┤
│                              SERVICES                                    │
│  ┌─────────────┐ ┌──────────────────┐ ┌────────────────┐ ┌────────────┐ │
│  │ CarService  │ │ ReservationSvc   │ │ CustomerService│ │PaymentSvc  │ │
│  │             │ │ (with observers) │ │                │ │            │ │
│  └──────┬──────┘ └────────┬─────────┘ └───────┬────────┘ └─────┬──────┘ │
├─────────┼─────────────────┼───────────────────┼────────────────┼────────┤
│         │                 │                   │                │        │
│  ┌──────▼──────┐ ┌────────▼────────┐ ┌───────▼────────┐ ┌─────▼──────┐ │
│  │CarRepository│ │ReservationRepo  │ │CustomerRepo    │ │PaymentRepo │ │
│  └─────────────┘ └─────────────────┘ └────────────────┘ └────────────┘ │
├─────────────────────────────────────────────────────────────────────────┤
│                            STRATEGIES                                    │
│  ┌────────────────┐  ┌─────────────────┐  ┌────────────────┐            │
│  │PricingStrategy │  │ PaymentStrategy │  │ SearchStrategy │            │
│  │ • Standard     │  │ • CreditCard    │  │ • Basic        │            │
│  │ • Weekend      │  │ • DebitCard     │  │ • Sorted       │            │
│  │ • LongTerm     │  │ • Cash          │  │                │            │
│  └────────────────┘  └─────────────────┘  └────────────────┘            │
├─────────────────────────────────────────────────────────────────────────┤
│                            OBSERVERS                                     │
│  ┌──────────────────────────┐  ┌──────────────────────────┐             │
│  │ EmailNotificationObserver│  │ SMSNotificationObserver  │             │
│  └──────────────────────────┘  └──────────────────────────┘             │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Core Components

### 1. Models (Domain Entities)

| Class | Purpose |
|-------|---------|
| `Car` | Represents a car with make, model, year, license plate, type, and pricing |
| `Customer` | Customer information including name, contact, and driver's license |
| `DriverLicense` | Value object for license details with validity checking |
| `Reservation` | Links car, customer, dates with status tracking |
| `Payment` | Payment record with method, amount, and status |
| `SearchCriteria` | Value object for flexible car search parameters |

### 2. Enums

| Enum | Values |
|------|--------|
| `CarType` | SEDAN, SUV, HATCHBACK, LUXURY, SPORTS, MINIVAN, CONVERTIBLE |
| `CarStatus` | AVAILABLE, RESERVED, RENTED, UNDER_MAINTENANCE, OUT_OF_SERVICE |
| `ReservationStatus` | PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED |
| `PaymentMethod` | CREDIT_CARD, DEBIT_CARD, CASH, BANK_TRANSFER, DIGITAL_WALLET |
| `PaymentStatus` | PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED |

### 3. Services

| Service | Responsibility |
|---------|----------------|
| `CarService` | Car management, search, availability checking |
| `CustomerService` | Customer registration and validation |
| `ReservationService` | Reservation lifecycle with concurrency control |
| `PaymentService` | Payment processing with multiple methods |

### 4. Repositories

| Repository | Purpose |
|------------|---------|
| `CarRepository` | CRUD + find by type, status, license plate |
| `CustomerRepository` | CRUD + find by email, phone, license |
| `ReservationRepository` | CRUD + find by customer, car, date range |
| `PaymentRepository` | CRUD + find by reservation, status |

---

## Design Patterns Used

### 1. **Strategy Pattern**
Used for interchangeable algorithms:

```java
// Pricing strategies
PricingStrategy pricing = new WeekendPricingStrategy();
BigDecimal price = pricing.calculatePrice(car, startDate, endDate);

// Payment strategies  
PaymentStrategy payment = new CreditCardPaymentStrategy();
payment.processPayment(payment);

// Search strategies
SearchStrategy search = new SortedSearchStrategy(SortBy.PRICE_ASC);
List<Car> results = search.search(cars, criteria);
```

**Why**: Allows adding new pricing models (seasonal, loyalty) or payment methods without modifying existing code.

### 2. **Observer Pattern**
Used for notifications:

```java
reservationService.addObserver(new EmailNotificationObserver());
reservationService.addObserver(new SMSNotificationObserver());

// When reservation is created, all observers are notified
Reservation r = reservationService.createReservation(...);
// → Email and SMS sent automatically
```

**Why**: Decouples reservation logic from notification delivery.

### 3. **Repository Pattern**
Abstracts data access:

```java
public interface CarRepository extends Repository<Car, String> {
    List<Car> findByType(CarType type);
    List<Car> findAvailable();
}
```

**Why**: Enables swapping in-memory storage for database without changing service logic.

### 4. **Factory Pattern**
Simplifies object creation:

```java
Car sedan = CarFactory.createSedan("Toyota", "Camry", 2023, "ABC-1234");
Customer john = CustomerFactory.createSampleCustomer("John", "john@email.com");
```

**Why**: Centralizes creation logic and enforces valid object construction.

### 5. **Builder Pattern**
For complex object construction:

```java
Car car = new Car.Builder()
    .id(UUID.randomUUID().toString())
    .make("Toyota")
    .model("Camry")
    .year(2023)
    .licensePlate("ABC-1234")
    .carType(CarType.SEDAN)
    .basePricePerDay(BigDecimal.valueOf(50))
    .build();
```

**Why**: Makes construction readable and allows optional parameters.

### 6. **Facade Pattern**
Simplifies subsystem access:

```java
CarRentalSystem system = new CarRentalSystem();
system.addCar(car);
system.registerCustomer(customer);
system.makeReservation(customerId, carId, start, end);
system.processPayment(reservationId, PaymentMethod.CREDIT_CARD);
```

**Why**: Provides a clean, simple API for clients.

---

## SOLID Principles Applied

### Single Responsibility (SRP)
- `CarService` only handles car operations
- `ReservationService` only handles bookings
- `PaymentService` only handles payments
- Each observer handles one notification channel

### Open/Closed (OCP)
- New `PricingStrategy` implementations can be added without modifying existing code
- New `PaymentStrategy` implementations can be added for new payment methods
- New `SearchStrategy` implementations for advanced search

### Liskov Substitution (LSP)
- All `PricingStrategy` implementations are interchangeable
- All `Repository` implementations can substitute each other

### Interface Segregation (ISP)
- `PricingStrategy`, `PaymentStrategy`, `SearchStrategy` are focused interfaces
- `ReservationObserver` only defines reservation-related events

### Dependency Inversion (DIP)
- Services depend on repository interfaces, not implementations
- `CarRentalSystem` accepts strategies via constructor injection

---

## Concurrency Handling

```java
public class ReservationServiceImpl {
    private final Lock reservationLock = new ReentrantLock();
    
    public Reservation createReservation(...) {
        reservationLock.lock();
        try {
            // Check availability
            if (!carService.isCarAvailable(carId, startDate, endDate)) {
                throw new CarNotAvailableException(...);
            }
            // Create reservation atomically
            return reservationRepository.save(reservation);
        } finally {
            reservationLock.unlock();
        }
    }
}
```

Thread-safety is ensured through:
1. **ReentrantLock** for reservation creation
2. **ConcurrentHashMap** in repositories
3. **Synchronized** setters for mutable state

---

## Extension Points

### Adding a New Pricing Strategy

```java
public class SeasonalPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Car car, LocalDate start, LocalDate end) {
        // Apply seasonal multipliers
    }
}

// Usage
CarRentalSystem system = new CarRentalSystem(
    new SeasonalPricingStrategy(), 
    new BasicSearchStrategy()
);
```

### Adding a New Payment Method

```java
public class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Integrate with PayPal API
    }
}

// Register
paymentService.registerPaymentStrategy(PaymentMethod.DIGITAL_WALLET, 
    new PayPalPaymentStrategy());
```

### Adding New Notifications

```java
public class PushNotificationObserver implements ReservationObserver {
    @Override
    public void onReservationCreated(Reservation r) {
        // Send push notification
    }
    // ... other events
}

system.addReservationObserver(new PushNotificationObserver());
```

---

## Class Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MODELS                                     │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐          │
│  │     Car      │    │   Customer   │    │   DriverLicense  │          │
│  ├──────────────┤    ├──────────────┤    ├──────────────────┤          │
│  │ id           │    │ id           │    │ licenseNumber    │          │
│  │ make         │    │ name         │    │ state            │          │
│  │ model        │    │ email        │    │ issueDate        │          │
│  │ year         │    │ phone        │    │ expiryDate       │          │
│  │ licensePlate │    │ driverLicense├───►│ isValid()        │          │
│  │ carType      │    └──────────────┘    └──────────────────┘          │
│  │ pricePerDay  │                                                       │
│  │ status       │                                                       │
│  └──────────────┘                                                       │
│         ▲                                                               │
│         │                                                               │
│  ┌──────┴───────────────────────────────────────────────────────────┐  │
│  │                        Reservation                                │  │
│  ├──────────────────────────────────────────────────────────────────┤  │
│  │ id, car, customer, startDate, endDate, status, totalAmount       │  │
│  │ isModifiable(), isCancellable(), overlaps(), getDurationInDays() │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│         ▲                                                               │
│         │                                                               │
│  ┌──────┴────────────────┐                                             │
│  │       Payment         │                                             │
│  ├───────────────────────┤                                             │
│  │ id, reservation, amount, paymentMethod, status, transactionRef    │ │
│  └───────────────────────┘                                             │
└────────────────────────────────────────────────────────────────────────┘
```

---

## Reservation Flow Sequence

```
Customer              CarRentalSystem          Services              Repository
   │                       │                      │                      │
   │ searchCars(criteria)  │                      │                      │
   │──────────────────────►│                      │                      │
   │                       │ search()             │                      │
   │                       │─────────────────────►│                      │
   │                       │                      │ findAll()            │
   │                       │                      │─────────────────────►│
   │                       │                      │◄─────────────────────│
   │◄──────────────────────│◄─────────────────────│                      │
   │                       │                      │                      │
   │ makeReservation()     │                      │                      │
   │──────────────────────►│                      │                      │
   │                       │ createReservation()  │                      │
   │                       │─────────────────────►│                      │
   │                       │                      │ [Lock acquired]      │
   │                       │                      │ isCarAvailable()     │
   │                       │                      │─────────────────────►│
   │                       │                      │◄─────────────────────│
   │                       │                      │ calculatePrice()     │
   │                       │                      │ save()               │
   │                       │                      │─────────────────────►│
   │                       │                      │ [Lock released]      │
   │                       │                      │ notifyObservers()    │
   │◄──────────────────────│◄─────────────────────│                      │
   │                       │                      │                      │
   │ processPayment()      │                      │                      │
   │──────────────────────►│─────────────────────►│                      │
   │                       │                      │ strategy.process()   │
   │◄──────────────────────│◄─────────────────────│                      │
```

---

## File Structure

```
carrental/
├── enums/
│   ├── CarType.java
│   ├── CarStatus.java
│   ├── ReservationStatus.java
│   ├── PaymentMethod.java
│   └── PaymentStatus.java
├── exceptions/
│   ├── CarRentalException.java
│   ├── CarNotFoundException.java
│   ├── CarNotAvailableException.java
│   ├── CustomerNotFoundException.java
│   ├── ReservationNotFoundException.java
│   ├── ReservationException.java
│   ├── PaymentException.java
│   └── InvalidDateRangeException.java
├── models/
│   ├── Car.java
│   ├── Customer.java
│   ├── DriverLicense.java
│   ├── Reservation.java
│   ├── Payment.java
│   └── SearchCriteria.java
├── repositories/
│   ├── Repository.java
│   ├── CarRepository.java
│   ├── CustomerRepository.java
│   ├── ReservationRepository.java
│   ├── PaymentRepository.java
│   └── impl/
│       ├── InMemoryCarRepository.java
│       ├── InMemoryCustomerRepository.java
│       ├── InMemoryReservationRepository.java
│       └── InMemoryPaymentRepository.java
├── strategies/
│   ├── pricing/
│   │   ├── PricingStrategy.java
│   │   ├── StandardPricingStrategy.java
│   │   ├── WeekendPricingStrategy.java
│   │   └── LongTermDiscountPricingStrategy.java
│   ├── payment/
│   │   ├── PaymentStrategy.java
│   │   ├── CreditCardPaymentStrategy.java
│   │   ├── DebitCardPaymentStrategy.java
│   │   └── CashPaymentStrategy.java
│   └── search/
│       ├── SearchStrategy.java
│       ├── BasicSearchStrategy.java
│       └── SortedSearchStrategy.java
├── observers/
│   ├── ReservationObserver.java
│   ├── EmailNotificationObserver.java
│   └── SMSNotificationObserver.java
├── services/
│   ├── CarService.java
│   ├── CustomerService.java
│   ├── ReservationService.java
│   ├── PaymentService.java
│   └── impl/
│       ├── CarServiceImpl.java
│       ├── CustomerServiceImpl.java
│       ├── ReservationServiceImpl.java
│       └── PaymentServiceImpl.java
├── factories/
│   ├── CarFactory.java
│   └── CustomerFactory.java
├── CarRentalSystem.java (Facade)
└── Main.java (Demo)
```

---

## Usage Example

```java
// Create system with custom strategies
CarRentalSystem system = new CarRentalSystem(
    new LongTermDiscountPricingStrategy(),
    new SortedSearchStrategy(SortBy.PRICE_ASC)
);

// Add cars
Car car = CarFactory.createSUV("Honda", "CR-V", 2023, "ABC-1234");
system.addCar(car);

// Register customer
Customer customer = CustomerFactory.createSampleCustomer("John", "john@email.com");
system.registerCustomer(customer);

// Search for cars
SearchCriteria criteria = new SearchCriteria.Builder()
    .carType(CarType.SUV)
    .maxPrice(BigDecimal.valueOf(100))
    .dateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5))
    .build();
List<Car> available = system.searchCars(criteria);

// Make reservation
Reservation reservation = system.makeReservation(
    customer.getId(), 
    car.getId(), 
    LocalDate.now().plusDays(1), 
    LocalDate.now().plusDays(5)
);

// Process payment
Payment payment = system.processPayment(reservation.getId(), PaymentMethod.CREDIT_CARD);

// Confirm and manage reservation
system.confirmReservation(reservation.getId());
system.pickupCar(reservation.getId());
system.returnCar(reservation.getId());
```

---

## Design Rationale

1. **Extensibility**: New pricing, payment, or search strategies can be added without modifying existing code
2. **Testability**: All services depend on interfaces, enabling mock implementations for testing
3. **Loose Coupling**: Components interact through well-defined interfaces
4. **Concurrency Safety**: Locking mechanism prevents double-booking
5. **Clean Separation**: Models, services, repositories, and strategies are clearly separated
6. **Notification Flexibility**: Observer pattern allows adding new notification channels easily



