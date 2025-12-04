# Parking Lot System - Low Level Design

## Problem Statement
Design a parking lot system that can manage multiple levels, different vehicle types, and handle concurrent access through multiple entry/exit points.

---

## Requirements

1. The parking lot should have multiple levels, each level with a certain number of parking spots
2. The parking lot should support different types of vehicles (cars, motorcycles, trucks)
3. Each parking spot should accommodate a specific type of vehicle
4. The system should assign a parking spot to a vehicle upon entry and release it when the vehicle exits
5. The system should track the availability of parking spots and provide real-time information
6. The system should handle multiple entry and exit points and support concurrent access

---

## Assumptions & Clarifications

1. **Entry/Exit Points**: Multiple entry/exit gates that can operate concurrently
2. **Pricing Model**: Hourly-based pricing with rates varying by vehicle type
3. **Spot Allocation**: First-available strategy by default, extensible for other strategies
4. **Payment**: Payment is collected at exit (no prepayment or reservations)
5. **Concurrency**: Thread-safe operations for multi-gate access
6. **Display**: Real-time availability display at each entry point

---

## LLD Overview

### 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| `ParkingLot` | Central manager (Singleton), coordinates levels, gates, handles vehicle parking/unparking |
| `Level` | Manages parking spots on a single floor, finds available spots for vehicles |
| `ParkingSpot` | Represents individual spot, tracks occupancy and vehicle type compatibility |
| `Vehicle` | Abstract representation of vehicles with type information |
| `VehicleType` | Enumeration of supported vehicle types with size factors |
| `ParkingTicket` | Represents parking transaction with entry time for billing |
| `EntryGate` | Handles vehicle entry, ticket issuance, displays availability |
| `ExitGate` | Handles vehicle exit, payment processing, ticket validation |
| `PricingStrategy` | Calculates parking fee based on duration and vehicle type |
| `SpotAllocationStrategy` | Determines which spot to allocate for a vehicle |
| `DisplayBoard` | Shows real-time availability information |
| `PaymentProcessor` | Handles payment collection and receipt generation |
| `ParkingObserver` | Receives notifications on parking/unparking events |

### 2. Key Abstractions

#### Enums
- **`VehicleType`**: MOTORCYCLE, CAR, TRUCK - defines vehicle categories with size factors
- **`PaymentStatus`**: PENDING, COMPLETED, FAILED - payment transaction states
- **`GateType`**: ENTRY, EXIT - gate classification

#### Models
- **`Vehicle`** (abstract): Base class for all vehicles with license plate and type
- **`Car`**, **`Motorcycle`**, **`Truck`**: Concrete vehicle implementations
- **`ParkingSpot`**: Individual parking space with type and availability status
- **`Level`**: Floor in parking lot containing multiple spots
- **`ParkingTicket`**: Transaction record with vehicle, spot, and timing info
- **`EntryGate`**: Entry point with display board
- **`ExitGate`**: Exit point with payment processor

#### Interfaces
- **`PricingStrategy`**: Strategy for calculating parking fees
- **`SpotAllocationStrategy`**: Strategy for selecting parking spots
- **`ParkingObserver`**: Observer for parking events
- **`PaymentProcessor`**: Payment handling abstraction

#### Core Service
- **`ParkingLot`**: Singleton service managing the entire parking operation

### 3. Relationships & Collaborations

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ParkingLot                                      │
│                              (Singleton)                                     │
│  ┌───────────────┐  ┌───────────────┐  ┌────────────────────────────────┐   │
│  │  EntryGate[]  │  │  ExitGate[]   │  │         Level[]                │   │
│  │               │  │               │  │  ┌────────────────────────┐    │   │
│  │ DisplayBoard  │  │PaymentProcessor│  │  │    ParkingSpot[]      │    │   │
│  └───────────────┘  └───────────────┘  │  └────────────────────────┘    │   │
│                                        └────────────────────────────────┘   │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                         Strategies                                     │  │
│  │    ┌─────────────────────┐    ┌─────────────────────────────┐         │  │
│  │    │  PricingStrategy    │    │  SpotAllocationStrategy     │         │  │
│  │    └─────────────────────┘    └─────────────────────────────┘         │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                      Observers (List<ParkingObserver>)                 │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   Vehicle       │
                    │   (abstract)    │
                    └─────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
    ┌──────────┐       ┌──────────┐       ┌──────────┐
    │   Car    │       │Motorcycle│       │  Truck   │
    └──────────┘       └──────────┘       └──────────┘
