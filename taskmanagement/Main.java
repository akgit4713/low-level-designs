package taskmanagement;

import taskmanagement.enums.HistoryAction;
import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.factories.TaskManagementSystemFactory;
import taskmanagement.factories.TaskManagementSystemFactory.TaskManagementSystem;
import taskmanagement.models.*;
import taskmanagement.services.*;
import taskmanagement.strategies.search.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Main class demonstrating the Task Management System.
 */
public class Main {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String LINE = "─".repeat(70);
    private static final String DOUBLE_LINE = "═".repeat(70);
    
    private static TaskManagementSystem system;
    private static TaskService taskService;
    private static UserService userService;
    private static SearchService searchService;
    private static ReminderService reminderService;
    private static TaskHistoryService historyService;
    
    public static void main(String[] args) {
        System.out.println(DOUBLE_LINE);
        System.out.println("      TASK MANAGEMENT SYSTEM - Demo");
        System.out.println(DOUBLE_LINE);
        System.out.println();
        
        // Initialize system
        initializeSystem();
        
        // Create sample data
        createSampleData();
        
        // Run interactive menu
        runInteractiveMenu();
    }
    
    private static void initializeSystem() {
        System.out.println("Initializing Task Management System...");
        system = TaskManagementSystemFactory.createDefaultSystem();
        taskService = system.getTaskService();
        userService = system.getUserService();
        searchService = system.getSearchService();
        reminderService = system.getReminderService();
        historyService = system.getHistoryService();
        System.out.println("System initialized successfully!");
        System.out.println();
    }
    
    private static void createSampleData() {
        System.out.println("Creating sample data...");
        System.out.println(LINE);
        
        // Create users
        User alice = userService.createUser("alice", "alice@company.com", "Alice Johnson");
        User bob = userService.createUser("bob", "bob@company.com", "Bob Smith");
        User carol = userService.createUser("carol", "carol@company.com", "Carol Williams");
        
        System.out.println("Created users: " + alice.getUsername() + ", " + 
                          bob.getUsername() + ", " + carol.getUsername());
        
        // Create tasks
        Task task1 = taskService.createTask(
                "Complete LLD Documentation",
                "Write comprehensive low-level design for task management system",
                TaskPriority.HIGH,
                LocalDateTime.now().plusDays(7),
                alice.getId()
        );
        
        Task task2 = taskService.createTask(
                "Review Code Changes",
                "Review pull request #234 for the new feature",
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(2),
                bob.getId()
        );
        
        Task task3 = taskService.createTask(
                "Fix Production Bug",
                "Critical: Users unable to login during peak hours",
                TaskPriority.CRITICAL,
                LocalDateTime.now().plusHours(4),
                carol.getId()
        );
        
        Task task4 = taskService.createTask(
                "Update Dependencies",
                "Update all npm packages to latest versions",
                TaskPriority.LOW,
                LocalDateTime.now().plusDays(14),
                alice.getId()
        );
        
        Task task5 = taskService.createTask(
                "Write Unit Tests",
                "Add unit tests for the payment module",
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(5),
                bob.getId()
        );
        
        System.out.println("Created 5 tasks");
        
        // Assign tasks
        taskService.assignTask(task1.getId(), bob.getId(), alice.getId());
        taskService.assignTask(task3.getId(), alice.getId(), carol.getId());
        taskService.assignTask(task5.getId(), carol.getId(), bob.getId());
        
        System.out.println("Assigned tasks to team members");
        
        // Update task statuses
        taskService.updateStatus(task3.getId(), TaskStatus.IN_PROGRESS, alice.getId());
        taskService.updateStatus(task2.getId(), TaskStatus.IN_PROGRESS, bob.getId());
        
        System.out.println("Updated task statuses");
        
        // Create reminders
        reminderService.createReminder(
                task1.getId(),
                bob.getId(),
                LocalDateTime.now().plusDays(6),
                "Documentation deadline approaching!"
        );
        
        reminderService.createReminder(
                task3.getId(),
                alice.getId(),
                LocalDateTime.now().plusHours(2),
                "Bug fix deadline in 2 hours!"
        );
        
        System.out.println("Created reminders for tasks");
        System.out.println(LINE);
        System.out.println();
    }
    
    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            switch (choice) {
                case "1" -> displayAllTasks();
                case "2" -> displayAllUsers();
                case "3" -> searchByPriority(scanner);
                case "4" -> searchByStatus(scanner);
                case "5" -> searchByAssignee(scanner);
                case "6" -> searchDueToday();
                case "7" -> searchOverdueTasks();
                case "8" -> createNewTask(scanner);
                case "9" -> updateTaskStatus(scanner);
                case "10" -> assignTask(scanner);
                case "11" -> completeTask(scanner);
                case "12" -> viewTaskHistory(scanner);
                case "13" -> viewRecentHistory();
                case "14" -> demonstrateCompositeSearch();
                case "15" -> triggerReminders();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
            
            System.out.println();
        }
        
