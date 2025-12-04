# Ride-Sharing Service - Low Level Design

## Overview

A comprehensive ride-sharing service similar to Uber, designed using SOLID principles and common design patterns. The system handles ride requests, driver matching, fare calculation, payments, real-time tracking, and notifications.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         RideSharingService (Facade)                         │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐│
│  │ RideService │ │ UserService │ │ FareService │ │ NotificationService     ││
│  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └───────────┬─────────────┘│
│         │               │               │                     │              │
│  ┌──────┴──────┐ ┌──────┴──────┐ ┌──────┴──────┐     ┌───────┴───────┐     │
│  │PaymentSvc   │ │TrackingSvc  │ │MatchingSvc  │     │   Observers   │     │
│  └─────────────┘ └─────────────┘ └─────────────┘     └───────────────┘     │
├─────────────────────────────────────────────────────────────────────────────┤
│                              STRATEGIES                                      │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐               │
│  │ PricingStrategy │ │MatchingStrategy │ │ PaymentStrategy │               │
│  │  - Standard     │ │  - Nearest      │ │  - Card         │               │
│  │  - Surge        │ │  - RatingBased  │ │  - Wallet       │               │
│  │  - PeakHour     │ │  - Composite    │ │  - Cash         │               │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘               │
├─────────────────────────────────────────────────────────────────────────────┤
│                              REPOSITORIES                                    │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐ ┌───────────────┐│
│  │ RideRepository │ │DriverRepository│ │PassengerRepo   │ │PaymentRepo    ││
│  └────────────────┘ └────────────────┘ └────────────────┘ └───────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
```

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                   MODELS                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐          ┌─────────────────┐                           │
│  │   <<abstract>>  │          │    Location     │                           │
│  │      User       │          ├─────────────────┤                           │
│  ├─────────────────┤          │ - latitude      │                           │
│  │ - userId        │          │ - longitude     │                           │
│  │ - name          │          │ - address       │                           │
│  │ - email         │          └─────────────────┘                           │
│  │ - phone         │                                                         │
│  │ - rating        │          ┌─────────────────┐                           │
│  └────────┬────────┘          │    Vehicle      │                           │
│           │                   ├─────────────────┤                           │
│     ┌─────┴─────┐             │ - vehicleId     │                           │
│     │           │             │ - licensePlate  │                           │
│ ┌───┴───┐   ┌───┴───┐         │ - make/model    │                           │
│ │Passenger│ │ Driver │◆──────│ - vehicleType   │                           │
│ ├────────┤ ├────────┤         └─────────────────┘                           │
│ │payMethod│ │vehicle │                                                       │
│ │rideHist │ │status  │         ┌─────────────────┐                          │
│ └────────┘ │location │         │      Ride       │                          │
│            │earnings │         ├─────────────────┤                          │
│            └────────┘          │ - rideId        │                          │
│                                │ - passengerId   │                          │
│                                │ - driverId      │                          │
│                                │ - pickup/dropoff│                          │
│                                │ - rideType      │                          │
│                                │ - status        │                          │
│                                │ - fare          │                          │
│                                │ - payment       │                          │
│                                └─────────────────┘                          │
│                                                                              │
│  ┌─────────────────┐          ┌─────────────────┐                           │
│  │      Fare       │          │    Payment      │                           │
│  ├─────────────────┤          ├─────────────────┤                           │
│  │ - baseFare      │          │ - paymentId     │                           │
│  │ - distanceFare  │          │ - amount        │                           │
│  │ - timeFare      │          │ - paymentMethod │                           │
│  │ - surgeMultiplier          │ - status        │                           │
│  │ - getTotalAmount()         │ - transactionRef│                           │
│  └─────────────────┘          └─────────────────┘                           │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Design Patterns Used

### 1. Strategy Pattern

Used for pluggable algorithms that can be swapped at runtime:

| Strategy Type | Implementations | Purpose |
|---------------|-----------------|---------|
| **PricingStrategy** | StandardPricingStrategy, SurgePricingStrategy, PeakHourPricingStrategy | Calculate ride fares with different pricing models |
| **DriverMatchingStrategy** | NearestDriverStrategy, RatingBasedMatchingStrategy, CompositeMatchingStrategy | Match drivers to ride requests |
| **PaymentStrategy** | CardPaymentStrategy, WalletPaymentStrategy, CashPaymentStrategy | Process different payment methods |
| **DistanceCalculationStrategy** | HaversineDistanceStrategy, ManhattanDistanceStrategy | Calculate distances between locations |

```java
// Example: Adding a new pricing strategy
public class HolidayPricingStrategy implements PricingStrategy {
    @Override
    public Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType) {
        // Apply holiday rates
    }
}
```

### 2. Observer Pattern

Used for real-time notifications when ride status changes:

```
RideService ──notifies──> NotificationService ──broadcasts──> Observers
                                                    │
                                    ┌───────────────┼───────────────┐
                                    ▼               ▼               ▼
                          PassengerObserver  DriverObserver  AnalyticsObserver
