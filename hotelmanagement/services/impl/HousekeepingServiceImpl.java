package hotelmanagement.services.impl;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.exceptions.RoomException;
import hotelmanagement.models.HousekeepingTask;
import hotelmanagement.models.Room;
import hotelmanagement.repositories.RoomRepository;
import hotelmanagement.services.HousekeepingService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of HousekeepingService
 */
public class HousekeepingServiceImpl implements HousekeepingService {
    
    private final RoomRepository roomRepository;
    private final Map<String, HousekeepingTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, List<String>> roomTasks = new ConcurrentHashMap<>();
    
    public HousekeepingServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    
    @Override
    public HousekeepingTask createCleaningTask(Room room, String priority) {
        HousekeepingTask task = new HousekeepingTask(
            room, 
            HousekeepingTask.TaskType.CLEANING, 
            priority
        );
        saveTask(task);
        return task;
    }
    
    @Override
    public HousekeepingTask createMaintenanceTask(Room room, String issue, String priority) {
        HousekeepingTask task = new HousekeepingTask(
            room,
            HousekeepingTask.TaskType.MAINTENANCE,
            priority
        );
        task.setNotes(issue);
        
        // Mark room for maintenance
        room.transitionTo(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
        
        saveTask(task);
        return task;
    }
    
    @Override
    public Optional<HousekeepingTask> getTask(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }
    
    @Override
    public List<HousekeepingTask> getPendingTasks() {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == HousekeepingTask.TaskStatus.PENDING)
            .sorted(Comparator.comparing(HousekeepingTask::getPriority)
                .thenComparing(HousekeepingTask::getCreatedAt))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<HousekeepingTask> getPendingTasksByPriority(String priority) {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == HousekeepingTask.TaskStatus.PENDING)
            .filter(t -> t.getPriority().equalsIgnoreCase(priority))
            .sorted(Comparator.comparing(HousekeepingTask::getCreatedAt))
            .collect(Collectors.toList());
    }
    
    @Override
    public void assignTask(String taskId, String staffName) {
        HousekeepingTask task = tasks.get(taskId);
        if (task != null) {
            task.assignTo(staffName);
        }
    }
    
    @Override
    public void startTask(String taskId) {
        HousekeepingTask task = tasks.get(taskId);
        if (task != null) {
            task.start();
        }
    }
    
    @Override
    public void completeTask(String taskId) {
        HousekeepingTask task = tasks.get(taskId);
        if (task != null) {
            task.complete();
            
            // If it was a cleaning task, mark room as available
            if (task.getTaskType() == HousekeepingTask.TaskType.CLEANING) {
                Room room = task.getRoom();
                if (room.getStatus() == RoomStatus.CLEANING) {
                    room.markClean();
                    roomRepository.save(room);
                }
            }
            
            // If it was a maintenance task, check if room can be made available
            if (task.getTaskType() == HousekeepingTask.TaskType.MAINTENANCE) {
                Room room = task.getRoom();
                // Create a cleaning task after maintenance
                createCleaningTask(room, "MEDIUM");
            }
        }
    }
    
    @Override
    public void cancelTask(String taskId) {
        HousekeepingTask task = tasks.get(taskId);
        if (task != null) {
            task.cancel();
        }
    }
    
    @Override
    public List<HousekeepingTask> getTasksForRoom(String roomId) {
        List<String> taskIds = roomTasks.getOrDefault(roomId, Collections.emptyList());
        return taskIds.stream()
            .map(tasks::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    @Override
    public void markRoomClean(String roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> RoomException.roomNotFound(roomId));
        
        if (room.getStatus() == RoomStatus.CLEANING) {
            room.markClean();
            roomRepository.save(room);
            
            System.out.println("✅ Room " + room.getRoomNumber() + " marked as clean and available");
        }
    }
    
    private void saveTask(HousekeepingTask task) {
        tasks.put(task.getId(), task);
        
        // Index by room
        String roomId = task.getRoom().getId();
        roomTasks.computeIfAbsent(roomId, k -> new ArrayList<>()).add(task.getId());
    }
}



