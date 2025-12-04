package trafficsignal.enums;

/**
 * Represents the traffic density levels for adaptive timing.
 */
public enum TrafficDensity {
    LOW(0.5),
    NORMAL(1.0),
    HIGH(1.5),
    VERY_HIGH(2.0);

    private final double timingMultiplier;

    TrafficDensity(double timingMultiplier) {
        this.timingMultiplier = timingMultiplier;
    }

    public double getTimingMultiplier() {
        return timingMultiplier;
    }
}



