# Concert Ticket Booking System - Low-Level Design

## Overview

A comprehensive concert ticket booking system that handles seat selection, booking management, payment processing, notifications, and waitlist functionality. The system is designed following SOLID principles and common design patterns for extensibility, maintainability, and thread safety.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **ConcertBookingSystem (Facade)** | Single entry point for all operations; wires components together |
| **ConcertService** | Manage concerts, venues, and seating arrangements |
| **BookingService** | Handle seat selection, booking creation, confirmation, and cancellation |
| **PaymentService** | Process payments via multiple strategies |
| **NotificationService** | Send confirmations via email/SMS |
| **WaitlistService** | Manage waiting list for sold-out concerts |
| **SearchService** | Search concerts by various criteria (artist, venue, date) |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────┐
│  SeatStatus       - AVAILABLE, HELD, BOOKED, BLOCKED           │
│  BookingStatus    - PENDING, CONFIRMED, CANCELLED, EXPIRED,    │
│                     REFUNDED                                    │
│  ConcertStatus    - SCHEDULED, ON_SALE, SOLD_OUT, ONGOING,     │
│                     COMPLETED, CANCELLED, POSTPONED             │
│  PaymentStatus    - PENDING, PROCESSING, COMPLETED, FAILED,    │
│                     REFUNDED                                    │
│  PaymentMethod    - CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, │
│                     WALLET                                      │
│  SectionType      - VIP, PLATINUM, GOLD, SILVER, GENERAL,      │
│                     BALCONY, STANDING                           │
│  NotificationType - EMAIL, SMS, PUSH, ALL                      │
└─────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `User` | User with contact information |
| `Venue` | Concert venue with sections and capacity |
| `Section` | Group of seats (VIP, General, etc.) with pricing multiplier |
| `Seat` | Individual seat with status, locking for concurrency |
| `Concert` | Concert event with venue, artist, pricing, and seats |
| `Booking` | Booking record with seats, user, status, expiration |
| `Payment` | Payment transaction with method and status |
| `Ticket` | Generated ticket with QR code for entry |
| `WaitlistEntry` | Entry in waitlist for sold-out concerts |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `PaymentStrategy` | Process different payment types | Card, UPI, NetBanking, Wallet |
| `NotificationStrategy` | Send notifications | Email, SMS |
| `SearchStrategy` | Search concerts | Artist, Venue, Date, Composite |
| `PricingStrategy` | Calculate prices | Standard, Dynamic, EarlyBird |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `BookingObserver` | React to booking lifecycle events |
| `WaitlistObserver` | React to waitlist events |

---

## 3. Class Diagram

```
                          ┌────────────────────────┐
                          │  ConcertBookingSystem  │
                          │       (Facade)         │
                          └───────────┬────────────┘
                                      │
       ┌──────────────┬───────────────┼───────────────┬──────────────┐
       │              │               │               │              │
       ▼              ▼               ▼               ▼              ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ Concert     │ │ Booking     │ │ Payment     │ │ Waitlist    │ │ Search      │
│ Service     │ │ Service     │ │ Service     │ │ Service     │ │ Service     │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌───────────┐  ┌─────────────┐ ┌───────────┐
│Concert      │ │Booking      │ │Payment    │  │Waitlist     │ │Search     │
│Repository   │ │Repository   │ │Strategy   │  │Repository   │ │Strategy   │
└─────────────┘ └─────────────┘ └───────────┘  └─────────────┘ └───────────┘


                    ┌─────────────────────────────────┐
                    │       NotificationService       │
                    └─────────────┬───────────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
              ┌───────────┐               ┌───────────┐
              │  Email    │               │   SMS     │
              │ Strategy  │               │ Strategy  │
              └───────────┘               └───────────┘
```

---

## 4. Design Patterns Used

### 1. **Facade Pattern** - `ConcertBookingSystem`
- Provides a simplified interface to the complex subsystem
- Coordinates between all services
- Entry point for all booking operations

```java
ConcertBookingSystem system = new ConcertBookingSystem("TicketMaster");
Booking booking = system.initiateBooking(userId, concertId, seatIds);
BookingResult result = system.completeBooking(bookingId, PaymentMethod.UPI);
```

### 2. **Strategy Pattern** - Payment, Notification, Search, Pricing
- Allows runtime selection of algorithms
- Easy to add new payment methods, notification channels
- Follows Open/Closed Principle

