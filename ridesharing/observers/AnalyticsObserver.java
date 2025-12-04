package ridesharing.observers;

import ridesharing.models.Ride;
import ridesharing.enums.RideStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Observer that collects analytics and metrics for rides.
 */
public class AnalyticsObserver implements RideObserver {
    
    private final AtomicInteger totalRides = new AtomicInteger(0);
    private final AtomicInteger completedRides = new AtomicInteger(0);
    private final AtomicInteger cancelledRides = new AtomicInteger(0);
    private final AtomicLong totalRevenue = new AtomicLong(0);
    private final Map<String, AtomicInteger> ridesByType = new ConcurrentHashMap<>();

    @Override
    public void onRideStatusChanged(Ride ride) {
        switch (ride.getStatus()) {
            case REQUESTED:
                totalRides.incrementAndGet();
                ridesByType.computeIfAbsent(ride.getRideType().name(), k -> new AtomicInteger())
                        .incrementAndGet();
                break;
            case COMPLETED:
                completedRides.incrementAndGet();
                if (ride.getFare() != null) {
                    totalRevenue.addAndGet((long) (ride.getFare().getTotalAmount() * 100)); // Store as cents
                }
                logMetric("ride_completed", ride);
                break;
            case CANCELLED:
                cancelledRides.incrementAndGet();
                logMetric("ride_cancelled", ride);
                break;
        }
    }

    @Override
    public void onLocationUpdated(Ride ride) {
        // Track location updates for heatmap analysis
    }

    @Override
    public void onDriverMatched(Ride ride) {
        logMetric("driver_matched", ride);
    }

    private void logMetric(String eventType, Ride ride) {
        // In production: send to analytics service (Mixpanel, Amplitude, etc.)
        System.out.println(String.format("[ANALYTICS] Event: %s, RideId: %s, Type: %s", 
                eventType, ride.getRideId(), ride.getRideType()));
    }

    // Getters for metrics
    public int getTotalRides() {
        return totalRides.get();
    }

    public int getCompletedRides() {
        return completedRides.get();
    }

    public int getCancelledRides() {
        return cancelledRides.get();
    }

    public double getTotalRevenue() {
        return totalRevenue.get() / 100.0;
    }

    public double getCompletionRate() {
        int total = totalRides.get();
        return total > 0 ? (double) completedRides.get() / total : 0;
    }

    public Map<String, AtomicInteger> getRidesByType() {
        return ridesByType;
    }

    public void printSummary() {
        System.out.println("\n=== RIDE ANALYTICS SUMMARY ===");
        System.out.println(String.format("Total Rides: %d", getTotalRides()));
        System.out.println(String.format("Completed: %d", getCompletedRides()));
        System.out.println(String.format("Cancelled: %d", getCancelledRides()));
        System.out.println(String.format("Completion Rate: %.2f%%", getCompletionRate() * 100));
        System.out.println(String.format("Total Revenue: $%.2f", getTotalRevenue()));
        System.out.println("================================\n");
    }
}



