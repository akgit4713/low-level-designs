# Delivery Service - Low Level Design

## Problem Statement

Implement a service to compute the total cost of all deliveries. The service should expose three methods:
- `addDriver(driverId)` - Add a driver to the system
- `addDelivery(driverId, startTime, endTime)` - Add a delivery for a driver
- `getTotalCost()` - Get the total cost of all deliveries (optimized O(1))

## Design Decisions

### 1. BigDecimal for Financial Calculations

**Why not `double`?** Floating-point types have precision issues that are unacceptable for financial calculations:

```java
double result = 0.1 + 0.2;  // Returns 0.30000000000000004, NOT 0.3!
```

**Solution:** Use `BigDecimal` with String constructor:

```java
BigDecimal bd1 = new BigDecimal("0.1");
BigDecimal bd2 = new BigDecimal("0.2");
BigDecimal result = bd1.add(bd2);  // Returns exactly 0.3
```

**Key practices:**
- Always use String constructor: `new BigDecimal("2.50")` NOT `new BigDecimal(2.50)`
- Define consistent scale (decimal places) and rounding mode
- Use `compareTo()` for comparisons, not `equals()` (scale-sensitive)

### 2. O(1) getTotalCost() Optimization

Instead of iterating through all deliveries each time `getTotalCost()` is called, we maintain a **running total** that gets updated when each delivery is added.

```java
// In addDelivery()
double cost = costStrategy.calculateCost(startTime, endTime);
totalCost += cost;  // O(1) update

// getTotalCost() - O(1) retrieval
public double getTotalCost() {
    return totalCost;
}
```

**Trade-off**: Slightly more work during insertion (negligible), but O(1) read performance.

### 2. Strategy Pattern for Cost Calculation

The cost calculation is abstracted behind a `CostCalculationStrategy` interface, allowing:
- Easy extension with new pricing models
- Runtime strategy switching
- Open/Closed Principle compliance

**Current Implementations:**
- `DurationBasedCostStrategy` - Cost based on time duration
- `DistanceBasedCostStrategy` - Cost based on estimated distance

**Future Extensions:**
- Peak hour pricing
- Zone-based pricing
- Weight-based pricing
- Surge pricing

### 3. Class Structure

```
deliveryservice/
├── models/
│   ├── Driver.java          # Driver entity with list of deliveries
│   └── Delivery.java        # Delivery entity with calculated cost
├── strategies/
│   ├── CostCalculationStrategy.java    # Strategy interface
│   ├── DurationBasedCostStrategy.java  # Time-based cost
│   └── DistanceBasedCostStrategy.java  # Distance-based cost
├── exceptions/
│   ├── DriverNotFoundException.java
│   └── DriverAlreadyExistsException.java
├── services/
│   └── DeliveryService.java  # Main service with optimized getTotalCost()
└── Main.java                 # Demo
```

## Time Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| `addDriver(driverId)` | O(1) | O(1) |
| `addDelivery(driverId, startTime, endTime)` | O(1) | O(1) |
| `getTotalCost()` | **O(1)** | O(1) |
| `getCostToBePaid()` | **O(1)** | O(1) |
| `payUpToTime(upToTime)` | O(n) | O(1) |
| `getTotalCostForDriver(driverId)` | O(d) | O(1) |

Where `n` = total number of deliveries, `d` = deliveries for a specific driver.

## API Usage

```java
// Create service with default duration-based strategy
DeliveryService service = new DeliveryService();

// Or with custom strategy
DeliveryService service = new DeliveryService(new DurationBasedCostStrategy("2.50"));

// Add drivers
service.addDriver("D001");
service.addDriver("D002");

// Add deliveries
LocalDateTime now = LocalDateTime.now();
service.addDelivery("D001", now, now.plusMinutes(30));           // ends at now+30min
service.addDelivery("D002", now.plusHours(1), now.plusHours(2)); // ends at now+2hours

// Get total cost - O(1)
BigDecimal total = service.getTotalCost();  // 90.00 (30 + 60)

// Get unpaid cost - O(1)
BigDecimal unpaid = service.getCostToBePaid();  // 90.00 (nothing paid yet)

// Pay deliveries ending by a certain time
LocalDateTime cutoff = now.plusMinutes(45);
BigDecimal paid = service.payUpToTime(cutoff);  // Pays 30.00 (first delivery)

// Check remaining - O(1)
BigDecimal remaining = service.getCostToBePaid();  // 60.00
```

## Payment Settlement

The service tracks payment status for each delivery:

```java
// Timeline: Delivery 1 ends at 10:00, Delivery 2 ends at 12:00

service.getCostToBePaid();           // 100.00 (both unpaid)

service.payUpToTime(time_11_00);     // Returns 40.00 (pays Delivery 1)
service.getCostToBePaid();           // 60.00 (Delivery 2 still unpaid)

service.payUpToTime(time_13_00);     // Returns 60.00 (pays Delivery 2)
service.getCostToBePaid();           // 0.00 (all paid)
```

**Key points:**
- `payUpToTime(upToTime)` pays all unpaid deliveries with `endTime <= upToTime`
- Already-paid deliveries are skipped (idempotent for same cutoff time)
- `getCostToBePaid()` is O(1) using running totals: `totalCost - totalPaidCost`

## Extending with New Cost Strategies

```java
// Example: Peak hour pricing
public class PeakHourCostStrategy implements CostCalculationStrategy {
    private final double normalRate;
    private final double peakRate;
    private final LocalTime peakStart;
    private final LocalTime peakEnd;

    @Override
    public double calculateCost(LocalDateTime startTime, LocalDateTime endTime) {
        // Calculate with different rates for peak/off-peak hours
        // ...
    }
}

// Usage
DeliveryService service = new DeliveryService(
    new PeakHourCostStrategy(1.0, 2.0, LocalTime.of(17, 0), LocalTime.of(20, 0))
);
```

## Key Interview Points

1. **Why maintain running total?**
   - `getTotalCost()` is likely called frequently
   - Avoids O(n) iteration on every call
   - Trade-off: minimal extra work during insertion

2. **Why Strategy Pattern?**
   - Open for extension, closed for modification
   - Easy to add new pricing models without changing existing code
   - Supports runtime strategy switching

3. **Thread Safety Consideration** (if asked):
   - Current implementation is not thread-safe
   - For concurrent access, consider:
     - `synchronized` methods
     - `AtomicDouble` for totalCost
     - `ConcurrentHashMap` for drivers
     - Read-write locks for better read performance

## Design Patterns Used

1. **Strategy Pattern** - For pluggable cost calculation algorithms
2. **Repository Pattern** (implicit) - Drivers stored in HashMap
3. **Immutable Collections** - `getDeliveries()` returns unmodifiable list
