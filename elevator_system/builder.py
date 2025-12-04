"""
Builder for constructing elevator systems.

This module implements the Builder pattern for constructing
complex elevator system configurations in a readable, fluent style.

Usage:
    system = (ElevatorSystemBuilder()
        .with_floors(0, 20)
        .with_elevators(4, capacity=10)
        .with_dispatch_strategy(LookDispatchStrategy())
        .with_step_interval(0.5)
        .build())
"""

from typing import List, Optional, Type

from elevator_system.elevator import Elevator
from elevator_system.controller import ElevatorController
from elevator_system.strategies import DispatchStrategy, LookDispatchStrategy
from elevator_system.observers import ElevatorObserver


class ElevatorSystemBuilder:
    """
    Builder for constructing ElevatorController with desired configuration.
    
    Provides a fluent interface for step-by-step system configuration.
    
    Example:
        system = (ElevatorSystemBuilder()
            .with_floors(0, 50)
            .with_elevators(6, capacity=15)
            .with_dispatch_strategy(LookDispatchStrategy())
            .with_observer(LoggingObserver())
            .build())
    """
    
    def __init__(self):
        """Initialize builder with defaults."""
        self._min_floor = 0
        self._max_floor = 10
        self._num_elevators = 1
        self._capacity = 8
        self._start_floors: Optional[List[int]] = None
        self._dispatch_strategy: Optional[DispatchStrategy] = None
        self._step_interval = 0.5
        self._observers: List[ElevatorObserver] = []
    
    def with_floors(self, min_floor: int, max_floor: int) -> "ElevatorSystemBuilder":
        """
        Set the floor range for all elevators.
        
        Args:
            min_floor: Lowest floor (typically 0 or 1)
            max_floor: Highest floor
            
        Returns:
            Self for method chaining
        """
        if min_floor > max_floor:
            raise ValueError(f"min_floor ({min_floor}) cannot be greater than max_floor ({max_floor})")
        self._min_floor = min_floor
        self._max_floor = max_floor
        return self
    
    def with_elevators(
        self,
        count: int,
        capacity: int = 8,
        start_floors: Optional[List[int]] = None,
    ) -> "ElevatorSystemBuilder":
        """
        Configure the number and capacity of elevators.
        
        Args:
            count: Number of elevators
            capacity: Passenger capacity per elevator
            start_floors: Optional list of starting floors for each elevator
            
        Returns:
            Self for method chaining
        """
        if count < 1:
            raise ValueError(f"Count must be >= 1, got {count}")
        if capacity < 1:
            raise ValueError(f"Capacity must be >= 1, got {capacity}")
        if start_floors and len(start_floors) != count:
            raise ValueError(
                f"start_floors length ({len(start_floors)}) must match count ({count})"
            )
        
        self._num_elevators = count
        self._capacity = capacity
        self._start_floors = start_floors
        return self
    
    def with_dispatch_strategy(
        self,
        strategy: DispatchStrategy,
    ) -> "ElevatorSystemBuilder":
        """
        Set the dispatch strategy for elevator selection.
        
        Args:
            strategy: The dispatch strategy instance
            
        Returns:
            Self for method chaining
        """
        self._dispatch_strategy = strategy
        return self
    
    def with_step_interval(self, seconds: float) -> "ElevatorSystemBuilder":
        """
        Set the simulation step interval.
        
        Args:
            seconds: Time between simulation steps
            
        Returns:
            Self for method chaining
        """
        if seconds <= 0:
            raise ValueError(f"Step interval must be > 0, got {seconds}")
        self._step_interval = seconds
        return self
    
    def with_observer(self, observer: ElevatorObserver) -> "ElevatorSystemBuilder":
        """
        Add an observer to all elevators.
        
        Args:
            observer: Observer to add
            
        Returns:
            Self for method chaining
        """
        self._observers.append(observer)
        return self
    
    def build(self) -> ElevatorController:
        """
        Build and return the configured ElevatorController.
        
        Returns:
            Configured ElevatorController ready to use
        """
        # Create elevators
        elevators: List[Elevator] = []
        for i in range(self._num_elevators):
            start_floor = (
                self._start_floors[i]
                if self._start_floors
                else self._min_floor
            )
            
            elevator = Elevator(
                elevator_id=i,
                min_floor=self._min_floor,
                max_floor=self._max_floor,
                capacity=self._capacity,
                start_floor=start_floor,
            )
            
            # Add observers
            for observer in self._observers:
                elevator.add_observer(observer)
            
            elevators.append(elevator)
        
        # Create controller
        controller = ElevatorController(
            elevators=elevators,
            dispatch_strategy=self._dispatch_strategy or LookDispatchStrategy(),
            step_interval_seconds=self._step_interval,
        )
        
        return controller
    
    def __repr__(self) -> str:
        return (
            f"ElevatorSystemBuilder("
            f"floors={self._min_floor}-{self._max_floor}, "
            f"elevators={self._num_elevators}, "
            f"capacity={self._capacity})"
        )



