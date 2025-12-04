package parkinglot;

import parkinglot.exceptions.ParkingException;
import parkinglot.factories.VehicleFactory;
import parkinglot.models.*;
import parkinglot.observers.DisplayBoardObserver;
import parkinglot.observers.NotificationObserver;
import parkinglot.strategies.allocation.NearestEntryStrategy;
import parkinglot.strategies.allocation.SpreadOutStrategy;
import parkinglot.strategies.payment.CardPaymentProcessor;
import parkinglot.strategies.pricing.WeekendPricingStrategy;

/**
 * Demonstration of the Parking Lot System.
 * Showcases all features including:
 * - Multiple levels with different spot types
 * - Entry/Exit gates with displays
 * - Strategy patterns (pricing, allocation, payment)
 * - Observer pattern (notifications, display updates)
 * - Thread-safe concurrent operations
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║           PARKING LOT SYSTEM - COMPREHENSIVE DEMO             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Reset any previous instance (for clean demo)
        ParkingLot.resetInstance();
        
        // Get the singleton parking lot instance
        ParkingLot parkingLot = ParkingLot.getInstance();
        
        // ==================== Setup Levels ====================
        System.out.println("▶ Setting up parking levels...");
        
        // Level 1: 5 motorcycle spots, 10 car spots, 3 truck spots
        parkingLot.addLevel(new Level(1, 5, 10, 3));
        
        // Level 2: 3 motorcycle spots, 15 car spots, 5 truck spots
        parkingLot.addLevel(new Level(2, 3, 15, 5));
        
        // Level 3: 2 motorcycle spots, 20 car spots, 2 truck spots
        parkingLot.addLevel(new Level(3, 2, 20, 2));

        // ==================== Setup Gates ====================
        System.out.println("▶ Setting up entry and exit gates...");
        
        // Entry gates
        EntryGate entryA = new EntryGate("ENTRY-A");
        EntryGate entryB = new EntryGate("ENTRY-B");
        parkingLot.addEntryGate(entryA);
        parkingLot.addEntryGate(entryB);
        
        // Exit gates with different payment processors
        ExitGate exitA = new ExitGate("EXIT-A"); // Cash (default)
        ExitGate exitB = new ExitGate("EXIT-B", new CardPaymentProcessor());
        parkingLot.addExitGate(exitA);
        parkingLot.addExitGate(exitB);

        // ==================== Setup Observers ====================
        System.out.println("▶ Setting up observers for notifications...\n");
        
        parkingLot.addObserver(new DisplayBoardObserver("MAIN-DISPLAY"));
        parkingLot.addObserver(new NotificationObserver());

        // Display initial status
        parkingLot.displayStatus();

        // Show entry display
        System.out.println("--- Entry Gate A Display ---");
        entryA.updateDisplay(parkingLot.getAvailability());

        // ==================== Parking Vehicles ====================
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║         PARKING VEHICLES              ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        // Create vehicles using factory
        Car car1 = VehicleFactory.createCar("CAR-001");
        Car car2 = VehicleFactory.createCar("CAR-002");
        Car car3 = VehicleFactory.createCar("CAR-003");
        
        Motorcycle bike1 = VehicleFactory.createMotorcycle("BIKE-001");
        Motorcycle bike2 = VehicleFactory.createMotorcycle("BIKE-002");
        
        Truck truck1 = VehicleFactory.createTruck("TRUCK-001");
        Truck truck2 = VehicleFactory.createTruck("TRUCK-002");

        // Park vehicles through Entry Gate A
        System.out.println("--- Vehicles entering via Gate A ---\n");
        entryA.showWelcome(car1);
        ParkingTicket ticket1 = parkingLot.parkVehicle(car1, entryA);
        
        entryA.showWelcome(bike1);
        ParkingTicket ticket2 = parkingLot.parkVehicle(bike1, entryA);
        
        entryA.showWelcome(truck1);
        ParkingTicket ticket3 = parkingLot.parkVehicle(truck1, entryA);

        // Park vehicles through Entry Gate B
        System.out.println("\n--- Vehicles entering via Gate B ---\n");
        entryB.showWelcome(car2);
        ParkingTicket ticket4 = parkingLot.parkVehicle(car2, entryB);
        
        entryB.showWelcome(bike2);
        ParkingTicket ticket5 = parkingLot.parkVehicle(bike2, entryB);
        
        entryB.showWelcome(truck2);
        ParkingTicket ticket6 = parkingLot.parkVehicle(truck2, entryB);

        // Park one more car without specifying gate
        System.out.println("\n--- Vehicle entering (no specific gate) ---\n");
        ParkingTicket ticket7 = parkingLot.parkVehicle(car3);

        // Display updated status
        parkingLot.displayStatus();

        // ==================== Ticket Details ====================
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║           TICKET DETAILS              ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        System.out.println(ticket1);
        System.out.println(ticket2);
        System.out.println(ticket3);
        System.out.println(ticket4);

        // ==================== Error Handling Demo ====================
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║          ERROR HANDLING               ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        // Try to park a vehicle that's already parked
        System.out.println("Attempting to park CAR-001 again...");
        try {
            parkingLot.parkVehicle(car1);
        } catch (ParkingException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // ==================== Unparking Vehicles ====================
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║         UNPARKING VEHICLES            ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        // Exit through Gate A (Cash payment)
        System.out.println("--- Vehicles exiting via Gate A (Cash) ---\n");
        double fee1 = parkingLot.unparkVehicle(ticket1, exitA);
        
        // Exit through Gate B (Card payment)
        System.out.println("\n--- Vehicles exiting via Gate B (Card) ---\n");
        double fee2 = parkingLot.unparkVehicle(ticket3, exitB);
        
        // Exit without specific gate
        System.out.println("\n--- Vehicle exiting (no specific gate) ---\n");
        double fee3 = parkingLot.unparkVehicle(ticket2);

        // ==================== Strategy Pattern Demo ====================
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║      STRATEGY PATTERN DEMO            ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        // Change to spread out allocation strategy
        System.out.println("Switching to SpreadOut allocation strategy...");
        parkingLot.setAllocationStrategy(new SpreadOutStrategy());
        
        // Park new vehicles with new strategy
        Car newCar1 = VehicleFactory.createCar("NEW-CAR-1");
        Car newCar2 = VehicleFactory.createCar("NEW-CAR-2");
        
        System.out.println("\nParking with SpreadOut strategy:");
        ParkingTicket newTicket1 = parkingLot.parkVehicle(newCar1, entryA);
        ParkingTicket newTicket2 = parkingLot.parkVehicle(newCar2, entryB);

        // Change to weekend pricing strategy
        System.out.println("\nSwitching to Weekend pricing strategy...");
        parkingLot.setPricingStrategy(new WeekendPricingStrategy());
        
        // Exit with new pricing
        System.out.println("\nExiting with Weekend pricing:");
        parkingLot.unparkVehicle(newTicket1, exitA);

        // Switch to nearest entry allocation strategy
        System.out.println("\nSwitching to NearestEntry allocation strategy...");
        parkingLot.setAllocationStrategy(new NearestEntryStrategy());

        Car newCar3 = VehicleFactory.createCar("NEW-CAR-3");
        System.out.println("\nParking with NearestEntry strategy:");
        parkingLot.parkVehicle(newCar3, entryA);

        // ==================== Final Status ====================
        parkingLot.displayStatus();

        // ==================== Availability Report ====================
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║      AVAILABILITY BY LEVEL & TYPE     ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        parkingLot.getAvailability().forEach((level, availability) -> {
            System.out.println("Level " + level + ": " + availability);
        });

        // Update all entry displays
        System.out.println("\n--- Updating All Entry Displays ---");
        parkingLot.updateAllDisplays();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║            DEMO COMPLETED SUCCESSFULLY                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
