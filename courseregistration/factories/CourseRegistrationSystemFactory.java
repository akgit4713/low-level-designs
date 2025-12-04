package courseregistration.factories;

import courseregistration.observers.AuditLogObserver;
import courseregistration.observers.CapacityAlertObserver;
import courseregistration.observers.EmailNotificationObserver;
import courseregistration.observers.RegistrationObserver;
import courseregistration.repositories.CourseRepository;
import courseregistration.repositories.RegistrationRepository;
import courseregistration.repositories.StudentRepository;
import courseregistration.repositories.impl.InMemoryCourseRepository;
import courseregistration.repositories.impl.InMemoryRegistrationRepository;
import courseregistration.repositories.impl.InMemoryStudentRepository;
import courseregistration.services.CourseService;
import courseregistration.services.RegistrationService;
import courseregistration.services.StudentService;
import courseregistration.services.impl.CourseServiceImpl;
import courseregistration.services.impl.RegistrationServiceImpl;
import courseregistration.services.impl.StudentServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating and wiring the Course Registration System.
 * Provides dependency injection without a DI framework.
 */
public class CourseRegistrationSystemFactory {
    
    /**
     * Encapsulates all system components.
     */
    public static class CourseRegistrationSystem {
        private final StudentService studentService;
        private final CourseService courseService;
        private final RegistrationService registrationService;
        private final List<RegistrationObserver> observers;
        
        private CourseRegistrationSystem(StudentService studentService,
                                         CourseService courseService,
                                         RegistrationService registrationService,
                                         List<RegistrationObserver> observers) {
            this.studentService = studentService;
            this.courseService = courseService;
            this.registrationService = registrationService;
            this.observers = new ArrayList<>(observers);
        }
        
        public StudentService getStudentService() {
            return studentService;
        }
        
        public CourseService getCourseService() {
            return courseService;
        }
        
        public RegistrationService getRegistrationService() {
            return registrationService;
        }
        
        public List<RegistrationObserver> getObservers() {
            return List.copyOf(observers);
        }
    }
    
    /**
     * Creates a default system with in-memory repositories and standard observers.
     */
    public static CourseRegistrationSystem createDefaultSystem() {
        // Create repositories
        StudentRepository studentRepository = new InMemoryStudentRepository();
        CourseRepository courseRepository = new InMemoryCourseRepository();
        RegistrationRepository registrationRepository = new InMemoryRegistrationRepository();
        
        // Create services
        StudentService studentService = new StudentServiceImpl(studentRepository);
        CourseService courseService = new CourseServiceImpl(courseRepository);
        RegistrationService registrationService = new RegistrationServiceImpl(
                studentRepository, courseRepository, registrationRepository);
        
        // Create observers
        List<RegistrationObserver> observers = new ArrayList<>();
        EmailNotificationObserver emailObserver = new EmailNotificationObserver();
        CapacityAlertObserver capacityObserver = new CapacityAlertObserver(0.8);
        AuditLogObserver auditObserver = new AuditLogObserver();
        
        observers.add(emailObserver);
        observers.add(capacityObserver);
        observers.add(auditObserver);
        
        // Register observers
        for (RegistrationObserver observer : observers) {
            registrationService.registerObserver(observer);
        }
        
        return new CourseRegistrationSystem(
                studentService, courseService, registrationService, observers);
    }
    
    /**
     * Creates a minimal system without observers (for testing).
     */
    public static CourseRegistrationSystem createMinimalSystem() {
        // Create repositories
        StudentRepository studentRepository = new InMemoryStudentRepository();
        CourseRepository courseRepository = new InMemoryCourseRepository();
        RegistrationRepository registrationRepository = new InMemoryRegistrationRepository();
        
        // Create services
        StudentService studentService = new StudentServiceImpl(studentRepository);
        CourseService courseService = new CourseServiceImpl(courseRepository);
        RegistrationService registrationService = new RegistrationServiceImpl(
                studentRepository, courseRepository, registrationRepository);
        
        return new CourseRegistrationSystem(
                studentService, courseService, registrationService, List.of());
    }
    
    /**
     * Builder for custom system configuration.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private StudentRepository studentRepository;
        private CourseRepository courseRepository;
        private RegistrationRepository registrationRepository;
        private final List<RegistrationObserver> observers = new ArrayList<>();
        
        public Builder withStudentRepository(StudentRepository repository) {
            this.studentRepository = repository;
            return this;
        }
        
        public Builder withCourseRepository(CourseRepository repository) {
            this.courseRepository = repository;
            return this;
        }
        
        public Builder withRegistrationRepository(RegistrationRepository repository) {
            this.registrationRepository = repository;
            return this;
        }
        
        public Builder withObserver(RegistrationObserver observer) {
            this.observers.add(observer);
            return this;
        }
        
        public CourseRegistrationSystem build() {
            // Use defaults if not provided
            if (studentRepository == null) {
                studentRepository = new InMemoryStudentRepository();
            }
            if (courseRepository == null) {
                courseRepository = new InMemoryCourseRepository();
            }
            if (registrationRepository == null) {
                registrationRepository = new InMemoryRegistrationRepository();
            }
            
            // Create services
            StudentService studentService = new StudentServiceImpl(studentRepository);
            CourseService courseService = new CourseServiceImpl(courseRepository);
            RegistrationService registrationService = new RegistrationServiceImpl(
                    studentRepository, courseRepository, registrationRepository);
            
            // Register observers
            for (RegistrationObserver observer : observers) {
                registrationService.registerObserver(observer);
            }
            
            return new CourseRegistrationSystem(
                    studentService, courseService, registrationService, observers);
        }
    }
}



