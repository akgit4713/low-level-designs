package courseregistration.strategies.search;

import courseregistration.models.Course;

/**
 * Search strategy that matches courses with available seats.
 */
public class AvailableSeatsSearchStrategy implements CourseSearchStrategy {
    
    private final int minimumSeats;
    
    public AvailableSeatsSearchStrategy() {
        this(1);
    }
    
    public AvailableSeatsSearchStrategy(int minimumSeats) {
        if (minimumSeats < 0) {
            throw new IllegalArgumentException("Minimum seats cannot be negative");
        }
        this.minimumSeats = minimumSeats;
    }
    
    @Override
    public boolean matches(Course course) {
        return course.getAvailableSeats() >= minimumSeats;
    }
    
    @Override
    public String getDescription() {
        return "At least " + minimumSeats + " seat(s) available";
    }
}



