package trafficsignal.factories;

import trafficsignal.enums.Direction;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;

/**
 * Factory Pattern: Creates pre-configured intersections.
 */
public class IntersectionFactory {
    
    /**
     * Creates a standard 4-way intersection.
     */
    public static Intersection createFourWayIntersection(String name) {
        Intersection intersection = new Intersection(name);
        
        intersection.addRoad(new Road("North Road", Direction.NORTH));
        intersection.addRoad(new Road("South Road", Direction.SOUTH));
        intersection.addRoad(new Road("East Road", Direction.EAST));
        intersection.addRoad(new Road("West Road", Direction.WEST));
        
        return intersection;
    }

    /**
     * Creates a T-junction (3-way intersection).
     */
    public static Intersection createTJunction(String name, Direction excludedDirection) {
        Intersection intersection = new Intersection(name);
        
        for (Direction dir : Direction.values()) {
            if (dir != excludedDirection) {
                intersection.addRoad(new Road(dir.getDisplayName() + " Road", dir));
            }
        }
        
        return intersection;
    }

    /**
     * Creates a 2-way intersection (straight road).
     */
    public static Intersection createTwoWayIntersection(String name, Direction direction1) {
        Intersection intersection = new Intersection(name);
        
        intersection.addRoad(new Road(direction1.getDisplayName() + " Road", direction1));
        intersection.addRoad(new Road(direction1.getOpposite().getDisplayName() + " Road", 
            direction1.getOpposite()));
        
        return intersection;
    }

    /**
     * Creates a custom intersection with specified roads.
     */
    public static Intersection createCustomIntersection(String name, Direction... directions) {
        Intersection intersection = new Intersection(name);
        
        for (Direction dir : directions) {
            intersection.addRoad(new Road(dir.getDisplayName() + " Road", dir));
        }
        
        return intersection;
    }

    /**
     * Creates an intersection with custom road names.
     */
    public static IntersectionBuilder builder(String name) {
        return new IntersectionBuilder(name);
    }

    /**
     * Builder for creating custom intersections.
     */
    public static class IntersectionBuilder {
        private final Intersection intersection;

        private IntersectionBuilder(String name) {
            this.intersection = new Intersection(name);
        }

        public IntersectionBuilder addRoad(String roadName, Direction direction) {
            intersection.addRoad(new Road(roadName, direction));
            return this;
        }

        public IntersectionBuilder addNorthRoad(String name) {
            return addRoad(name, Direction.NORTH);
        }

        public IntersectionBuilder addSouthRoad(String name) {
            return addRoad(name, Direction.SOUTH);
        }

        public IntersectionBuilder addEastRoad(String name) {
            return addRoad(name, Direction.EAST);
        }

        public IntersectionBuilder addWestRoad(String name) {
            return addRoad(name, Direction.WEST);
        }

        public Intersection build() {
            return intersection;
        }
    }
}



