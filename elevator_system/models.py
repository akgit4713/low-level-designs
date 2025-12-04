"""
Core domain models for the Elevator System.

This module contains value objects and enums that represent
the fundamental concepts in the elevator domain.
"""

from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum, auto
from typing import Set, FrozenSet
from uuid import UUID, uuid4


class Direction(Enum):
    """
    Represents the movement direction of an elevator.
    
    IDLE indicates the elevator is stationary with no pending requests.
    """
    UP = auto()
    DOWN = auto()
    IDLE = auto()
    
    def opposite(self) -> "Direction":
        """Return the opposite direction (UP <-> DOWN, IDLE stays IDLE)."""
        if self == Direction.UP:
            return Direction.DOWN
        elif self == Direction.DOWN:
            return Direction.UP
        return Direction.IDLE


class DoorState(Enum):
    """Represents the state of elevator doors."""
    OPEN = auto()
    CLOSED = auto()
    OPENING = auto()
    CLOSING = auto()


@dataclass(frozen=True)
class Request:
    """
    Immutable value object representing an elevator request.
    
    A request captures the user's intent to travel from one floor to another.
    The direction is automatically computed from the pickup and destination floors.
    
    Attributes:
        id: Unique identifier for tracking
        pickup_floor: Floor where user is waiting
        destination_floor: Floor where user wants to go
        timestamp: When the request was created
        passengers: Number of passengers in this request (default 1)
    """
    pickup_floor: int
    destination_floor: int
    id: UUID = field(default_factory=uuid4)
    timestamp: datetime = field(default_factory=datetime.now)
    passengers: int = 1
    
    def __post_init__(self):
        if self.pickup_floor == self.destination_floor:
            raise ValueError(
                f"Pickup floor ({self.pickup_floor}) cannot equal "
                f"destination floor ({self.destination_floor})"
            )
        if self.passengers < 1:
            raise ValueError(f"Passengers must be >= 1, got {self.passengers}")
    
    @property
    def direction(self) -> Direction:
        """Compute the direction of travel."""
        if self.destination_floor > self.pickup_floor:
            return Direction.UP
        return Direction.DOWN
    
    @property
    def floors_to_serve(self) -> FrozenSet[int]:
        """Return the set of floors this request needs the elevator to stop at."""
        return frozenset({self.pickup_floor, self.destination_floor})
    
    def __hash__(self) -> int:
        return hash(self.id)
    
    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Request):
            return NotImplemented
        return self.id == other.id


@dataclass
class ElevatorState:
    """
    Represents the current state of an elevator.
    
    This is a mutable snapshot that gets updated as the elevator moves.
    Used for reporting status and making dispatch decisions.
    """
    elevator_id: int
    current_floor: int
    direction: Direction
    door_state: DoorState
    current_load: int  # Number of passengers currently inside
    capacity: int
    pending_stops_up: FrozenSet[int] = field(default_factory=frozenset)
    pending_stops_down: FrozenSet[int] = field(default_factory=frozenset)
    
    @property
    def is_idle(self) -> bool:
        """Check if elevator has no pending work."""
        return (
            self.direction == Direction.IDLE 
            and len(self.pending_stops_up) == 0 
            and len(self.pending_stops_down) == 0
        )
    
    @property
    def available_capacity(self) -> int:
        """Return remaining capacity for new passengers."""
        return self.capacity - self.current_load
    
    @property
    def total_pending_stops(self) -> int:
        """Total number of floors the elevator needs to visit."""
        return len(self.pending_stops_up) + len(self.pending_stops_down)
    
    def distance_to(self, floor: int) -> int:
        """Calculate absolute distance to a floor."""
        return abs(self.current_floor - floor)
    
    def copy(self) -> "ElevatorState":
        """Create a copy of this state."""
        return ElevatorState(
            elevator_id=self.elevator_id,
            current_floor=self.current_floor,
            direction=self.direction,
            door_state=self.door_state,
            current_load=self.current_load,
            capacity=self.capacity,
            pending_stops_up=self.pending_stops_up,
            pending_stops_down=self.pending_stops_down,
        )



