"""
Dispatch strategies for selecting which elevator handles a request.

This module implements the Strategy pattern, allowing different
algorithms for elevator selection to be swapped at runtime.

Available Strategies:
- LookDispatchStrategy: Considers direction and position (recommended)
- NearestElevatorStrategy: Selects closest idle/compatible elevator
- FCFSDispatchStrategy: First-come-first-served (round robin)

Usage:
    controller = ElevatorController(elevators, LookDispatchStrategy())
    controller.set_dispatch_strategy(NearestElevatorStrategy())  # Change at runtime
"""

from abc import ABC, abstractmethod
from typing import List, Optional, TYPE_CHECKING

from elevator_system.models import Direction, Request, ElevatorState

if TYPE_CHECKING:
    from elevator_system.elevator import Elevator


class DispatchStrategy(ABC):
    """
    Abstract base class for elevator dispatch strategies.
    
    Implementations define the algorithm for selecting the best
    elevator to handle a given request.
    
    This follows the Strategy pattern (OCP - Open/Closed Principle):
    New strategies can be added without modifying existing code.
    """
    
    @abstractmethod
    def select_elevator(
        self,
        request: Request,
        elevators: List["Elevator"],
    ) -> Optional["Elevator"]:
        """
        Select the best elevator to handle the given request.
        
        Args:
            request: The user request to be assigned
            elevators: List of available elevators
            
        Returns:
            The selected elevator, or None if no elevator can handle the request
        """
        pass
    
    def _get_compatible_elevators(
        self,
        request: Request,
        elevators: List["Elevator"],
    ) -> List["Elevator"]:
        """Filter elevators that can accept the request."""
        return [e for e in elevators if e.can_accept_request(request)]


class LookDispatchStrategy(DispatchStrategy):
    """
    Dispatch strategy based on the LOOK elevator algorithm.
    
    Prioritizes elevators that:
    1. Are moving toward the request floor in the same direction
    2. Are idle and closest to the request floor
    3. Will pass the request floor on their current trajectory
    
    This minimizes wait time by leveraging existing elevator movement.
    """
    
    def select_elevator(
        self,
        request: Request,
        elevators: List["Elevator"],
    ) -> Optional["Elevator"]:
        compatible = self._get_compatible_elevators(request, elevators)
        if not compatible:
            return None
        
        pickup_floor = request.pickup_floor
        request_direction = request.direction
        
        best_elevator: Optional["Elevator"] = None
        best_score = float("inf")
        
        for elevator in compatible:
            state = elevator.get_state()
            score = self._calculate_score(state, pickup_floor, request_direction)
            
            if score < best_score:
                best_score = score
                best_elevator = elevator
        
        return best_elevator
    
    def _calculate_score(
        self,
        state: ElevatorState,
        pickup_floor: int,
        request_direction: Direction,
    ) -> float:
        """
        Calculate a score for how suitable an elevator is.
        Lower score is better.
        
        Scoring factors:
        - Distance to pickup floor
        - Whether elevator is moving toward pickup floor
        - Whether elevator direction matches request direction
        - Number of pending stops (less is better)
        """
        distance = state.distance_to(pickup_floor)
        
        # Base score is distance
        score = float(distance)
        
        if state.is_idle:
            # Idle elevators are good, small penalty for pending stops
            score += state.total_pending_stops * 0.5
            return score
        
        current = state.current_floor
        direction = state.direction
        
        # Check if elevator will pass this floor on its way
        if direction == Direction.UP:
            if pickup_floor >= current:
                if request_direction == Direction.UP:
                    # Perfect: going up, pickup above, request going up
                    score *= 0.5
                else:
                    # Going up, pickup above, but request going down
                    # Will need to pick up after reversing or on return
                    score *= 1.5
            else:
                # Pickup is below, elevator going up
                # Will need to reverse first
                score *= 2.0
                
        elif direction == Direction.DOWN:
            if pickup_floor <= current:
                if request_direction == Direction.DOWN:
                    # Perfect: going down, pickup below, request going down
                    score *= 0.5
                else:
                    # Going down, pickup below, but request going up
                    score *= 1.5
            else:
                # Pickup is above, elevator going down
                score *= 2.0
        
        # Penalize busy elevators
        score += state.total_pending_stops * 0.3
        
        # Penalize full elevators
        if state.current_load >= state.capacity * 0.8:
            score *= 1.5
        
        return score


