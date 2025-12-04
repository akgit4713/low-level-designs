package courseregistration.repositories.impl;

import courseregistration.enums.RegistrationStatus;
import courseregistration.models.Registration;
import courseregistration.repositories.RegistrationRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of RegistrationRepository.
 */
public class InMemoryRegistrationRepository implements RegistrationRepository {
    
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();
    // Composite key: studentId:courseId -> registrationId
    private final Map<String, String> studentCourseIndex = new ConcurrentHashMap<>();
    
    @Override
    public Registration save(Registration registration) {
        registrations.put(registration.getId(), registration);
        String compositeKey = createCompositeKey(registration.getStudentId(), registration.getCourseId());
        studentCourseIndex.put(compositeKey, registration.getId());
        return registration;
    }
    
    @Override
    public Optional<Registration> findById(String id) {
        return Optional.ofNullable(registrations.get(id));
    }
    
    @Override
    public List<Registration> findAll() {
        return List.copyOf(registrations.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        Registration removed = registrations.remove(id);
        if (removed != null) {
            String compositeKey = createCompositeKey(removed.getStudentId(), removed.getCourseId());
            studentCourseIndex.remove(compositeKey);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String id) {
        return registrations.containsKey(id);
    }
    
    @Override
    public long count() {
        return registrations.size();
    }
    
    @Override
    public List<Registration> findByStudentId(String studentId) {
        return registrations.values().stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Registration> findByCourseId(String courseId) {
        return registrations.values().stream()
                .filter(r -> r.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Registration> findByStudentIdAndCourseId(String studentId, String courseId) {
        String compositeKey = createCompositeKey(studentId, courseId);
        String registrationId = studentCourseIndex.get(compositeKey);
        return registrationId != null ? findById(registrationId) : Optional.empty();
    }
    
    @Override
    public List<Registration> findByStatus(RegistrationStatus status) {
        return registrations.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Registration> findActiveByStudentId(String studentId) {
        return registrations.values().stream()
                .filter(r -> r.getStudentId().equals(studentId) && r.isActive())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Registration> findActiveByCourseId(String courseId) {
        return registrations.values().stream()
                .filter(r -> r.getCourseId().equals(courseId) && r.isActive())
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean hasActiveRegistration(String studentId, String courseId) {
        return findByStudentIdAndCourseId(studentId, courseId)
                .map(Registration::isActive)
                .orElse(false);
    }
    
    private String createCompositeKey(String studentId, String courseId) {
        return studentId + ":" + courseId;
    }
}