```

### 3. Factory Pattern

Used for creating complex objects with proper initialization:

```java
RideSharingService service = RideSharingServiceFactory.createDefault();
// or with custom configuration
RideSharingService service = RideSharingServiceFactory.create(
    new SurgePricingStrategy(new StandardPricingStrategy(), 1.5),
    new RatingBasedMatchingStrategy(distanceStrategy),
    paymentStrategies
);
```

### 4. Facade Pattern

`RideSharingService` provides a simplified interface to the complex subsystem:

```java
// Simple API hiding complexity
service.requestRide(passengerId, pickup, dropoff, RideType.REGULAR);
service.acceptRide(rideId, driverId);
service.completeRide(rideId);
```

### 5. Repository Pattern

Abstracts data access, allowing easy swap between in-memory and database storage:

```java
public interface RideRepository {
    Ride save(Ride ride);
    Optional<Ride> findById(String rideId);
    List<Ride> findByPassengerId(String passengerId);
    // ... other methods
}
```

### 6. Builder Pattern

Used for creating complex objects with many optional parameters:

```java
Ride ride = Ride.builder()
    .passengerId(passengerId)
    .pickupLocation(pickup)
    .dropoffLocation(dropoff)
    .rideType(RideType.PREMIUM)
    .estimatedDistance(15.5)
    .build();
```

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each service handles one aspect: `RideService` for rides, `PaymentService` for payments
- Observers handle specific notification channels

### Open/Closed Principle (OCP)
- New pricing strategies can be added without modifying existing code
- New payment methods are added via new strategy implementations

### Liskov Substitution Principle (LSP)
- All strategy implementations are interchangeable
- `Passenger` and `Driver` can be used where `User` is expected

### Interface Segregation Principle (ISP)
- Small, focused interfaces: `PricingStrategy`, `PaymentStrategy`
- Clients depend only on methods they use

### Dependency Inversion Principle (DIP)
- Services depend on interfaces, not concrete implementations
- Constructor injection for all dependencies

## Ride Lifecycle (State Machine)

```
┌─────────────┐
│  REQUESTED  │ ──────────────────────────────────┐
└──────┬──────┘                                    │
       │ Driver matched                            │
       ▼                                           │
┌─────────────┐                                    │
│   MATCHED   │ ──────────────────────────────────┼──┐
└──────┬──────┘                                    │  │
       │ Driver accepts                            │  │
       ▼                                           │  │
┌─────────────┐                                    │  │
│  ACCEPTED   │ ──────────────────────────────────┼──┤
└──────┬──────┘                                    │  │
       │ Driver arrives                            │  │  Cancel
       ▼                                           │  │
┌─────────────────┐                                │  │
│ DRIVER_ARRIVED  │ ──────────────────────────────┼──┤
└────────┬────────┘                                │  │
         │ Passenger picked up                     │  │
         ▼                                         │  │
