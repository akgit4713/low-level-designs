package courseregistration.strategies.search;

import courseregistration.models.Course;

import java.util.Objects;

/**
 * Search strategy that matches courses by department.
 */
public class DepartmentSearchStrategy implements CourseSearchStrategy {
    
    private final String department;
    
    public DepartmentSearchStrategy(String department) {
        this.department = Objects.requireNonNull(department, "Department cannot be null")
                .toLowerCase();
    }
    
    @Override
    public boolean matches(Course course) {
        return course.getDepartment() != null && 
               course.getDepartment().toLowerCase().contains(department);
    }
    
    @Override
    public String getDescription() {
        return "Department contains '" + department + "'";
    }
}



