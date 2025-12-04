"""
Elevator System - A thread-safe, extensible elevator management system.

This package provides a complete elevator simulation with:
- Multiple elevators with configurable capacity
- Pluggable dispatch strategies (LOOK, Nearest, FCFS)
- Thread-safe concurrent request handling
- Observable elevator events

Example:
    >>> from elevator_system import ElevatorSystemBuilder, LookDispatchStrategy
    >>> system = (ElevatorSystemBuilder()
    ...     .with_floors(0, 10)
    ...     .with_elevators(3, capacity=8)
    ...     .with_dispatch_strategy(LookDispatchStrategy())
    ...     .build())
    >>> system.start()
    >>> request = system.request_elevator(from_floor=0, to_floor=5)
    >>> system.stop()
"""

from elevator_system.models import Direction, Request, ElevatorState
from elevator_system.elevator import Elevator
from elevator_system.strategies import (
    DispatchStrategy,
    LookDispatchStrategy,
    NearestElevatorStrategy,
    FCFSDispatchStrategy,
)
from elevator_system.observers import ElevatorObserver, LoggingObserver
from elevator_system.controller import ElevatorController
from elevator_system.builder import ElevatorSystemBuilder

__all__ = [
    "Direction",
    "Request",
    "ElevatorState",
    "Elevator",
    "DispatchStrategy",
    "LookDispatchStrategy",
    "NearestElevatorStrategy",
    "FCFSDispatchStrategy",
    "ElevatorObserver",
    "LoggingObserver",
    "ElevatorController",
    "ElevatorSystemBuilder",
]

__version__ = "1.0.0"