┌─────────────┐                                    │  │
│ IN_PROGRESS │ ──────────────────────────────────┘  │
└──────┬──────┘                                       │
       │ Reached destination                          │
       ▼                                              ▼
┌─────────────┐                              ┌─────────────┐
│  COMPLETED  │                              │  CANCELLED  │
└─────────────┘                              └─────────────┘
```

## Key Flows

### 1. Ride Request Flow

```
Passenger                    RideService            MatchingService          Driver
    │                            │                        │                     │
    │ requestRide(request)       │                        │                     │
    │ ─────────────────────────> │                        │                     │
    │                            │ findBestDriver()       │                     │
    │                            │ ─────────────────────> │                     │
    │                            │                        │ filter by proximity │
    │                            │                        │ sort by score       │
    │                            │ <───────────────────── │                     │
    │                            │                        │                     │
    │                            │ notifyDriverMatched()  │                     │
    │                            │ ────────────────────────────────────────────>│
    │ <───────────────────────── │                        │                     │
    │     Ride (with driver)     │                        │                     │
```

### 2. Fare Calculation Flow

```
RideRequest ──> FareService
                    │
                    ├──> DistanceStrategy.calculateDistance()
                    ├──> DistanceStrategy.estimateTravelTime()
                    ├──> Check surge multiplier
                    ├──> PricingStrategy.calculateFare()
                    │
                    └──> Fare {
                            baseFare: $2.50
                            distanceFare: $15.00
                            timeFare: $5.00
                            surgeMultiplier: 1.25
                            rideType: PREMIUM (1.5x)
                            ─────────────────────
                            total: $33.75
                         }
```

## Extension Points

### Adding a New Ride Type

1. Add to `RideType` enum:
```java
public enum RideType {
    REGULAR(1.0), PREMIUM(1.5), POOL(0.7),
    GREEN(0.9, "Eco-friendly electric vehicles")  // NEW
}
```

2. Optionally add matching strategy for the new type in `CompositeMatchingStrategy`

### Adding a New Payment Method

1. Create new strategy:
```java
public class CryptoPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean processPayment(Payment payment) {
        // Integrate with crypto payment gateway
    }
}
```

2. Register in factory:
```java
paymentStrategies.add(new CryptoPaymentStrategy());
```

### Adding a New Notification Channel

1. Implement `RideObserver`:
```java
public class SMSNotificationObserver implements RideObserver {
    @Override
    public void onRideStatusChanged(Ride ride) {
        smsService.send(ride.getPassengerId(), "Your ride status: " + ride.getStatus());
    }
}
```

2. Register observer:
```java
notificationService.registerObserver(new SMSNotificationObserver());
```

## Concurrency Handling

- **Thread-safe collections**: `ConcurrentHashMap` in repositories
- **Atomic operations**: `AtomicInteger`, `AtomicLong` for counters
- **Copy-on-write**: `CopyOnWriteArrayList` for observer list
- **Immutable value objects**: `Location`, `Fare`, `RideRequest`

## Project Structure

```
ridesharing/
├── enums/
│   ├── DriverStatus.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   ├── RideStatus.java
│   ├── RideType.java
│   └── VehicleType.java
├── exceptions/
│   ├── DriverNotFoundException.java
│   ├── InvalidLocationException.java
│   ├── InvalidRideStateException.java
│   ├── NoDriverAvailableException.java
│   ├── PassengerNotFoundException.java
│   ├── PaymentException.java
│   ├── RideNotFoundException.java
│   └── RideSharingException.java
├── factories/
│   ├── RideSharingService.java      # Main facade
│   └── RideSharingServiceFactory.java
├── models/
│   ├── Driver.java
│   ├── Fare.java
│   ├── Location.java
│   ├── Passenger.java
│   ├── Payment.java
│   ├── Ride.java
│   ├── RideRequest.java
│   ├── User.java
│   └── Vehicle.java
├── observers/
│   ├── AnalyticsObserver.java
│   ├── DriverNotificationObserver.java
│   ├── PassengerNotificationObserver.java
│   └── RideObserver.java
├── repositories/
│   ├── DriverRepository.java
│   ├── PassengerRepository.java
│   ├── PaymentRepository.java
│   ├── RideRepository.java
│   └── impl/
│       ├── InMemoryDriverRepository.java
│       ├── InMemoryPassengerRepository.java
│       ├── InMemoryPaymentRepository.java
│       └── InMemoryRideRepository.java
├── services/
│   ├── DriverMatchingService.java
│   ├── FareService.java
│   ├── NotificationService.java
│   ├── PaymentService.java
│   ├── RideService.java
│   ├── TrackingService.java
│   ├── UserService.java
│   └── impl/
│       ├── DriverMatchingServiceImpl.java
│       ├── FareServiceImpl.java
│       ├── NotificationServiceImpl.java
│       ├── PaymentServiceImpl.java
│       ├── RideServiceImpl.java
│       ├── TrackingServiceImpl.java
│       └── UserServiceImpl.java
├── strategies/
│   ├── distance/
│   │   ├── DistanceCalculationStrategy.java
│   │   ├── HaversineDistanceStrategy.java
│   │   └── ManhattanDistanceStrategy.java
│   ├── matching/
│   │   ├── CompositeMatchingStrategy.java
│   │   ├── DriverMatchingStrategy.java
│   │   ├── NearestDriverStrategy.java
│   │   └── RatingBasedMatchingStrategy.java
│   ├── payment/
│   │   ├── CardPaymentStrategy.java
│   │   ├── CashPaymentStrategy.java
│   │   ├── PaymentStrategy.java
│   │   └── WalletPaymentStrategy.java
│   └── pricing/
│       ├── PeakHourPricingStrategy.java
│       ├── PricingStrategy.java
│       ├── StandardPricingStrategy.java
│       └── SurgePricingStrategy.java
└── Main.java
```

## Usage Example

```java
// Create service
RideSharingService service = RideSharingServiceFactory.createDefault();

