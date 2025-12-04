# University Course Registration System - Low-Level Design

## Overview

A thread-safe, extensible course registration system that allows students to register for courses, search courses, and handles concurrent registrations with proper locking mechanisms.

## Requirements

1. ✅ Students can register for courses and view their registered courses
2. ✅ Courses have course code, name, instructor, and maximum enrollment capacity
3. ✅ Students can search for courses by course code or name
4. ✅ System prevents registration for courses at maximum capacity
5. ✅ System handles concurrent registration requests
6. ✅ System ensures data consistency and prevents race conditions
7. ✅ System is extensible for future enhancements

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     COURSE REGISTRATION SYSTEM                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                         PRESENTATION LAYER                           │    │
│  │                              Main.java                               │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                          FACTORY LAYER                               │    │
│  │              CourseRegistrationSystemFactory                         │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                          SERVICE LAYER                               │    │
│  │  ┌──────────────┐  ┌───────────────┐  ┌───────────────────┐         │    │
│  │  │StudentService│  │ CourseService │  │RegistrationService│         │    │
│  │  └──────────────┘  └───────────────┘  └───────────────────┘         │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                        REPOSITORY LAYER                              │    │
│  │  ┌────────────────┐  ┌────────────────┐  ┌────────────────────┐     │    │
│  │  │StudentRepository│  │CourseRepository│  │RegistrationRepository│   │    │
│  │  └────────────────┘  └────────────────┘  └────────────────────┘     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                          MODEL LAYER                                 │    │
│  │      ┌──────────┐      ┌──────────┐      ┌──────────────┐           │    │
│  │      │  Student │      │  Course  │      │ Registration │           │    │
│  │      └──────────┘      └──────────┘      └──────────────┘           │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      CROSS-CUTTING CONCERNS                          │    │
│  │  ┌─────────────────────┐    ┌─────────────────────────────────┐     │    │
│  │  │   Search Strategies │    │         Observers               │     │    │
│  │  │  • CourseCode       │    │  • EmailNotification            │     │    │
│  │  │  • CourseName       │    │  • CapacityAlert                │     │    │
│  │  │  • Instructor       │    │  • AuditLog                     │     │    │
│  │  │  • AvailableSeats   │    └─────────────────────────────────┘     │    │
│  │  │  • Composite        │                                            │    │
│  │  └─────────────────────┘                                            │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Design Patterns Used

### 1. Repository Pattern
Abstracts data access, allowing easy switching between in-memory and database implementations.

```java
public interface CourseRepository extends Repository<Course, String> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findAvailableCourses();
}
```

### 2. Strategy Pattern
Enables flexible course search algorithms without modifying existing code.

```java
public interface CourseSearchStrategy {
    boolean matches(Course course);
    String getDescription();
}

// Usage
CompositeSearchStrategy.and(
    new CourseCodeSearchStrategy("CS"),
    new AvailableSeatsSearchStrategy()
);
```

### 3. Observer Pattern
Decouples registration events from notification handling.

```java
public interface RegistrationObserver {
    void onRegistrationCreated(Registration reg, Student student, Course course);
    void onCourseFullCapacity(Course course);
}
```

### 4. Factory Pattern
Centralizes system assembly and dependency injection.

```java
CourseRegistrationSystem system = CourseRegistrationSystemFactory.createDefaultSystem();
```

### 5. Builder Pattern
Provides flexible system configuration.

```java
CourseRegistrationSystem system = CourseRegistrationSystemFactory.builder()
    .withObserver(new EmailNotificationObserver())
    .withObserver(new AuditLogObserver())
    .build();
```

## SOLID Principles

### Single Responsibility (SRP)
- `StudentService` handles only student operations
- `CourseService` handles only course operations
- `RegistrationService` handles only registration logic
- Each observer handles one type of notification

### Open/Closed (OCP)
- New search strategies can be added without modifying existing code
- New observers can be registered without changing the registration service
- New repository implementations can be created for different storage backends

### Liskov Substitution (LSP)
- All repository implementations are interchangeable
- All search strategies implement the same interface
- All observers follow the same contract

### Interface Segregation (ISP)
- Separate interfaces for each repository type
- `CourseSearchStrategy` interface is focused on search only
- `RegistrationObserver` defines specific event methods

### Dependency Inversion (DIP)
- Services depend on repository interfaces, not implementations
- Factory injects concrete implementations at runtime

## Concurrency Handling

### Thread-Safe Registration

```java
// Course.java - Uses ReentrantLock for thread-safe operations
public boolean tryIncrementEnrollment() {
    while (true) {
        int current = currentEnrollment.get();
        if (current >= maxCapacity) {
            return false;
        }
        if (currentEnrollment.compareAndSet(current, current + 1)) {
            return true;
        }
    }
}

// RegistrationServiceImpl.java - Acquires lock per course
private Registration doRegister(Student student, Course course) {
    course.lock();
    try {
        if (registrationRepository.hasActiveRegistration(student.getId(), course.getId())) {
            throw new DuplicateRegistrationException(...);
        }
        if (!course.tryIncrementEnrollment()) {
            throw new CourseFullException(...);
        }
        // ... create registration
    } finally {
        course.unlock();
    }
}
```

### Thread-Safe Collections
- `ConcurrentHashMap` for all repository storage
- `CopyOnWriteArrayList` for observers
- `AtomicInteger` for enrollment counts

