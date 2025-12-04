package courseregistration.strategies.search;

import courseregistration.models.Course;

import java.util.Objects;

/**
 * Search strategy that matches courses by course code.
 */
public class CourseCodeSearchStrategy implements CourseSearchStrategy {
    
    private final String courseCode;
    private final boolean exactMatch;
    
    public CourseCodeSearchStrategy(String courseCode) {
        this(courseCode, false);
    }
    
    public CourseCodeSearchStrategy(String courseCode, boolean exactMatch) {
        this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null")
                .toUpperCase();
        this.exactMatch = exactMatch;
    }
    
    @Override
    public boolean matches(Course course) {
        String code = course.getCourseCode().toUpperCase();
        if (exactMatch) {
            return code.equals(courseCode);
        }
        return code.contains(courseCode);
    }
    
    @Override
    public String getDescription() {
        return "Course code " + (exactMatch ? "equals" : "contains") + " '" + courseCode + "'";
    }
}



