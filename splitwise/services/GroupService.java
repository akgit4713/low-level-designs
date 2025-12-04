package splitwise.services;

import splitwise.models.Group;
import splitwise.models.User;

import java.util.List;
import java.util.Set;

/**
 * Service interface for group management operations.
 */
public interface GroupService {
    
    /**
     * Create a new group.
     */
    Group createGroup(String name, String creatorId);
    
    /**
     * Get group by ID.
     */
    Group getGroup(String groupId);
    
    /**
     * Add a user to a group.
     */
    void addUserToGroup(String groupId, String userId);
    
    /**
     * Remove a user from a group.
     */
    void removeUserFromGroup(String groupId, String userId);
    
    /**
     * Get all members of a group.
     */
    Set<String> getGroupMembers(String groupId);
    
    /**
     * Get all groups a user belongs to.
     */
    List<Group> getUserGroups(String userId);
    
    /**
     * Update group details.
     */
    Group updateGroup(String groupId, String name, String description);
    
    /**
     * Check if user is a member of the group.
     */
    boolean isMember(String groupId, String userId);
}