```

### 4. SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **SRP** | Each class has single responsibility (Level manages spots, ParkingSpot tracks occupancy, Gate handles entry/exit) |
| **OCP** | New vehicle types, pricing strategies, and allocation strategies can be added without modifying existing code |
| **LSP** | All Vehicle subclasses can be used interchangeably where Vehicle is expected |
| **ISP** | Separate interfaces for PricingStrategy, SpotAllocationStrategy, PaymentProcessor |
| **DIP** | ParkingLot depends on abstractions (strategies, observers), not concrete implementations |

### 5. Design Patterns Used

| Pattern | Usage |
|---------|-------|
| **Singleton** | ParkingLot ensures only one instance manages the parking system |
| **Factory** | VehicleFactory creates appropriate vehicle instances |
| **Strategy** | PricingStrategy and SpotAllocationStrategy for pluggable algorithms |
| **Observer** | ParkingObserver notifies components of parking/unparking events |
| **Template Method** | Vehicle base class defines common behavior, subclasses customize |

### 6. Extension Points

- **New Vehicle Types**: Add new VehicleType enum value and corresponding Vehicle subclass
- **Pricing Strategies**: Implement PricingStrategy interface (hourly, daily, weekend rates, etc.)
- **Spot Allocation Strategies**: Implement SpotAllocationStrategy (nearest, spread, level-balanced)
- **Payment Methods**: Implement PaymentProcessor for cash, card, mobile payments
- **Notification System**: Implement ParkingObserver for SMS/email notifications
- **Display Integration**: Implement DisplayBoard for LED panels, mobile apps

---

## Class Diagram

```
┌────────────────────────────────────────────────────────────────────────────┐
│                        <<enumeration>> VehicleType                          │
├────────────────────────────────────────────────────────────────────────────┤
│ MOTORCYCLE(1), CAR(2), TRUCK(3)                                             │
│ + getSizeFactor(): int                                                      │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                         <<interface>> PricingStrategy                       │
├────────────────────────────────────────────────────────────────────────────┤
│ + calculateFee(ticket: ParkingTicket): double                               │
└────────────────────────────────────────────────────────────────────────────┘
            △
            │
    ┌───────┴───────────────────┐
    │                           │
┌───┴──────────────┐  ┌─────────┴────────────┐
│HourlyPricingStrategy│ │WeekendPricingStrategy│
└──────────────────┘  └──────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                    <<interface>> SpotAllocationStrategy                     │
├────────────────────────────────────────────────────────────────────────────┤
│ + findSpot(levels: List<Level>, vehicle: Vehicle): Optional<SpotResult>    │
└────────────────────────────────────────────────────────────────────────────┘
            △
            │
    ┌───────┴───────────────────┐
    │                           │
┌───┴──────────────┐  ┌─────────┴────────────┐
│FirstAvailableStrategy│ │NearestEntryStrategy│
└──────────────────┘  └──────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                      <<interface>> ParkingObserver                          │
├────────────────────────────────────────────────────────────────────────────┤
│ + onVehicleParked(ticket: ParkingTicket): void                              │
│ + onVehicleUnparked(ticket: ParkingTicket): void                            │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                     <<interface>> PaymentProcessor                          │
├────────────────────────────────────────────────────────────────────────────┤
│ + processPayment(amount: double, ticket: ParkingTicket): PaymentResult     │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                         Vehicle (abstract)                                  │
├────────────────────────────────────────────────────────────────────────────┤
│ - licensePlate: String                                                      │
│ - type: VehicleType                                                         │
├────────────────────────────────────────────────────────────────────────────┤
│ + getLicensePlate(): String                                                 │
│ + getType(): VehicleType                                                    │
└────────────────────────────────────────────────────────────────────────────┘
            △
            │
    ┌───────┼───────┐
    │       │       │
