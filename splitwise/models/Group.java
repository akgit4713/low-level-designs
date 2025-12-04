package splitwise.models;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a group of users who share expenses.
 */
public class Group {
    private final String id;
    private String name;
    private String description;
    private final String creatorId;
    private final Set<String> memberIds;
    private final List<String> expenseIds;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Group(String name, String creatorId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.creatorId = creatorId;
        this.memberIds = new HashSet<>();
        this.memberIds.add(creatorId); // Creator is automatically a member
        this.expenseIds = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCreatorId() {
        return creatorId;
    }
    
    public Set<String> getMemberIds() {
        return Collections.unmodifiableSet(memberIds);
    }
    
    public List<String> getExpenseIds() {
        return Collections.unmodifiableList(expenseIds);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Member management
    public boolean addMember(String userId) {
        boolean added = memberIds.add(userId);
        if (added) {
            this.updatedAt = LocalDateTime.now();
        }
        return added;
    }
    
    public boolean removeMember(String userId) {
        // Cannot remove creator
        if (userId.equals(creatorId)) {
            return false;
        }
        boolean removed = memberIds.remove(userId);
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    public boolean hasMember(String userId) {
        return memberIds.contains(userId);
    }
    
    // Expense management
    public void addExpense(String expenseId) {
        expenseIds.add(expenseId);
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getMemberCount() {
        return memberIds.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", memberCount=" + memberIds.size() +
                '}';
    }
}



