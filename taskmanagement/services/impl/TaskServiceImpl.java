package taskmanagement.services.impl;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.exceptions.TaskException;
import taskmanagement.exceptions.UserException;
import taskmanagement.models.Task;
import taskmanagement.models.User;
import taskmanagement.observers.TaskObserver;
import taskmanagement.repositories.TaskRepository;
import taskmanagement.services.TaskService;
import taskmanagement.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TaskService.
 */
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final List<TaskObserver> observers = new ArrayList<>();
    private final Object observerLock = new Object();

    public TaskServiceImpl(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public Task createTask(String title, String description, TaskPriority priority,
                           LocalDateTime dueDate, String createdBy) {
        // Validate creator exists
        User creator = userService.getUserById(createdBy)
                .orElseThrow(() -> UserException.notFound(createdBy));
        
        String id = "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Task task = Task.builder()
                .id(id)
                .title(title)
                .description(description)
                .priority(priority != null ? priority : TaskPriority.MEDIUM)
                .dueDate(dueDate)
                .createdBy(createdBy)
                .build();
        
        Task savedTask = taskRepository.save(task);
        notifyTaskCreated(savedTask, creator);
        
        return savedTask;
    }

    @Override
    public Optional<Task> getTaskById(String taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksCreatedBy(String userId) {
        return taskRepository.findByCreatedBy(userId);
    }

    @Override
    public List<Task> getTasksAssignedTo(String userId) {
        return taskRepository.findByAssignedTo(userId);
    }

    @Override
    public Task updateTitle(String taskId, String newTitle, String updatedBy) {
        Task task = getTaskOrThrow(taskId);
        User updater = getUserOrNull(updatedBy);
        
        String previousTitle = task.getTitle();
        task.setTitle(newTitle);
        taskRepository.save(task);
        
        notifyTaskUpdated(task, "title", previousTitle, newTitle, updater);
        return task;
    }

    @Override
    public Task updateDescription(String taskId, String newDescription, String updatedBy) {
        Task task = getTaskOrThrow(taskId);
        User updater = getUserOrNull(updatedBy);
        
        String previousDescription = task.getDescription();
        task.setDescription(newDescription);
        taskRepository.save(task);
        
        notifyTaskUpdated(task, "description", previousDescription, newDescription, updater);
        return task;
    }

    @Override
    public Task updatePriority(String taskId, TaskPriority newPriority, String updatedBy) {
        Task task = getTaskOrThrow(taskId);
        User updater = getUserOrNull(updatedBy);
        
        TaskPriority previousPriority = task.getPriority();
        task.setPriority(newPriority);
        taskRepository.save(task);
        
        notifyTaskUpdated(task, "priority", 
                previousPriority.getDisplayName(), 
                newPriority.getDisplayName(), 
                updater);
        return task;
    }

    @Override
    public Task updateDueDate(String taskId, LocalDateTime newDueDate, String updatedBy) {
        Task task = getTaskOrThrow(taskId);
        User updater = getUserOrNull(updatedBy);
        
        LocalDateTime previousDueDate = task.getDueDate();
        task.setDueDate(newDueDate);
        taskRepository.save(task);
        
        notifyTaskUpdated(task, "dueDate",
                previousDueDate != null ? previousDueDate.toString() : null,
                newDueDate != null ? newDueDate.toString() : null,
                updater);
        return task;
    }

    @Override
    public Task updateStatus(String taskId, TaskStatus newStatus, String updatedBy) {
        Task task = getTaskOrThrow(taskId);
        User updater = getUserOrNull(updatedBy);
        
        TaskStatus previousStatus = task.getStatus();
        task.updateStatus(newStatus);
        taskRepository.save(task);
        
        notifyTaskStatusChanged(task, previousStatus, newStatus, updater);
        
        if (newStatus == TaskStatus.COMPLETED) {
            notifyTaskCompleted(task, updater);
        }
        
        return task;
    }

    @Override
    public Task assignTask(String taskId, String assigneeId, String assignedBy) {
        Task task = getTaskOrThrow(taskId);
        User assigner = getUserOrNull(assignedBy);
        
        // Validate assignee exists
        User newAssignee = userService.getUserById(assigneeId)
                .orElseThrow(() -> UserException.notFound(assigneeId));
        
        User previousAssignee = null;
        if (task.getAssignedTo() != null) {
            previousAssignee = userService.getUserById(task.getAssignedTo()).orElse(null);
        }
        
        task.assignTo(assigneeId);
        taskRepository.save(task);
        
        notifyTaskAssigned(task, previousAssignee, newAssignee, assigner);
        return task;
    }

    @Override
    public Task unassignTask(String taskId, String unassignedBy) {
        Task task = getTaskOrThrow(taskId);
        User unassigner = getUserOrNull(unassignedBy);
        
        User previousAssignee = null;
        if (task.getAssignedTo() != null) {
            previousAssignee = userService.getUserById(task.getAssignedTo()).orElse(null);
        }
        
        task.unassign();
        taskRepository.save(task);
        
        notifyTaskAssigned(task, previousAssignee, null, unassigner);
        return task;
    }

    @Override
    public Task completeTask(String taskId, String completedBy) {
        return updateStatus(taskId, TaskStatus.COMPLETED, completedBy);
    }

    @Override
    public Task cancelTask(String taskId, String cancelledBy) {
        return updateStatus(taskId, TaskStatus.CANCELLED, cancelledBy);
    }

    @Override
    public boolean deleteTask(String taskId, String deletedBy) {
        Task task = getTaskOrThrow(taskId);
        User deleter = getUserOrNull(deletedBy);
        
        boolean deleted = taskRepository.delete(taskId);
        if (deleted) {
            notifyTaskDeleted(task, deleter);
        }
        return deleted;
    }

    @Override
    public void registerObserver(TaskObserver observer) {
        synchronized (observerLock) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }
    }

    @Override
    public void unregisterObserver(TaskObserver observer) {
        synchronized (observerLock) {
            observers.remove(observer);
        }
    }
    
    // Helper methods
    private Task getTaskOrThrow(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> TaskException.notFound(taskId));
    }
    
    private User getUserOrNull(String userId) {
        if (userId == null) return null;
        return userService.getUserById(userId).orElse(null);
    }
    
    // Observer notification methods
    private void notifyTaskCreated(Task task, User creator) {
        synchronized (observerLock) {
            for (TaskObserver observer : observers) {
                try {
                    observer.onTaskCreated(task, creator);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifyTaskUpdated(Task task, String fieldName, 
                                   String previousValue, String newValue, User updater) {
        synchronized (observerLock) {
            for (TaskObserver observer : observers) {
                try {
                    observer.onTaskUpdated(task, fieldName, previousValue, newValue, updater);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifyTaskDeleted(Task task, User deleter) {
        synchronized (observerLock) {
            for (TaskObserver observer : observers) {
                try {
                    observer.onTaskDeleted(task, deleter);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifyTaskAssigned(Task task, User previousAssignee, 
                                    User newAssignee, User assigner) {
        synchronized (observerLock) {
            for (TaskObserver observer : observers) {
                try {
                    observer.onTaskAssigned(task, previousAssignee, newAssignee, assigner);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifyTaskStatusChanged(Task task, TaskStatus previousStatus, 
                                         TaskStatus newStatus, User changer) {
        synchronized (observerLock) {
            for (TaskObserver observer : observers) {
                try {
                    observer.onTaskStatusChanged(task, previousStatus, newStatus, changer);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
    
    private void notifyTaskCompleted(Task task, User completer) {
        synchronized (observerLock) {
            for (TaskObserver observer : observers) {
                try {
                    observer.onTaskCompleted(task, completer);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
}



