package ridesharing.strategies.distance;

import ridesharing.models.Location;

/**
 * Calculates distance using the Haversine formula (great-circle distance).
 * Accurate for real-world geographical distances.
 */
public class HaversineDistanceStrategy implements DistanceCalculationStrategy {
    
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public double calculateDistance(Location from, Location to) {
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
}



