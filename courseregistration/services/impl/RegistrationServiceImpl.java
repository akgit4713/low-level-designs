package courseregistration.services.impl;

import courseregistration.enums.RegistrationStatus;
import courseregistration.exceptions.*;
import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.models.Student;
import courseregistration.observers.RegistrationObserver;
import courseregistration.repositories.CourseRepository;
import courseregistration.repositories.RegistrationRepository;
import courseregistration.repositories.StudentRepository;
import courseregistration.services.RegistrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Thread-safe implementation of RegistrationService.
 * Uses course-level locking to handle concurrent registrations.
 */
public class RegistrationServiceImpl implements RegistrationService {
    
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;
    private final List<RegistrationObserver> observers;
    
    public RegistrationServiceImpl(StudentRepository studentRepository,
                                   CourseRepository courseRepository,
                                   RegistrationRepository registrationRepository) {
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.courseRepository = Objects.requireNonNull(courseRepository);
        this.registrationRepository = Objects.requireNonNull(registrationRepository);
        this.observers = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public Registration registerStudentForCourse(String studentId, String courseId) {
        // Validate student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        // Validate course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        
        return doRegister(student, course);
    }
    
    @Override
    public Registration registerStudentForCourseByCode(String studentId, String courseCode) {
        // Validate student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        // Validate course exists
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException(courseCode));
        
        return doRegister(student, course);
    }
    
    /**
     * Performs the actual registration with thread-safety.
     * Uses course-level locking to prevent race conditions.
     */
    private Registration doRegister(Student student, Course course) {
        // Acquire lock on the course for thread-safe registration
        course.lock();
        try {
            // Check for duplicate registration
            if (registrationRepository.hasActiveRegistration(student.getId(), course.getId())) {
                throw new DuplicateRegistrationException(student.getStudentId(), course.getCourseCode());
            }
            
            // Check if course is full
            boolean wasFull = course.isFull();
            if (!course.tryIncrementEnrollment()) {
                throw new CourseFullException(course.getCourseCode(), course.getMaxCapacity());
            }
            
            // Create and save registration
            Registration registration = new Registration(student.getId(), course.getId());
            registrationRepository.save(registration);
            
            // Notify observers
            notifyRegistrationCreated(registration, student, course);
            
            // Check if course just became full
            if (!wasFull && course.isFull()) {
                notifyCourseFullCapacity(course);
            }
            
            return registration;
            
        } finally {
            course.unlock();
        }
    }
    
    @Override
    public Registration dropRegistration(String studentId, String courseId) {
        // Validate student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        // Validate course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        
        // Find the registration
        Registration registration = registrationRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RegistrationNotFoundException(
                        "No registration found for student " + studentId + " and course " + courseId));
        
        if (!registration.isActive()) {
            throw new CourseRegistrationException("Registration is not active");
        }
        
        // Acquire lock on the course
        course.lock();
        try {
            boolean wasFull = course.isFull();
            
            // Update registration status
            registration.setStatus(RegistrationStatus.DROPPED);
            registrationRepository.save(registration);
            
            // Decrement enrollment
            course.decrementEnrollment();
            
            // Notify observers
            notifyRegistrationDropped(registration, student, course);
            
            // Check if course just became available
            if (wasFull && !course.isFull()) {
                notifyCourseSeatsAvailable(course);
            }
            
            return registration;
            
        } finally {
            course.unlock();
        }
    }
    
    @Override
    public List<Course> getStudentCourses(String studentId) {
        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        
        return registrationRepository.findActiveByStudentId(studentId).stream()
                .map(r -> courseRepository.findById(r.getCourseId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Registration> getStudentRegistrations(String studentId) {
        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        
        return registrationRepository.findByStudentId(studentId);
    }
    
    @Override
    public List<Registration> getCourseRegistrations(String courseId) {
        // Validate course exists
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(courseId);
        }
        
        return registrationRepository.findActiveByCourseId(courseId);
    }
    
    @Override
    public boolean isStudentRegistered(String studentId, String courseId) {
        return registrationRepository.hasActiveRegistration(studentId, courseId);
    }
    
    @Override
    public void registerObserver(RegistrationObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }
    
    @Override
    public void unregisterObserver(RegistrationObserver observer) {
        observers.remove(observer);
    }
    
    // Observer notification methods
    private void notifyRegistrationCreated(Registration registration, Student student, Course course) {
        for (RegistrationObserver observer : observers) {
            try {
                observer.onRegistrationCreated(registration, student, course);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    private void notifyRegistrationDropped(Registration registration, Student student, Course course) {
        for (RegistrationObserver observer : observers) {
            try {
                observer.onRegistrationDropped(registration, student, course);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    private void notifyCourseFullCapacity(Course course) {
        for (RegistrationObserver observer : observers) {
            try {
                observer.onCourseFullCapacity(course);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    private void notifyCourseSeatsAvailable(Course course) {
        for (RegistrationObserver observer : observers) {
            try {
                observer.onCourseSeatsAvailable(course);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
}



