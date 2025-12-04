package courseregistration.services.impl;

import courseregistration.exceptions.StudentNotFoundException;
import courseregistration.models.Student;
import courseregistration.repositories.StudentRepository;
import courseregistration.services.StudentService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of StudentService.
 */
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = Objects.requireNonNull(studentRepository, 
                "Student repository cannot be null");
    }
    
    @Override
    public Student createStudent(String studentId, String name, String email, String department) {
        // Check for duplicate student ID
        if (studentRepository.findByStudentId(studentId).isPresent()) {
            throw new IllegalArgumentException("Student ID already exists: " + studentId);
        }
        
        // Check for duplicate email
        if (studentRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        
        Student student = new Student(studentId, name, email, department);
        return studentRepository.save(student);
    }
    
    @Override
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }
    
    @Override
    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }
    
    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    @Override
    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }
    
    @Override
    public List<Student> searchByName(String name) {
        return studentRepository.searchByName(name);
    }
    
    @Override
    public Student updateStudent(String id, String name, String email, String department) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        
        if (name != null && !name.isEmpty()) {
            student.setName(name);
        }
        if (email != null && !email.isEmpty()) {
            student.setEmail(email);
        }
        if (department != null) {
            student.setDepartment(department);
        }
        
        return studentRepository.save(student);
    }
    
    @Override
    public boolean deleteStudent(String id) {
        return studentRepository.deleteById(id);
    }
}



