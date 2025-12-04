package courseregistration.strategies.search;

import courseregistration.models.Course;

import java.util.Objects;

/**
 * Search strategy that matches courses by name.
 */
public class CourseNameSearchStrategy implements CourseSearchStrategy {
    
    private final String name;
    
    public CourseNameSearchStrategy(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null").toLowerCase();
    }
    
    @Override
    public boolean matches(Course course) {
        return course.getName().toLowerCase().contains(name);
    }
    
    @Override
    public String getDescription() {
        return "Course name contains '" + name + "'";
    }
}



