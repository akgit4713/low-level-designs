# Elevator System

A thread-safe, extensible elevator management system implemented in Python.

## Features

- **Multiple Elevators**: Manage any number of elevators serving multiple floors
- **Capacity Management**: Each elevator respects passenger capacity limits
- **Pluggable Dispatch Strategies**: LOOK, Nearest, FCFS, or custom algorithms
- **Thread-Safe**: Concurrent request handling with proper synchronization
- **Observable**: Event-driven architecture with observer pattern
- **Fluent Builder**: Easy configuration with builder pattern

## Installation

```bash
cd elevator_system
pip install -r requirements.txt
```

## Quick Start

```python
from elevator_system import (
    ElevatorSystemBuilder,
    LookDispatchStrategy,
    LoggingObserver,
)

# Build the system
system = (
    ElevatorSystemBuilder()
    .with_floors(0, 20)                          # Ground to 20th floor
    .with_elevators(4, capacity=10)              # 4 elevators, 10 passengers each
    .with_dispatch_strategy(LookDispatchStrategy())
    .with_step_interval(0.5)                     # 0.5s per simulation step
    .build()
)

# Add logging
system.add_observer_to_all(LoggingObserver())

# Use as context manager (auto start/stop)
with system:
    # Request elevators
    system.request_elevator(from_floor=0, to_floor=15)
    system.request_elevator(from_floor=5, to_floor=10)
    
    # Check status
    status = system.get_system_status()
    print(f"Active elevators: {status['total_elevators']}")
```

## Architecture

### Core Components

| Component | Responsibility |
|-----------|----------------|
| `Elevator` | Manages single elevator state, movement, internal queue |
| `ElevatorController` | Orchestrates multiple elevators, dispatches requests |
| `DispatchStrategy` | Selects optimal elevator for a request (Strategy pattern) |
| `ElevatorObserver` | Receives elevator events (Observer pattern) |
| `ElevatorSystemBuilder` | Constructs configured systems (Builder pattern) |

### Dispatch Strategies

- **`LookDispatchStrategy`** (default): Optimizes for direction and proximity
- **`NearestElevatorStrategy`**: Selects closest idle elevator
- **`FCFSDispatchStrategy`**: Round-robin distribution
- **`ZonedDispatchStrategy`**: Floor-zone based assignment

### Design Patterns

1. **Strategy Pattern**: Pluggable dispatch algorithms
2. **Observer Pattern**: Decoupled event handling
3. **Builder Pattern**: Fluent system configuration
4. **Command Pattern**: Requests as immutable value objects

## Running Tests

```bash
# Run all tests
pytest elevator_system/tests/ -v

# Run with coverage
pytest elevator_system/tests/ --cov=elevator_system --cov-report=html
```

## Running Examples

```bash
python -m elevator_system.example
```

## Thread Safety

The system is designed for concurrent access:

- All elevator state modifications are protected by `RLock`
- Request dispatch uses thread-safe queues
- Observers are notified safely (errors don't affect elevator operation)

## Extending the System

### Custom Dispatch Strategy

```python
from elevator_system import DispatchStrategy, Elevator, Request

class PriorityDispatchStrategy(DispatchStrategy):
    def select_elevator(
        self,
        request: Request,
        elevators: list[Elevator],
    ) -> Elevator | None:
        # Your custom logic here
        compatible = self._get_compatible_elevators(request, elevators)
        if not compatible:
            return None
        
        # Example: prefer elevators with least load
        return min(compatible, key=lambda e: e.get_state().current_load)
```

### Custom Observer

```python
from elevator_system import ElevatorObserver, ElevatorState, Request

class MetricsPublisher(ElevatorObserver):
    def on_request_completed(self, state: ElevatorState, request: Request) -> None:
        # Publish metrics to your monitoring system
        metrics.increment("elevator.requests.completed", 
                         tags={"elevator_id": state.elevator_id})
```

## License

MIT



