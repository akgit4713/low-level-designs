# Airline Management System - Low-Level Design

## Overview

A comprehensive airline management system that handles flight search, booking, seat selection, payments, crew/aircraft management, and passenger information. Designed following SOLID principles and common design patterns for extensibility and maintainability.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **AirlineManagement (Facade)** | Single entry point for all operations; wires components together |
| **FlightService** | Manage flights, schedules, search by route and date |
| **BookingService** | Create, manage bookings through their lifecycle |
| **SeatService** | Handle seat selection, availability, seat maps |
| **PaymentService** | Process payments via multiple payment methods |
| **PassengerService** | Manage passenger information and baggage |
| **AircraftService** | Manage aircraft fleet, assignments to flights |
| **CrewService** | Manage crew members and assignments to flights |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────────┐
│  BookingStatus  - PENDING, CONFIRMED, CANCELLED, COMPLETED, REFUNDED│
│  FlightStatus   - SCHEDULED, BOARDING, DEPARTED, IN_AIR, LANDED,    │
│                   DELAYED, CANCELLED                                 │
│  SeatClass      - ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST         │
│  SeatStatus     - AVAILABLE, BOOKED, BLOCKED, UNAVAILABLE           │
│  PaymentMethod  - CREDIT_CARD, DEBIT_CARD, NET_BANKING, WALLET      │
│  PaymentStatus  - PENDING, COMPLETED, FAILED, REFUNDED              │
│  UserRole       - PASSENGER, AIRLINE_STAFF, ADMINISTRATOR           │
│  CrewRole       - PILOT, CO_PILOT, FLIGHT_ATTENDANT, PURSER         │
│  BaggageType    - CABIN, CHECKED                                    │
│  AircraftStatus - AVAILABLE, IN_FLIGHT, MAINTENANCE, RETIRED        │
└─────────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `Flight` | Flight with route, schedule, aircraft, seats, status |
| `Booking` | Customer booking with passengers, seats, payment |
| `Passenger` | Passenger with personal details, documents, baggage |
| `Seat` | Individual seat with class, position, price, status |
| `Aircraft` | Aircraft with model, capacity, seat configuration |
| `Crew` | Crew member with role, certification, availability |
| `Baggage` | Baggage information with weight and type |
| `Payment` | Payment transaction with method and status |
| `Ticket` | Issued ticket after confirmed booking |
| `User` | System user with role-based access |
| `Airport` | Airport with IATA code and location |
| `FlightSearchResult` | Search result with availability and pricing |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `PricingStrategy` | Calculate flight prices | Standard, Dynamic, Seasonal |
| `PaymentStrategy` | Process payments | CreditCard, DebitCard, Wallet, NetBanking |
| `FlightSearchStrategy` | Sort search results | Cheapest, Fastest, EarliestDeparture |
| `RefundStrategy` | Calculate refund amounts | FullRefund, TimeBased, NoRefund |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `FlightObserver` | React to flight status changes, delays, cancellations |
| `BookingObserver` | React to booking creation, confirmation, cancellation |

---

## 3. Class Diagram