        System.out.println("Thank you for using Task Management System!");
        scanner.close();
    }
    
    private static void printMenu() {
        System.out.println(DOUBLE_LINE);
        System.out.println("                    MAIN MENU");
        System.out.println(DOUBLE_LINE);
        System.out.println("  VIEW");
        System.out.println("    1.  View All Tasks");
        System.out.println("    2.  View All Users");
        System.out.println();
        System.out.println("  SEARCH");
        System.out.println("    3.  Search by Priority");
        System.out.println("    4.  Search by Status");
        System.out.println("    5.  Search by Assignee");
        System.out.println("    6.  Search Due Today");
        System.out.println("    7.  Search Overdue Tasks");
        System.out.println();
        System.out.println("  MANAGE");
        System.out.println("    8.  Create New Task");
        System.out.println("    9.  Update Task Status");
        System.out.println("    10. Assign Task");
        System.out.println("    11. Complete Task");
        System.out.println();
        System.out.println("  HISTORY");
        System.out.println("    12. View Task History");
        System.out.println("    13. View Recent Activity");
        System.out.println();
        System.out.println("  ADVANCED");
        System.out.println("    14. Demo: Composite Search");
        System.out.println("    15. Trigger Due Reminders");
        System.out.println();
        System.out.println("    0.  Exit");
        System.out.println(LINE);
    }
    
    private static void displayAllTasks() {
        System.out.println("ALL TASKS");
        System.out.println(LINE);
        
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        
        for (Task task : tasks) {
            printTaskSummary(task);
        }
        
        System.out.println(LINE);
        System.out.println("Total: " + tasks.size() + " task(s)");
    }
    
    private static void displayAllUsers() {
        System.out.println("ALL USERS");
        System.out.println(LINE);
        
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.printf("  [%s] %s (%s) - %s%n", 
                    user.getId(), user.getName(), user.getUsername(), user.getEmail());
        }
        
        System.out.println(LINE);
        System.out.println("Total: " + users.size() + " user(s)");
    }
    
    private static void searchByPriority(Scanner scanner) {
        System.out.println("SEARCH BY PRIORITY");
        System.out.println("1. LOW  2. MEDIUM  3. HIGH  4. CRITICAL");
        System.out.print("Enter priority (1-4): ");
        
        try {
            int level = Integer.parseInt(scanner.nextLine().trim());
            TaskPriority priority = TaskPriority.fromLevel(level);
            
            List<Task> tasks = searchService.searchByPriority(priority);
            System.out.println();
            System.out.println("Tasks with priority " + priority.getDisplayName() + ":");
            System.out.println(LINE);
            
            if (tasks.isEmpty()) {
                System.out.println("No tasks found.");
            } else {
                tasks.forEach(Main::printTaskSummary);
            }
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
    
    private static void searchByStatus(Scanner scanner) {
        System.out.println("SEARCH BY STATUS");
        System.out.println("1. PENDING  2. IN_PROGRESS  3. COMPLETED  4. CANCELLED");
        System.out.print("Enter status (1-4): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            TaskStatus status = switch (choice) {
                case 1 -> TaskStatus.PENDING;
                case 2 -> TaskStatus.IN_PROGRESS;
                case 3 -> TaskStatus.COMPLETED;
                case 4 -> TaskStatus.CANCELLED;
                default -> throw new IllegalArgumentException("Invalid choice");
            };
            
            List<Task> tasks = searchService.searchByStatus(status);
            System.out.println();
            System.out.println("Tasks with status " + status.getDisplayName() + ":");
            System.out.println(LINE);
            
            if (tasks.isEmpty()) {
                System.out.println("No tasks found.");
            } else {
                tasks.forEach(Main::printTaskSummary);
            }
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
    
    private static void searchByAssignee(Scanner scanner) {
        System.out.println("SEARCH BY ASSIGNEE");
        displayAllUsers();
        System.out.print("Enter user ID: ");
        
        String userId = scanner.nextLine().trim();
        List<Task> tasks = searchService.searchByAssignee(userId);
        
        System.out.println();
        System.out.println("Tasks assigned to " + userId + ":");
        System.out.println(LINE);
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            tasks.forEach(Main::printTaskSummary);
        }
    }
    
    private static void searchDueToday() {
        System.out.println("TASKS DUE TODAY");
        System.out.println(LINE);
        
        List<Task> tasks = searchService.searchDueToday();
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks due today.");
        } else {
            tasks.forEach(Main::printTaskSummary);
        }
    }
    
    private static void searchOverdueTasks() {
        System.out.println("OVERDUE TASKS");
        System.out.println(LINE);
        
        List<Task> tasks = searchService.searchOverdue();
        
        if (tasks.isEmpty()) {
            System.out.println("No overdue tasks. Great job!");
        } else {
            tasks.forEach(Main::printTaskSummary);
            System.out.println(LINE);
            System.out.println("⚠️  " + tasks.size() + " overdue task(s)!");
        }
    }
    
    private static void createNewTask(Scanner scanner) {
        System.out.println("CREATE NEW TASK");
        System.out.println(LINE);
        
        displayAllUsers();
        System.out.print("Enter creator user ID: ");
        String creatorId = scanner.nextLine().trim();
        
        System.out.print("Enter task title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine().trim();
        
        System.out.println("Priority: 1. LOW  2. MEDIUM  3. HIGH  4. CRITICAL");
        System.out.print("Enter priority (1-4, default 2): ");
        String priorityInput = scanner.nextLine().trim();
        TaskPriority priority = priorityInput.isEmpty() ? TaskPriority.MEDIUM 
                : TaskPriority.fromLevel(Integer.parseInt(priorityInput));
        
        System.out.print("Due in how many days? (default 7): ");
        String daysInput = scanner.nextLine().trim();
        int days = daysInput.isEmpty() ? 7 : Integer.parseInt(daysInput);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(days);
        
        try {
            Task task = taskService.createTask(title, description, priority, dueDate, creatorId);
            System.out.println();
            System.out.println("✅ Task created successfully!");
            printTaskDetails(task);
        } catch (Exception e) {
            System.out.println("❌ Error creating task: " + e.getMessage());
        }
    }
    
    private static void updateTaskStatus(Scanner scanner) {
        System.out.println("UPDATE TASK STATUS");
        System.out.println(LINE);
        
        displayAllTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine().trim();
        
        System.out.println("New Status: 1. PENDING  2. IN_PROGRESS  3. COMPLETED  4. CANCELLED");
        System.out.print("Enter new status (1-4): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            TaskStatus newStatus = switch (choice) {
                case 1 -> TaskStatus.PENDING;
                case 2 -> TaskStatus.IN_PROGRESS;
                case 3 -> TaskStatus.COMPLETED;
                case 4 -> TaskStatus.CANCELLED;
                default -> throw new IllegalArgumentException("Invalid choice");
            };
            
            displayAllUsers();
            System.out.print("Enter your user ID: ");
            String userId = scanner.nextLine().trim();
            
            Task task = taskService.updateStatus(taskId, newStatus, userId);
            System.out.println();
            System.out.println("✅ Task status updated!");
            printTaskDetails(task);
        } catch (Exception e) {
            System.out.println("❌ Error updating status: " + e.getMessage());
        }
    }
    
    private static void assignTask(Scanner scanner) {
        System.out.println("ASSIGN TASK");
        System.out.println(LINE);
        
        displayAllTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine().trim();
        
        displayAllUsers();
        System.out.print("Enter assignee user ID: ");
        String assigneeId = scanner.nextLine().trim();
        
        System.out.print("Enter your user ID (assigner): ");
        String assignerId = scanner.nextLine().trim();
        
        try {
            Task task = taskService.assignTask(taskId, assigneeId, assignerId);
            System.out.println();
            System.out.println("✅ Task assigned successfully!");
            printTaskDetails(task);
        } catch (Exception e) {
            System.out.println("❌ Error assigning task: " + e.getMessage());
        }
    }
    
    private static void completeTask(Scanner scanner) {
        System.out.println("COMPLETE TASK");
        System.out.println(LINE);
        
        // Show only active tasks
        List<Task> activeTasks = searchService.searchActiveTasks();
        if (activeTasks.isEmpty()) {
            System.out.println("No active tasks to complete.");
            return;
        }
        
        System.out.println("Active Tasks:");
        activeTasks.forEach(Main::printTaskSummary);
        
        System.out.print("Enter task ID to complete: ");
        String taskId = scanner.nextLine().trim();
        
        displayAllUsers();
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine().trim();
        
        try {
            Task task = taskService.completeTask(taskId, userId);
            System.out.println();
            System.out.println("✅ Task completed!");
            printTaskDetails(task);
        } catch (Exception e) {
            System.out.println("❌ Error completing task: " + e.getMessage());
        }
    }
    
    private static void viewTaskHistory(Scanner scanner) {
        System.out.println("VIEW TASK HISTORY");
        System.out.println(LINE);
        
        displayAllTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine().trim();
        
        List<TaskHistory> history = historyService.getHistoryForTask(taskId);
        
        if (history.isEmpty()) {
            System.out.println("No history found for this task.");
            return;
        }
        
        System.out.println();
        System.out.println("History for task " + taskId + ":");
        System.out.println(LINE);
        
        for (TaskHistory entry : history) {
            System.out.printf("  [%s] %s%n", 
                    entry.getTimestamp().format(FORMATTER),
                    entry.getDescription());
            if (entry.getChangedBy() != null) {
                System.out.println("        Changed by: " + entry.getChangedBy());
            }
        }
    }
    
    private static void viewRecentHistory() {
        System.out.println("RECENT ACTIVITY");
        System.out.println(LINE);
        
        List<TaskHistory> history = historyService.getRecentHistory(10);
        
        if (history.isEmpty()) {
            System.out.println("No recent activity.");
            return;
        }
        
        for (TaskHistory entry : history) {
            System.out.printf("  [%s] Task %s - %s%n", 
                    entry.getTimestamp().format(FORMATTER),
                    entry.getTaskId(),
                    entry.getDescription());
        }
    }
    
    private static void demonstrateCompositeSearch() {
        System.out.println("COMPOSITE SEARCH DEMO");
        System.out.println(LINE);
        
        // High priority AND in progress
        System.out.println("1. Finding HIGH priority tasks IN_PROGRESS:");
        CompositeSearchCriteria criteria1 = CompositeSearchCriteria.and(
                new PrioritySearchCriteria(TaskPriority.HIGH, true),
                new StatusSearchCriteria(TaskStatus.IN_PROGRESS)
        );
        
        List<Task> results1 = searchService.search(criteria1);
        System.out.println("   Criteria: " + criteria1.getDescription());
        System.out.println("   Found: " + results1.size() + " task(s)");
        results1.forEach(t -> System.out.println("   - " + t.getTitle()));
        
        System.out.println();
        
        // Active tasks (pending OR in progress)
        System.out.println("2. Finding all ACTIVE tasks (Pending OR In Progress):");
        CompositeSearchCriteria criteria2 = CompositeSearchCriteria.or(
                new StatusSearchCriteria(TaskStatus.PENDING),
                new StatusSearchCriteria(TaskStatus.IN_PROGRESS)
        );
        
        List<Task> results2 = searchService.search(criteria2);
        System.out.println("   Criteria: " + criteria2.getDescription());
        System.out.println("   Found: " + results2.size() + " task(s)");
        results2.forEach(t -> System.out.println("   - " + t.getTitle() + " [" + t.getStatus() + "]"));
        
        System.out.println();
        
        // Unassigned tasks
        System.out.println("3. Finding UNASSIGNED tasks:");
        List<Task> unassigned = searchService.searchUnassigned();
        System.out.println("   Found: " + unassigned.size() + " task(s)");
        unassigned.forEach(t -> System.out.println("   - " + t.getTitle()));
    }
    
    private static void triggerReminders() {
        System.out.println("TRIGGERING DUE REMINDERS");
        System.out.println(LINE);
        
        List<Reminder> triggered = reminderService.checkAndTriggerReminders();
        
        if (triggered.isEmpty()) {
            System.out.println("No reminders due at this time.");
        } else {
            System.out.println("Triggered " + triggered.size() + " reminder(s).");
        }
        
        System.out.println();
        System.out.println("Pending reminders:");
        List<Reminder> pending = reminderService.getPendingReminders();
        if (pending.isEmpty()) {
            System.out.println("  No pending reminders.");
        } else {
            for (Reminder r : pending) {
                System.out.printf("  - Task %s at %s: %s%n",
                        r.getTaskId(),
                        r.getReminderTime().format(FORMATTER),
                        r.getMessage());
            }
        }
    }
    
    private static void printTaskSummary(Task task) {
        String assignee = task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned";
        String dueDate = task.getDueDate() != null ? task.getDueDate().format(FORMATTER) : "No due date";
        String overdue = task.isOverdue() ? " ⚠️OVERDUE" : "";
        
        System.out.printf("  [%s] %s%n", task.getId(), task.getTitle());
        System.out.printf("        Priority: %-8s | Status: %-12s | Assignee: %s%n",
                task.getPriority().getDisplayName(),
                task.getStatus().getDisplayName(),
                assignee);
        System.out.printf("        Due: %s%s%n", dueDate, overdue);
        System.out.println();
    }
    
    private static void printTaskDetails(Task task) {
        System.out.println(LINE);
        System.out.println("Task ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            System.out.println("Description: " + task.getDescription());
        }
        System.out.println("Priority: " + task.getPriority().getDisplayName());
        System.out.println("Status: " + task.getStatus().getDisplayName());
        System.out.println("Created by: " + task.getCreatedBy());
        System.out.println("Assigned to: " + (task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned"));
        System.out.println("Created at: " + task.getCreatedAt().format(FORMATTER));
        if (task.getDueDate() != null) {
            System.out.println("Due date: " + task.getDueDate().format(FORMATTER));
        }
        if (task.getCompletedAt() != null) {
            System.out.println("Completed at: " + task.getCompletedAt().format(FORMATTER));
        }
        System.out.println(LINE);
    }
}



