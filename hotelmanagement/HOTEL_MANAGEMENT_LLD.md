# Hotel Management System - Low-Level Design

## Overview

A comprehensive hotel management system that handles guest bookings, room management, check-in/check-out, billing, housekeeping, and reporting. The system is designed following SOLID principles and common design patterns for extensibility and maintainability.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **Hotel (Facade)** | Single entry point for all operations; wires components together |
| **RoomService** | Manage rooms, types, availability, and housekeeping status |
| **ReservationService** | Handle room bookings, modifications, and cancellations |
| **GuestService** | Manage guest profiles, preferences, and loyalty programs |
| **CheckInOutService** | Process guest check-ins and check-outs |
| **BillingService** | Generate bills with room charges, services, and taxes |
| **PaymentService** | Process payments via multiple payment methods |
| **HousekeepingService** | Manage room cleaning schedules and maintenance |
| **ReportService** | Generate analytics: occupancy reports, revenue analysis |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────┐
│  RoomType         - SINGLE, DOUBLE, DELUXE, SUITE               │
│  RoomStatus       - AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE, │
│                     CLEANING                                     │
│  ReservationStatus- PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT,│
│                     CANCELLED, NO_SHOW                          │
│  PaymentMethod    - CASH, CREDIT_CARD, DEBIT_CARD, ONLINE       │
│  PaymentStatus    - PENDING, COMPLETED, FAILED, REFUNDED        │
│  ServiceType      - ROOM_SERVICE, LAUNDRY, SPA, MINIBAR, PARKING│
└─────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `Room` | Hotel room with type, floor, price, amenities, status |
| `Guest` | Guest profile with contact info, preferences, loyalty status |
| `Reservation` | Booking with check-in/out dates, room, guest, status |
| `Bill` | Invoice with room charges, services, discounts, taxes |
| `Payment` | Payment transaction with method, amount, status |
| `ServiceCharge` | Additional service charges (room service, minibar, etc.) |
| `HousekeepingTask` | Cleaning/maintenance task for rooms |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `PricingStrategy` | Calculate room rates | Standard, Weekend, Seasonal, Dynamic |
| `PaymentStrategy` | Process different payment types | Cash, Card, Online |
| `DiscountStrategy` | Calculate discounts | Loyalty, Early Booking, Long Stay |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `ReservationObserver` | React to booking events (confirmation, cancellation) |
| `RoomStatusObserver` | React to room status changes (cleaned, occupied) |

---

## 3. Class Diagram

```
                          ┌──────────────────────┐
                          │       Hotel          │
                          │      (Facade)        │
                          └──────────┬───────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       │             │               │               │             │
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ RoomService │ │Reservation  │ │ Guest       │ │Housekeeping │ │CheckInOut   │
│             │ │ Service     │ │ Service     │ │ Service     │ │ Service     │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Room         │ │Reservation  │ │Guest        │ │Housekeeping │ │Billing      │
│Repository   │ │Repository   │ │Repository   │ │Repository   │ │Service      │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └──────┬──────┘
                                                                       │
                                                               ┌───────┴───────┐
                                                               │               │
                                                               ▼               ▼
                                                        ┌───────────┐   ┌───────────┐
                                                        │Payment    │   │Pricing    │
                                                        │Service    │   │Strategy   │
                                                        └───────────┘   └───────────┘
```

---

## 4. Design Patterns Used

### 1. **Facade Pattern** - `Hotel` class
- Provides a simplified interface to the complex subsystem
- Coordinates between all services
- Entry point for all hotel operations

### 2. **Strategy Pattern** - Pricing, Payment, Discount
- Allows runtime selection of algorithms
- Easy to add new pricing models, payment methods, or discount types
- Follows Open/Closed Principle

```java
// Adding a new pricing strategy:
roomService.setPricingStrategy(new DynamicPricingStrategy(demandFactor));

// Adding a new payment method:
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTOCURRENCY, 
    new CryptoPaymentStrategy()
);
```

### 3. **Observer Pattern** - Reservation & Room notifications
- Guests receive booking confirmations
- Housekeeping notified of checkouts
- Decouples event producers from consumers