```
                          ┌──────────────────────┐
                          │  AirlineManagement   │
                          │      (Facade)        │
                          └──────────┬───────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       │             │               │               │             │
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│FlightService│ │BookingService│ │SeatService  │ │PassengerSvc │ │PaymentService│
└──────┬──────┘ └──────┬──────┘ └─────────────┘ └──────┬──────┘ └──────┬──────┘
       │               │                               │               │
       ▼               ▼                               ▼               ▼
┌─────────────┐ ┌─────────────┐                 ┌─────────────┐ ┌─────────────┐
│Flight       │ │Booking      │                 │Passenger    │ │Payment      │
│Repository   │ │Repository   │                 │Repository   │ │Strategies   │
└─────────────┘ └─────────────┘                 └─────────────┘ └─────────────┘


       ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
       │AircraftSvc  │  │ CrewService │  │TicketFactory│
       └──────┬──────┘  └──────┬──────┘  └─────────────┘
              │                │
              ▼                ▼
       ┌─────────────┐  ┌─────────────┐
       │Aircraft     │  │Crew         │
       │Repository   │  │Repository   │
       └─────────────┘  └─────────────┘


                    ┌─────────────────────────────────────┐
                    │           Strategies                 │
                    ├─────────────────────────────────────┤
                    │  ┌─────────────────────┐            │
                    │  │  PricingStrategy    │            │
                    │  │  - Standard         │            │
                    │  │  - Dynamic          │            │
                    │  │  - Seasonal         │            │
                    │  └─────────────────────┘            │
                    │  ┌─────────────────────┐            │
                    │  │  PaymentStrategy    │            │
                    │  │  - CreditCard       │            │
                    │  │  - DebitCard        │            │
                    │  │  - Wallet           │            │
                    │  │  - NetBanking       │            │
                    │  └─────────────────────┘            │
                    │  ┌─────────────────────┐            │
                    │  │  SearchStrategy     │            │
                    │  │  - Cheapest         │            │
                    │  │  - Fastest          │            │
                    │  │  - EarliestDeparture│            │
                    │  └─────────────────────┘            │
                    │  ┌─────────────────────┐            │
                    │  │  RefundStrategy     │            │
                    │  │  - FullRefund       │            │
                    │  │  - TimeBased        │            │
                    │  │  - NoRefund         │            │
                    │  └─────────────────────┘            │
                    └─────────────────────────────────────┘
```

---

## 4. Design Patterns Used

### 1. **Facade Pattern** - `AirlineManagement` class
- Provides simplified interface to complex subsystem
- Coordinates between all services
- Entry point for all airline operations
- Hides complexity from clients

```java
AirlineManagement airline = new AirlineManagement("SkyHigh Airways");
Booking booking = airline.createBooking(flight, passenger, "1A");
airline.confirmBooking(booking.getId(), PaymentMethod.CREDIT_CARD);
```

### 2. **Strategy Pattern** - Pricing, Payment, Search, Refund

#### Pricing Strategy
```java
// Switch between pricing strategies at runtime
airline = new AirlineManagement("Airline", new DynamicPricingStrategy());
// or
airline = new AirlineManagement("Airline", new SeasonalPricingStrategy());
```

#### Payment Strategy
```java
// Register different payment processors
paymentService.registerPaymentStrategy(PaymentMethod.CREDIT_CARD, new CreditCardPaymentStrategy());
paymentService.registerPaymentStrategy(PaymentMethod.WALLET, new WalletPaymentStrategy());
```

#### Search Strategy
```java
// Sort flights by cheapest first
airline.setSearchStrategy(new CheapestFlightStrategy());
// or by fastest
airline.setSearchStrategy(new FastestFlightStrategy());
```

#### Refund Strategy
```java
// Time-based refund policy
paymentService.setRefundStrategy(new TimeBasedRefundStrategy());
// or full refund
paymentService.setRefundStrategy(new FullRefundStrategy());
```

### 3. **Observer Pattern** - Flight & Booking notifications

```java
public interface FlightObserver {
    void onFlightStatusChanged(Flight flight, FlightStatus oldStatus, FlightStatus newStatus);
    void onFlightDelayed(Flight flight, String reason);
    void onFlightCancelled(Flight flight, String reason);
}

public interface BookingObserver {
    void onBookingCreated(Booking booking);
    void onBookingConfirmed(Booking booking);
    void onBookingCancelled(Booking booking);
}
```

### 4. **Builder Pattern** - Complex Object Construction

```java
Flight flight = Flight.builder()
    .flightNumber("SH101")
    .source(jfk)
    .destination(lax)
    .departureTime(departure)
    .arrivalTime(arrival)
    .aircraft(boeing737)
    .basePrice(SeatClass.ECONOMY, new BigDecimal("299.99"))
    .build();

Passenger passenger = Passenger.builder()
    .id("PAX-001")
    .firstName("John")
    .lastName("Doe")
    .email("john@email.com")
    .passportNumber("US123456")
    .build();
```

