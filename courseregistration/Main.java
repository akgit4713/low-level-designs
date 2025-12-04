package courseregistration;

import courseregistration.exceptions.CourseFullException;
import courseregistration.exceptions.DuplicateRegistrationException;
import courseregistration.factories.CourseRegistrationSystemFactory;
import courseregistration.factories.CourseRegistrationSystemFactory.CourseRegistrationSystem;
import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.models.Student;
import courseregistration.observers.AuditLogObserver;
import courseregistration.services.CourseService;
import courseregistration.services.RegistrationService;
import courseregistration.services.StudentService;
import courseregistration.strategies.search.*;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main demonstration class for the University Course Registration System.
 */
public class Main {
    
    private static final String LINE = "─".repeat(70);
    private static final String DOUBLE_LINE = "═".repeat(70);
    
    private static CourseRegistrationSystem system;
    private static StudentService studentService;
    private static CourseService courseService;
    private static RegistrationService registrationService;
    
    public static void main(String[] args) {
        printHeader();
        
        // Initialize system
        initializeSystem();
        
        // Create sample data
        createSampleData();
        
        // Run interactive menu
        runInteractiveMenu();
    }
    
    private static void printHeader() {
        System.out.println(DOUBLE_LINE);
        System.out.println("      UNIVERSITY COURSE REGISTRATION SYSTEM");
        System.out.println(DOUBLE_LINE);
        System.out.println();
    }
    
    private static void initializeSystem() {
        System.out.println("Initializing Course Registration System...");
        system = CourseRegistrationSystemFactory.createDefaultSystem();
        studentService = system.getStudentService();
        courseService = system.getCourseService();
        registrationService = system.getRegistrationService();
        System.out.println("System initialized successfully!");
        System.out.println();
    }
    
    private static void createSampleData() {
        System.out.println("Creating sample data...");
        System.out.println(LINE);
        
        // Create students
        Student alice = studentService.createStudent("STU2024001", "Alice Johnson", 
                "alice@university.edu", "Computer Science");
        Student bob = studentService.createStudent("STU2024002", "Bob Smith", 
                "bob@university.edu", "Computer Science");
        Student carol = studentService.createStudent("STU2024003", "Carol Williams", 
                "carol@university.edu", "Mathematics");
        Student david = studentService.createStudent("STU2024004", "David Brown", 
                "david@university.edu", "Physics");
        Student eve = studentService.createStudent("STU2024005", "Eve Davis", 
                "eve@university.edu", "Computer Science");
        
        System.out.println("Created 5 students");
        
        // Create courses
        Course cs101 = courseService.createCourse("CS101", "Introduction to Computer Science",
                "Dr. Smith", 3, "Computer Science", 4);
        Course cs201 = courseService.createCourse("CS201", "Data Structures and Algorithms",
                "Dr. Johnson", 30, "Computer Science", 4);
        Course math101 = courseService.createCourse("MATH101", "Calculus I",
                "Dr. Williams", 25, "Mathematics", 3);
        Course phys101 = courseService.createCourse("PHYS101", "Physics I",
                "Dr. Brown", 20, "Physics", 4);
        Course cs301 = courseService.createCourse("CS301", "Database Systems",
                "Dr. Davis", 25, "Computer Science", 3);
        
        System.out.println("Created 5 courses");
        System.out.println();
        
        // Register some students for courses
        System.out.println("Registering students for courses...");
        System.out.println(LINE);
        
        registrationService.registerStudentForCourse(alice.getId(), cs101.getId());
        registrationService.registerStudentForCourse(bob.getId(), cs101.getId());
        registrationService.registerStudentForCourse(carol.getId(), math101.getId());
        registrationService.registerStudentForCourse(alice.getId(), cs201.getId());
        registrationService.registerStudentForCourse(david.getId(), phys101.getId());
        
        System.out.println(LINE);
        System.out.println("Sample data created successfully!");
        System.out.println();
    }
    
    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            switch (choice) {
                case "1" -> displayAllCourses();
                case "2" -> displayAllStudents();
                case "3" -> searchCourses(scanner);
                case "4" -> registerForCourse(scanner);
                case "5" -> dropCourse(scanner);
                case "6" -> viewStudentRegistrations(scanner);
                case "7" -> viewCourseEnrollment(scanner);
                case "8" -> demonstrateConcurrency();
                case "9" -> demonstrateSearchStrategies();
                case "10" -> viewAuditLog();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
            
            System.out.println();
        }
        
