#!/usr/bin/env python3
"""
Elevator System - Usage Example

This script demonstrates how to use the elevator system with:
- Multiple elevators
- Different dispatch strategies
- Observers for logging and metrics
- Concurrent request handling
"""

import logging
import time
from threading import Thread
import random

from elevator_system import (
    ElevatorSystemBuilder,
    LookDispatchStrategy,
    NearestElevatorStrategy,
    FCFSDispatchStrategy,
    LoggingObserver,
)
from elevator_system.observers import MetricsObserver


def configure_logging():
    """Set up logging for the example."""
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s - %(levelname)s - %(message)s",
        datefmt="%H:%M:%S",
    )


def basic_example():
    """
    Basic usage example with a single elevator.
    """
    print("\n" + "=" * 60)
    print("BASIC EXAMPLE: Single Elevator")
    print("=" * 60 + "\n")
    
    # Build a simple system
    system = (
        ElevatorSystemBuilder()
        .with_floors(0, 10)
        .with_elevators(1, capacity=8)
        .with_dispatch_strategy(LookDispatchStrategy())
        .with_step_interval(0.1)
        .build()
    )
    
    # Add logging observer
    logger = logging.getLogger("basic_example")
    system.add_observer_to_all(LoggingObserver(logger))
    
    # Make some requests
    print("Making requests: 0->5, 3->7, 2->0")
    system.request_elevator(from_floor=0, to_floor=5)
    system.request_elevator(from_floor=3, to_floor=7)
    system.request_elevator(from_floor=2, to_floor=0)
    
    # Run simulation
    print("\nRunning simulation...")
    for i in range(25):
        system.step()
        status = system.get_system_status()
        elevator = status["elevators"][0]
        print(
            f"  Step {i+1:2d}: Floor {elevator['floor']:2d}, "
            f"Direction: {elevator['direction']:5s}, "
            f"Load: {elevator['load']}/{elevator['capacity']}, "
            f"Pending: {elevator['pending_stops']}"
        )
        time.sleep(0.05)
    
    print("\n✓ Basic example complete")


def multi_elevator_example():
    """
    Example with multiple elevators and LOOK dispatch strategy.
    """
    print("\n" + "=" * 60)
    print("MULTI-ELEVATOR EXAMPLE: 3 Elevators, LOOK Strategy")
    print("=" * 60 + "\n")
    
    # Build a system with 3 elevators
    system = (
        ElevatorSystemBuilder()
        .with_floors(0, 20)
        .with_elevators(3, capacity=10, start_floors=[0, 10, 20])
        .with_dispatch_strategy(LookDispatchStrategy())
        .with_step_interval(0.05)
        .build()
    )
    
    # Add metrics observer
    metrics = MetricsObserver()
    system.add_observer_to_all(metrics)
    
    # Make multiple requests
    print("Making requests from various floors...")
    requests = [
        (0, 15),   # Ground to high floor
        (5, 12),   # Middle to high
        (18, 3),   # High to low
        (10, 0),   # Middle to ground
        (7, 14),   # Middle to high
    ]
    
    for from_floor, to_floor in requests:
        req = system.request_elevator(from_floor=from_floor, to_floor=to_floor)
        print(f"  Request: Floor {from_floor} -> {to_floor}")
    
    # Run simulation
    print("\nRunning simulation...")
    for i in range(50):
        system.step()
        
        if i % 10 == 9:  # Print status every 10 steps
            status = system.get_system_status()
            print(f"\n  --- Step {i+1} ---")
            for elev in status["elevators"]:
                print(
                    f"  Elevator {elev['id']}: Floor {elev['floor']:2d}, "
                    f"Dir: {elev['direction']:5s}, Pending: {elev['pending_stops']}"
                )
    
    # Print metrics
    print("\n📊 Metrics:")
    m = metrics.get_metrics()
    print(f"  Requests completed: {m['total_requests_completed']}")
    print(f"  Total floor visits: {m['total_floor_visits']}")
    print(f"  Average wait time: {m['average_wait_time_seconds']:.2f}s")
    
    print("\n✓ Multi-elevator example complete")


def strategy_comparison_example():
    """
    Compare different dispatch strategies.
    """
    print("\n" + "=" * 60)
    print("STRATEGY COMPARISON: LOOK vs Nearest vs FCFS")
    print("=" * 60 + "\n")
    
    strategies = [
        ("LOOK", LookDispatchStrategy()),
        ("Nearest", NearestElevatorStrategy()),
        ("FCFS", FCFSDispatchStrategy()),
    ]
    
    # Fixed set of requests for fair comparison
    test_requests = [
        (0, 10), (5, 15), (12, 3), (8, 18), (15, 5),
        (2, 8), (10, 2), (18, 10), (7, 12), (4, 0),
    ]
    
    for strategy_name, strategy in strategies:
        # Build fresh system for each strategy
        system = (
            ElevatorSystemBuilder()
            .with_floors(0, 20)
            .with_elevators(3, capacity=10, start_floors=[0, 10, 20])
            .with_dispatch_strategy(strategy)
            .with_step_interval(0.01)
            .build()
        )
        
        metrics = MetricsObserver()
        system.add_observer_to_all(metrics)
        
        # Submit all requests
        for from_floor, to_floor in test_requests:
            system.request_elevator(from_floor=from_floor, to_floor=to_floor)
        
        # Run until all requests complete or max steps
        max_steps = 100
        for _ in range(max_steps):
            system.step()
            if metrics.total_requests_completed >= len(test_requests):
                break
        
        m = metrics.get_metrics()
        print(f"  {strategy_name:8s}: Completed {m['total_requests_completed']:2d}, "
              f"Floor visits: {m['total_floor_visits']:3d}")
    
    print("\n✓ Strategy comparison complete")