### 5. **Repository Pattern** - Data Access

```java
public interface FlightRepository extends Repository<Flight, String> {
    List<Flight> findByRouteAndDate(Airport source, Airport destination, LocalDate date);
    List<Flight> findByStatus(FlightStatus status);
}
```

### 6. **Factory Pattern** - Ticket Creation

```java
public class TicketFactory {
    public List<Ticket> createTickets(Booking booking) {
        // Creates tickets for all passengers in a confirmed booking
    }
}
```

### 7. **State Pattern** - Booking & Flight Lifecycle

```java
// BookingStatus with valid transitions
public boolean canTransitionTo(BookingStatus newStatus) {
    return switch (this) {
        case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
        case CONFIRMED -> newStatus == CANCELLED || newStatus == COMPLETED;
        case CANCELLED -> newStatus == REFUNDED;
        case COMPLETED, REFUNDED -> false;
    };
}
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- `FlightService` only manages flights
- `BookingService` only manages bookings
- `PaymentService` only handles payments
- `SeatService` only handles seat operations

### Open/Closed Principle (OCP)
- New pricing strategies via `PricingStrategy` interface
- New payment methods via `PaymentStrategy` interface
- New search algorithms via `FlightSearchStrategy` interface
- New refund policies via `RefundStrategy` interface

### Liskov Substitution Principle (LSP)
- All `PaymentStrategy` implementations are interchangeable
- All `PricingStrategy` implementations follow the same contract

### Interface Segregation Principle (ISP)
- `FlightObserver` is separate from `BookingObserver`
- Small, focused interfaces for each strategy type

### Dependency Inversion Principle (DIP)
- Services depend on repository interfaces, not implementations
- `FlightService` depends on `FlightRepository` interface
- `PaymentService` depends on `PaymentStrategy` interface

---

## 6. Concurrency Handling

### Thread-Safe Components

```java
// Seat uses ReentrantLock for atomic booking operations
public class Seat {
    private final ReentrantLock lock = new ReentrantLock();
    
    public boolean book(String passengerId) {
        lock.lock();
        try {
            if (status != SeatStatus.AVAILABLE) return false;
            this.status = SeatStatus.BOOKED;
            this.bookedByPassengerId = passengerId;
            return true;
        } finally {
            lock.unlock();
        }
    }
}
```

- `Flight` uses `ReentrantReadWriteLock` for status transitions
- `Booking` uses `ReentrantReadWriteLock` for state changes
- `Aircraft` uses `ReentrantLock` for status updates
- All repositories use `ConcurrentHashMap`

---

## 7. Extension Points

### Adding a New Payment Method

```java
// 1. Create new strategy
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Implementation
    }
    
    @Override
    public boolean processRefund(Payment payment, BigDecimal amount) {
        // Implementation
    }
}

// 2. Register with service
paymentService.registerPaymentStrategy(PaymentMethod.CRYPTO, new CryptoPaymentStrategy());
```

### Adding a New Pricing Strategy

```java
// Create strategy for promotional pricing
public class PromotionalPricingStrategy implements PricingStrategy {
    private final BigDecimal discountPercentage;
    
    @Override
    public BigDecimal calculatePrice(Flight flight, SeatClass seatClass) {
        BigDecimal basePrice = flight.getBasePrice(seatClass);
        return basePrice.multiply(BigDecimal.ONE.subtract(discountPercentage));
    }
}
```

### Adding a New Notification Channel

```java
// Implement both observer interfaces for push notifications
public class PushNotificationObserver implements FlightObserver, BookingObserver {
    @Override
    public void onBookingConfirmed(Booking booking) {
        sendPushNotification(booking.getPassengers(), "Booking confirmed: " + booking.getPnr());
    }
    // ... other methods
}

