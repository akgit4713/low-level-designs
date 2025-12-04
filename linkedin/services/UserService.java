package linkedin.services;

import linkedin.exceptions.AuthenticationException;
import linkedin.exceptions.UserNotFoundException;
import linkedin.exceptions.ValidationException;
import linkedin.models.*;
import linkedin.repositories.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Service for user registration, authentication, and profile management.
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // === Registration & Authentication ===
    
    public User register(String name, String email, String password) {
        validateRegistration(name, email, password);
        
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("Email already registered: " + email);
        }
        
        String passwordHash = hashPassword(password);
        User user = new User(email, passwordHash, name);
        return userRepository.save(user);
    }
    
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
        
        String passwordHash = hashPassword(password);
        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        if (!user.isActive()) {
            throw new AuthenticationException("Account is deactivated");
        }
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        return user;
    }
    
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        String oldHash = hashPassword(oldPassword);
        if (!user.getPasswordHash().equals(oldHash)) {
            throw new AuthenticationException("Current password is incorrect");
        }
        
        validatePassword(newPassword);
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }
    
    // === User Profile ===
    
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }
    
    public void updateProfile(String userId, String headline, String summary, 
                             String location, String industry) {
        User user = getUserById(userId);
        Profile profile = user.getProfile();
        
        if (headline != null) profile.setHeadline(headline);
        if (summary != null) profile.setSummary(summary);
        if (location != null) profile.setLocation(location);
        if (industry != null) profile.setIndustry(industry);
        
        userRepository.save(user);
    }
    
    public void updateProfilePicture(String userId, String pictureUrl) {
        User user = getUserById(userId);
        user.getProfile().setProfilePictureUrl(pictureUrl);
        userRepository.save(user);
    }
    
    // === Experience ===
    
    public Experience addExperience(String userId, String title, String company, 
                                    LocalDate startDate, String location, String description) {
        User user = getUserById(userId);
        
        Experience experience = new Experience(title, company, startDate);
        experience.setLocation(location);
        experience.setDescription(description);
        
        user.getProfile().addExperience(experience);
        userRepository.save(user);
        
        return experience;
    }
    
    public void updateExperience(String userId, String experienceId, LocalDate endDate, 
                                String description, boolean isCurrent) {
        User user = getUserById(userId);
        
        user.getProfile().getExperiences().stream()
                .filter(e -> e.getId().equals(experienceId))
                .findFirst()
                .ifPresent(exp -> {
                    if (endDate != null) exp.setEndDate(endDate);
                    if (description != null) exp.setDescription(description);
                    exp.setCurrent(isCurrent);
                });
        
        userRepository.save(user);
    }
    
    public void removeExperience(String userId, String experienceId) {
        User user = getUserById(userId);
        user.getProfile().removeExperience(experienceId);
        userRepository.save(user);
    }
    
    // === Education ===
    
    public Education addEducation(String userId, String institution, String degree, 
                                  String fieldOfStudy, int startYear, int endYear) {
        User user = getUserById(userId);
        
        Education education = new Education(institution, degree, fieldOfStudy, startYear);
        education.setEndYear(endYear);
        
        user.getProfile().addEducation(education);
        userRepository.save(user);
        
        return education;
    }
    
    public void removeEducation(String userId, String educationId) {
        User user = getUserById(userId);
        user.getProfile().removeEducation(educationId);
        userRepository.save(user);
    }
    
    // === Skills ===
    
    public Skill addSkill(String userId, String skillName) {
        User user = getUserById(userId);
        
        // Check if skill already exists
        boolean exists = user.getProfile().getSkills().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(skillName));
        
        if (exists) {
            throw new ValidationException("Skill already added: " + skillName);
        }
        
        Skill skill = new Skill(skillName);
        user.getProfile().addSkill(skill);
        userRepository.save(user);
        
        return skill;
    }
    
    public void removeSkill(String userId, String skillId) {
        User user = getUserById(userId);
        user.getProfile().removeSkill(skillId);
        userRepository.save(user);
    }
    
    public void endorseSkill(String userId, String skillId) {
        User user = getUserById(userId);
        
        user.getProfile().getSkills().stream()
                .filter(s -> s.getId().equals(skillId))
                .findFirst()
                .ifPresent(Skill::addEndorsement);
        
        userRepository.save(user);
    }
    
    // === Account Management ===
    
    public void deactivateAccount(String userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }
    
    public void reactivateAccount(String userId) {
        User user = getUserById(userId);
        user.setActive(true);
        userRepository.save(user);
    }
    
    // === Search ===
    
    public List<User> searchByName(String name) {
        return userRepository.findByNameContaining(name);
    }
    
    public List<User> searchBySkill(String skill) {
        return userRepository.findBySkill(skill);
    }
    
    // === Validation ===
    
    private void validateRegistration(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Valid email is required");
        }
        validatePassword(password);
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}