class NearestElevatorStrategy(DispatchStrategy):
    """
    Simple strategy that selects the nearest available elevator.
    
    Prioritizes:
    1. Idle elevators (by distance)
    2. Any available elevator (by distance)
    
    This is simpler but less optimal than LookDispatchStrategy
    for high-traffic scenarios.
    """
    
    def select_elevator(
        self,
        request: Request,
        elevators: List["Elevator"],
    ) -> Optional["Elevator"]:
        compatible = self._get_compatible_elevators(request, elevators)
        if not compatible:
            return None
        
        pickup_floor = request.pickup_floor
        
        # First, try to find idle elevators
        idle_elevators = [
            e for e in compatible
            if e.get_state().is_idle
        ]
        
        if idle_elevators:
            return min(
                idle_elevators,
                key=lambda e: e.get_state().distance_to(pickup_floor)
            )
        
        # Otherwise, find the closest elevator
        return min(
            compatible,
            key=lambda e: e.get_state().distance_to(pickup_floor)
        )


class FCFSDispatchStrategy(DispatchStrategy):
    """
    First-Come-First-Served (Round Robin) strategy.
    
    Distributes requests evenly across elevators in round-robin fashion.
    This ensures fair load distribution but may not minimize wait times.
    
    Useful for:
    - Testing and debugging
    - Scenarios requiring even load distribution
    """
    
    def __init__(self):
        self._next_index = 0
    
    def select_elevator(
        self,
        request: Request,
        elevators: List["Elevator"],
    ) -> Optional["Elevator"]:
        compatible = self._get_compatible_elevators(request, elevators)
        if not compatible:
            return None
        
        # Find next compatible elevator in round-robin order
        n = len(elevators)
        for _ in range(n):
            idx = self._next_index % n
            self._next_index = (self._next_index + 1) % n
            
            if elevators[idx] in compatible:
                return elevators[idx]
        
        # Fallback to first compatible
        return compatible[0]


class ZonedDispatchStrategy(DispatchStrategy):
    """
    Strategy that assigns elevators to serve specific floor zones.
    
    Each elevator is assigned a primary zone. Requests within that
    zone are preferentially handled by the zone's elevator.
    
    Useful for:
    - Large buildings with many floors
    - Reducing long-distance travel
    """
    
    def __init__(self, zone_assignments: Optional[dict] = None):
        """
        Initialize with zone assignments.
        
        Args:
            zone_assignments: Dict mapping elevator_id to (min_floor, max_floor) zone
        """
        self._zone_assignments = zone_assignments or {}
        self._fallback = NearestElevatorStrategy()
    
    def set_zone(self, elevator_id: int, min_floor: int, max_floor: int) -> None:
        """Assign a zone to an elevator."""
        self._zone_assignments[elevator_id] = (min_floor, max_floor)
    
    def select_elevator(
        self,
        request: Request,
        elevators: List["Elevator"],
    ) -> Optional["Elevator"]:
        compatible = self._get_compatible_elevators(request, elevators)
        if not compatible:
            return None
        
        pickup_floor = request.pickup_floor
        
        # Find elevators whose zone includes the pickup floor
        zone_elevators = []
        for elevator in compatible:
            zone = self._zone_assignments.get(elevator.id)
            if zone and zone[0] <= pickup_floor <= zone[1]:
                zone_elevators.append(elevator)
        
        if zone_elevators:
            # Select the best one within the zone
            return min(
                zone_elevators,
                key=lambda e: e.get_state().distance_to(pickup_floor)
            )
        
        # Fallback to nearest elevator
        return self._fallback.select_elevator(request, compatible)



