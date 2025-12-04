package courseregistration.strategies.search;

import courseregistration.models.Course;

/**
 * Strategy interface for searching/filtering courses.
 * Follows the Strategy pattern for extensible search algorithms.
 */
public interface CourseSearchStrategy {
    
    /**
     * Checks if the given course matches this search criteria.
     */
    boolean matches(Course course);
    
    /**
     * Returns a description of this search strategy.
     */
    String getDescription();
}



