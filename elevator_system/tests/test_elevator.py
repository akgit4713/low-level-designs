"""Tests for the Elevator class."""

import pytest
from threading import Thread
from unittest.mock import Mock

from elevator_system.elevator import Elevator
from elevator_system.models import Direction, DoorState, Request
from elevator_system.observers import ElevatorObserver


class TestElevatorInitialization:
    """Tests for elevator initialization."""
    
    def test_create_elevator_with_defaults(self):
        elevator = Elevator(elevator_id=0)
        state = elevator.get_state()
        
        assert state.elevator_id == 0
        assert state.current_floor == 0
        assert state.direction == Direction.IDLE
        assert state.capacity == 8
    
    def test_create_elevator_with_custom_config(self):
        elevator = Elevator(
            elevator_id=1,
            min_floor=1,
            max_floor=20,
            capacity=12,
            start_floor=10,
        )
        state = elevator.get_state()
        
        assert state.elevator_id == 1
        assert state.current_floor == 10
        assert state.capacity == 12
    
    def test_reject_invalid_floor_range(self):
        with pytest.raises(ValueError, match="min_floor"):
            Elevator(elevator_id=0, min_floor=10, max_floor=5)
    
    def test_reject_invalid_start_floor(self):
        with pytest.raises(ValueError, match="start_floor"):
            Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=15)
    
    def test_reject_zero_capacity(self):
        with pytest.raises(ValueError, match="capacity"):
            Elevator(elevator_id=0, capacity=0)


class TestElevatorRequestHandling:
    """Tests for request acceptance and validation."""
    
    def test_accept_valid_request(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10)
        request = Request(pickup_floor=0, destination_floor=5)
        
        assert elevator.add_request(request) is True
    
    def test_reject_request_with_invalid_pickup_floor(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10)
        request = Request(pickup_floor=15, destination_floor=5)
        
        assert elevator.can_accept_request(request) is False
        assert elevator.add_request(request) is False
    
    def test_reject_request_with_invalid_destination_floor(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10)
        request = Request(pickup_floor=5, destination_floor=15)
        
        assert elevator.can_accept_request(request) is False
    
    def test_reject_request_exceeding_capacity(self):
        elevator = Elevator(elevator_id=0, capacity=5)
        request = Request(pickup_floor=0, destination_floor=5, passengers=10)
        
        assert elevator.can_accept_request(request) is False


class TestElevatorMovement:
    """Tests for elevator movement and state transitions."""
    
    def test_elevator_moves_up_to_pickup(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=0)
        request = Request(pickup_floor=3, destination_floor=5)
        elevator.add_request(request)
        
        # Initial state
        assert elevator.get_state().current_floor == 0
        assert elevator.get_state().direction == Direction.UP
        
        # Step through floors
        elevator.step()  # 0 -> 1
        assert elevator.get_state().current_floor == 1
        
        elevator.step()  # 1 -> 2
        assert elevator.get_state().current_floor == 2
        
        elevator.step()  # 2 -> 3 (pickup floor)
        assert elevator.get_state().current_floor == 3
    
    def test_elevator_moves_down_to_pickup(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=5)
        request = Request(pickup_floor=2, destination_floor=0)
        elevator.add_request(request)
        
        assert elevator.get_state().direction == Direction.DOWN
        
        elevator.step()  # 5 -> 4
        assert elevator.get_state().current_floor == 4
    
    def test_elevator_becomes_idle_after_completing_requests(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=0)
        request = Request(pickup_floor=0, destination_floor=2)
        elevator.add_request(request)
        
        # Move through stops
        elevator.step()  # 0 (pickup) -> 1
        elevator.step()  # 1 -> 2 (destination)
        elevator.step()  # Process arrival at 2
        
        # Should be idle now
        assert elevator.get_state().direction == Direction.IDLE
    
    def test_elevator_serves_requests_in_direction_order(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=10, start_floor=0)
        
        # Add requests going up
        elevator.add_request(Request(pickup_floor=0, destination_floor=5))
        elevator.add_request(Request(pickup_floor=2, destination_floor=4))
        
        # Elevator should visit floors in order: 0 -> 2 -> 4 -> 5
        visited_floors = []
        for _ in range(10):  # Enough steps to complete
            state = elevator.get_state()
            if state.current_floor not in visited_floors:
                visited_floors.append(state.current_floor)
            elevator.step()
            if state.is_idle:
                break
        
        # Should visit floors in ascending order
        assert visited_floors == sorted(visited_floors)


class TestElevatorObservers:
    """Tests for observer notifications."""
    
    def test_observer_receives_request_accepted(self):
        elevator = Elevator(elevator_id=0)
        observer = Mock(spec=ElevatorObserver)
        elevator.add_observer(observer)
        
        request = Request(pickup_floor=0, destination_floor=5)
        elevator.add_request(request)
        
        observer.on_request_accepted.assert_called_once()
        call_args = observer.on_request_accepted.call_args
        assert call_args[0][1] == request
    
    def test_observer_receives_floor_reached(self):
        elevator = Elevator(elevator_id=0, start_floor=0)
        observer = Mock(spec=ElevatorObserver)
        elevator.add_observer(observer)
        
        request = Request(pickup_floor=0, destination_floor=2)
        elevator.add_request(request)
        elevator.step()
        
        observer.on_floor_reached.assert_called()
    
    def test_observer_error_does_not_crash_elevator(self):
        elevator = Elevator(elevator_id=0)
        
        bad_observer = Mock(spec=ElevatorObserver)
        bad_observer.on_request_accepted.side_effect = RuntimeError("Observer failed")
        elevator.add_observer(bad_observer)
        
        # Should not raise
        request = Request(pickup_floor=0, destination_floor=5)
        elevator.add_request(request)
    
    def test_remove_observer(self):
        elevator = Elevator(elevator_id=0)
        observer = Mock(spec=ElevatorObserver)
        elevator.add_observer(observer)
        elevator.remove_observer(observer)
        
        request = Request(pickup_floor=0, destination_floor=5)
        elevator.add_request(request)
        
        observer.on_request_accepted.assert_not_called()


class TestElevatorThreadSafety:
    """Tests for thread-safe operations."""
    
    def test_concurrent_request_additions(self):
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=20, capacity=100)
        
        def add_requests():
            for i in range(10):
                request = Request(
                    pickup_floor=i % 10,
                    destination_floor=(i % 10) + 5,
                )
                elevator.add_request(request)
        
        threads = [Thread(target=add_requests) for _ in range(5)]
        for t in threads:
            t.start()
        for t in threads:
            t.join()
        
        # Elevator should have accepted all requests without crashing
        state = elevator.get_state()
        assert state.total_pending_stops > 0
    
    def test_concurrent_state_reads(self):
        elevator = Elevator(elevator_id=0)
        request = Request(pickup_floor=0, destination_floor=5)
        elevator.add_request(request)
        
        states = []
        
        def read_state():
            for _ in range(100):
                states.append(elevator.get_state())
        
        threads = [Thread(target=read_state) for _ in range(10)]
        for t in threads:
            t.start()
        for t in threads:
            t.join()
        
        assert len(states) == 1000
        assert all(s.elevator_id == 0 for s in states)