┌───┴───┐ ┌─┴─┐ ┌───┴────┐
│  Car  │ │ M │ │ Truck  │
└───────┘ └───┘ └────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                              ParkingSpot                                    │
├────────────────────────────────────────────────────────────────────────────┤
│ - spotNumber: int                                                           │
│ - vehicleType: VehicleType                                                  │
│ - parkedVehicle: Vehicle                                                    │
├────────────────────────────────────────────────────────────────────────────┤
│ + isAvailable(): boolean                                                    │
│ + canFitVehicle(Vehicle): boolean                                           │
│ + parkVehicle(Vehicle): boolean                                             │
│ + unparkVehicle(): Vehicle                                                  │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                                 Level                                       │
├────────────────────────────────────────────────────────────────────────────┤
│ - floorNumber: int                                                          │
│ - parkingSpots: List<ParkingSpot>                                           │
├────────────────────────────────────────────────────────────────────────────┤
│ + parkVehicle(Vehicle): ParkingSpot                                         │
│ + unparkVehicle(Vehicle): boolean                                           │
│ + getAvailableSpots(): List<ParkingSpot>                                    │
│ + getAvailableSpotCount(VehicleType): int                                   │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                          ParkingLot (Singleton)                             │
├────────────────────────────────────────────────────────────────────────────┤
│ - instance: ParkingLot                                                      │
│ - levels: List<Level>                                                       │
│ - entryGates: List<EntryGate>                                               │
│ - exitGates: List<ExitGate>                                                 │
│ - activeTickets: Map<String, ParkingTicket>                                 │
│ - pricingStrategy: PricingStrategy                                          │
│ - allocationStrategy: SpotAllocationStrategy                                │
│ - observers: List<ParkingObserver>                                          │
├────────────────────────────────────────────────────────────────────────────┤
│ + getInstance(): ParkingLot                                                 │
│ + parkVehicle(Vehicle, EntryGate): ParkingTicket                            │
│ + unparkVehicle(ParkingTicket, ExitGate): PaymentResult                     │
│ + getAvailability(): Map<Level, Map<VehicleType, Integer>>                  │
│ + addObserver(ParkingObserver): void                                        │
│ + setPricingStrategy(PricingStrategy): void                                 │
│ + setAllocationStrategy(SpotAllocationStrategy): void                       │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                              ParkingTicket                                  │
├────────────────────────────────────────────────────────────────────────────┤
│ - ticketId: String                                                          │
│ - vehicle: Vehicle                                                          │
│ - parkingSpot: ParkingSpot                                                  │
│ - level: Level                                                              │
│ - entryGate: EntryGate                                                      │
│ - entryTime: LocalDateTime                                                  │
│ - exitTime: LocalDateTime                                                   │
├────────────────────────────────────────────────────────────────────────────┤
│ + calculateDuration(): Duration                                             │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                              EntryGate                                      │
├────────────────────────────────────────────────────────────────────────────┤
│ - gateId: String                                                            │
│ - displayBoard: DisplayBoard                                                │
├────────────────────────────────────────────────────────────────────────────┤
│ + issueTicket(Vehicle): ParkingTicket                                       │
│ + updateDisplay(): void                                                     │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                               ExitGate                                      │
├────────────────────────────────────────────────────────────────────────────┤
│ - gateId: String                                                            │
│ - paymentProcessor: PaymentProcessor                                        │
├────────────────────────────────────────────────────────────────────────────┤
│ + processExit(ParkingTicket): PaymentResult                                 │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                             DisplayBoard                                    │
├────────────────────────────────────────────────────────────────────────────┤
│ - displayId: String                                                         │
├────────────────────────────────────────────────────────────────────────────┤
│ + showAvailability(Map<Level, Map<VehicleType, Integer>>): void            │
│ + showMessage(String): void                                                 │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## Sequence Diagram - Vehicle Entry