// Register users
Passenger passenger = service.registerPassenger("Alice", "alice@email.com", "555-0101");
Vehicle vehicle = Vehicle.builder()
    .vehicleId("V001")
    .licensePlate("ABC-1234")
    .make("Toyota").model("Camry").color("Black").year(2022)
    .vehicleType(VehicleType.SEDAN)
    .build();
Driver driver = service.registerDriver("John", "john@email.com", "555-0201", vehicle, "DL-12345");

// Driver goes online
service.setDriverOnline(driver.getUserId(), new Location(40.7128, -74.0060, "Times Square"));

// Get fare estimate
Fare estimate = service.getFareEstimate(
    new Location(40.7484, -73.9857, "Empire State Building"),
    new Location(40.6892, -74.0445, "Statue of Liberty"),
    RideType.REGULAR
);
System.out.println("Estimated fare: $" + estimate.getTotalAmount());

// Request ride
Ride ride = service.requestRide(
    passenger.getUserId(),
    new Location(40.7484, -73.9857, "Empire State Building"),
    new Location(40.6892, -74.0445, "Statue of Liberty"),
    RideType.REGULAR
);

// Complete ride flow
service.acceptRide(ride.getRideId(), driver.getUserId());
service.driverArrived(ride.getRideId());
service.startRide(ride.getRideId());
Ride completed = service.completeRide(ride.getRideId());

// Rate the experience
service.rateDriver(ride.getRideId(), 5);
```

## Future Enhancements

1. **Database Integration**: Replace in-memory repositories with JPA/JDBC implementations
2. **Scheduled Rides**: Add support for booking rides in advance
3. **Multi-stop Rides**: Support for multiple pickup/dropoff locations
4. **Ride Sharing (Pool)**: Match multiple passengers going in the same direction
5. **Dynamic Surge Pricing**: ML-based demand prediction
6. **Driver Incentives**: Bonus system for high-performing drivers
7. **Promo Codes**: Discount system for passengers
8. **Trip History Analytics**: Detailed reporting and insights



