package linkedin.repositories.impl;

import linkedin.models.User;
import linkedin.repositories.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void delete(String id) {
        users.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    @Override
    public List<User> findByNameContaining(String name) {
        String lowerName = name.toLowerCase();
        return users.values().stream()
                .filter(u -> u.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findBySkill(String skillName) {
        String lowerSkill = skillName.toLowerCase();
        return users.values().stream()
                .filter(u -> u.getProfile() != null &&
                        u.getProfile().getSkills().stream()
                                .anyMatch(s -> s.getName().toLowerCase().contains(lowerSkill)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByLocation(String location) {
        String lowerLocation = location.toLowerCase();
        return users.values().stream()
                .filter(u -> u.getProfile() != null &&
                        u.getProfile().getLocation() != null &&
                        u.getProfile().getLocation().toLowerCase().contains(lowerLocation))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findByIndustry(String industry) {
        String lowerIndustry = industry.toLowerCase();
        return users.values().stream()
                .filter(u -> u.getProfile() != null &&
                        u.getProfile().getIndustry() != null &&
                        u.getProfile().getIndustry().toLowerCase().contains(lowerIndustry))
                .collect(Collectors.toList());
    }
}



