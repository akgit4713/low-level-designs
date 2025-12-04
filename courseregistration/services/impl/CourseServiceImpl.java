package courseregistration.services.impl;

import courseregistration.exceptions.CourseNotFoundException;
import courseregistration.models.Course;
import courseregistration.repositories.CourseRepository;
import courseregistration.services.CourseService;
import courseregistration.strategies.search.CourseSearchStrategy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CourseService.
 */
public class CourseServiceImpl implements CourseService {
    
    private final CourseRepository courseRepository;
    
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = Objects.requireNonNull(courseRepository, 
                "Course repository cannot be null");
    }
    
    @Override
    public Course createCourse(String courseCode, String name, String instructor, int maxCapacity) {
        // Check for duplicate course code
        if (courseRepository.findByCourseCode(courseCode).isPresent()) {
            throw new IllegalArgumentException("Course code already exists: " + courseCode);
        }
        
        Course course = new Course(courseCode, name, instructor, maxCapacity);
        return courseRepository.save(course);
    }
    
    @Override
    public Course createCourse(String courseCode, String name, String instructor, int maxCapacity,
                               String department, int credits) {
        // Check for duplicate course code
        if (courseRepository.findByCourseCode(courseCode).isPresent()) {
            throw new IllegalArgumentException("Course code already exists: " + courseCode);
        }
        
        Course course = new Course(courseCode, name, instructor, maxCapacity, department, credits);
        return courseRepository.save(course);
    }
    
    @Override
    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }
    
    @Override
    public Optional<Course> getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }
    
    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    @Override
    public List<Course> searchByCode(String code) {
        return courseRepository.findAll().stream()
                .filter(c -> c.getCourseCode().toUpperCase().contains(code.toUpperCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> searchByName(String name) {
        return courseRepository.searchByName(name);
    }
    
    @Override
    public List<Course> searchByInstructor(String instructor) {
        return courseRepository.findByInstructor(instructor);
    }
    
    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }
    
    @Override
    public List<Course> search(CourseSearchStrategy strategy) {
        return courseRepository.findAll().stream()
                .filter(strategy::matches)
                .collect(Collectors.toList());
    }
    
    @Override
    public Course updateCourse(String id, String name, String instructor, 
                               String department, int credits) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        
        if (name != null && !name.isEmpty()) {
            course.setName(name);
        }
        if (instructor != null && !instructor.isEmpty()) {
            course.setInstructor(instructor);
        }
        if (department != null) {
            course.setDepartment(department);
        }
        if (credits >= 0) {
            course.setCredits(credits);
        }
        
        return courseRepository.save(course);
    }
    
    @Override
    public boolean deleteCourse(String id) {
        return courseRepository.deleteById(id);
    }
}



