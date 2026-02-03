package deliveryservice.services;

import deliveryservice.exceptions.DriverAlreadyExistsException;
import deliveryservice.exceptions.DriverNotFoundException;
import deliveryservice.models.Delivery;
import deliveryservice.models.Driver;
import deliveryservice.strategies.CostCalculationStrategy;
import deliveryservice.strategies.DurationBasedCostStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to manage drivers and their deliveries.
 * Maintains running totals for O(1) getTotalCost() and getCostToBePaid() retrieval.
 * 
 * Uses BigDecimal for precise financial calculations to avoid
 * floating-point rounding errors.
 */
public class DeliveryService {
    
    private final Map<String, Driver> drivers;
    private final CostCalculationStrategy costStrategy;
    private BigDecimal totalCost;
    private BigDecimal totalPaidCost;

    /**
     * Creates a DeliveryService with default duration-based cost strategy.
     */
    public DeliveryService() {
        this(new DurationBasedCostStrategy());
    }

    /**
     * Creates a DeliveryService with a custom cost calculation strategy.
     * 
     * @param costStrategy the strategy to use for calculating delivery costs
     */
    public DeliveryService(CostCalculationStrategy costStrategy) {
        this.drivers = new HashMap<>();
        this.costStrategy = costStrategy;
        this.totalCost = BigDecimal.ZERO;
        this.totalPaidCost = BigDecimal.ZERO;
    }

    /**
     * Adds a new driver to the system.
     * 
     * @param driverId unique identifier for the driver
     * @throws DriverAlreadyExistsException if driver with same ID already exists
     */
    public void addDriver(String driverId) {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be null or empty");
        }
        if (drivers.containsKey(driverId)) {
            throw new DriverAlreadyExistsException(driverId);
        }
        drivers.put(driverId, new Driver(driverId));
    }

    /**
     * Adds a delivery for a specific driver.
     * Cost is calculated and added to running total at insertion time for O(1) getTotalCost().
     * 
     * @param driverId the driver making the delivery
     * @param startTime delivery start time
     * @param endTime delivery end time
     * @return the created Delivery object
     * @throws DriverNotFoundException if driver doesn't exist
     */
    public Delivery addDelivery(String driverId, LocalDateTime startTime, LocalDateTime endTime) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            throw new DriverNotFoundException(driverId);
        }

        // Calculate cost at insertion time (optimization for O(1) getTotalCost)
        BigDecimal cost = costStrategy.calculateCost(startTime, endTime);
        
        Delivery delivery = new Delivery(driverId, startTime, endTime, cost);
        driver.addDelivery(delivery);
        
        // Maintain running total for O(1) retrieval
        totalCost = totalCost.add(cost);
        
        return delivery;
    }

    /**
     * Returns the total cost of all deliveries.
     * Runs in O(1) time since we maintain a running total.
     * 
     * @return total cost of all deliveries
     */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Settles payment for all deliveries with endTime <= upToTime.
     * Marks those deliveries as paid and updates the paid cost total.
     * 
     * Time Complexity: O(n) where n = total number of deliveries
     * 
     * @param upToTime the cutoff time - all deliveries ending at or before this time will be paid
     * @return the amount paid in this settlement
     */
    public BigDecimal payUpToTime(LocalDateTime upToTime) {
        if (upToTime == null) {
            throw new IllegalArgumentException("upToTime cannot be null");
        }

        BigDecimal amountPaid = BigDecimal.ZERO;

        for (Driver driver : drivers.values()) {
            for (Delivery delivery : driver.getDeliveries()) {
                // Pay unpaid deliveries with endTime <= upToTime
                if (!delivery.isPaid() && !delivery.getEndTime().isAfter(upToTime)) {
                    delivery.markAsPaid();
                    amountPaid = amountPaid.add(delivery.getCost());
                }
            }
        }

        // Update running total for O(1) getCostToBePaid()
        totalPaidCost = totalPaidCost.add(amountPaid);

        return amountPaid;
    }

    /**
     * Returns the remaining cost to be paid (unpaid deliveries).
     * Runs in O(1) time since we maintain running totals.
     * 
     * @return cost of unpaid deliveries (totalCost - totalPaidCost)
     */
    public BigDecimal getCostToBePaid() {
        return totalCost.subtract(totalPaidCost);
    }

    /**
     * Returns the total amount that has been paid.
     * Runs in O(1) time.
     * 
     * @return total paid cost
     */
    public BigDecimal getTotalPaidCost() {
        return totalPaidCost;
    }

    /**
     * Gets a driver by ID.
     * 
     * @param driverId the driver ID
     * @return the Driver object
     * @throws DriverNotFoundException if driver doesn't exist
     */
    public Driver getDriver(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            throw new DriverNotFoundException(driverId);
        }
        return driver;
    }

    /**
     * Gets all deliveries for a specific driver.
     * 
     * @param driverId the driver ID
     * @return list of deliveries for the driver
     */
    public List<Delivery> getDeliveriesForDriver(String driverId) {
        return getDriver(driverId).getDeliveries();
    }

    /**
     * Gets total cost for a specific driver.
     * 
     * @param driverId the driver ID
     * @return total cost of deliveries for this driver
     */
    public BigDecimal getTotalCostForDriver(String driverId) {
        return getDriver(driverId).getDeliveries().stream()
                .map(Delivery::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets the total number of drivers.
     * 
     * @return number of drivers
     */
    public int getDriverCount() {
        return drivers.size();
    }

    /**
     * Gets all drivers.
     * 
     * @return unmodifiable map of drivers
     */
    public Map<String, Driver> getAllDrivers() {
        return Collections.unmodifiableMap(drivers);
    }
}
