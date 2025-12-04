package trafficsignal.services;

import trafficsignal.enums.Direction;
import trafficsignal.enums.SignalColor;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;
import trafficsignal.observers.SignalObserver;
import trafficsignal.states.GreenState;
import trafficsignal.states.RedState;
import trafficsignal.states.SignalState;
import trafficsignal.states.YellowState;
import trafficsignal.strategies.NormalTimingStrategy;
import trafficsignal.strategies.TimingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Main controller for traffic signal management.
 * Orchestrates signal timing, transitions, and coordinates with observers.
 */
public class SignalController {
    
    private final Intersection intersection;
    private TimingStrategy timingStrategy;
    private final List<SignalObserver> observers;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> currentCycle;
    private Direction currentGreenDirection;
    private boolean isRunning;
    private boolean isPaused;

    public SignalController(Intersection intersection) {
        this(intersection, new NormalTimingStrategy());
    }

    public SignalController(Intersection intersection, TimingStrategy timingStrategy) {
        this.intersection = intersection;
        this.timingStrategy = timingStrategy;
        this.observers = new ArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.isRunning = false;
        this.isPaused = false;
    }

    /**
     * Starts the signal control system.
     */
    public void start() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        intersection.setOperational(true);
        
        // Initialize all signals to RED first
        initializeSignals();
        
        // Start with first available direction
        currentGreenDirection = getFirstDirection();
        if (currentGreenDirection != null) {
            startSignalCycle();
        }
    }

    /**
     * Stops the signal control system.
     */
    public void stop() {
        isRunning = false;
        intersection.setOperational(false);
        
        if (currentCycle != null) {
            currentCycle.cancel(false);
        }
    }

    /**
     * Pauses the current cycle (signals remain in current state).
     */
    public void pause() {
        if (!isRunning) return;
        
        isPaused = true;
        if (currentCycle != null) {
            currentCycle.cancel(false);
        }
    }

    /**
     * Resumes from paused state.
     */
    public void resume() {
        if (!isRunning || !isPaused) return;
        
        isPaused = false;
        startSignalCycle();
    }

    /**
     * Changes the timing strategy.
     */
    public void setTimingStrategy(TimingStrategy strategy) {
        this.timingStrategy = strategy;
    }

    public TimingStrategy getTimingStrategy() {
        return timingStrategy;
    }

    /**
     * Adds a signal observer.
     */
    public void addObserver(SignalObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes a signal observer.
     */
    public void removeObserver(SignalObserver observer) {
        observers.remove(observer);
    }

    /**
     * Gets current state of all signals.
     */
    public void printCurrentState() {
        System.out.println("\n=== Current Signal State ===");
        System.out.println("Strategy: " + timingStrategy.getStrategyName());
        System.out.println("Running: " + isRunning + ", Paused: " + isPaused);
        for (Road road : intersection.getAllRoads()) {
            System.out.printf("  %s (%s): %s%n", 
                road.getName(), 
                road.getDirection(), 
                road.getSignal().getCurrentColor());
        }
        System.out.println();
    }

    private void initializeSignals() {
        for (Road road : intersection.getAllRoads()) {
            road.getSignal().forceTransitionTo(new RedState());
        }
    }

    private Direction getFirstDirection() {
        // Start with NORTH, or whatever is available
        if (intersection.hasRoad(Direction.NORTH)) return Direction.NORTH;
        if (intersection.hasRoad(Direction.EAST)) return Direction.EAST;
        if (intersection.hasRoad(Direction.SOUTH)) return Direction.SOUTH;
        if (intersection.hasRoad(Direction.WEST)) return Direction.WEST;
        return null;
    }

    private Direction getNextDirection() {
        Direction[] order = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        int currentIndex = -1;
        
        for (int i = 0; i < order.length; i++) {
            if (order[i] == currentGreenDirection) {
                currentIndex = i;
                break;
            }
        }

        // Find next available direction (skip opposite as they share the same phase)
        for (int i = 1; i <= order.length; i++) {
            int nextIndex = (currentIndex + i) % order.length;
            Direction nextDir = order[nextIndex];
            
            // Skip opposite direction (they share the same green phase)
            if (nextDir == currentGreenDirection.getOpposite()) {
                continue;
            }
            
            if (intersection.hasRoad(nextDir)) {
                return nextDir;
            }
        }

        return currentGreenDirection;
    }

    private void startSignalCycle() {
        if (!isRunning || isPaused || currentGreenDirection == null) {
            return;
        }

        // Set current direction (and opposite) to GREEN, others to RED
        setGreenPhase(currentGreenDirection);
        
        // Calculate duration based on strategy and traffic
        Road currentRoad = intersection.getRoad(currentGreenDirection);
        int greenDuration = timingStrategy.getAdjustedDuration(SignalColor.GREEN, currentRoad);
        
        // Schedule transition to yellow after green duration
        currentCycle = scheduler.schedule(
            this::transitionToYellow, 
            greenDuration, 
            TimeUnit.SECONDS
        );
    }

    private void setGreenPhase(Direction greenDirection) {
        for (Road road : intersection.getAllRoads()) {
            SignalState previousState = road.getSignal().getCurrentState();
            SignalState newState;
            
            if (road.getDirection() == greenDirection || 
                road.getDirection() == greenDirection.getOpposite()) {
                newState = new GreenState();
            } else {
                newState = new RedState();
            }
            
            road.getSignal().forceTransitionTo(newState);
            road.getSignal().setCurrentDuration(
                timingStrategy.getAdjustedDuration(newState.getColor(), road)
            );
            
            notifySignalChange(road, previousState, newState);
        }
    }

    private void transitionToYellow() {
        if (!isRunning || isPaused) return;

        // Current green roads transition to yellow
        for (Road road : intersection.getRoadsInAxis(currentGreenDirection)) {
            SignalState previousState = road.getSignal().getCurrentState();
            SignalState newState = new YellowState();
            
            road.getSignal().forceTransitionTo(newState);
            road.getSignal().setCurrentDuration(
                timingStrategy.getDuration(SignalColor.YELLOW)
            );
            
            notifySignalChange(road, previousState, newState);
        }

        // Schedule transition to red/next green after yellow duration
        int yellowDuration = timingStrategy.getDuration(SignalColor.YELLOW);
        currentCycle = scheduler.schedule(
            this::completeTransition, 
            yellowDuration, 
            TimeUnit.SECONDS
        );
    }

    private void completeTransition() {
        if (!isRunning || isPaused) return;

        // Current yellow roads transition to red
        for (Road road : intersection.getRoadsInAxis(currentGreenDirection)) {
            SignalState previousState = road.getSignal().getCurrentState();
            SignalState newState = new RedState();
            
            road.getSignal().forceTransitionTo(newState);
            notifySignalChange(road, previousState, newState);
        }

        // Notify cycle complete
        notifyCycleComplete();

        // Move to next direction
        currentGreenDirection = getNextDirection();
        
        // Start next cycle
        startSignalCycle();
    }

    private void notifySignalChange(Road road, SignalState from, SignalState to) {
        for (SignalObserver observer : observers) {
            observer.onSignalChange(road, from, to);
        }
    }

    private void notifyCycleComplete() {
        for (SignalObserver observer : observers) {
            observer.onCycleComplete(intersection.getId());
        }
    }

    /**
     * Shuts down the controller and releases resources.
     */
    public void shutdown() {
        stop();
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

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Direction getCurrentGreenDirection() {
        return currentGreenDirection;
    }
}



