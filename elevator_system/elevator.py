"""
Elevator implementation with thread-safe operations.

This module contains the core Elevator class that manages:
- State transitions and movement
- Request scheduling using LOOK algorithm internally
- Observer notifications
- Thread-safe concurrent access
"""

from threading import RLock
from typing import List, Optional, Dict, Set
from sortedcontainers import SortedSet  # type: ignore

from elevator_system.models import (
    Direction,
    DoorState,
    Request,
    ElevatorState,
)
from elevator_system.observers import ElevatorObserver


class Elevator:
    """
    Thread-safe elevator that processes requests using LOOK algorithm.
    
    The elevator maintains two sorted sets of stops:
    - _up_stops: Floors to visit when going UP
    - _down_stops: Floors to visit when going DOWN
    
    It serves all requests in the current direction before reversing,
    which minimizes direction changes and optimizes travel time.
    
    Thread Safety:
        All public methods acquire the internal lock before modifying state.
        The lock is reentrant (RLock) to allow nested calls safely.
    """
    
    def __init__(
        self,
        elevator_id: int,
        min_floor: int = 0,
        max_floor: int = 10,
        capacity: int = 8,
        start_floor: int = 0,
    ):
        """
        Initialize an elevator.
        
        Args:
            elevator_id: Unique identifier for this elevator
            min_floor: Lowest floor this elevator serves
            max_floor: Highest floor this elevator serves
            capacity: Maximum number of passengers
            start_floor: Initial floor position
        """
        if min_floor > max_floor:
            raise ValueError(f"min_floor ({min_floor}) > max_floor ({max_floor})")
        if not (min_floor <= start_floor <= max_floor):
            raise ValueError(
                f"start_floor ({start_floor}) must be between "
                f"min_floor ({min_floor}) and max_floor ({max_floor})"
            )
        if capacity < 1:
            raise ValueError(f"capacity must be >= 1, got {capacity}")
        
        self._id = elevator_id
        self._min_floor = min_floor
        self._max_floor = max_floor
        self._capacity = capacity
        
        # State (protected by lock)
        self._current_floor = start_floor
        self._direction = Direction.IDLE
        self._door_state = DoorState.CLOSED
        self._current_load = 0
        
        # Request tracking - maps floor to requests for that floor
        self._up_stops: SortedSet = SortedSet()  # Floors to visit going up
        self._down_stops: SortedSet = SortedSet()  # Floors to visit going down
        self._active_requests: Dict[int, Set[Request]] = {}  # floor -> requests
        
        # Thread safety
        self._lock = RLock()
        
        # Observers
        self._observers: List[ElevatorObserver] = []
    
    @property
    def id(self) -> int:
        """Get elevator ID."""
        return self._id
    
    @property
    def capacity(self) -> int:
        """Get elevator capacity."""
        return self._capacity
    
    def add_observer(self, observer: ElevatorObserver) -> None:
        """Add an observer to receive elevator events."""
        with self._lock:
            if observer not in self._observers:
                self._observers.append(observer)
    
    def remove_observer(self, observer: ElevatorObserver) -> None:
        """Remove an observer."""
        with self._lock:
            if observer in self._observers:
                self._observers.remove(observer)
    
    def get_state(self) -> ElevatorState:
        """
        Get a snapshot of the current elevator state.
        
        Returns:
            ElevatorState with current floor, direction, load, etc.
        """
        with self._lock:
            return ElevatorState(
                elevator_id=self._id,
                current_floor=self._current_floor,
                direction=self._direction,
                door_state=self._door_state,
                current_load=self._current_load,
                capacity=self._capacity,
                pending_stops_up=frozenset(self._up_stops),
                pending_stops_down=frozenset(self._down_stops),
            )
    
    def can_accept_request(self, request: Request) -> bool:
        """
        Check if this elevator can accept a new request.
        
        Validates floor bounds and capacity constraints.
        """
        with self._lock:
            # Check floor bounds
            if not (self._min_floor <= request.pickup_floor <= self._max_floor):
                return False
            if not (self._min_floor <= request.destination_floor <= self._max_floor):
                return False
            
            # Check if we can fit the passengers
            # Note: We do a simplified check - real systems would be more sophisticated
            if request.passengers > self._capacity:
                return False
            
            return True
    
    def add_request(self, request: Request) -> bool:
        """
        Add a request to this elevator's queue.
        
        The request's pickup and destination floors are added to the
        appropriate direction queues based on the current elevator state.
        
        Args:
            request: The request to add
            
        Returns:
            True if request was accepted, False otherwise
        """
        with self._lock:
            if not self.can_accept_request(request):
                return False
            
            pickup = request.pickup_floor
            destination = request.destination_floor
            request_direction = request.direction
            
            # Track the request
            if pickup not in self._active_requests:
                self._active_requests[pickup] = set()
            self._active_requests[pickup].add(request)
            
            if destination not in self._active_requests:
                self._active_requests[destination] = set()
            self._active_requests[destination].add(request)
            
            # Add stops based on request direction
            if request_direction == Direction.UP:
                self._up_stops.add(pickup)
                self._up_stops.add(destination)
            else:
                self._down_stops.add(pickup)
                self._down_stops.add(destination)
            
            # If idle, set initial direction based on request
            if self._direction == Direction.IDLE:
                if pickup > self._current_floor:
                    self._direction = Direction.UP
                elif pickup < self._current_floor:
                    self._direction = Direction.DOWN
                else:
                    # Pickup is at current floor
                    self._direction = request_direction
            
            self._notify_request_accepted(request)
            return True
    
    def step(self) -> None:
        """
        Process one time unit of elevator operation.
        
        This method:
        1. Opens doors if at a stop floor
        2. Processes passengers (load/unload)
        3. Closes doors
        4. Moves one floor in current direction
        5. Updates direction if needed
        """
        with self._lock:
            if self._direction == Direction.IDLE:
                return
            
            # Check if current floor is a stop
            if self._is_current_floor_a_stop():
                self._process_current_floor()
            
            # Move if we have more stops
            if self._has_pending_stops():
                self._move()
                self._notify_floor_reached(self._current_floor)
                
                # Check new floor for stops
                if self._is_current_floor_a_stop():
                    self._process_current_floor()
            
            # Update direction for next step
            self._update_direction()
    
    def _is_current_floor_a_stop(self) -> bool:
        """Check if elevator needs to stop at current floor."""
        floor = self._current_floor
        if self._direction == Direction.UP:
            return floor in self._up_stops
        elif self._direction == Direction.DOWN:
            return floor in self._down_stops
        return floor in self._up_stops or floor in self._down_stops
    
    def _process_current_floor(self) -> None:
        """Handle arrival at a stop floor - open doors, process passengers."""
        floor = self._current_floor
        
        # Open doors
        self._door_state = DoorState.OPEN
        self._notify_door_opened()
        
        # Remove floor from appropriate stop list
        if self._direction == Direction.UP and floor in self._up_stops:
            self._up_stops.remove(floor)
        elif self._direction == Direction.DOWN and floor in self._down_stops:
            self._down_stops.remove(floor)
        
        # Process completed requests at this floor
        if floor in self._active_requests:
            completed = []
            for request in self._active_requests[floor]:
                if request.destination_floor == floor:
                    # Passenger exiting
                    self._current_load = max(0, self._current_load - request.passengers)
                    completed.append(request)
                    self._notify_request_completed(request)
                elif request.pickup_floor == floor:
                    # Passenger entering
                    if self._current_load + request.passengers <= self._capacity:
                        self._current_load += request.passengers
            
            # Clean up completed requests
            for request in completed:
                self._active_requests[floor].discard(request)
                # Also remove from pickup floor if different
                if request.pickup_floor in self._active_requests:
                    self._active_requests[request.pickup_floor].discard(request)
            
            if not self._active_requests[floor]:
                del self._active_requests[floor]
        
        # Close doors
        self._door_state = DoorState.CLOSED
        self._notify_door_closed()
    
    def _move(self) -> None:
        """Move one floor in current direction."""
        if self._direction == Direction.UP:
            if self._current_floor < self._max_floor:
                self._current_floor += 1
        elif self._direction == Direction.DOWN:
            if self._current_floor > self._min_floor:
                self._current_floor -= 1
    
    def _has_pending_stops(self) -> bool:
        """Check if there are any pending stops."""
        return len(self._up_stops) > 0 or len(self._down_stops) > 0
    
    def _update_direction(self) -> None:
        """Update direction based on pending stops."""
        if self._direction == Direction.UP:
            # Check if there are more stops above
            higher_stops = [f for f in self._up_stops if f > self._current_floor]
            if higher_stops:
                return  # Continue going up
            
            # No more stops above, check if we need to go down
            if self._down_stops:
                self._direction = Direction.DOWN
            elif self._up_stops:
                # There are up stops below us (picked up along the way)
                self._direction = Direction.DOWN
            else:
                self._direction = Direction.IDLE
                
        elif self._direction == Direction.DOWN:
            # Check if there are more stops below
            lower_stops = [f for f in self._down_stops if f < self._current_floor]
            if lower_stops:
                return  # Continue going down
            
            # No more stops below, check if we need to go up
            if self._up_stops:
                self._direction = Direction.UP
            elif self._down_stops:
                # There are down stops above us
                self._direction = Direction.UP
            else:
                self._direction = Direction.IDLE
    
    def _notify_floor_reached(self, floor: int) -> None:
        """Notify observers of floor arrival."""
        state = self.get_state()
        for observer in self._observers:
            try:
                observer.on_floor_reached(state, floor)
            except Exception:
                pass  # Don't let observer errors affect elevator
    
    def _notify_door_opened(self) -> None:
        """Notify observers of door opening."""
        state = self.get_state()
        for observer in self._observers:
            try:
                observer.on_door_opened(state)
            except Exception:
                pass
    
    def _notify_door_closed(self) -> None:
        """Notify observers of door closing."""
        state = self.get_state()
        for observer in self._observers:
            try:
                observer.on_door_closed(state)
            except Exception:
                pass
    
    def _notify_request_accepted(self, request: Request) -> None:
        """Notify observers of request acceptance."""
        state = self.get_state()
        for observer in self._observers:
            try:
                observer.on_request_accepted(state, request)
            except Exception:
                pass
    
    def _notify_request_completed(self, request: Request) -> None:
        """Notify observers of request completion."""
        state = self.get_state()
        for observer in self._observers:
            try:
                observer.on_request_completed(state, request)
            except Exception:
                pass
    
    def __repr__(self) -> str:
        with self._lock:
            return (
                f"Elevator(id={self._id}, floor={self._current_floor}, "
                f"direction={self._direction.name}, load={self._current_load}/{self._capacity})"
            )



