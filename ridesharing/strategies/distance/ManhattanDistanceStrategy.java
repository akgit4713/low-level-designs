package ridesharing.strategies.distance;

import ridesharing.models.Location;

/**
 * Calculates distance using Manhattan distance approximation.
 * More realistic for grid-based city streets.
 */
public class ManhattanDistanceStrategy implements DistanceCalculationStrategy {
    
    private static final double KM_PER_DEGREE_LAT = 111.0;
    private static final double KM_PER_DEGREE_LON_AT_EQUATOR = 111.32;

    @Override
    public double calculateDistance(Location from, Location to) {
        // Approximate km per degree of longitude at the average latitude
        double avgLat = Math.toRadians((from.getLatitude() + to.getLatitude()) / 2);
        double kmPerDegreeLon = KM_PER_DEGREE_LON_AT_EQUATOR * Math.cos(avgLat);
        
        double deltaLat = Math.abs(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.abs(to.getLongitude() - from.getLongitude());
        
        double latDistance = deltaLat * KM_PER_DEGREE_LAT;
        double lonDistance = deltaLon * kmPerDegreeLon;
        
        // Manhattan distance (sum of absolute differences)
        // Multiply by 1.3 to account for non-straight routes
        return (latDistance + lonDistance) * 1.3;
    }
}



