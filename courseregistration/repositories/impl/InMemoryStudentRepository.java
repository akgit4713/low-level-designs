package courseregistration.repositories.impl;

import courseregistration.models.Student;
import courseregistration.repositories.StudentRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of StudentRepository.
 */
public class InMemoryStudentRepository implements StudentRepository {
    
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, String> studentIdToId = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();
    
    @Override
    public Student save(Student student) {
        students.put(student.getId(), student);
        studentIdToId.put(student.getStudentId().toLowerCase(), student.getId());
        emailToId.put(student.getEmail().toLowerCase(), student.getId());
        return student;
    }
    
    @Override
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(students.get(id));
    }
    
    @Override
    public List<Student> findAll() {
        return List.copyOf(students.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        Student removed = students.remove(id);
        if (removed != null) {
            studentIdToId.remove(removed.getStudentId().toLowerCase());
            emailToId.remove(removed.getEmail().toLowerCase());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String id) {
        return students.containsKey(id);
    }
    
    @Override
    public long count() {
        return students.size();
    }
    
    @Override
    public Optional<Student> findByStudentId(String studentId) {
        String id = studentIdToId.get(studentId.toLowerCase());
        return id != null ? findById(id) : Optional.empty();
    }
    
    @Override
    public Optional<Student> findByEmail(String email) {
        String id = emailToId.get(email.toLowerCase());
        return id != null ? findById(id) : Optional.empty();
    }
    
    @Override
    public List<Student> findByDepartment(String department) {
        return students.values().stream()
                .filter(s -> department.equalsIgnoreCase(s.getDepartment()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> searchByName(String name) {
        String lowerName = name.toLowerCase();
        return students.values().stream()
                .filter(s -> s.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
}