```java
public interface ReservationObserver {
    void onReservationCreated(Reservation reservation);
    void onReservationConfirmed(Reservation reservation);
    void onReservationCancelled(Reservation reservation);
    void onCheckIn(Reservation reservation);
    void onCheckOut(Reservation reservation);
}
```

### 4. **Builder Pattern** - `Reservation`, `Room`, `Bill`
- Complex object construction with many optional parameters
- Fluent API for readability
- Validation during build

```java
Reservation reservation = Reservation.builder()
    .id("RES-001")
    .guest(guest)
    .room(room)
    .checkInDate(LocalDate.now())
    .checkOutDate(LocalDate.now().plusDays(3))
    .numberOfGuests(2)
    .build();
```

### 5. **Repository Pattern** - Data Access
- Abstracts data storage from business logic
- Easy to swap implementations (in-memory → database)
- Follows Dependency Inversion Principle

### 6. **State Pattern** - Room & Reservation Lifecycle
- `RoomStatus` and `ReservationStatus` enums with valid transition rules
- Encapsulates state-specific behavior
- Prevents invalid state transitions

```java
public boolean canTransitionTo(RoomStatus newStatus) {
    return switch (this) {
        case AVAILABLE -> newStatus == OCCUPIED || newStatus == RESERVED || newStatus == MAINTENANCE;
        case OCCUPIED -> newStatus == CLEANING;
        case CLEANING -> newStatus == AVAILABLE || newStatus == MAINTENANCE;
        case RESERVED -> newStatus == OCCUPIED || newStatus == AVAILABLE;
        case MAINTENANCE -> newStatus == AVAILABLE;
    };
}
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each service handles one domain concern
- `RoomService` only manages rooms
- `ReservationService` only manages bookings
- Models are pure data holders

### Open/Closed Principle (OCP)
- New pricing strategies via `PricingStrategy` without modifying `RoomService`
- New payment methods via `PaymentStrategy`
- New discount types via `DiscountStrategy`

### Liskov Substitution Principle (LSP)
- All `PricingStrategy` implementations are interchangeable
- All `Repository` implementations fulfill the same contract

### Interface Segregation Principle (ISP)
- `ReservationObserver` is separate from `RoomStatusObserver`
- Clients only depend on interfaces they need

### Dependency Inversion Principle (DIP)
- Services depend on interfaces, not concrete implementations
- `ReservationService` depends on `ReservationRepository` interface
- `PaymentService` depends on `PaymentStrategy` interface

---

## 6. Concurrency Handling

### Thread-Safe Components

```java
// Room uses ReentrantLock for atomic status changes
public class Room {
    private final ReentrantLock lock = new ReentrantLock();
    