```
┌──────┐     ┌──────────┐     ┌───────────┐     ┌────────────────────┐     ┌───────┐
│Vehicle│     │EntryGate │     │ParkingLot │     │SpotAllocationStrategy│     │ Level │
└──┬───┘     └────┬─────┘     └─────┬─────┘     └──────────┬─────────┘     └───┬───┘
   │              │                 │                      │                   │
   │ arrive()     │                 │                      │                   │
   │─────────────>│                 │                      │                   │
   │              │                 │                      │                   │
   │              │ parkVehicle()   │                      │                   │
   │              │────────────────>│                      │                   │
   │              │                 │                      │                   │
   │              │                 │ findSpot(levels, v)  │                   │
   │              │                 │─────────────────────>│                   │
   │              │                 │                      │                   │
   │              │                 │                      │  getAvailable()   │
   │              │                 │                      │──────────────────>│
   │              │                 │                      │                   │
   │              │                 │                      │ spotList          │
   │              │                 │                      │<──────────────────│
   │              │                 │                      │                   │
   │              │                 │   SpotResult         │                   │
   │              │                 │<─────────────────────│                   │
   │              │                 │                      │                   │
   │              │                 │       parkVehicle()  │                   │
   │              │                 │──────────────────────────────────────────>
   │              │                 │                      │                   │
   │              │ ParkingTicket   │                      │                   │
   │              │<────────────────│                      │                   │
   │              │                 │                      │                   │
   │              │ notifyObservers()                      │                   │
   │              │<────────────────│                      │                   │
   │              │                 │                      │                   │
   │ ticket       │                 │                      │                   │
   │<─────────────│                 │                      │                   │
   │              │                 │                      │                   │
```

---

## Sequence Diagram - Vehicle Exit

```
┌──────┐     ┌─────────┐     ┌───────────┐     ┌────────────────┐     ┌─────────────────┐
│Ticket│     │ExitGate │     │ParkingLot │     │PricingStrategy │     │PaymentProcessor │
└──┬───┘     └────┬────┘     └─────┬─────┘     └───────┬────────┘     └────────┬────────┘
   │              │                │                   │                       │
   │ present()    │                │                   │                       │
   │─────────────>│                │                   │                       │
   │              │                │                   │                       │
   │              │ processExit()  │                   │                       │
   │              │───────────────>│                   │                       │
   │              │                │                   │                       │
   │              │                │ calculateFee()    │                       │
   │              │                │──────────────────>│                       │
   │              │                │                   │                       │
   │              │                │    feeAmount      │                       │
   │              │                │<──────────────────│                       │
   │              │                │                   │                       │
   │              │                │              processPayment()             │
   │              │                │──────────────────────────────────────────>│
   │              │                │                   │                       │
   │              │                │              PaymentResult                │
   │              │                │<──────────────────────────────────────────│
   │              │                │                   │                       │
   │              │                │ unparkVehicle()   │                       │
   │              │                │ notifyObservers() │                       │
   │              │                │                   │                       │
   │              │ PaymentResult  │                   │                       │
   │              │<───────────────│                   │                       │
   │              │                │                   │                       │
   │ exit()       │                │                   │                       │
   │<─────────────│                │                   │                       │
```

---

## Implementation Notes

- Thread safety achieved using `synchronized` blocks on critical sections
- Double-checked locking for Singleton pattern
- `ConcurrentHashMap` for active tickets to support concurrent gate operations
- Vehicles are assigned to appropriate spots based on VehicleType matching
- Strategy pattern enables runtime switching of pricing and allocation algorithms
- Observer pattern decouples parking events from display/notification systems

---

## File Structure

```
parkinglot/
├── enums/
│   ├── VehicleType.java
│   ├── PaymentStatus.java
│   └── GateType.java
├── exceptions/
│   └── ParkingException.java
├── factories/
│   └── VehicleFactory.java
├── models/
│   ├── Vehicle.java (abstract)
│   ├── Car.java
│   ├── Motorcycle.java
│   ├── Truck.java
│   ├── ParkingSpot.java
│   ├── Level.java
│   ├── ParkingTicket.java
│   ├── EntryGate.java
│   ├── ExitGate.java
│   ├── DisplayBoard.java
│   ├── PaymentResult.java
│   └── SpotResult.java
├── observers/
│   ├── ParkingObserver.java (interface)
│   ├── DisplayBoardObserver.java
│   └── NotificationObserver.java
├── strategies/
│   ├── pricing/
│   │   ├── PricingStrategy.java (interface)
│   │   ├── HourlyPricingStrategy.java
│   │   └── WeekendPricingStrategy.java
│   ├── allocation/
│   │   ├── SpotAllocationStrategy.java (interface)
│   │   └── FirstAvailableStrategy.java
│   └── payment/
│       ├── PaymentProcessor.java (interface)
│       └── CashPaymentProcessor.java
├── ParkingLot.java
└── Main.java
```
