"""Tests for dispatch strategies."""

import pytest
from unittest.mock import Mock

from elevator_system.elevator import Elevator
from elevator_system.models import Direction, Request
from elevator_system.strategies import (
    LookDispatchStrategy,
    NearestElevatorStrategy,
    FCFSDispatchStrategy,
    ZonedDispatchStrategy,
)


class TestLookDispatchStrategy:
    """Tests for LOOK-based dispatch strategy."""
    
    def test_selects_idle_elevator_nearest_to_pickup(self):
        strategy = LookDispatchStrategy()
        
        elevator1 = Elevator(elevator_id=0, start_floor=0)
        elevator2 = Elevator(elevator_id=1, start_floor=5)
        elevator3 = Elevator(elevator_id=2, start_floor=10)
        
        request = Request(pickup_floor=6, destination_floor=10)
        selected = strategy.select_elevator(request, [elevator1, elevator2, elevator3])
        
        assert selected is elevator2  # Closest to floor 6
    
    def test_prefers_elevator_moving_toward_pickup_in_same_direction(self):
        strategy = LookDispatchStrategy()
        
        # Elevator going up, at floor 3
        elevator_up = Elevator(elevator_id=0, start_floor=3)
        elevator_up.add_request(Request(pickup_floor=3, destination_floor=8))
        
        # Idle elevator at floor 5
        elevator_idle = Elevator(elevator_id=1, start_floor=5)
        
        # Request going up from floor 4
        request = Request(pickup_floor=4, destination_floor=7)
        selected = strategy.select_elevator(request, [elevator_up, elevator_idle])
        
        # Should prefer the elevator already going up
        assert selected is elevator_up
    
    def test_returns_none_when_no_elevators(self):
        strategy = LookDispatchStrategy()
        request = Request(pickup_floor=0, destination_floor=5)
        
        assert strategy.select_elevator(request, []) is None
    
    def test_returns_none_when_no_compatible_elevators(self):
        strategy = LookDispatchStrategy()
        
        # Elevator only serves floors 0-5
        elevator = Elevator(elevator_id=0, min_floor=0, max_floor=5)
        
        # Request for floors outside range
        request = Request(pickup_floor=10, destination_floor=15)
        
        assert strategy.select_elevator(request, [elevator]) is None


class TestNearestElevatorStrategy:
    """Tests for nearest elevator strategy."""
    
    def test_selects_nearest_idle_elevator(self):
        strategy = NearestElevatorStrategy()
        
        elevator1 = Elevator(elevator_id=0, start_floor=0)
        elevator2 = Elevator(elevator_id=1, start_floor=3)
        
        request = Request(pickup_floor=2, destination_floor=5)
        selected = strategy.select_elevator(request, [elevator1, elevator2])
        
        assert selected is elevator2
    
    def test_prefers_idle_over_busy_even_if_busy_is_closer(self):
        strategy = NearestElevatorStrategy()
        
        # Busy elevator at floor 5
        busy_elevator = Elevator(elevator_id=0, start_floor=5)
        busy_elevator.add_request(Request(pickup_floor=5, destination_floor=10))
        
        # Idle elevator at floor 0
        idle_elevator = Elevator(elevator_id=1, start_floor=0)
        
        request = Request(pickup_floor=4, destination_floor=8)
        selected = strategy.select_elevator(request, [busy_elevator, idle_elevator])
        
        # Should prefer idle elevator
        assert selected is idle_elevator


class TestFCFSDispatchStrategy:
    """Tests for round-robin dispatch strategy."""
    
    def test_distributes_requests_round_robin(self):
        strategy = FCFSDispatchStrategy()
        
        elevator1 = Elevator(elevator_id=0, capacity=100)
        elevator2 = Elevator(elevator_id=1, capacity=100)
        elevator3 = Elevator(elevator_id=2, capacity=100)
        elevators = [elevator1, elevator2, elevator3]
        
        selections = []
        for i in range(6):
            request = Request(pickup_floor=0, destination_floor=5)
            selected = strategy.select_elevator(request, elevators)
            selections.append(selected.id)
        
        # Should cycle through elevators
        assert selections == [0, 1, 2, 0, 1, 2]
    
    def test_skips_incompatible_elevators_in_rotation(self):
        strategy = FCFSDispatchStrategy()
        
        elevator1 = Elevator(elevator_id=0, max_floor=3)  # Can't reach floor 5
        elevator2 = Elevator(elevator_id=1, max_floor=10)
        elevator3 = Elevator(elevator_id=2, max_floor=10)
        elevators = [elevator1, elevator2, elevator3]
        
        request = Request(pickup_floor=0, destination_floor=5)
        selected = strategy.select_elevator(request, elevators)
        
        # Should skip elevator1
        assert selected in [elevator2, elevator3]


class TestZonedDispatchStrategy:
    """Tests for zone-based dispatch strategy."""
    
    def test_selects_elevator_in_zone(self):
        strategy = ZonedDispatchStrategy()
        
        elevator1 = Elevator(elevator_id=0, min_floor=0, max_floor=20)
        elevator2 = Elevator(elevator_id=1, min_floor=0, max_floor=20)
        
        # Assign zones
        strategy.set_zone(elevator1.id, min_floor=0, max_floor=10)
        strategy.set_zone(elevator2.id, min_floor=11, max_floor=20)
        
        # Request in elevator1's zone
        request = Request(pickup_floor=5, destination_floor=8)
        selected = strategy.select_elevator(request, [elevator1, elevator2])
        
        assert selected is elevator1
    
    def test_falls_back_to_nearest_when_no_zone_match(self):
        strategy = ZonedDispatchStrategy()
        
        elevator1 = Elevator(elevator_id=0, min_floor=0, max_floor=20, start_floor=0)
        elevator2 = Elevator(elevator_id=1, min_floor=0, max_floor=20, start_floor=15)
        
        # Only assign zone to elevator1
        strategy.set_zone(elevator1.id, min_floor=0, max_floor=5)
        
        # Request outside any zone
        request = Request(pickup_floor=12, destination_floor=18)
        selected = strategy.select_elevator(request, [elevator1, elevator2])
        
        # Should fall back to nearest (elevator2 is closer to floor 12)
        assert selected is elevator2



