# BookMyShow - Movie Ticket Booking System LLD

## Overview

A comprehensive Low-Level Design for a movie ticket booking system similar to BookMyShow, implementing SOLID principles and various design patterns.

## Table of Contents

1. [Requirements](#requirements)
2. [Architecture Overview](#architecture-overview)
3. [Design Patterns Used](#design-patterns-used)
4. [SOLID Principles Application](#solid-principles-application)
5. [Class Diagram](#class-diagram)
6. [Data Models](#data-models)
7. [Core Components](#core-components)
8. [Concurrency Handling](#concurrency-handling)
9. [Extension Points](#extension-points)
10. [API Summary](#api-summary)

---

## Requirements

### Functional Requirements

| ID | Requirement | Implementation |
|----|-------------|----------------|
| FR1 | View movies playing in theaters | `MovieService`, `ShowService` |
| FR2 | Select movie, theater, show timing | `ShowService.getShowsByMovieAndCity()` |
| FR3 | Display seating arrangement | `ShowService.getAvailableSeats()` |
| FR4 | Select seats and book tickets | `BookingService.initiateBooking()` |
| FR5 | Make payments | `PaymentService`, `PaymentStrategy` |
| FR6 | Handle concurrent bookings | Seat locking with `ReentrantLock` |
| FR7 | Different seat types and pricing | `SeatType` enum, `PricingStrategy` |
| FR8 | Admin manage movies/shows | Full CRUD via services |

### Non-Functional Requirements

| ID | Requirement | Implementation |
|----|-------------|----------------|
| NFR1 | Scalability | Stateless services, Repository pattern |
| NFR2 | Real-time availability | Optimistic locking, seat status |
| NFR3 | Extensibility | Strategy pattern, Observer pattern |
| NFR4 | Maintainability | SOLID principles, clean architecture |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                PRESENTATION LAYER                                │
│                           (BookMyShow Facade / Main)                             │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                 SERVICE LAYER                                    │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐        │
│  │ MovieService  │ │ TheaterService│ │ ShowService   │ │ BookingService│        │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘        │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐                          │
│  │ PaymentService│ │ UserService   │ │ CityService   │                          │
│  └───────────────┘ └───────────────┘ └───────────────┘                          │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                STRATEGY LAYER                                    │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐        │
│  │PricingStrategy│ │PaymentStrategy│ │ SearchStrategy│ │ RefundStrategy│        │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                               REPOSITORY LAYER                                   │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐        │
│  │MovieRepository│ │TheaterRepo    │ │ ShowRepository│ │BookingRepo    │        │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                  DATA LAYER                                      │
│                     (In-Memory / Database Implementation)                        │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. Strategy Pattern

**Purpose:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

```
┌─────────────────────┐
│  <<interface>>      │
│  PricingStrategy    │
├─────────────────────┤
│+ calculateTotalPrice│
│+ calculateSeatPrice │
└─────────────────────┘
          △
          │
    ┌─────┼─────┬─────────────┐
    │     │     │             │
┌───┴───┐ │ ┌───┴────┐  ┌─────┴─────┐
│ Base  │ │ │Weekend │  │ Dynamic   │
│Pricing│ │ │Pricing │  │ Pricing   │
└───────┘ │ └────────┘  └───────────┘
          │
    ┌─────┴────┐
    │ PeakHour │
    │ Pricing  │
    └──────────┘
```

**Implementations:**
- `PricingStrategy` → `BasePricingStrategy`, `WeekendPricingStrategy`, `PeakHourPricingStrategy`, `DynamicPricingStrategy`
- `PaymentStrategy` → `CreditCardPaymentStrategy`, `UPIPaymentStrategy`, `WalletPaymentStrategy`, `NetBankingPaymentStrategy`
- `RefundStrategy` → `FullRefundStrategy`, `TimeBasedRefundStrategy`, `NoRefundStrategy`
- `SearchStrategy` → `TitleSearchStrategy`, `GenreSearchStrategy`

### 2. Observer Pattern

**Purpose:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified.

```
┌─────────────────┐         ┌───────────────────┐
│ BookingService  │────────▶│  BookingObserver  │
│   (Subject)     │         │   (Interface)     │
└─────────────────┘         └───────────────────┘
                                     △
                                     │
              ┌──────────────────────┼──────────────────────┐
              │                      │                      │
    ┌─────────┴─────────┐  ┌────────┴────────┐  ┌─────────┴─────────┐
    │EmailNotification  │  │SMSNotification  │  │AnalyticsObserver  │
    │    Observer       │  │   Observer      │  │                   │
    └───────────────────┘  └─────────────────┘  └───────────────────┘
```

### 3. Factory Pattern

**Purpose:** Define an interface for creating an object, but let subclasses decide which class to instantiate.

```java
// PaymentStrategyFactory
public static PaymentStrategy getStrategy(PaymentMethod method) {
    return switch (method) {
        case CREDIT_CARD, DEBIT_CARD -> new CreditCardPaymentStrategy();
        case UPI -> new UPIPaymentStrategy();
        case NET_BANKING -> new NetBankingPaymentStrategy();
        case WALLET -> new WalletPaymentStrategy();
    };
}

// SeatFactory
public static List<Seat> createStandardLayout(screenId, regularRows, premiumRows, ...);
public static List<Seat> createVIPLayout(screenId, vipRows, premiumRows, ...);
```

### 4. Singleton Pattern

**Purpose:** Ensure a class has only one instance and provide a global point of access to it.

```java
public class BookMyShow {
    private static volatile BookMyShow instance;
    
    public static BookMyShow getInstance() {
        if (instance == null) {
            synchronized (BookMyShow.class) {
                if (instance == null) {
                    instance = new BookMyShow();
                }
            }
        }
        return instance;
    }
}
```

### 5. Repository Pattern

**Purpose:** Mediates between the domain and data mapping layers using a collection-like interface.

```
┌────────────────────┐
│   <<interface>>    │
│  MovieRepository   │
├────────────────────┤
│+ save(Movie)       │
│+ findById(String)  │
│+ findByTitle(...)  │
│+ delete(String)    │
└────────────────────┘
          △
          │
┌─────────┴──────────┐
│InMemoryMovieRepo   │
│  (Implementation)  │
└────────────────────┘
```

### 6. Builder Pattern

**Purpose:** Separate the construction of a complex object from its representation.

```java
Movie movie = new Movie.Builder("Inception", Duration.ofMinutes(148))
    .description("A thief who steals corporate secrets...")
    .language("English")
    .releaseDate(LocalDate.of(2024, 1, 15))
    .genre(Genre.SCIENCE_FICTION)
    .genre(Genre.ACTION)
    .director("Christopher Nolan")
    .build();
```

---

## SOLID Principles Application

### Single Responsibility Principle (SRP)

Each class has one and only one reason to change:

| Class | Single Responsibility |
|-------|----------------------|
| `MovieService` | Manage movie lifecycle only |
| `BookingService` | Handle booking workflow only |
| `PaymentService` | Process payments only |
| `PricingStrategy` | Calculate prices only |
| `EmailNotificationObserver` | Send email notifications only |

### Open/Closed Principle (OCP)

Classes are open for extension but closed for modification:

```java
// Adding new pricing strategy WITHOUT modifying existing code
public class HolidayPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculateSeatPrice(Show show, ShowSeat seat) {
        // Holiday-specific pricing logic
    }
}
```

### Liskov Substitution Principle (LSP)

All strategy implementations are interchangeable:

```java
// Any PricingStrategy can be used interchangeably
PricingStrategy strategy1 = new BasePricingStrategy();
PricingStrategy strategy2 = new WeekendPricingStrategy();
PricingStrategy strategy3 = new DynamicPricingStrategy();

// All work the same way
BigDecimal price = strategy.calculateTotalPrice(show, seats);
```

### Interface Segregation Principle (ISP)

Clients are not forced to depend on interfaces they don't use:

```java
// Separate interfaces for different concerns
interface MovieRepository { ... }      // Movie persistence
interface ShowRepository { ... }       // Show persistence
interface PricingStrategy { ... }      // Pricing calculation
interface PaymentStrategy { ... }      // Payment processing
interface RefundStrategy { ... }       // Refund calculation
```

### Dependency Inversion Principle (DIP)

High-level modules depend on abstractions, not concretions:

```java
public class BookingServiceImpl implements BookingService {
    // Depends on interfaces, not implementations
    private final BookingRepository bookingRepository;  // Interface
    private final ShowRepository showRepository;        // Interface
    private final PaymentService paymentService;        // Interface
    private final PricingStrategy pricingStrategy;      // Interface
    
    public BookingServiceImpl(
        BookingRepository bookingRepository,  // Injected
        ShowRepository showRepository,        // Injected
        PaymentService paymentService,        // Injected
        PricingStrategy pricingStrategy       // Injected
    ) { ... }
}
```

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    DOMAIN MODEL                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────┐         ┌──────────┐         ┌────────┐
    │  City   │ 1────* │ Theater  │ 1────* │ Screen │
    │─────────│         │──────────│         │────────│
    │ id      │         │ id       │         │ id     │
    │ name    │         │ name     │         │ name   │
    │ state   │         │ address  │         │ seats  │
    └─────────┘         │ cityId   │         └────────┘
                        └──────────┘              │
                                                  │ 1
                                                  ▼ *
                                             ┌─────────┐
    ┌─────────┐                              │  Seat   │
    │  Movie  │                              │─────────│
    │─────────│                              │ id      │
    │ id      │                              │ rowLabel│
    │ title   │◄───────────────┐             │ seatNum │
    │ duration│                │             │ type    │
    │ genres  │                │ 1           └─────────┘
    │ language│                │                  │
    └─────────┘                │                  │
         │                     │                  │
         │ *                   │                  │
         ▼ 1                   │                  │
    ┌──────────┐         ┌──────────┐       ┌──────────┐
    │   Show   │ 1────* │ ShowSeat │◄──────│   Seat   │
    │──────────│         │──────────│       └──────────┘
    │ id       │         │ id       │
    │ movieId  │         │ status   │
    │ screenId │         │ price    │
    │ startTime│         │ lockedBy │
    │ basePrice│         └──────────┘
    └──────────┘
         │
         │ 1
         ▼ *
    ┌──────────┐         ┌──────────┐
    │ Booking  │ 1────1 │ Payment  │
    │──────────│         │──────────│
    │ id       │         │ id       │
    │ userId   │         │ bookingId│
    │ showId   │         │ amount   │
    │ seatIds  │         │ method   │
    │ status   │         │ status   │
    │ amount   │         └──────────┘
    └──────────┘
         │
         │ *
         ▼ 1
    ┌──────────┐
    │   User   │
    │──────────│
    │ id       │
    │ name     │
    │ email    │
    │ phone    │
    └──────────┘
```

---

## Data Models

### Enums

| Enum | Values | Purpose |
|------|--------|---------|
| `SeatType` | REGULAR, PREMIUM, RECLINER, VIP, WHEELCHAIR | Seat categories with price multipliers |
| `SeatStatus` | AVAILABLE, LOCKED, BOOKED, UNAVAILABLE | Seat availability state |
| `BookingStatus` | INITIATED, PENDING, CONFIRMED, CANCELLED, EXPIRED, REFUNDED | Booking lifecycle |
| `PaymentStatus` | PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED | Payment state |
| `PaymentMethod` | CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, WALLET, CASH | Payment options |
| `ShowStatus` | SCHEDULED, ONGOING, COMPLETED, CANCELLED | Show state |
| `Genre` | ACTION, COMEDY, DRAMA, HORROR, etc. | Movie genres |

### Key Models

| Model | Key Fields | Purpose |
|-------|------------|---------|
| `Movie` | id, title, duration, genres, language | Movie information |
| `Theater` | id, name, address, cityId, screens | Theater/multiplex |
| `Screen` | id, name, seats | Auditorium |
| `Seat` | id, rowLabel, seatNumber, seatType | Physical seat template |
| `Show` | id, movieId, screenId, startTime, basePrice | Movie showing |
| `ShowSeat` | id, showId, seat, status, price, lockedBy | Per-show seat state |
| `Booking` | id, userId, showId, seatIds, status, amount | User booking |
| `Payment` | id, bookingId, amount, method, status | Payment transaction |
| `User` | id, name, email, phone | User account |

---

## Core Components

### 1. Booking Flow

```
┌─────────┐     ┌─────────────┐     ┌───────────┐     ┌─────────────┐
│  User   │────▶│initiateBook │────▶│ lockSeats │────▶│ createBook  │
└─────────┘     └─────────────┘     └───────────┘     └─────────────┘
                                                              │
                                                              ▼
┌─────────┐     ┌─────────────┐     ┌───────────┐     ┌─────────────┐
│Confirmed│◀────│ updateSeats │◀────│processPaym│◀────│confirmBook  │
└─────────┘     └─────────────┘     └───────────┘     └─────────────┘
```

### 2. Seat Locking Mechanism

```java
// Seat locking with expiry
public boolean lock(String userId, int lockDurationMinutes) {
    if (!isAvailable()) return false;
    
    this.status = SeatStatus.LOCKED;
    this.lockedByUserId = userId;
    this.lockExpiry = LocalDateTime.now().plusMinutes(lockDurationMinutes);
    return true;
}

// Auto-unlock on expiry check
public boolean isAvailable() {
    if (status == SeatStatus.LOCKED && LocalDateTime.now().isAfter(lockExpiry)) {
        unlock();  // Auto-release expired lock
    }
    return status == SeatStatus.AVAILABLE;
}
```

---

## Concurrency Handling

### Thread-Safe Booking

```java
public class BookingServiceImpl implements BookingService {
    private final ReentrantLock bookingLock = new ReentrantLock();
    
    @Override
    public Booking initiateBooking(String userId, String showId, List<String> seatIds) {
        bookingLock.lock();
        try {
            // Validate seats are available
            // Lock all seats atomically
            // Create booking
            return booking;
        } finally {
            bookingLock.unlock();
        }
    }
}
```

### Thread-Safe Data Structures

```java
// All repositories use ConcurrentHashMap
private final Map<String, Show> shows = new ConcurrentHashMap<>();

// ShowSeats stored in ConcurrentHashMap within Show
private final Map<String, ShowSeat> showSeats = new ConcurrentHashMap<>();
```

### Distributed Locking (Future Enhancement)

For multi-server deployments, replace `ReentrantLock` with:
- Redis distributed locks (Redlock)
- Database-level row locking
- Optimistic locking with version numbers

---

## Extension Points

### 1. Adding New Payment Method

```java
// 1. Add to enum
enum PaymentMethod {
    // ... existing
    APPLE_PAY("Apple Pay")
}

// 2. Create strategy
public class ApplePayPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Apple Pay integration
    }
}

// 3. Update factory
case APPLE_PAY -> new ApplePayPaymentStrategy();
```

### 2. Adding New Pricing Strategy

```java
public class OfferPricingStrategy implements PricingStrategy {
    private final PricingStrategy baseStrategy;
    private final BigDecimal discountPercent;
    
    @Override
    public BigDecimal calculateSeatPrice(Show show, ShowSeat showSeat) {
        BigDecimal basePrice = baseStrategy.calculateSeatPrice(show, showSeat);
        return basePrice.multiply(BigDecimal.ONE.subtract(discountPercent));
    }
}
```

### 3. Adding New Notification Channel

```java
public class PushNotificationObserver implements BookingObserver {
    @Override
    public void onBookingConfirmed(Booking booking) {
        // Send push notification
    }
}

// Register with BookingService
bookingService.addObserver(new PushNotificationObserver());
```

### 4. Database Integration

```java
// Replace in-memory with JPA repository
public class JpaMovieRepository implements MovieRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void save(Movie movie) {
        em.persist(movie);
    }
    
    @Override
    public Optional<Movie> findById(String id) {
        return Optional.ofNullable(em.find(Movie.class, id));
    }
}
```

---

## API Summary

### BookMyShow Facade API

| Method | Description |
|--------|-------------|
| `addMovie(Movie)` | Add a new movie |
| `addTheater(Theater)` | Add a new theater |
| `createShow(Show)` | Schedule a new show |
| `registerUser(User)` | Register a new user |
| `searchMoviesByTitle(String)` | Search movies by title |
| `getAvailableSeats(showId)` | Get available seats for a show |
| `initiateBooking(userId, showId, seatIds)` | Start booking, lock seats |
| `confirmBooking(bookingId, paymentMethod)` | Pay and confirm booking |
| `cancelBooking(bookingId)` | Cancel booking with refund |
| `getUserBookings(userId)` | Get user's booking history |

### Booking Service API

```java
Booking initiateBooking(String userId, String showId, List<String> seatIds);
Booking confirmBooking(String bookingId, PaymentMethod paymentMethod);
Booking cancelBooking(String bookingId);
Optional<Booking> getBooking(String bookingId);
List<Booking> getUserBookings(String userId);
void processExpiredBookings();
```

---

## Running the Demo

```bash
cd bookmyshow
javac -d ../out *.java **/*.java
java -cp ../out bookmyshow.Main
```

This will demonstrate:
1. Setting up cities, theaters, screens, and seats
2. Adding movies with genres and metadata
3. Scheduling shows with different timings
4. User registration
5. Movie search by title, genre, and language
6. Viewing available seats
7. Booking workflow with seat locking
8. Payment processing
9. Concurrent booking protection
10. Analytics tracking

---

## Design Rationale

This design is:

1. **Extensible**: New payment methods, pricing strategies, and notification channels can be added without modifying existing code
2. **Loosely Coupled**: Services depend on interfaces, not implementations
3. **SOLID-Compliant**: Each class has a single responsibility, open for extension, follows LSP, segregated interfaces, and inverted dependencies
4. **Testable**: Dependencies are injected, making unit testing easy with mocks
5. **Scalable**: Stateless services with thread-safe data structures support horizontal scaling
6. **Maintainable**: Clear separation of concerns and well-defined interfaces

---

## Future Enhancements

1. **Caching Layer**: Add Redis for show/seat availability caching
2. **Event Sourcing**: Track all booking events for audit
3. **Rate Limiting**: Prevent booking abuse
4. **Recommendation Engine**: Suggest movies based on history
5. **Waitlist**: Queue for sold-out shows
6. **Loyalty Program**: Points and rewards system
7. **Multi-language Support**: Internationalization
8. **Admin Dashboard**: Theater management UI