def concurrent_requests_example():
    """
    Example demonstrating thread-safe concurrent request handling.
    """
    print("\n" + "=" * 60)
    print("CONCURRENT REQUESTS: Thread Safety Demo")
    print("=" * 60 + "\n")
    
    system = (
        ElevatorSystemBuilder()
        .with_floors(0, 30)
        .with_elevators(4, capacity=12)
        .with_dispatch_strategy(LookDispatchStrategy())
        .with_step_interval(0.01)
        .build()
    )
    
    metrics = MetricsObserver()
    system.add_observer_to_all(metrics)
    
    request_count = 0
    
    def make_random_requests(thread_id: int, count: int):
        nonlocal request_count
        for _ in range(count):
            from_floor = random.randint(0, 29)
            to_floor = random.randint(0, 29)
            while to_floor == from_floor:
                to_floor = random.randint(0, 29)
            
            system.request_elevator(from_floor=from_floor, to_floor=to_floor)
            request_count += 1
            time.sleep(0.01)
    
    # Start the simulation
    print("Starting elevator system...")
    system.start()
    
    # Create multiple threads making requests
    print("Creating 5 threads, each making 10 random requests...")
    threads = [
        Thread(target=make_random_requests, args=(i, 10))
        for i in range(5)
    ]
    
    for t in threads:
        t.start()
    
    for t in threads:
        t.join()
    
    print(f"All threads complete. Total requests: {request_count}")
    
    # Let simulation run a bit more
    print("Running simulation to complete requests...")
    time.sleep(1)
    
    # Stop and report
    system.stop()
    
    status = system.get_system_status()
    m = metrics.get_metrics()
    
    print(f"\n📊 Final Status:")
    print(f"  Requests completed: {m['total_requests_completed']}")
    print(f"  Pending requests: {status['pending_requests']}")
    print(f"  Total floor visits: {m['total_floor_visits']}")
    
    print("\n✓ Concurrent requests example complete")


def interactive_simulation():
    """
    Run an interactive simulation with visual output.
    """
    print("\n" + "=" * 60)
    print("INTERACTIVE SIMULATION")
    print("=" * 60 + "\n")
    
    # Build system
    num_floors = 15
    num_elevators = 3
    
    system = (
        ElevatorSystemBuilder()
        .with_floors(0, num_floors - 1)
        .with_elevators(num_elevators, capacity=6)
        .with_dispatch_strategy(LookDispatchStrategy())
        .with_step_interval(0.2)
        .build()
    )
    
    # Visual representation
    def print_building():
        status = system.get_system_status()
        elevator_floors = {e["id"]: e["floor"] for e in status["elevators"]}
        elevator_dirs = {e["id"]: e["direction"][0] for e in status["elevators"]}  # First char
        
        print("\n  Floor  |", end="")
        for i in range(num_elevators):
            print(f" E{i} |", end="")
        print()
        print("  -------+" + "----+" * num_elevators)
        
        for floor in range(num_floors - 1, -1, -1):
            print(f"    {floor:2d}   |", end="")
            for i in range(num_elevators):
                if elevator_floors[i] == floor:
                    d = elevator_dirs[i]
                    print(f" [{d}]|", end="")
                else:
                    print("    |", end="")
            print()
    
    # Initial state
    print("Initial building state:")
    print_building()
    
    # Add some requests
    print("\nAdding requests: 0->10, 5->12, 14->3")
    system.request_elevator(0, 10)
    system.request_elevator(5, 12)
    system.request_elevator(14, 3)
    
    # Run simulation with visual updates
    print("\nSimulation running (10 steps)...")
    for i in range(10):
        system.step()
        print(f"\n--- Step {i+1} ---")
        print_building()
        time.sleep(0.1)
    
    print("\n✓ Interactive simulation complete")


def main():
    """Run all examples."""
    configure_logging()
    
    print("\n" + "=" * 60)
    print("    ELEVATOR SYSTEM DEMONSTRATION")
    print("=" * 60)
    
    try:
        basic_example()
        multi_elevator_example()
        strategy_comparison_example()
        concurrent_requests_example()
        interactive_simulation()
        
        print("\n" + "=" * 60)
        print("    ALL EXAMPLES COMPLETED SUCCESSFULLY!")
        print("=" * 60 + "\n")
        
    except KeyboardInterrupt:
        print("\n\nInterrupted by user.")
    except Exception as e:
        print(f"\n\nError: {e}")
        raise


if __name__ == "__main__":
    main()



