package trafficsignal.services;

import trafficsignal.enums.TrafficDensity;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;
import trafficsignal.strategies.*;

import java.time.LocalTime;

/**
 * Monitors traffic conditions and adjusts timing strategy accordingly.
 */
public class TrafficMonitor {
    
    private final Intersection intersection;
    private final SignalController signalController;
    private final TimingStrategy normalStrategy;
    private final TimingStrategy rushHourStrategy;
    private final TimingStrategy nightModeStrategy;
    private final TimingStrategy adaptiveStrategy;

    // Configurable time ranges
    private LocalTime morningRushStart = LocalTime.of(7, 0);
    private LocalTime morningRushEnd = LocalTime.of(9, 30);
    private LocalTime eveningRushStart = LocalTime.of(17, 0);
    private LocalTime eveningRushEnd = LocalTime.of(19, 30);
    private LocalTime nightStart = LocalTime.of(22, 0);
    private LocalTime nightEnd = LocalTime.of(6, 0);

    public TrafficMonitor(Intersection intersection, SignalController signalController) {
        this.intersection = intersection;
        this.signalController = signalController;
        this.normalStrategy = new NormalTimingStrategy();
        this.rushHourStrategy = new RushHourTimingStrategy();
        this.nightModeStrategy = new NightModeTimingStrategy();
        this.adaptiveStrategy = new AdaptiveTimingStrategy();
    }

    /**
     * Updates the timing strategy based on current time.
     */
    public void updateStrategyByTime() {
        LocalTime now = LocalTime.now();
        TimingStrategy newStrategy = determineStrategyForTime(now);
        
        if (newStrategy != signalController.getTimingStrategy()) {
            signalController.setTimingStrategy(newStrategy);
            System.out.printf("Strategy changed to: %s%n", newStrategy.getStrategyName());
        }
    }

    /**
     * Determines the appropriate strategy for a given time.
     */
    public TimingStrategy determineStrategyForTime(LocalTime time) {
        if (isNightTime(time)) {
            return nightModeStrategy;
        } else if (isRushHour(time)) {
            return rushHourStrategy;
        } else {
            return normalStrategy;
        }
    }

    /**
     * Enables adaptive mode that adjusts based on real-time traffic.
     */
    public void enableAdaptiveMode() {
        signalController.setTimingStrategy(adaptiveStrategy);
    }

    /**
     * Updates traffic density for a road based on vehicle count.
     */
    public void updateTrafficDensity(Road road, int vehicleCount) {
        road.setVehicleCount(vehicleCount);
    }

    /**
     * Simulates traffic detection sensors.
     */
    public void simulateTrafficSensor(Road road) {
        // In real system, this would read from actual sensors
        // For simulation, randomly adjust vehicle count
        int change = (int) (Math.random() * 10) - 5;
        int newCount = Math.max(0, road.getVehicleCount() + change);
        road.setVehicleCount(newCount);
    }

    /**
     * Gets the overall traffic density for the intersection.
     */
    public TrafficDensity getOverallDensity() {
        int totalVehicles = 0;
        int roadCount = 0;
        
        for (Road road : intersection.getAllRoads()) {
            totalVehicles += road.getVehicleCount();
            roadCount++;
        }

        if (roadCount == 0) return TrafficDensity.LOW;
        
        int avgVehicles = totalVehicles / roadCount;
        
        if (avgVehicles < 5) return TrafficDensity.LOW;
        if (avgVehicles < 15) return TrafficDensity.NORMAL;
        if (avgVehicles < 30) return TrafficDensity.HIGH;
        return TrafficDensity.VERY_HIGH;
    }

    private boolean isNightTime(LocalTime time) {
        return time.isAfter(nightStart) || time.isBefore(nightEnd);
    }

    private boolean isRushHour(LocalTime time) {
        boolean morningRush = !time.isBefore(morningRushStart) && !time.isAfter(morningRushEnd);
        boolean eveningRush = !time.isBefore(eveningRushStart) && !time.isAfter(eveningRushEnd);
        return morningRush || eveningRush;
    }

    // Configuration setters
    public void setMorningRushHours(LocalTime start, LocalTime end) {
        this.morningRushStart = start;
        this.morningRushEnd = end;
    }

    public void setEveningRushHours(LocalTime start, LocalTime end) {
        this.eveningRushStart = start;
        this.eveningRushEnd = end;
    }

    public void setNightHours(LocalTime start, LocalTime end) {
        this.nightStart = start;
        this.nightEnd = end;
    }
}



