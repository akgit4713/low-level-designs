"""Tests for domain models."""

import pytest
from datetime import datetime

from elevator_system.models import Direction, DoorState, Request, ElevatorState


class TestDirection:
    """Tests for Direction enum."""
    
    def test_opposite_up_returns_down(self):
        assert Direction.UP.opposite() == Direction.DOWN
    
    def test_opposite_down_returns_up(self):
        assert Direction.DOWN.opposite() == Direction.UP
    
    def test_opposite_idle_returns_idle(self):
        assert Direction.IDLE.opposite() == Direction.IDLE


class TestRequest:
    """Tests for Request value object."""
    
    def test_create_valid_request(self):
        request = Request(pickup_floor=0, destination_floor=5)
        assert request.pickup_floor == 0
        assert request.destination_floor == 5
        assert request.passengers == 1
    
    def test_request_direction_up(self):
        request = Request(pickup_floor=2, destination_floor=8)
        assert request.direction == Direction.UP
    
    def test_request_direction_down(self):
        request = Request(pickup_floor=10, destination_floor=3)
        assert request.direction == Direction.DOWN
    
    def test_request_rejects_same_floor(self):
        with pytest.raises(ValueError, match="cannot equal"):
            Request(pickup_floor=5, destination_floor=5)
    
    def test_request_rejects_zero_passengers(self):
        with pytest.raises(ValueError, match="Passengers must be >= 1"):
            Request(pickup_floor=0, destination_floor=5, passengers=0)
    
    def test_request_floors_to_serve(self):
        request = Request(pickup_floor=3, destination_floor=7)
        assert request.floors_to_serve == frozenset({3, 7})
    
    def test_request_equality_by_id(self):
        request1 = Request(pickup_floor=0, destination_floor=5)
        request2 = Request(pickup_floor=0, destination_floor=5)
        
        # Different IDs means different requests
        assert request1 != request2
        assert request1 == request1
    
    def test_request_is_immutable(self):
        request = Request(pickup_floor=0, destination_floor=5)
        with pytest.raises(AttributeError):
            request.pickup_floor = 10  # type: ignore


class TestElevatorState:
    """Tests for ElevatorState."""
    
    def test_is_idle_when_no_pending_stops(self):
        state = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.IDLE,
            door_state=DoorState.CLOSED,
            current_load=0,
            capacity=8,
        )
        assert state.is_idle
    
    def test_is_not_idle_when_moving(self):
        state = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.UP,
            door_state=DoorState.CLOSED,
            current_load=0,
            capacity=8,
        )
        assert not state.is_idle
    
    def test_is_not_idle_when_has_pending_stops(self):
        state = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.IDLE,
            door_state=DoorState.CLOSED,
            current_load=0,
            capacity=8,
            pending_stops_up=frozenset({7, 8}),
        )
        assert not state.is_idle
    
    def test_available_capacity(self):
        state = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.IDLE,
            door_state=DoorState.CLOSED,
            current_load=3,
            capacity=8,
        )
        assert state.available_capacity == 5
    
    def test_distance_to(self):
        state = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.IDLE,
            door_state=DoorState.CLOSED,
            current_load=0,
            capacity=8,
        )
        assert state.distance_to(8) == 3
        assert state.distance_to(2) == 3
        assert state.distance_to(5) == 0
    
    def test_total_pending_stops(self):
        state = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.UP,
            door_state=DoorState.CLOSED,
            current_load=0,
            capacity=8,
            pending_stops_up=frozenset({6, 7, 8}),
            pending_stops_down=frozenset({3, 1}),
        )
        assert state.total_pending_stops == 5
    
    def test_copy_creates_independent_state(self):
        original = ElevatorState(
            elevator_id=0,
            current_floor=5,
            direction=Direction.UP,
            door_state=DoorState.CLOSED,
            current_load=2,
            capacity=8,
        )
        copy = original.copy()
        
        assert copy.elevator_id == original.elevator_id
        assert copy.current_floor == original.current_floor
        assert copy is not original



