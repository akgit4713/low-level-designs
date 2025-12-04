package splitwise.services.impl;

import splitwise.exceptions.GroupNotFoundException;
import splitwise.exceptions.SplitwiseException;
import splitwise.exceptions.UserNotFoundException;
import splitwise.models.Group;
import splitwise.repositories.GroupRepository;
import splitwise.services.GroupService;
import splitwise.services.UserService;

import java.util.List;
import java.util.Set;

/**
 * Implementation of GroupService.
 */
public class GroupServiceImpl implements GroupService {
    
    private final GroupRepository groupRepository;
    private final UserService userService;
    
    public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }
    
    @Override
    public Group createGroup(String name, String creatorId) {
        // Validate creator exists
        if (!userService.userExists(creatorId)) {
            throw new UserNotFoundException(creatorId);
        }
        
        Group group = new Group(name, creatorId);
        return groupRepository.save(group);
    }
    
    @Override
    public Group getGroup(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
    }
    
    @Override
    public void addUserToGroup(String groupId, String userId) {
        Group group = getGroup(groupId);
        
        // Validate user exists
        if (!userService.userExists(userId)) {
            throw new UserNotFoundException(userId);
        }
        
        if (!group.addMember(userId)) {
            throw new SplitwiseException("User is already a member of the group");
        }
        
        groupRepository.save(group);
    }
    
    @Override
    public void removeUserFromGroup(String groupId, String userId) {
        Group group = getGroup(groupId);
        
        if (!group.removeMember(userId)) {
            throw new SplitwiseException("Cannot remove user. User may be creator or not a member.");
        }
        
        groupRepository.save(group);
    }
    
    @Override
    public Set<String> getGroupMembers(String groupId) {
        Group group = getGroup(groupId);
        return group.getMemberIds();
    }
    
    @Override
    public List<Group> getUserGroups(String userId) {
        return groupRepository.findByMemberId(userId);
    }
    
    @Override
    public Group updateGroup(String groupId, String name, String description) {
        Group group = getGroup(groupId);
        
        if (name != null) {
            group.setName(name);
        }
        if (description != null) {
            group.setDescription(description);
        }
        
        return groupRepository.save(group);
    }
    
    @Override
    public boolean isMember(String groupId, String userId) {
        Group group = getGroup(groupId);
        return group.hasMember(userId);
    }
}



