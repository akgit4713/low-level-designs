package trafficsignal;

import trafficsignal.enums.Direction;
import trafficsignal.enums.EmergencyType;
import trafficsignal.factories.IntersectionFactory;
import trafficsignal.factories.TrafficSystemFactory;
import trafficsignal.factories.TrafficSystemFactory.TrafficControlSystem;
import trafficsignal.models.EmergencyVehicle;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;
import trafficsignal.observers.DisplayObserver;
import trafficsignal.observers.LoggingObserver;
import trafficsignal.services.EmergencyHandler;
import trafficsignal.services.SignalController;
import trafficsignal.services.TrafficMonitor;
import trafficsignal.strategies.AdaptiveTimingStrategy;
import trafficsignal.strategies.RushHourTimingStrategy;

/**
 * Main demonstration of the Traffic Signal Control System.
 * 
 * This example showcases:
 * 1. Creating intersections using factories
 * 2. Configuring signal controllers with different timing strategies
 * 3. Handling emergency vehicle situations
 * 4. Using observers for monitoring and logging
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        TRAFFIC SIGNAL CONTROL SYSTEM DEMONSTRATION         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Demo 1: Basic System Setup
        demo1_BasicSystemSetup();

        // Demo 2: Using Different Timing Strategies
        demo2_TimingStrategies();

        // Demo 3: Emergency Vehicle Handling
        demo3_EmergencyHandling();

        // Demo 4: Full System with Factory
        demo4_FullSystemDemo();

        System.out.println("\n✓ All demonstrations completed successfully!");
    }

    /**
     * Demo 1: Setting up a basic traffic signal system.
     */
    private static void demo1_BasicSystemSetup() {
        printDemoHeader("Demo 1: Basic System Setup");

        // Create a 4-way intersection using factory
        Intersection intersection = IntersectionFactory.createFourWayIntersection("Main St & Oak Ave");
        System.out.println("Created intersection: " + intersection.getName());
        System.out.println("Roads: " + intersection.getRoadCount());
        
        for (Road road : intersection.getAllRoads()) {
            System.out.println("  - " + road.getName() + " (" + road.getDirection() + ")");
        }

        // Create signal controller
        SignalController controller = new SignalController(intersection);
        
        // Add observers
        DisplayObserver display = new DisplayObserver("Main");
        controller.addObserver(display);

        // Show initial state
        controller.printCurrentState();

        System.out.println("✓ Basic system setup complete\n");
    }

    /**
     * Demo 2: Demonstrating different timing strategies.
     */
    private static void demo2_TimingStrategies() {
        printDemoHeader("Demo 2: Timing Strategies");

        Intersection intersection = IntersectionFactory.createFourWayIntersection("Test Intersection");
        SignalController controller = new SignalController(intersection);

        // Normal strategy (default)
        System.out.println("Current strategy: " + controller.getTimingStrategy().getStrategyName());
        System.out.println("  Description: " + controller.getTimingStrategy().getDescription());
        System.out.println("  Green duration: " + controller.getTimingStrategy().getDuration(
            trafficsignal.enums.SignalColor.GREEN) + "s");

        // Switch to rush hour strategy
        controller.setTimingStrategy(new RushHourTimingStrategy());
        System.out.println("\nSwitched to: " + controller.getTimingStrategy().getStrategyName());
        System.out.println("  Description: " + controller.getTimingStrategy().getDescription());
        System.out.println("  Green duration: " + controller.getTimingStrategy().getDuration(
            trafficsignal.enums.SignalColor.GREEN) + "s");

        // Switch to adaptive strategy
        controller.setTimingStrategy(new AdaptiveTimingStrategy());
        System.out.println("\nSwitched to: " + controller.getTimingStrategy().getStrategyName());
        System.out.println("  Description: " + controller.getTimingStrategy().getDescription());

        // Simulate traffic density affecting duration
        Road northRoad = intersection.getRoad(Direction.NORTH);
        northRoad.setVehicleCount(5);
        System.out.println("\n  Traffic on North Road: " + northRoad.getVehicleCount() + " vehicles");
        System.out.println("  Density: " + northRoad.getCurrentDensity());
        System.out.println("  Adjusted green: " + controller.getTimingStrategy()
            .getAdjustedDuration(trafficsignal.enums.SignalColor.GREEN, northRoad) + "s");

        northRoad.setVehicleCount(25);
        System.out.println("\n  Traffic on North Road: " + northRoad.getVehicleCount() + " vehicles");
        System.out.println("  Density: " + northRoad.getCurrentDensity());
        System.out.println("  Adjusted green: " + controller.getTimingStrategy()
            .getAdjustedDuration(trafficsignal.enums.SignalColor.GREEN, northRoad) + "s");

        System.out.println("\n✓ Timing strategies demonstration complete\n");
    }

    /**
     * Demo 3: Emergency vehicle handling.
     */
    private static void demo3_EmergencyHandling() {
        printDemoHeader("Demo 3: Emergency Vehicle Handling");

        // Create system
        Intersection intersection = IntersectionFactory.createFourWayIntersection("Emergency Demo");
        SignalController controller = new SignalController(intersection);
        EmergencyHandler emergencyHandler = new EmergencyHandler(intersection, controller);

        // Add observers
        DisplayObserver display = new DisplayObserver("Emergency");
        LoggingObserver logger = new LoggingObserver();
        controller.addObserver(display);
        controller.addObserver(logger);
        emergencyHandler.addObserver(display);
        emergencyHandler.addObserver(logger);

        // Start normal operation
        System.out.println("Starting normal operation...");
        controller.printCurrentState();

        // Detect emergency vehicle
        System.out.println("⚠️  Detecting ambulance from NORTH...");
        emergencyHandler.detectEmergency(EmergencyType.AMBULANCE, Direction.NORTH);
        
        System.out.println("\nEmergency mode: " + emergencyHandler.isEmergencyMode());
        System.out.println("Current emergency: " + emergencyHandler.getCurrentEmergency());
        
        // Show signal states during emergency
        System.out.println("\nSignal states during emergency:");
        for (Road road : intersection.getAllRoads()) {
            System.out.printf("  %s: %s (override: %s)%n", 
                road.getDirection(), 
                road.getSignal().getCurrentColor(),
                road.getSignal().isEmergencyOverride());
        }

        // Detect higher priority emergency
        System.out.println("\n⚠️  Detecting FIRE TRUCK from EAST (higher priority)...");
        emergencyHandler.detectEmergency(EmergencyType.FIRE_TRUCK, Direction.EAST);

        System.out.println("\nSignal states after fire truck priority:");
        for (Road road : intersection.getAllRoads()) {
            System.out.printf("  %s: %s%n", 
                road.getDirection(), 
                road.getSignal().getCurrentColor());
        }

        // Clear emergencies
        System.out.println("\n✓ Clearing all emergencies...");
        emergencyHandler.clearAllEmergencies();
        System.out.println("Emergency mode: " + emergencyHandler.isEmergencyMode());

        // Show logs
        System.out.println("\n📋 Event log:");
        for (LoggingObserver.LogEntry log : logger.getLogs()) {
            System.out.println("  [" + log.type() + "] " + log.message());
        }

        System.out.println("\n✓ Emergency handling demonstration complete\n");
    }

    /**
     * Demo 4: Full system using factory.
     */
    private static void demo4_FullSystemDemo() {
        printDemoHeader("Demo 4: Full System with Factory");

        // Create fully configured system using factory
        TrafficControlSystem system = TrafficSystemFactory.createFullyConfiguredSystem(
            "Downtown Junction");

        System.out.println("Created traffic control system:");
        System.out.println("  Intersection: " + system.getIntersection().getName());
        System.out.println("  Strategy: " + system.getController().getTimingStrategy().getStrategyName());
        System.out.println("  Roads: " + system.getIntersection().getRoadCount());

        // Using custom intersection builder
        System.out.println("\nCreating custom intersection with builder...");
        Intersection custom = IntersectionFactory.builder("Highway Junction")
            .addNorthRoad("Highway 101 North")
            .addSouthRoad("Highway 101 South")
            .addEastRoad("Exit Ramp East")
            .build();

        System.out.println("  Custom intersection: " + custom.getName());
        System.out.println("  Roads: " + custom.getRoadCount());
        for (Road road : custom.getAllRoads()) {
            System.out.println("    - " + road.getName());
        }

        // Traffic monitor usage
        System.out.println("\nTraffic monitoring:");
        TrafficMonitor monitor = system.getMonitor();
        
        // Simulate traffic
        for (Road road : system.getIntersection().getAllRoads()) {
            int vehicles = (int) (Math.random() * 40);
            road.setVehicleCount(vehicles);
            System.out.printf("  %s: %d vehicles (%s density)%n", 
                road.getName(), vehicles, road.getCurrentDensity());
        }

        System.out.println("  Overall density: " + monitor.getOverallDensity());

        // Clean up
        system.shutdown();

        System.out.println("\n✓ Full system demonstration complete\n");
    }

    private static void printDemoHeader(String title) {
        System.out.println("┌" + "─".repeat(60) + "┐");
        System.out.printf("│ %-58s │%n", title);
        System.out.println("└" + "─".repeat(60) + "┘");
    }
}



