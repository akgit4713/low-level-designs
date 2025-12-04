# Traffic Signal Control System - Low-Level Design

## Overview

A comprehensive traffic signal control system designed to manage traffic flow at intersections with multiple roads. The system supports configurable signal durations, adaptive timing based on traffic conditions, and emergency vehicle handling.

## Requirements Covered

1. ✅ Control traffic flow at an intersection with multiple roads
2. ✅ Support different signal types (RED, YELLOW, GREEN)
3. ✅ Configurable and adjustable signal durations based on traffic conditions
4. ✅ Smooth transitions between signals
5. ✅ Emergency vehicle detection and handling
6. ✅ Scalable and extensible architecture

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        TRAFFIC SIGNAL CONTROL SYSTEM                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌───────────────────┐              ┌───────────────────────────────────┐   │
│  │   Intersection    │──────────────│      SignalController             │   │
│  │  (Manages Roads)  │              │  (Orchestrates signal timing)     │   │
│  └───────────────────┘              └───────────────────────────────────┘   │
│           │                                       │                          │
│           ▼                                       ▼                          │
│  ┌───────────────────┐              ┌───────────────────────────────────┐   │
│  │       Road        │              │      TimingStrategy <<I>>         │   │
│  │  (Has a signal)   │              │  (Normal/RushHour/Night/Adaptive) │   │
│  └───────────────────┘              └───────────────────────────────────┘   │
│           │                                                                  │
│           ▼                                                                  │
│  ┌───────────────────┐              ┌───────────────────────────────────┐   │
│  │   TrafficSignal   │◄─────────────│      EmergencyHandler             │   │
│  │  (State machine)  │              │  (Handles emergency overrides)    │   │
│  └───────────────────┘              └───────────────────────────────────┘   │
│           │                                       │                          │
│           ▼                                       ▼                          │
│  ┌───────────────────┐              ┌───────────────────────────────────┐   │
│  │ SignalState <<I>> │              │    SignalCommand <<I>>            │   │
│  │ (Red/Yellow/Green)│              │  (Emergency/Manual Override)      │   │
│  └───────────────────┘              └───────────────────────────────────┘   │
│                                                                              │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                          OBSERVER PATTERN                              │  │
│  │  SignalObserver ──► DisplayObserver, LoggingObserver                  │  │
│  │  EmergencyObserver ──► DisplayObserver, LoggingObserver               │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. State Pattern
**Purpose**: Encapsulate signal color behavior and manage state transitions.

```
SignalState <<interface>>
    ├── RedState      (Stop - transitions to Green)
    ├── YellowState   (Caution - transitions to Red)
    └── GreenState    (Go - transitions to Yellow)
```

**Benefits**:
- Each state knows its valid transitions
- State-specific behavior is encapsulated
- Easy to add new states (e.g., flashing modes)

### 2. Strategy Pattern
**Purpose**: Allow interchangeable timing algorithms.

```
TimingStrategy <<interface>>
    ├── NormalTimingStrategy      (Standard timing)
    ├── RushHourTimingStrategy    (Extended green periods)
    ├── NightModeTimingStrategy   (Shorter cycles)
    ├── EmergencyTimingStrategy   (Minimal durations)
    └── AdaptiveTimingStrategy    (Real-time adjustment)
```

**Benefits**:
- Timing logic is decoupled from controller
- Easy to add new timing modes
- Runtime strategy switching

### 3. Observer Pattern
**Purpose**: Decouple signal/emergency events from handlers.

```
SignalObserver <<interface>>
    └── onSignalChange(), onCycleComplete()

EmergencyObserver <<interface>>
    └── onEmergencyDetected(), onEmergencyCleared(), etc.

Concrete Observers:
    ├── DisplayObserver   (Console/UI display)
    └── LoggingObserver   (Audit logging)
```

**Benefits**:
- Multiple observers can react to events
- Easy to add new notification types
- Loose coupling between components

### 4. Command Pattern
**Purpose**: Encapsulate override operations with undo capability.

```
SignalCommand <<interface>>
    ├── EmergencyOverrideCommand  (Sets emergency green corridor)
    └── ManualOverrideCommand     (Manual signal control)

CommandInvoker
    └── Maintains command history for undo
```

**Benefits**:
- Operations are undoable
- Command history for auditing
- Decouples invoker from receiver

### 5. Factory Pattern
**Purpose**: Centralize object creation.

