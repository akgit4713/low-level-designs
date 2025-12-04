package linkedin.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Company {
    private final String id;
    private String name;
    private String description;
    private String industry;
    private String website;
    private String logoUrl;
    private String location;
    private String size; // e.g., "1-50", "51-200", "201-500", etc.
    private final List<String> employeeIds;
    private final List<String> followerIds;
    private final LocalDateTime createdAt;
    
    public Company(String name, String industry) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.industry = industry;
        this.employeeIds = new ArrayList<>();
        this.followerIds = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIndustry() { return industry; }
    public String getWebsite() { return website; }
    public String getLogoUrl() { return logoUrl; }
    public String getLocation() { return location; }
    public String getSize() { return size; }
    public List<String> getEmployeeIds() { return new ArrayList<>(employeeIds); }
    public List<String> getFollowerIds() { return new ArrayList<>(followerIds); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setWebsite(String website) { this.website = website; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setLocation(String location) { this.location = location; }
    public void setSize(String size) { this.size = size; }
    
    // Methods
    public void addEmployee(String userId) {
        if (!employeeIds.contains(userId)) {
            employeeIds.add(userId);
        }
    }
    
    public void removeEmployee(String userId) {
        employeeIds.remove(userId);
    }
    
    public void addFollower(String userId) {
        if (!followerIds.contains(userId)) {
            followerIds.add(userId);
        }
    }
    
    public void removeFollower(String userId) {
        followerIds.remove(userId);
    }
    
    public boolean hasEmployee(String userId) {
        return employeeIds.contains(userId);
    }
    
    @Override
    public String toString() {
        return "Company{id='" + id + "', name='" + name + "', industry='" + industry + "'}";
    }
}



