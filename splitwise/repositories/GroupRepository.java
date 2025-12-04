package splitwise.repositories;

import splitwise.models.Group;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Group persistence operations.
 */
public interface GroupRepository {
    
    /**
     * Save a group (create or update).
     */
    Group save(Group group);
    
    /**
     * Find a group by ID.
     */
    Optional<Group> findById(String groupId);
    
    /**
     * Get all groups.
     */
    List<Group> findAll();
    
    /**
     * Find all groups that a user belongs to.
     */
    List<Group> findByMemberId(String userId);
    
    /**
     * Delete a group by ID.
     */
    void deleteById(String groupId);
    
    /**
     * Check if a group exists by ID.
     */
    boolean existsById(String groupId);
}