```java
// Adding a new payment method:
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTOCURRENCY, 
    new CryptoPaymentStrategy()
);

// Using different pricing strategies:
PricingStrategy dynamic = new DynamicPricingStrategy();
BigDecimal price = dynamic.calculatePrice(concert, seats);
```

### 3. **Observer Pattern** - Booking & Waitlist events
- Kitchen receives real-time order updates
- Waitlist users notified when seats become available
- Decouples event producers from consumers

```java
public interface BookingObserver {
    void onBookingCreated(Booking booking);
    void onBookingConfirmed(Booking booking);
    void onBookingCancelled(Booking booking);
    void onBookingExpired(Booking booking);
}
```

### 4. **Builder Pattern** - Complex object construction
- `Concert`, `Booking`, `Ticket`, `Venue`, `Section`
- Fluent API for readability
- Validation during build

```java
Concert concert = Concert.builder()
    .id("CONCERT-001")
    .name("The Eras Tour")
    .artist("Taylor Swift")
    .venue(venue)
    .dateTime(LocalDateTime.now().plusMonths(2))
    .basePrice(new BigDecimal("150.00"))
    .build();
```

### 5. **Repository Pattern** - Data Access
- Abstracts data storage from business logic
- Easy to swap implementations (in-memory → database)
- Follows Dependency Inversion Principle

### 6. **Factory Pattern** - Venue Creation
- Creates venues with sections and seats
- Predefined configurations (small, medium, large)

```java
Venue arena = VenueFactory.createMediumVenue(
    "Madison Square Garden",
    "4 Pennsylvania Plaza",
    "New York"
);
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each service handles one domain concern
- `BookingService` only manages bookings
- `PaymentService` only processes payments
- `Seat` manages its own locking/status

### Open/Closed Principle (OCP)
- New payment methods via `PaymentStrategy` without modifying core
- New notification channels via `NotificationStrategy`
- New search criteria via `SearchStrategy`
- New pricing models via `PricingStrategy`

### Liskov Substitution Principle (LSP)
- All `PaymentStrategy` implementations are interchangeable
- All `Repository` implementations fulfill the same contract

### Interface Segregation Principle (ISP)
- `BookingObserver` is separate from `WaitlistObserver`
- `NotificationStrategy` doesn't force SMS for email-only use

### Dependency Inversion Principle (DIP)
- Services depend on interfaces, not concrete implementations
- `BookingService` depends on `BookingRepository` interface
- `PaymentService` depends on `PaymentStrategy` interface

---

## 6. Concurrency Handling

### Thread-Safe Seat Locking

```java
public class Seat {
    private final ReentrantLock lock = new ReentrantLock();
    private volatile SeatStatus status;
    private volatile String heldByUserId;
    private volatile LocalDateTime holdExpiresAt;
    