    public boolean tryReserve() {
        lock.lock();
        try {
            if (status == RoomStatus.AVAILABLE) {
                status = RoomStatus.RESERVED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}

// Reservation uses optimistic locking for concurrent modifications
public class Reservation {
    private volatile ReservationStatus status;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}
```

- All repositories use `ConcurrentHashMap`
- Room booking uses double-checked locking
- Bill generation is atomic

---

## 7. Extension Points

### Adding a New Room Type

```java
// 1. Add to enum
public enum RoomType {
    SINGLE, DOUBLE, DELUXE, SUITE, PRESIDENTIAL  // New type
}

// 2. Configure pricing in strategy
pricingStrategy.setBaseRate(RoomType.PRESIDENTIAL, new BigDecimal("999.99"));
```

### Adding a New Pricing Strategy

```java
// 1. Create new strategy
public class DynamicPricingStrategy implements PricingStrategy {
    private final double demandMultiplier;
    
    @Override
    public BigDecimal calculateRate(Room room, LocalDate date) {
        BigDecimal baseRate = room.getBaseRate();
        return baseRate.multiply(BigDecimal.valueOf(demandMultiplier));
    }
}

// 2. Set on room service
roomService.setPricingStrategy(new DynamicPricingStrategy(1.5));
```

### Adding a New Payment Method

```java
// 1. Add to enum
public enum PaymentMethod {
    CASH, CREDIT_CARD, DEBIT_CARD, ONLINE, CRYPTOCURRENCY  // New
}

// 2. Create new strategy
public class CryptoPaymentStrategy implements PaymentStrategy { ... }

// 3. Register with service
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTOCURRENCY, 
    new CryptoPaymentStrategy()
);
```

### Adding New Service Types

```java
// 1. Add to enum
public enum ServiceType {
    ROOM_SERVICE, LAUNDRY, SPA, MINIBAR, PARKING, AIRPORT_TRANSFER  // New
}

// 2. Add pricing in billing service
billingService.setServiceRate(ServiceType.AIRPORT_TRANSFER, new BigDecimal("75.00"));
```

---

## 8. Reservation Lifecycle Flow

```
[Guest Makes Booking]
       │
       ▼
   ┌────────┐     ┌───────────┐
   │PENDING │────▶│ CONFIRMED │
   └────────┘     └─────┬─────┘
       │                │
       │ cancel         │ check-in
       │                ▼
       ▼          ┌───────────┐
  ┌─────────┐     │CHECKED_IN │
  │CANCELLED│     └─────┬─────┘
  └─────────┘           │
       ▲                │ check-out
       │                ▼
  ┌─────────┐     ┌────────────┐
  │ NO_SHOW │     │CHECKED_OUT │
  └─────────┘     └────────────┘
```

---

## 9. Room Status Flow

```
[Room Created]
       │
       ▼
  ┌───────────┐
  │ AVAILABLE │◄─────────────────┐
  └─────┬─────┘                  │
        │                        │
   reserve/check-in       cleaned│
        │                        │
        ▼                        │
  ┌───────────┐           ┌──────┴────┐
  │ RESERVED/ │──────────▶│ CLEANING  │
  │ OCCUPIED  │ check-out └───────────┘
  └─────┬─────┘                  │
        │                        │
        │ issue found            │ issue found
        ▼                        ▼
  ┌─────────────┐         ┌─────────────┐
  │ MAINTENANCE │◄────────│ MAINTENANCE │
  └──────┬──────┘         └─────────────┘
         │
         │ fixed
         ▼
  ┌───────────┐
  │ AVAILABLE │
  └───────────┘
```

---

## 10. File Structure

```
hotelmanagement/
├── enums/
│   ├── RoomType.java
│   ├── RoomStatus.java
│   ├── ReservationStatus.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   └── ServiceType.java
├── exceptions/
│   ├── HotelException.java
│   ├── ReservationException.java
│   ├── RoomException.java
│   ├── PaymentException.java
│   └── GuestException.java
├── models/
│   ├── Room.java
│   ├── Guest.java
│   ├── Reservation.java
│   ├── Bill.java
│   ├── Payment.java
│   ├── ServiceCharge.java
│   ├── HousekeepingTask.java
│   └── Address.java
├── observers/
│   ├── ReservationObserver.java (interface)
│   ├── RoomStatusObserver.java (interface)
│   ├── EmailNotificationObserver.java
│   └── HousekeepingNotificationObserver.java
├── repositories/
│   ├── Repository.java (interface)
│   ├── RoomRepository.java (interface)
│   ├── ReservationRepository.java (interface)
│   ├── GuestRepository.java (interface)
│   └── impl/
│       ├── InMemoryRoomRepository.java
│       ├── InMemoryReservationRepository.java
│       ├── InMemoryGuestRepository.java
│       └── InMemoryBillRepository.java
├── services/
│   ├── RoomService.java (interface)
│   ├── ReservationService.java (interface)
│   ├── GuestService.java (interface)
│   ├── CheckInOutService.java (interface)
│   ├── BillingService.java (interface)
│   ├── PaymentService.java (interface)
│   ├── HousekeepingService.java (interface)
│   ├── ReportService.java (interface)
│   └── impl/
│       ├── RoomServiceImpl.java
│       ├── ReservationServiceImpl.java
│       ├── GuestServiceImpl.java
│       ├── CheckInOutServiceImpl.java
│       ├── BillingServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── HousekeepingServiceImpl.java
│       └── ReportServiceImpl.java
├── strategies/
│   ├── pricing/
│   │   ├── PricingStrategy.java (interface)
│   │   ├── StandardPricingStrategy.java
│   │   ├── WeekendPricingStrategy.java
│   │   └── SeasonalPricingStrategy.java
│   ├── payment/
│   │   ├── PaymentStrategy.java (interface)
│   │   ├── CashPaymentStrategy.java
│   │   ├── CardPaymentStrategy.java
│   │   └── OnlinePaymentStrategy.java
│   └── discount/
│       ├── DiscountStrategy.java (interface)
│       ├── LoyaltyDiscountStrategy.java
│       └── LongStayDiscountStrategy.java
├── Hotel.java         # Facade
└── Main.java          # Demo
```

---

## 11. Usage Example

```java
// Initialize hotel
Hotel hotel = new Hotel("Grand Plaza Hotel");

// Add rooms
Room room = Room.builder()
    .roomNumber("101")
    .floor(1)
    .type(RoomType.DELUXE)
    .baseRate(new BigDecimal("199.99"))
    .capacity(2)
    .addAmenity("WiFi")
    .addAmenity("Mini Bar")
    .addAmenity("Ocean View")
    .build();
hotel.addRoom(room);

// Register guest
Guest guest = Guest.builder()
    .name("John Doe")
    .email("john@example.com")
    .phone("+1-555-0123")
    .idType("Passport")
    .idNumber("AB123456")
    .build();
hotel.registerGuest(guest);

// Make reservation
Reservation reservation = hotel.makeReservation(
    guest.getId(),
    RoomType.DELUXE,
    LocalDate.now(),
    LocalDate.now().plusDays(3),
    2
);

// Check-in guest
hotel.checkIn(reservation.getId());

// Add service charges
hotel.addServiceCharge(reservation.getId(), ServiceType.ROOM_SERVICE, 
    new BigDecimal("45.00"), "Dinner - 2 steaks");
hotel.addServiceCharge(reservation.getId(), ServiceType.MINIBAR, 
    new BigDecimal("25.00"), "Beverages");

// Check-out and generate bill
Bill bill = hotel.checkOut(reservation.getId());

// Process payment
Payment payment = hotel.processPayment(
    bill.getId(),
    bill.getTotalAmount(),
    PaymentMethod.CREDIT_CARD
);

// Generate reports
var occupancyReport = hotel.getOccupancyReport(
    LocalDate.now().minusDays(30),
    LocalDate.now()
);
var revenueReport = hotel.getRevenueReport(
    LocalDate.now().minusDays(30),
    LocalDate.now()
);
```

---

## 12. Key Design Decisions

1. **In-Memory Storage**: Used `ConcurrentHashMap` for thread-safe in-memory storage. Can be easily replaced with database repositories.

2. **Immutable Collections**: `Reservation` and `Bill` return unmodifiable lists to prevent external modification.

3. **Builder Pattern for Complex Objects**: `Room`, `Reservation`, `Guest`, and `Bill` use builders for clean construction with validation.

4. **Explicit State Machine**: `RoomStatus` and `ReservationStatus` enums define valid transitions, preventing illegal state changes.

5. **Facade for Simplicity**: `Hotel` class hides complexity and provides a clean API for common operations.

6. **Strategy for Flexibility**: Pricing, payment, and discount calculations can be easily extended without modifying core logic.

7. **Observer for Decoupling**: Email notifications and housekeeping alerts are decoupled from reservation/room processing.

8. **Thread-Safety**: Critical operations (room booking, payment processing) use proper locking mechanisms.

---

## 13. Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer
- [ ] Customer authentication and authorization
- [ ] Loyalty program with points and rewards
- [ ] Multi-property support (hotel chains)
- [ ] Real-time notifications (WebSocket)
- [ ] Integration with OTA (Online Travel Agencies)
- [ ] Revenue management system
- [ ] Mobile app APIs
- [ ] PMS (Property Management System) integration



