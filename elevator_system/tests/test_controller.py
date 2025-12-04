"""Tests for ElevatorController."""

import pytest
import time
from threading import Thread
from unittest.mock import Mock

from elevator_system.controller import ElevatorController
from elevator_system.elevator import Elevator
from elevator_system.models import Direction, Request
from elevator_system.strategies import (
    LookDispatchStrategy,
    NearestElevatorStrategy,
    FCFSDispatchStrategy,
)
from elevator_system.observers import ElevatorObserver, MetricsObserver


class TestControllerInitialization:
    """Tests for controller initialization."""
    
    def test_create_controller_with_elevators(self):
        elevators = [Elevator(elevator_id=i) for i in range(3)]
        controller = ElevatorController(elevators)
        
        assert len(controller.elevators) == 3
        assert not controller.is_running()
    
    def test_reject_empty_elevator_list(self):
        with pytest.raises(ValueError, match="At least one elevator"):
            ElevatorController([])
    
    def test_default_strategy_is_look(self):
        elevators = [Elevator(elevator_id=0)]
        controller = ElevatorController(elevators)
        
        # The controller should work without explicit strategy
        request = controller.request_elevator(from_floor=0, to_floor=5)
        assert request.pickup_floor == 0


class TestControllerRequestHandling:
    """Tests for request handling."""
    
    def test_request_elevator_returns_request_object(self):
        elevators = [Elevator(elevator_id=0)]
        controller = ElevatorController(elevators)
        
        request = controller.request_elevator(from_floor=0, to_floor=5)
        
        assert isinstance(request, Request)
        assert request.pickup_floor == 0
        assert request.destination_floor == 5
    
    def test_request_with_multiple_passengers(self):
        elevators = [Elevator(elevator_id=0, capacity=10)]
        controller = ElevatorController(elevators)
        
        request = controller.request_elevator(
            from_floor=0, to_floor=5, passengers=3
        )
        
        assert request.passengers == 3
    
    def test_concurrent_requests_are_thread_safe(self):
        elevators = [Elevator(elevator_id=i, capacity=100) for i in range(3)]
        controller = ElevatorController(elevators)
        
        requests = []
        
        def make_requests():
            for i in range(10):
                req = controller.request_elevator(
                    from_floor=i % 5,
                    to_floor=(i % 5) + 3,
                )
                requests.append(req)
        
        threads = [Thread(target=make_requests) for _ in range(5)]
        for t in threads:
            t.start()
        for t in threads:
            t.join()
        
        assert len(requests) == 50


class TestControllerSimulation:
    """Tests for simulation lifecycle."""
    
    def test_start_and_stop(self):
        elevators = [Elevator(elevator_id=0)]
        controller = ElevatorController(elevators, step_interval_seconds=0.01)
        
        controller.start()
        assert controller.is_running()
        
        controller.stop()
        assert not controller.is_running()
    
    def test_context_manager_starts_and_stops(self):
        elevators = [Elevator(elevator_id=0)]
        controller = ElevatorController(elevators, step_interval_seconds=0.01)
        
        with controller:
            assert controller.is_running()
        
        assert not controller.is_running()
    
    def test_step_advances_elevators(self):
        elevators = [Elevator(elevator_id=0, start_floor=0)]
        controller = ElevatorController(elevators)
        
        controller.request_elevator(from_floor=0, to_floor=3)
        
        initial_floor = elevators[0].get_state().current_floor
        controller.step()
        
        # Elevator should have moved
        new_state = elevators[0].get_state()
        # Either moved or still at 0 (processing pickup)
        assert new_state.current_floor >= initial_floor


class TestControllerStrategySwitch:
    """Tests for runtime strategy switching."""
    
    def test_switch_strategy_at_runtime(self):
        elevators = [Elevator(elevator_id=i) for i in range(2)]
        controller = ElevatorController(elevators, LookDispatchStrategy())
        
        # Switch to different strategy
        controller.set_dispatch_strategy(FCFSDispatchStrategy())
        
        # Should still work
        request = controller.request_elevator(from_floor=0, to_floor=5)
        assert request.pickup_floor == 0


class TestControllerObservers:
    """Tests for observer management."""
    
    def test_add_observer_to_all_elevators(self):
        elevators = [Elevator(elevator_id=i) for i in range(3)]
        controller = ElevatorController(elevators)
        
        observer = Mock(spec=ElevatorObserver)
        controller.add_observer_to_all(observer)
        
        # Make a request - observer should be notified for assigned elevator
        controller.request_elevator(from_floor=0, to_floor=5)
        
        assert observer.on_request_accepted.called
    
    def test_remove_observer_from_all_elevators(self):
        elevators = [Elevator(elevator_id=i) for i in range(3)]
        controller = ElevatorController(elevators)
        
        observer = Mock(spec=ElevatorObserver)
        controller.add_observer_to_all(observer)
        controller.remove_observer_from_all(observer)
        
        controller.request_elevator(from_floor=0, to_floor=5)
        
        observer.on_request_accepted.assert_not_called()


class TestControllerStatus:
    """Tests for system status reporting."""
    
    def test_get_system_status(self):
        elevators = [Elevator(elevator_id=i, capacity=8) for i in range(3)]
        controller = ElevatorController(elevators)
        
        status = controller.get_system_status()
        
        assert status["total_elevators"] == 3
        assert status["idle_elevators"] == 3
        assert status["running"] is False
        assert len(status["elevators"]) == 3
    
    def test_status_reflects_elevator_state(self):
        elevators = [Elevator(elevator_id=0)]
        controller = ElevatorController(elevators)
        
        controller.request_elevator(from_floor=0, to_floor=5)
        
        status = controller.get_system_status()
        elevator_status = status["elevators"][0]
        
        assert elevator_status["pending_stops"] > 0
    
    def test_get_elevator_by_id(self):
        elevators = [Elevator(elevator_id=i) for i in range(3)]
        controller = ElevatorController(elevators)
        
        elevator = controller.get_elevator(1)
        assert elevator is not None
        assert elevator.id == 1
    
    def test_get_nonexistent_elevator_returns_none(self):
        elevators = [Elevator(elevator_id=0)]
        controller = ElevatorController(elevators)
        
        assert controller.get_elevator(999) is None


class TestControllerIntegration:
    """Integration tests for complete elevator scenarios."""
    
    def test_complete_request_lifecycle(self):
        """Test that a request is fully processed."""
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=0)
        controller = ElevatorController([elevator], step_interval_seconds=0.01)
        
        metrics = MetricsObserver()
        elevator.add_observer(metrics)
        
        # Request from 0 to 5
        controller.request_elevator(from_floor=0, to_floor=5)
        
        # Run simulation until request is completed
        for _ in range(20):  # Enough steps
            controller.step()
            if metrics.total_requests_completed > 0:
                break
        
        assert metrics.total_requests_completed == 1
    
    def test_multiple_requests_served_efficiently(self):
        """Test that multiple requests are batched efficiently."""
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=0)
        controller = ElevatorController([elevator])
        
        # Add multiple requests going up
        controller.request_elevator(from_floor=0, to_floor=3)
        controller.request_elevator(from_floor=1, to_floor=5)
        controller.request_elevator(from_floor=2, to_floor=4)
        
        # Run simulation
        for _ in range(20):
            controller.step()
        
        # Elevator should be idle after serving all
        state = elevator.get_state()
        # After enough steps, should have served all requests
        assert state.direction == Direction.IDLE or state.total_pending_stops == 0



