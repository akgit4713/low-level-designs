package parkinglot;

import parkinglot.enums.VehicleType;
import parkinglot.exceptions.ParkingException;
import parkinglot.models.*;
import parkinglot.observers.ParkingObserver;
import parkinglot.strategies.allocation.FirstAvailableStrategy;
import parkinglot.strategies.allocation.SpotAllocationStrategy;
import parkinglot.strategies.pricing.HourlyPricingStrategy;
import parkinglot.strategies.pricing.PricingStrategy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class representing the entire Parking Lot system.
 * Manages multiple levels, entry/exit gates, and handles vehicle parking/unparking operations.
 * Thread-safe implementation for concurrent access from multiple gates.
 * 
 * Uses Strategy Pattern for pricing and spot allocation.
 * Uses Observer Pattern for event notifications.
 */
public class ParkingLot {
    private static volatile ParkingLot instance;
    
    private final List<Level> levels;
    private final List<EntryGate> entryGates;
    private final List<ExitGate> exitGates;
    private final Map<String, ParkingTicket> activeTickets; // licensePlate -> ticket
    private final List<ParkingObserver> observers;
    
    // Strategy patterns for extensibility
    private PricingStrategy pricingStrategy;
    private SpotAllocationStrategy allocationStrategy;

    private ParkingLot() {
        this.levels = new ArrayList<>();
        this.entryGates = new ArrayList<>();
        this.exitGates = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.observers = new ArrayList<>();
        
        // Default strategies
        this.pricingStrategy = new HourlyPricingStrategy();
        this.allocationStrategy = new FirstAvailableStrategy();
    }

    /**
     * Gets the singleton instance of ParkingLot.
     * Uses double-checked locking for thread safety.
     */
    public static ParkingLot getInstance() {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    instance = new ParkingLot();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    // ==================== Configuration Methods ====================

    /**
     * Adds a new level to the parking lot.
     */
    public void addLevel(Level level) {
        levels.add(level);
    }

    /**
     * Adds an entry gate to the parking lot.
     */
    public void addEntryGate(EntryGate gate) {
        entryGates.add(gate);
    }

    /**
     * Adds an exit gate to the parking lot.
     */
    public void addExitGate(ExitGate gate) {
        exitGates.add(gate);
    }

    /**
     * Sets the pricing strategy (Strategy Pattern).
     */
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
        System.out.println("⚙️ Pricing strategy updated: " + strategy.getDescription());
    }

    /**
     * Sets the spot allocation strategy (Strategy Pattern).
     */
    public void setAllocationStrategy(SpotAllocationStrategy strategy) {
        this.allocationStrategy = strategy;
        System.out.println("⚙️ Allocation strategy updated: " + strategy.getDescription());
    }