        System.out.println("Thank you for using the Course Registration System!");
        scanner.close();
    }
    
    private static void printMenu() {
        System.out.println(DOUBLE_LINE);
        System.out.println("                    MAIN MENU");
        System.out.println(DOUBLE_LINE);
        System.out.println("  VIEW");
        System.out.println("    1.  View All Courses");
        System.out.println("    2.  View All Students");
        System.out.println();
        System.out.println("  SEARCH");
        System.out.println("    3.  Search Courses");
        System.out.println();
        System.out.println("  REGISTRATION");
        System.out.println("    4.  Register for a Course");
        System.out.println("    5.  Drop a Course");
        System.out.println("    6.  View Student Registrations");
        System.out.println("    7.  View Course Enrollment");
        System.out.println();
        System.out.println("  ADVANCED");
        System.out.println("    8.  Demo: Concurrent Registration");
        System.out.println("    9.  Demo: Search Strategies");
        System.out.println("    10. View Audit Log");
        System.out.println();
        System.out.println("    0.  Exit");
        System.out.println(LINE);
    }
    
    private static void displayAllCourses() {
        System.out.println("ALL COURSES");
        System.out.println(LINE);
        
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        
        for (Course course : courses) {
            printCourseSummary(course);
        }
        
        System.out.println(LINE);
        System.out.println("Total: " + courses.size() + " course(s)");
    }
    
    private static void displayAllStudents() {
        System.out.println("ALL STUDENTS");
        System.out.println(LINE);
        
        List<Student> students = studentService.getAllStudents();
        for (Student student : students) {
            System.out.printf("  [%s] %s (%s) - %s%n",
                    student.getStudentId(), student.getName(), 
                    student.getEmail(), student.getDepartment());
        }
        
        System.out.println(LINE);
        System.out.println("Total: " + students.size() + " student(s)");
    }
    
    private static void searchCourses(Scanner scanner) {
        System.out.println("SEARCH COURSES");
        System.out.println("1. By Course Code");
        System.out.println("2. By Course Name");
        System.out.println("3. By Instructor");
        System.out.println("4. Available Courses Only");
        System.out.print("Enter search type (1-4): ");
        
        try {
            int searchType = Integer.parseInt(scanner.nextLine().trim());
            
            List<Course> results;
            switch (searchType) {
                case 1 -> {
                    System.out.print("Enter course code: ");
                    String code = scanner.nextLine().trim();
                    results = courseService.searchByCode(code);
                }
                case 2 -> {
                    System.out.print("Enter course name: ");
                    String name = scanner.nextLine().trim();
                    results = courseService.searchByName(name);
                }
                case 3 -> {
                    System.out.print("Enter instructor name: ");
                    String instructor = scanner.nextLine().trim();
                    results = courseService.searchByInstructor(instructor);
                }
                case 4 -> results = courseService.getAvailableCourses();
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
            
            System.out.println();
            System.out.println("Search Results:");
            System.out.println(LINE);
            
            if (results.isEmpty()) {
                System.out.println("No courses found.");
            } else {
                results.forEach(Main::printCourseSummary);
                System.out.println("Found: " + results.size() + " course(s)");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    private static void registerForCourse(Scanner scanner) {
        System.out.println("REGISTER FOR A COURSE");
        System.out.println(LINE);
        
        displayAllStudents();
        System.out.print("Enter student ID (e.g., STU2024001): ");
        String studentId = scanner.nextLine().trim();
        
        Student student = studentService.getStudentByStudentId(studentId).orElse(null);
        if (student == null) {
            System.out.println("❌ Student not found: " + studentId);
            return;
        }
        
        System.out.println();
        displayAllCourses();
        System.out.print("Enter course code (e.g., CS101): ");
        String courseCode = scanner.nextLine().trim();
        
        try {
            Registration registration = registrationService.registerStudentForCourseByCode(
                    student.getId(), courseCode);
            System.out.println();
            System.out.println("✅ Registration successful!");
            System.out.println("   Registration ID: " + registration.getId());
            System.out.println("   Status: " + registration.getStatus().getDisplayName());
        } catch (CourseFullException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (DuplicateRegistrationException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void dropCourse(Scanner scanner) {
        System.out.println("DROP A COURSE");
        System.out.println(LINE);
        
        displayAllStudents();
        System.out.print("Enter student ID (e.g., STU2024001): ");
        String studentId = scanner.nextLine().trim();
        
        Student student = studentService.getStudentByStudentId(studentId).orElse(null);
        if (student == null) {
            System.out.println("❌ Student not found: " + studentId);
            return;
        }
        
        // Show student's current registrations
        List<Course> courses = registrationService.getStudentCourses(student.getId());
        if (courses.isEmpty()) {
            System.out.println("Student is not registered for any courses.");
            return;
        }
        
        System.out.println("\nCurrent Registrations:");
        courses.forEach(Main::printCourseSummary);
        
        System.out.print("Enter course code to drop: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courseService.getCourseByCourseCode(courseCode).orElse(null);
        if (course == null) {
            System.out.println("❌ Course not found: " + courseCode);
            return;
        }
        
        try {
            Registration registration = registrationService.dropRegistration(
                    student.getId(), course.getId());
            System.out.println();
            System.out.println("✅ Course dropped successfully!");
            System.out.println("   Status: " + registration.getStatus().getDisplayName());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void viewStudentRegistrations(Scanner scanner) {
        System.out.println("VIEW STUDENT REGISTRATIONS");
        System.out.println(LINE);
        
        displayAllStudents();
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine().trim();
        
        Student student = studentService.getStudentByStudentId(studentId).orElse(null);
        if (student == null) {
            System.out.println("❌ Student not found: " + studentId);
            return;
        }
        
        List<Course> courses = registrationService.getStudentCourses(student.getId());
        
        System.out.println();
        System.out.println("Registered courses for " + student.getName() + ":");
        System.out.println(LINE);
        
        if (courses.isEmpty()) {
            System.out.println("Not registered for any courses.");
        } else {
            courses.forEach(Main::printCourseSummary);
            System.out.println("Total: " + courses.size() + " course(s)");
        }
    }
    
    private static void viewCourseEnrollment(Scanner scanner) {
        System.out.println("VIEW COURSE ENROLLMENT");
        System.out.println(LINE);
        
        displayAllCourses();
        System.out.print("Enter course code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courseService.getCourseByCourseCode(courseCode).orElse(null);
        if (course == null) {
            System.out.println("❌ Course not found: " + courseCode);
            return;
        }
        
        List<Registration> registrations = registrationService.getCourseRegistrations(course.getId());
        
        System.out.println();
        System.out.println("Enrollment for " + course.getName() + " (" + course.getCourseCode() + "):");
        System.out.println(LINE);
        System.out.println("Instructor: " + course.getInstructor());
        System.out.println("Enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxCapacity());
        System.out.println("Available: " + course.getAvailableSeats() + " seat(s)");
        System.out.println();
        
        if (registrations.isEmpty()) {
            System.out.println("No students enrolled.");
        } else {
            System.out.println("Enrolled Students:");
            for (Registration reg : registrations) {
                Student student = studentService.getStudentById(reg.getStudentId()).orElse(null);
                if (student != null) {
                    System.out.printf("  • %s (%s) - %s%n",
                            student.getName(), student.getStudentId(), 
                            reg.getStatus().getDisplayName());
                }
            }
        }
    }
    
    private static void demonstrateConcurrency() {
        System.out.println("CONCURRENT REGISTRATION DEMO");
        System.out.println(LINE);
        System.out.println("Creating a new course with limited capacity (2 seats)...");
        
        // Create a course with limited capacity
        Course limitedCourse = courseService.createCourse("DEMO101", "Concurrent Programming Demo",
                "Dr. Test", 2, "Demo", 3);
        
        // Create multiple students
        List<Student> testStudents = List.of(
                studentService.createStudent("TEST001", "Test Student 1", "test1@test.edu", "Test"),
                studentService.createStudent("TEST002", "Test Student 2", "test2@test.edu", "Test"),
                studentService.createStudent("TEST003", "Test Student 3", "test3@test.edu", "Test"),
                studentService.createStudent("TEST004", "Test Student 4", "test4@test.edu", "Test"),
                studentService.createStudent("TEST005", "Test Student 5", "test5@test.edu", "Test")
        );
        
        System.out.println("Created 5 test students attempting to register concurrently...");
        System.out.println();
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(5);
        
        for (Student student : testStudents) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    Registration reg = registrationService.registerStudentForCourse(
                            student.getId(), limitedCourse.getId());
                    System.out.println("✅ " + student.getName() + " registered successfully!");
                } catch (CourseFullException e) {
                    System.out.println("❌ " + student.getName() + " - Course is full!");
                } catch (Exception e) {
                    System.out.println("❌ " + student.getName() + " - " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        // Start all threads at once
        startLatch.countDown();
        
        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
        
        System.out.println();
        System.out.println(LINE);
        System.out.println("Final course status:");
        printCourseSummary(limitedCourse);
        System.out.println("Only 2 students were able to register despite 5 concurrent attempts!");
    }
    
    private static void demonstrateSearchStrategies() {
        System.out.println("SEARCH STRATEGIES DEMO");
        System.out.println(LINE);
        
        // Demo 1: Search by code
        System.out.println("1. Search by Course Code (contains 'CS'):");
        CourseSearchStrategy codeStrategy = new CourseCodeSearchStrategy("CS");
        List<Course> csCourses = courseService.search(codeStrategy);
        System.out.println("   Criteria: " + codeStrategy.getDescription());
        csCourses.forEach(c -> System.out.println("   - " + c.getCourseCode() + ": " + c.getName()));
        System.out.println();
        
        // Demo 2: Search by name
        System.out.println("2. Search by Name (contains 'Introduction'):");
        CourseSearchStrategy nameStrategy = new CourseNameSearchStrategy("Introduction");
        List<Course> introCourses = courseService.search(nameStrategy);
        System.out.println("   Criteria: " + nameStrategy.getDescription());
        introCourses.forEach(c -> System.out.println("   - " + c.getCourseCode() + ": " + c.getName()));
        System.out.println();
        
        // Demo 3: Composite search (AND)
        System.out.println("3. Composite Search (CS courses with available seats):");
        CompositeSearchStrategy compositeAnd = CompositeSearchStrategy.and(
                new CourseCodeSearchStrategy("CS"),
                new AvailableSeatsSearchStrategy()
        );
        List<Course> availableCs = courseService.search(compositeAnd);
        System.out.println("   Criteria: " + compositeAnd.getDescription());
        availableCs.forEach(c -> System.out.println("   - " + c.getCourseCode() + ": " + 
                c.getName() + " (" + c.getAvailableSeats() + " seats)"));
        System.out.println();
        
        // Demo 4: Composite search (OR)
        System.out.println("4. Composite Search (CS OR MATH courses):");
        CompositeSearchStrategy compositeOr = CompositeSearchStrategy.or(
                new CourseCodeSearchStrategy("CS"),
                new CourseCodeSearchStrategy("MATH")
        );
        List<Course> csOrMath = courseService.search(compositeOr);
        System.out.println("   Criteria: " + compositeOr.getDescription());
        csOrMath.forEach(c -> System.out.println("   - " + c.getCourseCode() + ": " + c.getName()));
    }
    
    private static void viewAuditLog() {
        System.out.println("AUDIT LOG");
        for (var observer : system.getObservers()) {
            if (observer instanceof AuditLogObserver auditLog) {
                auditLog.printAuditLog();
                return;
            }
        }
        System.out.println("Audit log not available.");
    }
    
    private static void printCourseSummary(Course course) {
        String availability = course.isFull() ? "FULL" : course.getAvailableSeats() + " available";
        System.out.printf("  [%s] %s%n", course.getCourseCode(), course.getName());
        System.out.printf("        Instructor: %-20s | Enrollment: %d/%d (%s)%n",
                course.getInstructor(),
                course.getCurrentEnrollment(),
                course.getMaxCapacity(),
                availability);
        System.out.println();
    }
}



