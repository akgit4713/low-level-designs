"""
Observer interfaces and implementations for elevator events.

This module implements the Observer pattern to decouple elevator
operations from their side effects (logging, metrics, UI updates).

Usage:
    elevator.add_observer(LoggingObserver())
    elevator.add_observer(MetricsObserver(metrics_client))
"""

from abc import ABC, abstractmethod
from datetime import datetime
from typing import List, Optional
import logging

from elevator_system.models import ElevatorState, Request


class ElevatorObserver(ABC):
    """
    Abstract base class for elevator event observers.
    
    Implement this interface to receive notifications about
    elevator state changes and request lifecycle events.
    
    All methods have default no-op implementations, so subclasses
    only need to override the events they care about (ISP).
    """
    
    def on_floor_reached(self, state: ElevatorState, floor: int) -> None:
        """Called when elevator arrives at a new floor."""
        pass
    
    def on_door_opened(self, state: ElevatorState) -> None:
        """Called when elevator doors open."""
        pass
    
    def on_door_closed(self, state: ElevatorState) -> None:
        """Called when elevator doors close."""
        pass
    
    def on_request_accepted(self, state: ElevatorState, request: Request) -> None:
        """Called when elevator accepts a new request."""
        pass
    
    def on_request_completed(self, state: ElevatorState, request: Request) -> None:
        """Called when a request is completed (passenger delivered)."""
        pass
    
    def on_direction_changed(self, state: ElevatorState) -> None:
        """Called when elevator changes direction."""
        pass


class LoggingObserver(ElevatorObserver):
    """
    Observer that logs all elevator events.
    
    Useful for debugging and monitoring elevator operations.
    """
    
    def __init__(self, logger: Optional[logging.Logger] = None):
        self._logger = logger or logging.getLogger(__name__)
    
    def on_floor_reached(self, state: ElevatorState, floor: int) -> None:
        self._logger.info(
            f"[Elevator {state.elevator_id}] Reached floor {floor} "
            f"(direction: {state.direction.name}, load: {state.current_load}/{state.capacity})"
        )
    
    def on_door_opened(self, state: ElevatorState) -> None:
        self._logger.debug(
            f"[Elevator {state.elevator_id}] Doors opened at floor {state.current_floor}"
        )
    
    def on_door_closed(self, state: ElevatorState) -> None:
        self._logger.debug(
            f"[Elevator {state.elevator_id}] Doors closed at floor {state.current_floor}"
        )
    
    def on_request_accepted(self, state: ElevatorState, request: Request) -> None:
        self._logger.info(
            f"[Elevator {state.elevator_id}] Accepted request: "
            f"floor {request.pickup_floor} -> {request.destination_floor} "
            f"({request.passengers} passenger(s))"
        )
    
    def on_request_completed(self, state: ElevatorState, request: Request) -> None:
        wait_time = (datetime.now() - request.timestamp).total_seconds()
        self._logger.info(
            f"[Elevator {state.elevator_id}] Completed request: "
            f"floor {request.pickup_floor} -> {request.destination_floor} "
            f"(wait time: {wait_time:.1f}s)"
        )
    
    def on_direction_changed(self, state: ElevatorState) -> None:
        self._logger.info(
            f"[Elevator {state.elevator_id}] Direction changed to {state.direction.name} "
            f"at floor {state.current_floor}"
        )


class MetricsObserver(ElevatorObserver):
    """
    Observer that collects metrics about elevator operations.
    
    Tracks:
    - Request completion times
    - Floor visits
    - Load statistics
    """
    
    def __init__(self):
        self.total_requests_completed = 0
        self.total_wait_time_seconds = 0.0
        self.floor_visits: List[int] = []
        self.load_samples: List[int] = []
    
    def on_floor_reached(self, state: ElevatorState, floor: int) -> None:
        self.floor_visits.append(floor)
        self.load_samples.append(state.current_load)
    
    def on_request_completed(self, state: ElevatorState, request: Request) -> None:
        self.total_requests_completed += 1
        wait_time = (datetime.now() - request.timestamp).total_seconds()
        self.total_wait_time_seconds += wait_time
    
    @property
    def average_wait_time(self) -> float:
        """Calculate average wait time for completed requests."""
        if self.total_requests_completed == 0:
            return 0.0
        return self.total_wait_time_seconds / self.total_requests_completed
    
    @property
    def average_load(self) -> float:
        """Calculate average elevator load."""
        if not self.load_samples:
            return 0.0
        return sum(self.load_samples) / len(self.load_samples)
    
    def get_metrics(self) -> dict:
        """Return all collected metrics as a dictionary."""
        return {
            "total_requests_completed": self.total_requests_completed,
            "total_wait_time_seconds": self.total_wait_time_seconds,
            "average_wait_time_seconds": self.average_wait_time,
            "total_floor_visits": len(self.floor_visits),
            "average_load": self.average_load,
        }


class CompositeObserver(ElevatorObserver):
    """
    Observer that delegates to multiple child observers.
    
    Useful for combining multiple observers without modifying
    the elevator's observer list.
    """
    
    def __init__(self, observers: Optional[List[ElevatorObserver]] = None):
        self._observers = observers or []
    
    def add(self, observer: ElevatorObserver) -> None:
        """Add a child observer."""
        self._observers.append(observer)
    
    def remove(self, observer: ElevatorObserver) -> None:
        """Remove a child observer."""
        if observer in self._observers:
            self._observers.remove(observer)
    
    def on_floor_reached(self, state: ElevatorState, floor: int) -> None:
        for observer in self._observers:
            observer.on_floor_reached(state, floor)
    
    def on_door_opened(self, state: ElevatorState) -> None:
        for observer in self._observers:
            observer.on_door_opened(state)
    
    def on_door_closed(self, state: ElevatorState) -> None:
        for observer in self._observers:
            observer.on_door_closed(state)
    
    def on_request_accepted(self, state: ElevatorState, request: Request) -> None:
        for observer in self._observers:
            observer.on_request_accepted(state, request)
    
    def on_request_completed(self, state: ElevatorState, request: Request) -> None:
        for observer in self._observers:
            observer.on_request_completed(state, request)
    
    def on_direction_changed(self, state: ElevatorState) -> None:
        for observer in self._observers:
            observer.on_direction_changed(state)