    /**
     * Adds an observer for parking events (Observer Pattern).
     */
    public void addObserver(ParkingObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer.
     */
    public void removeObserver(ParkingObserver observer) {
        observers.remove(observer);
    }

    // ==================== Core Parking Operations ====================

    /**
     * Parks a vehicle in the parking lot.
     * Uses the configured allocation strategy to find a spot.
     * 
     * @param vehicle The vehicle to park
     * @return ParkingTicket if successful
     * @throws ParkingException if no spot is available or vehicle already parked
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle) {
        return parkVehicle(vehicle, null);
    }

    /**
     * Parks a vehicle through a specific entry gate.
     * 
     * @param vehicle The vehicle to park
     * @param entryGate The entry gate used (can be null)
     * @return ParkingTicket if successful
     * @throws ParkingException if no spot is available or vehicle already parked
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle, EntryGate entryGate) {
        if (vehicle == null) {
            throw new ParkingException("Vehicle cannot be null");
        }

        // Check if vehicle is already parked
        if (activeTickets.containsKey(vehicle.getLicensePlate())) {
            throw new ParkingException("Vehicle " + vehicle.getLicensePlate() + " is already parked");
        }

        // Use allocation strategy to find a spot
        Optional<SpotResult> spotResult = allocationStrategy.findSpot(levels, vehicle);
        
        if (spotResult.isEmpty()) {
            throw new ParkingException("No available spot for " + vehicle.getType() + " vehicle");
        }

        SpotResult result = spotResult.get();
        Level level = result.getLevel();
        ParkingSpot spot = result.getSpot();

        // Park the vehicle
        if (!spot.parkVehicle(vehicle)) {
            throw new ParkingException("Failed to park vehicle in the allocated spot");
        }

        // Create and store ticket
        ParkingTicket ticket = new ParkingTicket(vehicle, spot, level, entryGate);
        activeTickets.put(vehicle.getLicensePlate(), ticket);
        
        System.out.println("✓ Parked " + vehicle + " at Level " + level.getFloorNumber() + 
                           ", Spot " + spot.getSpotNumber());

        // Notify observers
        notifyVehicleParked(ticket);

        return ticket;
    }

    /**
     * Unparks a vehicle using the parking ticket.
     * Uses the configured pricing strategy to calculate fees.
     * 
     * @param ticket The parking ticket
     * @return The fee to be paid
     * @throws ParkingException if ticket is invalid
     */
    public synchronized double unparkVehicle(ParkingTicket ticket) {
        return unparkVehicle(ticket, null);
    }

    /**
     * Unparks a vehicle through a specific exit gate.
     * 
     * @param ticket The parking ticket
     * @param exitGate The exit gate used (can be null)
     * @return The fee paid
     * @throws ParkingException if ticket is invalid
     */
    public synchronized double unparkVehicle(ParkingTicket ticket, ExitGate exitGate) {
        if (ticket == null) {
            throw new ParkingException("Ticket cannot be null");
        }

        Vehicle vehicle = ticket.getVehicle();
        if (!activeTickets.containsKey(vehicle.getLicensePlate())) {
            throw new ParkingException("No active parking found for this ticket");
        }

        // Set exit time
        ticket.setExitTime(LocalDateTime.now());

        // Calculate fee using pricing strategy
        double fee = pricingStrategy.calculateFee(ticket);

        // Process payment if exit gate provided
        if (exitGate != null) {
            PaymentResult paymentResult = exitGate.processPayment(fee, ticket);
            if (!paymentResult.isSuccessful()) {
                throw new ParkingException("Payment failed: " + paymentResult.getMessage());
            }
        }

        // Unpark the vehicle
        Level level = ticket.getLevel();
        if (!level.unparkVehicle(vehicle)) {
            throw new ParkingException("Failed to unpark vehicle");
        }

        activeTickets.remove(vehicle.getLicensePlate());
        
        System.out.println("✓ Unparked " + vehicle + " | Duration: " + 
                           ticket.calculateDuration().toMinutes() + " mins | Fee: $" + 
                           String.format("%.2f", fee));

        // Show goodbye at exit gate
        if (exitGate != null) {
            exitGate.showGoodbye(vehicle);
        }

        // Notify observers
        notifyVehicleUnparked(ticket);

        return fee;
    }

    // ==================== Observer Notifications ====================

    private void notifyVehicleParked(ParkingTicket ticket) {
        for (ParkingObserver observer : observers) {
            observer.onVehicleParked(ticket);
        }
    }

    private void notifyVehicleUnparked(ParkingTicket ticket) {
        for (ParkingObserver observer : observers) {
            observer.onVehicleUnparked(ticket);
        }
    }

    // ==================== Query Methods ====================

    /**
     * Gets availability information for all levels.
     */
    public Map<Integer, Map<VehicleType, Integer>> getAvailability() {
        Map<Integer, Map<VehicleType, Integer>> availability = new LinkedHashMap<>();
        
        for (Level level : levels) {
            Map<VehicleType, Integer> levelAvailability = new EnumMap<>(VehicleType.class);
            for (VehicleType type : VehicleType.values()) {
                levelAvailability.put(type, level.getAvailableSpotCount(type));
            }
            availability.put(level.getFloorNumber(), levelAvailability);
        }
        
        return availability;
    }

    /**
     * Updates all entry gate displays with current availability.
     */
    public void updateAllDisplays() {
        Map<Integer, Map<VehicleType, Integer>> availability = getAvailability();
        for (EntryGate gate : entryGates) {
            gate.updateDisplay(availability);
        }
    }

    /**
     * Displays current parking lot status.
     */
    public void displayStatus() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        PARKING LOT STATUS");
        System.out.println("═══════════════════════════════════════");
        
        for (Level level : levels) {
            System.out.println("\n" + level);
            System.out.println("  Motorcycle spots: " + level.getAvailableSpotCount(VehicleType.MOTORCYCLE) + " available");
            System.out.println("  Car spots: " + level.getAvailableSpotCount(VehicleType.CAR) + " available");
            System.out.println("  Truck spots: " + level.getAvailableSpotCount(VehicleType.TRUCK) + " available");
        }
        
        System.out.println("\nEntry Gates: " + entryGates.size());
        System.out.println("Exit Gates: " + exitGates.size());
        System.out.println("Active Tickets: " + activeTickets.size());
        System.out.println("Pricing: " + pricingStrategy.getDescription());
        System.out.println("Allocation: " + allocationStrategy.getDescription());
        System.out.println("═══════════════════════════════════════\n");
    }

    /**
     * Checks if a specific vehicle is currently parked.
     */
    public boolean isVehicleParked(String licensePlate) {
        return activeTickets.containsKey(licensePlate);
    }

    /**
     * Gets ticket for a parked vehicle.
     */
    public ParkingTicket getTicket(String licensePlate) {
        return activeTickets.get(licensePlate);
    }

    /**
     * Gets all levels in the parking lot.
     */
    public List<Level> getLevels() {
        return new ArrayList<>(levels);
    }

    /**
     * Gets all entry gates.
     */
    public List<EntryGate> getEntryGates() {
        return new ArrayList<>(entryGates);
    }

    /**
     * Gets all exit gates.
     */
    public List<ExitGate> getExitGates() {
        return new ArrayList<>(exitGates);
    }

    /**
     * Gets total capacity of the parking lot.
     */
    public int getTotalCapacity() {
        return levels.stream()
            .mapToInt(Level::getTotalSpots)
            .sum();
    }

    /**
     * Gets total available spots in the parking lot.
     */
    public int getTotalAvailableSpots() {
        return levels.stream()
            .mapToInt(Level::getTotalAvailableSpots)
            .sum();
    }

    /**
     * Gets the current pricing strategy.
     */
    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    /**
     * Gets the current allocation strategy.
     */
    public SpotAllocationStrategy getAllocationStrategy() {
        return allocationStrategy;
    }
}
