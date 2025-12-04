package taskmanagement.strategies.search;

import taskmanagement.models.Task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Criteria for filtering tasks by assignee.
 */
public class AssigneeSearchCriteria implements SearchCriteria {
    
    private final Set<String> userIds;
    private final boolean includeUnassigned;

    /**
     * Creates criteria for tasks assigned to a specific user.
     */
    public AssigneeSearchCriteria(String userId) {
        this(userId, false);
    }

    /**
     * Creates criteria for tasks assigned to a specific user.
     * @param includeUnassigned if true, also matches unassigned tasks
     */
    public AssigneeSearchCriteria(String userId, boolean includeUnassigned) {
        this.userIds = new HashSet<>();
        if (userId != null) {
            this.userIds.add(userId);
        }
        this.includeUnassigned = includeUnassigned;
    }

    /**
     * Creates criteria for tasks assigned to any of the specified users.
     */
    public AssigneeSearchCriteria(String... userIds) {
        this.userIds = new HashSet<>(Arrays.asList(userIds));
        this.userIds.removeIf(Objects::isNull);
        this.includeUnassigned = false;
    }

    /**
     * Creates criteria for unassigned tasks only.
     */
    public static AssigneeSearchCriteria unassigned() {
        AssigneeSearchCriteria criteria = new AssigneeSearchCriteria((String) null, true);
        criteria.userIds.clear();
        return criteria;
    }

    @Override
    public boolean matches(Task task) {
        String assignedTo = task.getAssignedTo();
        
        if (assignedTo == null) {
            return includeUnassigned;
        }
        
        return userIds.contains(assignedTo);
    }

    @Override
    public String getDescription() {
        if (userIds.isEmpty() && includeUnassigned) {
            return "Unassigned tasks";
        }
        
        StringBuilder sb = new StringBuilder("Tasks assigned to: ");
        sb.append(userIds.stream().collect(Collectors.joining(", ")));
        
        if (includeUnassigned) {
            sb.append(" (including unassigned)");
        }
        
        return sb.toString();
    }

    public Set<String> getUserIds() {
        return new HashSet<>(userIds);
    }

    public boolean isIncludeUnassigned() {
        return includeUnassigned;
    }
}



