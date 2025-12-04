package trafficsignal.models;

import trafficsignal.enums.Direction;

import java.util.*;

/**
 * Represents an intersection with multiple roads.
 */
public class Intersection {
    
    private final String id;
    private final String name;
    private final Map<Direction, Road> roads;
    private final List<EmergencyVehicle> activeEmergencies;
    private boolean isOperational;

    public Intersection(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.roads = new EnumMap<>(Direction.class);
        this.activeEmergencies = new ArrayList<>();
        this.isOperational = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addRoad(Road road) {
        roads.put(road.getDirection(), road);
    }

    public Road getRoad(Direction direction) {
        return roads.get(direction);
    }

    public Collection<Road> getAllRoads() {
        return Collections.unmodifiableCollection(roads.values());
    }

    public Map<Direction, Road> getRoadMap() {
        return Collections.unmodifiableMap(roads);
    }

    public int getRoadCount() {
        return roads.size();
    }

    public boolean hasRoad(Direction direction) {
        return roads.containsKey(direction);
    }

    /**
     * Gets roads in a specific axis (N-S or E-W).
     */
    public List<Road> getRoadsInAxis(Direction direction) {
        List<Road> axisRoads = new ArrayList<>();
        Road road1 = roads.get(direction);
        Road road2 = roads.get(direction.getOpposite());
        
        if (road1 != null) axisRoads.add(road1);
        if (road2 != null) axisRoads.add(road2);
        
        return axisRoads;
    }

    /**
     * Gets perpendicular roads to a given direction.
     */
    public List<Road> getPerpendicularRoads(Direction direction) {
        List<Road> perpRoads = new ArrayList<>();
        for (Road road : roads.values()) {
            if (direction.isPerpendicularTo(road.getDirection())) {
                perpRoads.add(road);
            }
        }
        return perpRoads;
    }

    public void addEmergency(EmergencyVehicle vehicle) {
        activeEmergencies.add(vehicle);
    }

    public void removeEmergency(EmergencyVehicle vehicle) {
        activeEmergencies.remove(vehicle);
    }

    public List<EmergencyVehicle> getActiveEmergencies() {
        return Collections.unmodifiableList(activeEmergencies);
    }

    public boolean hasActiveEmergency() {
        return !activeEmergencies.isEmpty();
    }

    public void clearCompletedEmergencies() {
        activeEmergencies.removeIf(EmergencyVehicle::isCleared);
    }

    public boolean isOperational() {
        return isOperational;
    }

    public void setOperational(boolean operational) {
        this.isOperational = operational;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Intersection[name=%s, operational=%s]\n", name, isOperational));
        for (Road road : roads.values()) {
            sb.append("  ").append(road).append("\n");
        }
        return sb.toString();
    }
}



