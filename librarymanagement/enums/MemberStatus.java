package librarymanagement.enums;

/**
 * Represents the status of a library member.
 */
public enum MemberStatus {
    ACTIVE,         // Member is in good standing
    SUSPENDED,      // Member is temporarily suspended (e.g., overdue books)
    EXPIRED,        // Membership has expired
    BLACKLISTED     // Member is permanently banned
}



