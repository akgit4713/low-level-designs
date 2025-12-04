"""
Elevator Controller - Orchestrates the elevator system.

This module contains the central controller that:
- Manages multiple elevators
- Dispatches requests using configurable strategies
- Runs the simulation loop
- Provides system-wide status and control
"""

from threading import RLock, Thread, Event
from typing import List, Dict, Optional
from queue import Queue
import time

from elevator_system.models import Request, Direction, ElevatorState
from elevator_system.elevator import Elevator
from elevator_system.strategies import DispatchStrategy, LookDispatchStrategy
from elevator_system.observers import ElevatorObserver


class ElevatorController:
    """
    Central controller for the elevator system.
    
    Manages elevator fleet, dispatches requests, and runs the simulation.
    Thread-safe for concurrent request submission.
    
    Usage:
        controller = ElevatorController(elevators, LookDispatchStrategy())
        controller.start()
        controller.request_elevator(from_floor=0, to_floor=5)
        # ... later ...
        controller.stop()
    """
    
    def __init__(
        self,
        elevators: List[Elevator],
        dispatch_strategy: Optional[DispatchStrategy] = None,
        step_interval_seconds: float = 0.5,
    ):
        """
        Initialize the elevator controller.
        
        Args:
            elevators: List of elevators to manage
            dispatch_strategy: Strategy for selecting elevators (default: LookDispatchStrategy)
            step_interval_seconds: Time between simulation steps
        """
        if not elevators:
            raise ValueError("At least one elevator is required")
        
        self._elevators = list(elevators)
        self._dispatch_strategy = dispatch_strategy or LookDispatchStrategy()
        self._step_interval = step_interval_seconds
        
        # Request queue for pending requests that couldn't be assigned
        self._pending_requests: Queue[Request] = Queue()
        
        # Thread safety
        self._lock = RLock()
        self._running = False
        self._stop_event = Event()
        self._simulation_thread: Optional[Thread] = None
    
    @property
    def elevators(self) -> List[Elevator]:
        """Get list of managed elevators."""
        return list(self._elevators)
    
    def set_dispatch_strategy(self, strategy: DispatchStrategy) -> None:
        """
        Change the dispatch strategy at runtime.
        
        Thread-safe operation.
        """
        with self._lock:
            self._dispatch_strategy = strategy
    
    def add_observer_to_all(self, observer: ElevatorObserver) -> None:
        """Add an observer to all elevators."""
        for elevator in self._elevators:
            elevator.add_observer(observer)
    
    def remove_observer_from_all(self, observer: ElevatorObserver) -> None:
        """Remove an observer from all elevators."""
        for elevator in self._elevators:
            elevator.remove_observer(observer)
    
    def request_elevator(
        self,
        from_floor: int,
        to_floor: int,
        passengers: int = 1,
    ) -> Request:
        """
        Request an elevator from one floor to another.
        
        This method is thread-safe and can be called from multiple threads.
        
        Args:
            from_floor: Pickup floor
            to_floor: Destination floor
            passengers: Number of passengers
            
        Returns:
            The created Request object
            
        Raises:
            ValueError: If floors are invalid or equal
        """
        request = Request(
            pickup_floor=from_floor,
            destination_floor=to_floor,
            passengers=passengers,
        )
        
        self._dispatch_request(request)
        return request
    
    def _dispatch_request(self, request: Request) -> bool:
        """
        Dispatch a request to the best available elevator.
        
        Returns True if request was assigned, False if queued.
        """
        with self._lock:
            elevator = self._dispatch_strategy.select_elevator(
                request, self._elevators
            )
            
            if elevator and elevator.add_request(request):
                return True
            
            # No suitable elevator, queue the request
            self._pending_requests.put(request)
            return False
    
    def _process_pending_requests(self) -> None:
        """Try to dispatch any pending requests."""
        with self._lock:
            retry_queue: List[Request] = []
            
            while not self._pending_requests.empty():
                try:
                    request = self._pending_requests.get_nowait()
                    elevator = self._dispatch_strategy.select_elevator(
                        request, self._elevators
                    )
                    
                    if elevator and elevator.add_request(request):
                        continue  # Successfully dispatched
                    
                    retry_queue.append(request)
                except Exception:
                    break
            
            # Re-queue failed requests
            for request in retry_queue:
                self._pending_requests.put(request)
    
    def step(self) -> None:
        """
        Process one simulation step for all elevators.
        
        This advances each elevator by one time unit.
        """
        with self._lock:
            # First, try to dispatch pending requests
            self._process_pending_requests()
            
            # Step each elevator
            for elevator in self._elevators:
                elevator.step()
    
    def start(self) -> None:
        """
        Start the elevator simulation in a background thread.
        
        The simulation runs until stop() is called.
        """
        with self._lock:
            if self._running:
                return
            
            self._running = True
            self._stop_event.clear()
            self._simulation_thread = Thread(
                target=self._simulation_loop,
                daemon=True,
                name="ElevatorSimulation"
            )
            self._simulation_thread.start()
    
    def stop(self, timeout: float = 5.0) -> None:
        """
        Stop the elevator simulation.
        
        Args:
            timeout: Maximum time to wait for simulation to stop
        """
        with self._lock:
            if not self._running:
                return
            
            self._running = False
            self._stop_event.set()
        
        if self._simulation_thread:
            self._simulation_thread.join(timeout=timeout)
    
    def _simulation_loop(self) -> None:
        """Main simulation loop - runs in background thread."""
        while not self._stop_event.is_set():
            self.step()
            time.sleep(self._step_interval)
    
    def is_running(self) -> bool:
        """Check if simulation is running."""
        return self._running
    
    def get_system_status(self) -> Dict:
        """
        Get the current status of all elevators.
        
        Returns:
            Dictionary with system-wide status information
        """
        with self._lock:
            states = [e.get_state() for e in self._elevators]
            
            return {
                "running": self._running,
                "pending_requests": self._pending_requests.qsize(),
                "total_elevators": len(self._elevators),
                "idle_elevators": sum(1 for s in states if s.is_idle),
                "total_load": sum(s.current_load for s in states),
                "total_capacity": sum(s.capacity for s in states),
                "elevators": [
                    {
                        "id": s.elevator_id,
                        "floor": s.current_floor,
                        "direction": s.direction.name,
                        "load": s.current_load,
                        "capacity": s.capacity,
                        "pending_stops": s.total_pending_stops,
                    }
                    for s in states
                ],
            }
    
    def get_elevator(self, elevator_id: int) -> Optional[Elevator]:
        """Get an elevator by ID."""
        for elevator in self._elevators:
            if elevator.id == elevator_id:
                return elevator
        return None
    
    def __enter__(self) -> "ElevatorController":
        """Context manager entry - starts the simulation."""
        self.start()
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb) -> None:
        """Context manager exit - stops the simulation."""
        self.stop()



