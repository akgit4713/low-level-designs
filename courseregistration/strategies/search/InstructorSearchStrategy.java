package courseregistration.strategies.search;

import courseregistration.models.Course;

import java.util.Objects;

/**
 * Search strategy that matches courses by instructor name.
 */
public class InstructorSearchStrategy implements CourseSearchStrategy {
    
    private final String instructor;
    
    public InstructorSearchStrategy(String instructor) {
        this.instructor = Objects.requireNonNull(instructor, "Instructor cannot be null")
                .toLowerCase();
    }
    
    @Override
    public boolean matches(Course course) {
        return course.getInstructor().toLowerCase().contains(instructor);
    }
    
    @Override
    public String getDescription() {
        return "Instructor contains '" + instructor + "'";
    }
}