```
IntersectionFactory
    └── createFourWayIntersection(), createTJunction(), builder()

TrafficSystemFactory
    └── createDefaultSystem(), createFullyConfiguredSystem()
```

**Benefits**:
- Encapsulates complex object creation
- Builder pattern for custom configurations
- Consistent object initialization

---

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
| Class | Single Responsibility |
|-------|----------------------|
| `TrafficSignal` | Manages signal state and transitions |
| `SignalController` | Orchestrates signal timing cycles |
| `EmergencyHandler` | Handles emergency vehicle situations |
| `TimingStrategy` | Provides duration calculations |
| `SignalObserver` | Handles notification delivery |

### Open/Closed Principle (OCP)
- **New signal states**: Add new `SignalState` implementations (e.g., `FlashingRedState`)
- **New timing modes**: Add new `TimingStrategy` implementations
- **New observers**: Add new `SignalObserver`/`EmergencyObserver` implementations
- No modification to existing classes required

### Liskov Substitution Principle (LSP)
- All `TimingStrategy` implementations are interchangeable
- All `SignalState` implementations follow the same contract
- All observers implement consistent interfaces

### Interface Segregation Principle (ISP)
- `SignalObserver`: Only signal change methods
- `EmergencyObserver`: Only emergency-related methods
- Clients implement only what they need

### Dependency Inversion Principle (DIP)
- `SignalController` depends on `TimingStrategy` interface, not concrete implementations
- High-level modules depend on abstractions
- Dependencies are injected via constructors

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                 ENUMS                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│  SignalColor: RED, YELLOW, GREEN                                            │
│  Direction: NORTH, SOUTH, EAST, WEST                                        │
│  EmergencyType: AMBULANCE, FIRE_TRUCK, POLICE, VIP_CONVOY                   │
│  TrafficDensity: LOW, NORMAL, HIGH, VERY_HIGH                               │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                 MODELS                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐  │
│  │  Intersection   │    │      Road       │    │    TrafficSignal        │  │
│  ├─────────────────┤    ├─────────────────┤    ├─────────────────────────┤  │
│  │ - id: String    │    │ - id: String    │    │ - id: String            │  │
│  │ - name: String  │◄───│ - name: String  │───►│ - currentState          │  │
│  │ - roads: Map    │    │ - direction     │    │ - currentDuration       │  │
│  │ - emergencies   │    │ - signal        │    │ - isEmergencyOverride   │  │
│  └─────────────────┘    │ - density       │    │ - listeners             │  │
│                         └─────────────────┘    └─────────────────────────┘  │
│                                                                              │
│  ┌─────────────────────────────┐                                            │
│  │     EmergencyVehicle        │                                            │
│  ├─────────────────────────────┤                                            │
│  │ - id: String                │                                            │
│  │ - type: EmergencyType       │                                            │
│  │ - approachingFrom: Direction│                                            │
│  │ - isCleared: boolean        │                                            │
│  └─────────────────────────────┘                                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              STATES                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│        <<interface>>                                                         │
│  ┌─────────────────────────┐                                                │
│  │      SignalState        │                                                │
│  ├─────────────────────────┤                                                │
│  │ + getColor()            │                                                │
│  │ + onEnter()             │                                                │
│  │ + onExit()              │                                                │
│  │ + getNextState()        │                                                │
│  │ + canTransitionTo()     │                                                │
│  │ + getDefaultDuration()  │                                                │
│  └─────────────────────────┘                                                │
│              △                                                               │
│              │                                                               │
│    ┌─────────┼─────────┐                                                    │
│    │         │         │                                                    │
│ ┌──────┐ ┌────────┐ ┌───────┐                                               │
│ │ Red  │ │ Yellow │ │ Green │                                               │
│ │State │ │ State  │ │ State │                                               │
│ └──────┘ └────────┘ └───────┘                                               │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            STRATEGIES                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│        <<interface>>                                                         │
│  ┌─────────────────────────┐                                                │
│  │    TimingStrategy       │                                                │
│  ├─────────────────────────┤                                                │
│  │ + getDuration(color)    │                                                │
│  │ + getAdjustedDuration() │                                                │
│  │ + getStrategyName()     │                                                │
│  │ + getDescription()      │                                                │
│  └─────────────────────────┘                                                │
│              △                                                               │
│              │                                                               │
│    ┌─────────┴──────────────────────────────────┐                           │
│    │              │              │              │                           │
│ ┌────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐                      │
│ │ Normal │  │ RushHour │  │NightMode │  │  Adaptive  │                      │
│ └────────┘  └──────────┘  └──────────┘  └────────────┘                      │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                             SERVICES                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                       SignalController                                   ││
│  ├─────────────────────────────────────────────────────────────────────────┤│
│  │ - intersection: Intersection                                            ││
│  │ - timingStrategy: TimingStrategy                                        ││
│  │ - observers: List<SignalObserver>                                       ││
│  │ + start(), stop(), pause(), resume()                                    ││
│  │ + setTimingStrategy(strategy)                                           ││
│  │ + addObserver(observer)                                                 ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                       EmergencyHandler                                   ││
│  ├─────────────────────────────────────────────────────────────────────────┤│
│  │ - intersection: Intersection                                            ││
│  │ - signalController: SignalController                                    ││
│  │ - commandInvoker: CommandInvoker                                        ││
│  │ - emergencyQueue: PriorityQueue                                         ││
│  │ + detectEmergency(type, direction)                                      ││
│  │ + clearEmergency(vehicle)                                               ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                       TrafficMonitor                                     ││
│  ├─────────────────────────────────────────────────────────────────────────┤│
│  │ + updateStrategyByTime()                                                ││
│  │ + enableAdaptiveMode()                                                  ││
│  │ + updateTrafficDensity(road, count)                                     ││
│  │ + getOverallDensity()                                                   ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Sequence Diagrams

