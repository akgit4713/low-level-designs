package taskmanagement.strategies.search;

import taskmanagement.models.Task;

import java.time.LocalDateTime;

/**
 * Criteria for filtering tasks by due date.
 */
public class DueDateSearchCriteria implements SearchCriteria {
    
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean includeOverdue;
    private final boolean includeNoDueDate;

    private DueDateSearchCriteria(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.includeOverdue = builder.includeOverdue;
        this.includeNoDueDate = builder.includeNoDueDate;
    }

    /**
     * Creates criteria for tasks due before a specific date.
     */
    public static DueDateSearchCriteria before(LocalDateTime date) {
        return builder().endDate(date).build();
    }

    /**
     * Creates criteria for tasks due after a specific date.
     */
    public static DueDateSearchCriteria after(LocalDateTime date) {
        return builder().startDate(date).build();
    }

    /**
     * Creates criteria for tasks due within a date range.
     */
    public static DueDateSearchCriteria between(LocalDateTime start, LocalDateTime end) {
        return builder().startDate(start).endDate(end).build();
    }

    /**
     * Creates criteria for overdue tasks.
     */
    public static DueDateSearchCriteria overdue() {
        return builder().endDate(LocalDateTime.now()).includeOverdue(true).build();
    }

    /**
     * Creates criteria for tasks due today.
     */
    public static DueDateSearchCriteria today() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return between(startOfDay, endOfDay);
    }

    /**
     * Creates criteria for tasks due this week.
     */
    public static DueDateSearchCriteria thisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1).minusSeconds(1);
        return between(startOfWeek, endOfWeek);
    }

    @Override
    public boolean matches(Task task) {
        LocalDateTime dueDate = task.getDueDate();
        
        // Handle tasks without due date
        if (dueDate == null) {
            return includeNoDueDate;
        }
        
        // Check overdue (only for active tasks)
        if (includeOverdue && task.isOverdue()) {
            return true;
        }
        
        // Check date range
        boolean afterStart = startDate == null || 
                            dueDate.isEqual(startDate) || 
                            dueDate.isAfter(startDate);
        boolean beforeEnd = endDate == null || 
                           dueDate.isEqual(endDate) || 
                           dueDate.isBefore(endDate);
        
        return afterStart && beforeEnd;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("Tasks due ");
        
        if (includeOverdue) {
            sb.append("(overdue)");
        } else if (startDate != null && endDate != null) {
            sb.append("between ").append(startDate.toLocalDate())
              .append(" and ").append(endDate.toLocalDate());
        } else if (startDate != null) {
            sb.append("after ").append(startDate.toLocalDate());
        } else if (endDate != null) {
            sb.append("before ").append(endDate.toLocalDate());
        }
        
        if (includeNoDueDate) {
            sb.append(" (including tasks without due date)");
        }
        
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private boolean includeOverdue = false;
        private boolean includeNoDueDate = false;

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder includeOverdue(boolean includeOverdue) {
            this.includeOverdue = includeOverdue;
            return this;
        }

        public Builder includeNoDueDate(boolean includeNoDueDate) {
            this.includeNoDueDate = includeNoDueDate;
            return this;
        }

        public DueDateSearchCriteria build() {
            return new DueDateSearchCriteria(this);
        }
    }
}