// Register observer
airline.addBookingObserver(new PushNotificationObserver());
```

---

## 8. Booking Flow

```
[User Searches Flight]
       │
       ▼
   ┌───────────┐
   │ SEARCH    │ FlightService.searchFlights()
   └─────┬─────┘
         │
         ▼
   ┌───────────┐
   │ SELECT    │ SeatService.getAvailableSeats()
   │ SEATS     │
   └─────┬─────┘
         │
         ▼
   ┌───────────┐
   │ CREATE    │ BookingService.createBooking()
   │ BOOKING   │ Status: PENDING
   └─────┬─────┘
         │
         ▼
   ┌───────────┐
   │ PAYMENT   │ PaymentService.processPayment()
   │           │
   └─────┬─────┘
         │
    ┌────┴────┐
    ▼         ▼
┌───────┐  ┌───────┐
│SUCCESS│  │FAILED │
└───┬───┘  └───┬───┘
    │          │
    ▼          ▼
┌───────────┐  ┌───────────┐
│CONFIRMED  │  │PENDING    │
│Status     │  │(Retry)    │
└─────┬─────┘  └───────────┘
      │
      ▼
┌───────────┐
│ ISSUE     │ TicketFactory.createTickets()
│ TICKETS   │
└───────────┘
```

---

## 9. File Structure

```
airline/
├── enums/
│   ├── AircraftStatus.java
│   ├── BaggageType.java
│   ├── BookingStatus.java
│   ├── CrewRole.java
│   ├── FlightStatus.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   ├── SeatClass.java
│   ├── SeatStatus.java
│   └── UserRole.java
├── exceptions/
│   ├── AirlineException.java
│   ├── BookingException.java
│   ├── FlightException.java
│   ├── PassengerException.java
│   ├── PaymentException.java
│   └── SeatException.java
├── factories/
│   └── TicketFactory.java
├── models/
│   ├── Aircraft.java
│   ├── Airport.java
│   ├── Baggage.java
│   ├── Booking.java
│   ├── Crew.java
│   ├── Flight.java
│   ├── FlightSearchResult.java
│   ├── Passenger.java
│   ├── Payment.java
│   ├── Seat.java
│   ├── Ticket.java
│   └── User.java
├── observers/
│   ├── BookingObserver.java
│   ├── EmailNotificationObserver.java
│   ├── FlightObserver.java
│   └── SMSNotificationObserver.java
├── repositories/
│   ├── AircraftRepository.java
│   ├── BookingRepository.java
│   ├── CrewRepository.java
│   ├── FlightRepository.java
│   ├── PassengerRepository.java
│   ├── Repository.java
│   └── impl/
│       ├── InMemoryAircraftRepository.java
│       ├── InMemoryBookingRepository.java
│       ├── InMemoryCrewRepository.java
│       ├── InMemoryFlightRepository.java
│       └── InMemoryPassengerRepository.java
├── services/
│   ├── AircraftService.java
│   ├── BookingService.java
│   ├── CrewService.java
│   ├── FlightService.java
│   ├── PassengerService.java
│   ├── PaymentService.java
│   ├── SeatService.java
│   └── impl/
│       ├── AircraftServiceImpl.java
│       ├── BookingServiceImpl.java
│       ├── CrewServiceImpl.java
│       ├── FlightServiceImpl.java
│       ├── PassengerServiceImpl.java
│       ├── PaymentServiceImpl.java
│       └── SeatServiceImpl.java
├── strategies/
│   ├── payment/
│   │   ├── CreditCardPaymentStrategy.java
│   │   ├── DebitCardPaymentStrategy.java
│   │   ├── NetBankingPaymentStrategy.java
│   │   ├── PaymentStrategy.java
│   │   └── WalletPaymentStrategy.java
│   ├── pricing/
│   │   ├── DynamicPricingStrategy.java
│   │   ├── PricingStrategy.java
│   │   ├── SeasonalPricingStrategy.java
│   │   └── StandardPricingStrategy.java
│   ├── refund/
│   │   ├── FullRefundStrategy.java
│   │   ├── NoRefundStrategy.java
│   │   ├── RefundStrategy.java
│   │   └── TimeBasedRefundStrategy.java
│   └── search/
│       ├── CheapestFlightStrategy.java
│       ├── EarliestDepartureStrategy.java
│       ├── FastestFlightStrategy.java
│       └── FlightSearchStrategy.java
├── AirlineManagement.java  # Facade
└── Main.java               # Demo
```

---

## 10. Usage Example

```java
// Initialize the airline management system
AirlineManagement airline = new AirlineManagement("SkyHigh Airways");