    public boolean tryHold(String userId, int holdDurationMinutes) {
        lock.lock();
        try {
            // Auto-release expired holds
            if (status == SeatStatus.HELD && isHoldExpired()) {
                releaseHoldInternal();
            }
            
            if (status == SeatStatus.AVAILABLE) {
                status = SeatStatus.HELD;
                heldByUserId = userId;
                holdExpiresAt = LocalDateTime.now().plusMinutes(holdDurationMinutes);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
```

### Key Concurrency Features
- `Seat` uses `ReentrantLock` for atomic operations
- All repositories use `ConcurrentHashMap`
- `volatile` fields for visibility guarantees
- Temporary holds with expiration (15 minutes default)
- Atomic booking confirmation that verifies hold ownership

### Booking Flow with Concurrency

```
User A selects seats              User B selects same seats
        │                                  │
        ▼                                  ▼
   tryHold(seat1) ─────────────────> tryHold(seat1)
        │                                  │
   SUCCESS ✓                           FAILED ✗
        │                                  │
        ▼                                  ▼
   Booking Created              SeatNotAvailableException
        │
        ▼
   Payment (15 min window)
        │
        ▼
   confirmBooking()
        │
        ▼
   Tickets Generated
```

---

## 7. Extension Points

### Adding a New Payment Method

```java
// 1. Create new strategy
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Implementation for crypto
    }
    
    @Override
    public BigDecimal getProcessingFee(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.01")); // 1% fee
    }
}

// 2. Add enum value
public enum PaymentMethod {
    // ...existing...
    CRYPTOCURRENCY("Cryptocurrency");
}

// 3. Register with service
paymentService.registerPaymentStrategy(
    PaymentMethod.CRYPTOCURRENCY, 
    new CryptoPaymentStrategy()
);
```

### Adding a New Notification Channel

```java
// 1. Create new strategy
public class PushNotificationStrategy implements NotificationStrategy {
    @Override
    public boolean sendBookingConfirmation(User user, String bookingId, List<Ticket> tickets) {
        // Send push notification
    }
}

// 2. Register
notificationService.registerNotificationStrategy(
    NotificationType.PUSH, 
    new PushNotificationStrategy()
);
```

### Adding Dynamic Pricing

```java
// Switch pricing strategy
BookingServiceImpl bookingService = new BookingServiceImpl(
    bookingRepository,
    concertRepository,
    userRepository,
    new DynamicPricingStrategy() // Uses demand + time-based pricing
);
```

---

## 8. Booking Lifecycle Flow

```
[User Selects Seats]
        │
        ▼
   ┌─────────┐
   │ PENDING │ ─────────────────────────────────────┐
   └────┬────┘                                      │
        │ payment                                   │ timeout (15 min)
        ▼                                           ▼
  ┌───────────┐                               ┌─────────┐
  │ CONFIRMED │                               │ EXPIRED │
  └─────┬─────┘                               └─────────┘
        │ cancel/refund
        ▼
  ┌───────────┐
  │ CANCELLED │ or │ REFUNDED │
  └───────────┘    └──────────┘
```

---

## 9. File Structure

```
concertbooking/
├── enums/
│   ├── BookingStatus.java
│   ├── ConcertStatus.java
│   ├── NotificationType.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   ├── SeatStatus.java
│   └── SectionType.java
├── exceptions/
│   ├── BookingException.java
│   ├── ConcertBookingException.java
│   ├── ConcertNotFoundException.java
│   ├── PaymentException.java
│   ├── SeatNotAvailableException.java
│   └── WaitlistException.java
├── factories/
│   └── VenueFactory.java
├── models/
│   ├── Booking.java
│   ├── Concert.java
│   ├── Payment.java
│   ├── Seat.java
│   ├── Section.java
│   ├── Ticket.java
│   ├── User.java
│   ├── Venue.java
│   └── WaitlistEntry.java
├── observers/
│   ├── BookingObserver.java
│   ├── NotificationObserver.java
│   ├── WaitlistNotificationObserver.java
│   └── WaitlistObserver.java
├── repositories/
│   ├── BookingRepository.java
│   ├── ConcertRepository.java
│   ├── Repository.java
│   ├── UserRepository.java
│   ├── WaitlistRepository.java
│   └── impl/
│       ├── InMemoryBookingRepository.java
│       ├── InMemoryConcertRepository.java
│       ├── InMemoryUserRepository.java
│       └── InMemoryWaitlistRepository.java
├── services/
│   ├── BookingService.java
│   ├── ConcertService.java
│   ├── NotificationService.java
│   ├── PaymentService.java
│   ├── SearchService.java
│   ├── WaitlistService.java
│   └── impl/
│       ├── BookingServiceImpl.java
│       ├── ConcertServiceImpl.java
│       ├── NotificationServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── SearchServiceImpl.java
│       └── WaitlistServiceImpl.java
├── strategies/
│   ├── notification/
│   │   ├── NotificationStrategy.java
│   │   ├── EmailNotificationStrategy.java
│   │   └── SMSNotificationStrategy.java
│   ├── payment/
│   │   ├── PaymentStrategy.java
│   │   ├── CardPaymentStrategy.java
│   │   ├── NetBankingPaymentStrategy.java
│   │   ├── UPIPaymentStrategy.java
│   │   └── WalletPaymentStrategy.java
│   ├── pricing/
│   │   ├── PricingStrategy.java
│   │   ├── DynamicPricingStrategy.java
│   │   ├── EarlyBirdPricingStrategy.java
│   │   └── StandardPricingStrategy.java
│   └── search/
│       ├── SearchStrategy.java
│       ├── ArtistSearchStrategy.java
│       ├── CompositeSearchStrategy.java
│       ├── DateSearchStrategy.java
│       └── VenueSearchStrategy.java
├── ConcertBookingSystem.java    # Facade
└── Main.java                    # Demo
```

---

## 10. Usage Example

```java
// Initialize the system
ConcertBookingSystem system = new ConcertBookingSystem("TicketMaster Pro");

// Create venue
Venue venue = VenueFactory.createMediumVenue(
    "Madison Square Garden", 
    "NYC", 
    "New York"
);

// Create concert
Concert concert = Concert.builder()
    .id("CONCERT-001")
    .name("The Eras Tour")
    .artist("Taylor Swift")
    .venue(venue)
    .dateTime(LocalDateTime.now().plusMonths(2))
    .basePrice(new BigDecimal("150.00"))
    .build();
system.createConcert(concert);
system.openSales("CONCERT-001");

// Register user
User user = system.registerUser("USER-001", "Alice", "alice@email.com", "+1-555-0101");

// Search for concerts
List<Concert> results = system.searchByArtist("Taylor");

// View available seats
List<Seat> vipSeats = system.getAvailableSeatsBySection("CONCERT-001", SectionType.VIP);

// Book tickets (holds seats for 15 minutes)
List<String> seatIds = vipSeats.stream().limit(2).map(Seat::getId).toList();
Booking booking = system.initiateBooking(user.getId(), "CONCERT-001", seatIds);

// Complete booking with payment
BookingResult result = system.completeBooking(booking.getId(), PaymentMethod.UPI);

if (result.success()) {
    // Tickets are automatically generated and sent
    result.tickets().forEach(System.out::println);
}

// Cancel if needed
system.cancelBooking(booking.getId());

// Join waitlist for sold-out concert
WaitlistEntry entry = system.joinWaitlist(user.getId(), "CONCERT-002", 2, SectionType.GOLD);
```

---

## 11. Key Design Decisions

1. **Temporary Seat Holds**: 15-minute hold period prevents indefinite seat locking while giving users time to complete payment.

2. **Atomic Seat Operations**: Each seat has its own `ReentrantLock` for fine-grained concurrency control without blocking entire sections.

3. **Auto-expiring Holds**: Expired holds are automatically released when checked, with periodic cleanup.

4. **Observer for Notifications**: Decouples notification sending from booking logic, allowing multiple notification channels.

5. **Strategy for Flexibility**: Payment, pricing, and search algorithms are pluggable at runtime.

6. **Facade for Simplicity**: `ConcertBookingSystem` hides complexity and provides a clean API.

7. **Builder for Complex Objects**: Concerts and bookings use builders for clean construction with validation.

8. **Immutable Collections**: Booking seat lists are unmodifiable to prevent external tampering.

---

## 12. Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer with Spring Boot
- [ ] Async payment processing
- [ ] Real-time seat map with WebSocket updates
- [ ] Loyalty program integration
- [ ] Group booking with discounts
- [ ] Seat exchange between users
- [ ] Multi-currency support
- [ ] Fraud detection for booking patterns
- [ ] Analytics dashboard

---

## 13. Testing Considerations

### Unit Testing Key Components

```java
// Test concurrent seat booking
@Test
void testConcurrentSeatBooking() {
    Seat seat = new Seat("SEAT-1", "SEC-1", SectionType.VIP, 1, 1);
    
    // Two users try to book same seat
    ExecutorService executor = Executors.newFixedThreadPool(2);
    AtomicInteger successCount = new AtomicInteger(0);
    
    for (int i = 0; i < 2; i++) {
        final String userId = "USER-" + i;
        executor.submit(() -> {
            if (seat.tryHold(userId, 15)) {
                successCount.incrementAndGet();
            }
        });
    }
    
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    
    // Only one should succeed
    assertEquals(1, successCount.get());
}
```

### Integration Testing

```java
@Test
void testFullBookingFlow() {
    ConcertBookingSystem system = new ConcertBookingSystem("Test");
    
    // Setup
    User user = system.registerUser("U1", "Test User", "test@email.com", null);
    Concert concert = createTestConcert();
    system.createConcert(concert);
    system.openSales(concert.getId());
    
    // Get seats and book
    List<Seat> seats = system.getAvailableSeatsBySection(concert.getId(), SectionType.VIP);
    List<String> seatIds = seats.stream().limit(2).map(Seat::getId).toList();
    
    Booking booking = system.initiateBooking(user.getId(), concert.getId(), seatIds);
    assertNotNull(booking);
    assertEquals(BookingStatus.PENDING, booking.getStatus());
    
    // Complete booking
    BookingResult result = system.completeBooking(booking.getId(), PaymentMethod.UPI);
    assertTrue(result.success());
    assertEquals(2, result.tickets().size());
}
```



