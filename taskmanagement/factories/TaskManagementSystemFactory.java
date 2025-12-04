package taskmanagement.factories;

import taskmanagement.observers.NotificationObserver;
import taskmanagement.observers.TaskHistoryObserver;
import taskmanagement.repositories.impl.InMemoryHistoryRepository;
import taskmanagement.repositories.impl.InMemoryReminderRepository;
import taskmanagement.repositories.impl.InMemoryTaskRepository;
import taskmanagement.repositories.impl.InMemoryUserRepository;
import taskmanagement.services.*;
import taskmanagement.services.impl.*;
import taskmanagement.strategies.notification.ConsoleNotificationStrategy;
import taskmanagement.strategies.notification.EmailNotificationStrategy;
import taskmanagement.strategies.notification.NotificationStrategy;

/**
 * Factory for creating complete TaskManagementSystem instances.
 */
public class TaskManagementSystemFactory {
    
    private TaskManagementSystemFactory() {
        // Utility class
    }
    
    /**
     * Creates a TaskManagementSystem with default configuration.
     * Uses console notifications.
     */
    public static TaskManagementSystem createDefaultSystem() {
        return createSystem(new ConsoleNotificationStrategy());
    }
    
    /**
     * Creates a TaskManagementSystem with email notifications.
     */
    public static TaskManagementSystem createWithEmailNotifications(String smtpServer, String fromAddress) {
        return createSystem(new EmailNotificationStrategy(smtpServer, fromAddress));
    }
    
    /**
     * Creates a TaskManagementSystem with the specified notification strategy.
     */
    public static TaskManagementSystem createSystem(NotificationStrategy notificationStrategy) {
        // Create repositories
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
        InMemoryReminderRepository reminderRepository = new InMemoryReminderRepository();
        InMemoryHistoryRepository historyRepository = new InMemoryHistoryRepository();
        
        // Create services
        UserService userService = new UserServiceImpl(userRepository);
        TaskHistoryService historyService = new TaskHistoryServiceImpl(historyRepository);
        TaskService taskService = new TaskServiceImpl(taskRepository, userService);
        SearchService searchService = new SearchServiceImpl(taskRepository);
        ReminderService reminderService = new ReminderServiceImpl(
                reminderRepository, taskRepository, userService, notificationStrategy);
        
        // Create and register observers
        TaskHistoryObserver historyObserver = new TaskHistoryObserver(historyService);
        NotificationObserver notificationObserver = new NotificationObserver(notificationStrategy);
        
        taskService.registerObserver(historyObserver);
        taskService.registerObserver(notificationObserver);
        
        return new TaskManagementSystem(
                taskService, 
                userService, 
                searchService, 
                reminderService, 
                historyService
        );
    }
    
    /**
     * Container class for all task management system components.
     */
    public static class TaskManagementSystem {
        private final TaskService taskService;
        private final UserService userService;
        private final SearchService searchService;
        private final ReminderService reminderService;
        private final TaskHistoryService historyService;
        
        public TaskManagementSystem(TaskService taskService,
                                    UserService userService,
                                    SearchService searchService,
                                    ReminderService reminderService,
                                    TaskHistoryService historyService) {
            this.taskService = taskService;
            this.userService = userService;
            this.searchService = searchService;
            this.reminderService = reminderService;
            this.historyService = historyService;
        }
        
        public TaskService getTaskService() {
            return taskService;
        }
        
        public UserService getUserService() {
            return userService;
        }
        
        public SearchService getSearchService() {
            return searchService;
        }
        
        public ReminderService getReminderService() {
            return reminderService;
        }
        
        public TaskHistoryService getHistoryService() {
            return historyService;
        }
    }
}