### Normal Signal Cycle

```
┌──────────┐    ┌──────────────────┐    ┌───────────────┐    ┌──────────────┐
│  Client  │    │ SignalController │    │ TrafficSignal │    │SignalObserver│
└────┬─────┘    └────────┬─────────┘    └───────┬───────┘    └──────┬───────┘
     │                   │                      │                    │
     │  start()          │                      │                    │
     │──────────────────►│                      │                    │
     │                   │                      │                    │
     │                   │  forceTransitionTo   │                    │
     │                   │  (GreenState)        │                    │
     │                   │─────────────────────►│                    │
     │                   │                      │                    │
     │                   │                      │  onSignalChange    │
     │                   │──────────────────────┼───────────────────►│
     │                   │                      │                    │
     │                   │ [after green duration]                    │
     │                   │  forceTransitionTo   │                    │
     │                   │  (YellowState)       │                    │
     │                   │─────────────────────►│                    │
     │                   │                      │                    │
     │                   │ [after yellow duration]                   │
     │                   │  forceTransitionTo   │                    │
     │                   │  (RedState)          │                    │
     │                   │─────────────────────►│                    │
     │                   │                      │                    │
     │                   │ onCycleComplete()    │                    │
     │                   │──────────────────────┼───────────────────►│
     │                   │                      │                    │
```

### Emergency Override

```
┌──────────┐    ┌─────────────────┐    ┌──────────────────┐    ┌──────────────┐
│  Sensor  │    │EmergencyHandler │    │ SignalController │    │ CommandInvoker│
└────┬─────┘    └────────┬────────┘    └────────┬─────────┘    └──────┬───────┘
     │                   │                      │                     │
     │ detectEmergency   │                      │                     │
     │ (AMBULANCE, NORTH)│                      │                     │
     │──────────────────►│                      │                     │
     │                   │                      │                     │
     │                   │ pause()              │                     │
     │                   │─────────────────────►│                     │
     │                   │                      │                     │
     │                   │ execute              │                     │
     │                   │ (EmergencyOverride)  │                     │
     │                   │─────────────────────────────────────────►  │
     │                   │                      │                     │
     │                   │                      │  ◄── Saves previous │
     │                   │                      │      states         │
     │                   │                      │                     │
     │                   │                      │  ◄── Sets NORTH to  │
     │                   │                      │      GREEN, others  │
     │                   │                      │      to RED         │
     │                   │                      │                     │
     │ clearEmergency    │                      │                     │
     │──────────────────►│                      │                     │
     │                   │                      │                     │
     │                   │ undo()               │                     │
     │                   │─────────────────────────────────────────►  │
     │                   │                      │                     │
     │                   │                      │  ◄── Restores       │
     │                   │                      │      previous states│
     │                   │                      │                     │
     │                   │ resume()             │                     │
     │                   │─────────────────────►│                     │
     │                   │                      │                     │
```

---

## Extension Points

### 1. Adding New Signal States
```java
public class FlashingYellowState implements SignalState {
    @Override
    public SignalColor getColor() { return SignalColor.YELLOW; }
    
    @Override
    public SignalState getNextState() { return new RedState(); }
    
    // ... other methods
}
```

