package splitwise.repositories.impl;

import splitwise.models.Group;
import splitwise.repositories.GroupRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of GroupRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryGroupRepository implements GroupRepository {
    
    private final Map<String, Group> groups = new ConcurrentHashMap<>();
    
    @Override
    public Group save(Group group) {
        groups.put(group.getId(), group);
        return group;
    }
    
    @Override
    public Optional<Group> findById(String groupId) {
        return Optional.ofNullable(groups.get(groupId));
    }
    
    @Override
    public List<Group> findAll() {
        return new ArrayList<>(groups.values());
    }
    
    @Override
    public List<Group> findByMemberId(String userId) {
        return groups.values().stream()
                .filter(group -> group.getMemberIds().contains(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(String groupId) {
        groups.remove(groupId);
    }
    
    @Override
    public boolean existsById(String groupId) {
        return groups.containsKey(groupId);
    }
}