## Class Diagram

```
┌─────────────────────┐
│      Student        │
├─────────────────────┤
│ - id: String        │
│ - studentId: String │
│ - name: String      │
│ - email: String     │
│ - department: String│
└─────────────────────┘
         │
         │ registers for
         ▼
┌─────────────────────┐      ┌─────────────────────┐
│    Registration     │─────▶│       Course        │
├─────────────────────┤      ├─────────────────────┤
│ - id: String        │      │ - id: String        │
│ - studentId: String │      │ - courseCode: String│
│ - courseId: String  │      │ - name: String      │
│ - status: Status    │      │ - instructor: String│
│ - registeredAt: LDT │      │ - maxCapacity: int  │
└─────────────────────┘      │ - enrollment: AI    │
                             │ - lock: ReentrantLock│
                             └─────────────────────┘
```

## Extension Points

### 1. New Search Criteria
```java
public class CreditsSearchStrategy implements CourseSearchStrategy {
    private final int minCredits;
    
    @Override
    public boolean matches(Course course) {
        return course.getCredits() >= minCredits;
    }
}
```

### 2. New Storage Backend
```java
public class JpaCourseRepository implements CourseRepository {
    private final EntityManager em;
    
    @Override
    public Optional<Course> findByCourseCode(String code) {
        return em.createQuery("SELECT c FROM Course c WHERE c.code = :code")
                 .setParameter("code", code)
                 .getResultStream().findFirst();
    }
}
```

### 3. New Notification Channel
```java
public class SMSNotificationObserver implements RegistrationObserver {
    @Override
    public void onRegistrationCreated(Registration reg, Student s, Course c) {
        smsService.send(s.getPhone(), "Registered for " + c.getName());
    }
}
```

### 4. Waitlist Support
```java
public class WaitlistService {
    public void addToWaitlist(String studentId, String courseId) {
        // Add student to waitlist queue
    }
    
    public void processWaitlist(Course course) {
        // When a seat becomes available, auto-register next waitlisted student
    }
}
```

## File Structure

```
courseregistration/
├── Main.java                           # Demo application
├── enums/
│   └── RegistrationStatus.java         # Registration states
├── exceptions/
│   ├── CourseRegistrationException.java
│   ├── CourseNotFoundException.java
│   ├── StudentNotFoundException.java
│   ├── CourseFullException.java
│   ├── DuplicateRegistrationException.java
│   └── RegistrationNotFoundException.java
├── models/
│   ├── Student.java
│   ├── Course.java                      # Thread-safe with ReentrantLock
│   └── Registration.java
├── repositories/
│   ├── Repository.java                  # Generic CRUD interface
│   ├── StudentRepository.java
│   ├── CourseRepository.java
│   ├── RegistrationRepository.java
│   └── impl/
│       ├── InMemoryStudentRepository.java
│       ├── InMemoryCourseRepository.java
│       └── InMemoryRegistrationRepository.java
├── services/
│   ├── StudentService.java
│   ├── CourseService.java
│   ├── RegistrationService.java
│   └── impl/
│       ├── StudentServiceImpl.java
│       ├── CourseServiceImpl.java
│       └── RegistrationServiceImpl.java  # Thread-safe registration
├── strategies/
│   └── search/
│       ├── CourseSearchStrategy.java     # Strategy interface
│       ├── CourseCodeSearchStrategy.java
│       ├── CourseNameSearchStrategy.java
│       ├── InstructorSearchStrategy.java
│       ├── AvailableSeatsSearchStrategy.java
│       ├── DepartmentSearchStrategy.java
│       └── CompositeSearchStrategy.java   # AND/OR composition
├── observers/
│   ├── RegistrationObserver.java         # Observer interface
│   ├── EmailNotificationObserver.java
│   ├── CapacityAlertObserver.java
│   └── AuditLogObserver.java
└── factories/
    └── CourseRegistrationSystemFactory.java  # DI and system assembly
```

## Usage Example

```java
// Create system with default configuration
CourseRegistrationSystem system = CourseRegistrationSystemFactory.createDefaultSystem();

// Get services
StudentService studentService = system.getStudentService();
CourseService courseService = system.getCourseService();
RegistrationService registrationService = system.getRegistrationService();

// Create entities
Student student = studentService.createStudent("STU001", "John Doe", "john@univ.edu", "CS");
Course course = courseService.createCourse("CS101", "Intro to CS", "Dr. Smith", 30);

// Register for course
Registration reg = registrationService.registerStudentForCourse(student.getId(), course.getId());

// Search courses
List<Course> csCourses = courseService.search(new CourseCodeSearchStrategy("CS"));

// View student's courses
List<Course> myCourses = registrationService.getStudentCourses(student.getId());
```

## Running the Demo

```bash
cd /path/to/project
javac -d out courseregistration/**/*.java
java -cp out courseregistration.Main
```

## Key Design Decisions

1. **Course-level locking**: Chosen over global locks for better concurrency
2. **AtomicInteger for enrollment**: CAS operations for lock-free enrollment checks
3. **CopyOnWriteArrayList for observers**: Thread-safe iteration without locking
4. **Factory pattern for DI**: Clean dependency injection without frameworks
5. **Strategy pattern for search**: Extensible without modifying core logic
6. **Observer pattern for events**: Decoupled notification system