### 2. Adding New Timing Strategies
```java
public class PedestrianPriorityStrategy implements TimingStrategy {
    // Extended walk signal durations
    @Override
    public int getDuration(SignalColor color) {
        if (color == SignalColor.RED) return 60; // Longer for pedestrians
        return 30;
    }
}
```

### 3. Adding New Observer Types
```java
public class SmsAlertObserver implements EmergencyObserver {
    @Override
    public void onEmergencyDetected(Intersection intersection, EmergencyVehicle vehicle) {
        sendSms("Emergency at " + intersection.getName());
    }
}
```

### 4. Adding New Intersection Types
```java
// Roundabout with 5 exits
Intersection roundabout = IntersectionFactory.builder("Main Roundabout")
    .addRoad("Exit 1", Direction.NORTH)
    .addRoad("Exit 2", Direction.EAST)
    .addRoad("Exit 3", Direction.SOUTH)
    .addRoad("Exit 4", Direction.WEST)
    // Could extend Direction enum for more exits
    .build();
```

---

## File Structure

```
trafficsignal/
├── Main.java                          # Entry point and demo
├── enums/
│   ├── SignalColor.java              # RED, YELLOW, GREEN
│   ├── Direction.java                # NORTH, SOUTH, EAST, WEST
│   ├── EmergencyType.java            # AMBULANCE, FIRE_TRUCK, etc.
│   └── TrafficDensity.java           # LOW, NORMAL, HIGH, VERY_HIGH
├── exceptions/
│   ├── TrafficSignalException.java   # Base exception
│   ├── InvalidStateTransitionException.java
│   └── EmergencyHandlingException.java
├── models/
│   ├── Intersection.java             # Collection of roads
│   ├── Road.java                     # Road with signal
│   ├── TrafficSignal.java            # Signal state machine
│   └── EmergencyVehicle.java         # Emergency vehicle data
├── states/
│   ├── SignalState.java              # State interface
│   ├── RedState.java
│   ├── YellowState.java
│   └── GreenState.java
├── strategies/
│   ├── TimingStrategy.java           # Strategy interface
│   ├── NormalTimingStrategy.java
│   ├── RushHourTimingStrategy.java
│   ├── NightModeTimingStrategy.java
│   ├── EmergencyTimingStrategy.java
│   └── AdaptiveTimingStrategy.java
├── observers/
│   ├── SignalObserver.java           # Signal change observer
│   ├── EmergencyObserver.java        # Emergency event observer
│   ├── DisplayObserver.java          # Console display
│   └── LoggingObserver.java          # Event logging
├── commands/
│   ├── SignalCommand.java            # Command interface
│   ├── EmergencyOverrideCommand.java
│   ├── ManualOverrideCommand.java
│   └── CommandInvoker.java           # Command executor
├── services/
│   ├── SignalController.java         # Main controller
│   ├── EmergencyHandler.java         # Emergency handling
│   └── TrafficMonitor.java           # Traffic monitoring
└── factories/
    ├── IntersectionFactory.java      # Creates intersections
    └── TrafficSystemFactory.java     # Creates full systems
```

---

## Usage Example

```java
// Create a fully configured traffic control system
TrafficControlSystem system = TrafficSystemFactory.createFullyConfiguredSystem("Main & Oak");

// Start the system
system.start();

// Detect an emergency vehicle
system.getEmergencyHandler().detectEmergency(EmergencyType.AMBULANCE, Direction.NORTH);

// Change timing strategy at runtime
system.getController().setTimingStrategy(new RushHourTimingStrategy());

// Clean shutdown
system.shutdown();
```

---

## Design Rationale

1. **State Pattern for Signals**: Natural fit for signal color transitions with well-defined rules (Green→Yellow→Red→Green).

2. **Strategy Pattern for Timing**: Allows runtime switching between timing modes without modifying controller logic.

3. **Observer Pattern for Events**: Decouples event generation from handling, enabling multiple notification channels.

4. **Command Pattern for Overrides**: Provides undo capability essential for emergency situations, plus audit trail.

5. **Factory Pattern for Creation**: Simplifies complex object construction and ensures consistent initialization.

6. **Concurrency with ScheduledExecutorService**: Real-time signal timing using Java's concurrent utilities.

7. **Priority Queue for Emergencies**: Ensures higher-priority emergencies (ambulance > police) are handled first.

This design is production-ready, testable, and can scale to manage multiple intersections in a networked traffic management system.



