package courseregistration.repositories.impl;

import courseregistration.models.Course;
import courseregistration.repositories.CourseRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of CourseRepository.
 */
public class InMemoryCourseRepository implements CourseRepository {
    
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final Map<String, String> courseCodeToId = new ConcurrentHashMap<>();
    
    @Override
    public Course save(Course course) {
        courses.put(course.getId(), course);
        courseCodeToId.put(course.getCourseCode().toUpperCase(), course.getId());
        return course;
    }
    
    @Override
    public Optional<Course> findById(String id) {
        return Optional.ofNullable(courses.get(id));
    }
    
    @Override
    public List<Course> findAll() {
        return List.copyOf(courses.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        Course removed = courses.remove(id);
        if (removed != null) {
            courseCodeToId.remove(removed.getCourseCode().toUpperCase());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String id) {
        return courses.containsKey(id);
    }
    
    @Override
    public long count() {
        return courses.size();
    }
    
    @Override
    public Optional<Course> findByCourseCode(String courseCode) {
        String id = courseCodeToId.get(courseCode.toUpperCase());
        return id != null ? findById(id) : Optional.empty();
    }
    
    @Override
    public List<Course> findByInstructor(String instructor) {
        String lowerInstructor = instructor.toLowerCase();
        return courses.values().stream()
                .filter(c -> c.getInstructor().toLowerCase().contains(lowerInstructor))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findByDepartment(String department) {
        return courses.values().stream()
                .filter(c -> department.equalsIgnoreCase(c.getDepartment()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> searchByName(String name) {
        String lowerName = name.toLowerCase();
        return courses.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findAvailableCourses() {
        return courses.values().stream()
                .filter(c -> !c.isFull())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findFullCourses() {
        return courses.values().stream()
                .filter(Course::isFull)
                .collect(Collectors.toList());
    }
}



