package librarymanagement.factories;

import librarymanagement.enums.MemberType;
import librarymanagement.models.Member;

import java.util.regex.Pattern;

/**
 * Factory for creating Member instances with validation.
 */
public class MemberFactory {
    
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^[+]?[0-9]{10,15}$");

    private MemberFactory() {
        // Private constructor - use static methods
    }

    /**
     * Creates a new Member with validation.
     */
    public static Member createMember(String name, String email, MemberType memberType) {
        validateName(name);
        validateEmail(email);
        if (memberType == null) {
            memberType = MemberType.STANDARD;
        }
        
        return new Member(name, email, memberType);
    }

    /**
     * Creates a new standard member.
     */
    public static Member createStandardMember(String name, String email) {
        return createMember(name, email, MemberType.STANDARD);
    }

    /**
     * Creates a new student member.
     */
    public static Member createStudentMember(String name, String email) {
        return createMember(name, email, MemberType.STUDENT);
    }

    /**
     * Creates a new faculty member.
     */
    public static Member createFacultyMember(String name, String email) {
        return createMember(name, email, MemberType.FACULTY);
    }

    /**
     * Creates a new premium member.
     */
    public static Member createPremiumMember(String name, String email) {
        return createMember(name, email, MemberType.PREMIUM);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates a phone number format.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        return PHONE_PATTERN.matcher(phone.replaceAll("[\\s()-]", "")).matches();
    }
}