// Create airports
Airport jfk = airline.createAirport("JFK", "John F. Kennedy", "New York", "USA", "America/New_York");
Airport lax = airline.createAirport("LAX", "Los Angeles International", "Los Angeles", "USA", "America/Los_Angeles");

// Add aircraft
Aircraft boeing737 = airline.addAircraft("N12345", "737-800", "Boeing", 120, 24, 12);

// Add crew
Crew pilot = airline.addCrewMember("John", "Smith", CrewRole.PILOT, "737-800");
Crew coPilot = airline.addCrewMember("Jane", "Doe", CrewRole.CO_PILOT, "737-800");

// Create flight
Flight flight = airline.addFlight("SH101", jfk, lax, departure, arrival, boeing737, new BigDecimal("299.99"));

// Assign crew
airline.assignCrewToFlight(pilot.getId(), flight);
airline.assignCrewToFlight(coPilot.getId(), flight);

// Search flights
airline.setSearchStrategy(new CheapestFlightStrategy());
List<FlightSearchResult> results = airline.searchFlights(jfk, lax, LocalDate.now().plusDays(1));

// Register passenger
Passenger passenger = airline.registerPassenger("Alice", "Williams", "alice@email.com", "+1-555-0101", 
        LocalDate.of(1990, 5, 15), "US123456", "USA");

// Add baggage
airline.addBaggage(passenger.getId(), BaggageType.CHECKED, 20.0);

// Create booking
Booking booking = airline.createBooking(flight, passenger, "1A");

// Confirm with payment
airline.confirmBooking(booking.getId(), PaymentMethod.CREDIT_CARD);

// Issue tickets
List<Ticket> tickets = airline.issueTickets(booking);

// Cancel if needed
airline.cancelBooking(booking.getId(), "Customer requested");
airline.processRefund(booking.getId());
```

---

## 11. Key Design Decisions

1. **Dynamic Pricing as Default**: Uses `DynamicPricingStrategy` by default which adjusts prices based on seat availability and time to departure.

2. **Time-Based Refunds**: Default refund policy reduces refund amount as departure approaches (100% > 7 days, 75% 3-7 days, 50% 1-3 days, 25% < 24 hours).

3. **Thread-Safe Seat Booking**: Seat booking uses `ReentrantLock` to prevent double-booking in concurrent scenarios.

4. **Automatic Seat Generation**: Seats are automatically generated from aircraft configuration when a flight is created.

5. **Builder Pattern for Complex Objects**: Flight, Booking, Passenger, Aircraft, and Crew use Builder pattern for flexible construction.

6. **Facade for Simplicity**: `AirlineManagement` class hides complexity and provides a clean API for common operations.

7. **Observer for Notifications**: Flight status changes and booking events are automatically broadcast to registered observers.

---

## 12. Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer
- [ ] Multi-leg/connecting flights
- [ ] Loyalty program integration
- [ ] Real-time flight tracking
- [ ] Check-in and boarding pass generation
- [ ] Seat upgrade workflow
- [ ] Group booking discounts
- [ ] Travel insurance integration
- [ ] Airport lounge access management



