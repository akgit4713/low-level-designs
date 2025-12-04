package trafficsignal.services;

import trafficsignal.commands.CommandInvoker;
import trafficsignal.commands.EmergencyOverrideCommand;
import trafficsignal.enums.Direction;
import trafficsignal.enums.EmergencyType;
import trafficsignal.models.EmergencyVehicle;
import trafficsignal.models.Intersection;
import trafficsignal.observers.EmergencyObserver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.*;

/**
 * Handles emergency vehicle detection and signal override.
 */
public class EmergencyHandler {
    
    private final Intersection intersection;
    private final SignalController signalController;
    private final CommandInvoker commandInvoker;
    private final List<EmergencyObserver> observers;
    private final PriorityQueue<EmergencyVehicle> emergencyQueue;
    private final ScheduledExecutorService scheduler;
    private EmergencyVehicle currentEmergency;
    private boolean isEmergencyMode;

    public EmergencyHandler(Intersection intersection, SignalController signalController) {
        this.intersection = intersection;
        this.signalController = signalController;
        this.commandInvoker = new CommandInvoker();
        this.observers = new ArrayList<>();
        // Priority queue: lower priority level = higher priority
        this.emergencyQueue = new PriorityQueue<>(
            Comparator.comparingInt(EmergencyVehicle::getPriority)
        );
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.isEmergencyMode = false;
    }

    /**
     * Adds an emergency observer.
     */
    public void addObserver(EmergencyObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an emergency observer.
     */
    public void removeObserver(EmergencyObserver observer) {
        observers.remove(observer);
    }

    /**
     * Detects an emergency vehicle approaching the intersection.
     */
    public void detectEmergency(EmergencyType type, Direction approachingFrom) {
        EmergencyVehicle vehicle = new EmergencyVehicle(type, approachingFrom);
        intersection.addEmergency(vehicle);
        emergencyQueue.offer(vehicle);

        // Notify observers
        notifyEmergencyDetected(vehicle);

        // Handle if not already in emergency mode or if higher priority
        if (!isEmergencyMode) {
            activateEmergencyMode(vehicle);
        } else if (vehicle.getPriority() < currentEmergency.getPriority()) {
            // Higher priority emergency - switch to it
            handleHigherPriorityEmergency(vehicle);
        }
    }

    /**
     * Signals that an emergency vehicle has cleared the intersection.
     */
    public void clearEmergency(EmergencyVehicle vehicle) {
        vehicle.markCleared();
        emergencyQueue.remove(vehicle);
        intersection.removeEmergency(vehicle);

        // Notify observers
        notifyEmergencyCleared(vehicle);

        if (vehicle == currentEmergency) {
            // Current emergency cleared - check for pending emergencies
            if (!emergencyQueue.isEmpty()) {
                // Handle next emergency in queue
                EmergencyVehicle next = emergencyQueue.poll();
                activateEmergencyMode(next);
            } else {
                // No more emergencies - resume normal operation
                deactivateEmergencyMode();
            }
        }
    }

    /**
     * Manually clears the current emergency.
     */
    public void clearCurrentEmergency() {
        if (currentEmergency != null) {
            clearEmergency(currentEmergency);
        }
    }

    /**
     * Clears all active emergencies and resumes normal operation.
     */
    public void clearAllEmergencies() {
        while (!emergencyQueue.isEmpty()) {
            EmergencyVehicle vehicle = emergencyQueue.poll();
            vehicle.markCleared();
            intersection.removeEmergency(vehicle);
            notifyEmergencyCleared(vehicle);
        }
        
        if (isEmergencyMode) {
            deactivateEmergencyMode();
        }
    }

    private void activateEmergencyMode(EmergencyVehicle vehicle) {
        isEmergencyMode = true;
        currentEmergency = vehicle;

        // Pause normal signal control
        signalController.pause();

        // Execute emergency override command
        EmergencyOverrideCommand command = new EmergencyOverrideCommand(intersection, vehicle);
        commandInvoker.executeCommand(command);

        // Notify observers
        notifyEmergencyOverrideActivated();
    }

    private void handleHigherPriorityEmergency(EmergencyVehicle higherPriority) {
        // Undo current emergency override
        commandInvoker.undoLastCommand();

        // Activate for higher priority
        activateEmergencyMode(higherPriority);
    }

    private void deactivateEmergencyMode() {
        isEmergencyMode = false;
        currentEmergency = null;

        // Undo emergency override
        commandInvoker.undoLastCommand();

        // Resume normal signal control
        signalController.resume();

        // Notify observers
        notifyNormalOperationResumed();
    }

    /**
     * Schedules automatic clearing of emergency after a duration.
     */
    public ScheduledFuture<?> scheduleEmergencyClear(EmergencyVehicle vehicle, int delaySeconds) {
        return scheduler.schedule(
            () -> clearEmergency(vehicle),
            delaySeconds,
            TimeUnit.SECONDS
        );
    }

    private void notifyEmergencyDetected(EmergencyVehicle vehicle) {
        for (EmergencyObserver observer : observers) {
            observer.onEmergencyDetected(intersection, vehicle);
        }
    }

    private void notifyEmergencyCleared(EmergencyVehicle vehicle) {
        for (EmergencyObserver observer : observers) {
            observer.onEmergencyCleared(intersection, vehicle);
        }
    }

    private void notifyEmergencyOverrideActivated() {
        for (EmergencyObserver observer : observers) {
            observer.onEmergencyOverrideActivated(intersection);
        }
    }

    private void notifyNormalOperationResumed() {
        for (EmergencyObserver observer : observers) {
            observer.onNormalOperationResumed(intersection);
        }
    }

    public boolean isEmergencyMode() {
        return isEmergencyMode;
    }

    public EmergencyVehicle getCurrentEmergency() {
        return currentEmergency;
    }

    public int getPendingEmergencyCount() {
        return emergencyQueue.size();
    }

    /**
     * Shuts down the handler and releases resources.
     */
    public void shutdown() {
        clearAllEmergencies();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}



