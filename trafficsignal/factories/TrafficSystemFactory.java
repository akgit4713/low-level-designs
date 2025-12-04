package trafficsignal.factories;

import trafficsignal.models.Intersection;
import trafficsignal.observers.DisplayObserver;
import trafficsignal.observers.EmergencyObserver;
import trafficsignal.observers.LoggingObserver;
import trafficsignal.observers.SignalObserver;
import trafficsignal.services.EmergencyHandler;
import trafficsignal.services.SignalController;
import trafficsignal.services.TrafficMonitor;
import trafficsignal.strategies.*;

/**
 * Factory Pattern: Creates complete traffic control systems.
 */
public class TrafficSystemFactory {
    
    /**
     * Creates a complete traffic control system with default configuration.
     */
    public static TrafficControlSystem createDefaultSystem(String intersectionName) {
        Intersection intersection = IntersectionFactory.createFourWayIntersection(intersectionName);
        return createSystem(intersection, new NormalTimingStrategy());
    }

    /**
     * Creates a traffic control system with specified strategy.
     */
    public static TrafficControlSystem createSystem(Intersection intersection, 
                                                     TimingStrategy strategy) {
        SignalController controller = new SignalController(intersection, strategy);
        EmergencyHandler emergencyHandler = new EmergencyHandler(intersection, controller);
        TrafficMonitor monitor = new TrafficMonitor(intersection, controller);

        return new TrafficControlSystem(intersection, controller, emergencyHandler, monitor);
    }

    /**
     * Creates a fully configured system with observers.
     */
    public static TrafficControlSystem createFullyConfiguredSystem(String intersectionName) {
        Intersection intersection = IntersectionFactory.createFourWayIntersection(intersectionName);
        SignalController controller = new SignalController(intersection, new AdaptiveTimingStrategy());
        EmergencyHandler emergencyHandler = new EmergencyHandler(intersection, controller);
        TrafficMonitor monitor = new TrafficMonitor(intersection, controller);

        // Add observers
        DisplayObserver displayObserver = new DisplayObserver(intersectionName);
        LoggingObserver loggingObserver = new LoggingObserver();

        controller.addObserver(displayObserver);
        controller.addObserver(loggingObserver);
        emergencyHandler.addObserver(displayObserver);
        emergencyHandler.addObserver(loggingObserver);

        return new TrafficControlSystem(intersection, controller, emergencyHandler, monitor, 
            displayObserver, loggingObserver);
    }

    /**
     * Container for a complete traffic control system.
     */
    public static class TrafficControlSystem {
        private final Intersection intersection;
        private final SignalController controller;
        private final EmergencyHandler emergencyHandler;
        private final TrafficMonitor monitor;
        private final DisplayObserver displayObserver;
        private final LoggingObserver loggingObserver;

        public TrafficControlSystem(Intersection intersection, 
                                   SignalController controller,
                                   EmergencyHandler emergencyHandler,
                                   TrafficMonitor monitor) {
            this(intersection, controller, emergencyHandler, monitor, null, null);
        }

        public TrafficControlSystem(Intersection intersection, 
                                   SignalController controller,
                                   EmergencyHandler emergencyHandler,
                                   TrafficMonitor monitor,
                                   DisplayObserver displayObserver,
                                   LoggingObserver loggingObserver) {
            this.intersection = intersection;
            this.controller = controller;
            this.emergencyHandler = emergencyHandler;
            this.monitor = monitor;
            this.displayObserver = displayObserver;
            this.loggingObserver = loggingObserver;
        }

        public Intersection getIntersection() {
            return intersection;
        }

        public SignalController getController() {
            return controller;
        }

        public EmergencyHandler getEmergencyHandler() {
            return emergencyHandler;
        }

        public TrafficMonitor getMonitor() {
            return monitor;
        }

        public DisplayObserver getDisplayObserver() {
            return displayObserver;
        }

        public LoggingObserver getLoggingObserver() {
            return loggingObserver;
        }

        /**
         * Adds a signal observer to the controller.
         */
        public void addSignalObserver(SignalObserver observer) {
            controller.addObserver(observer);
        }

        /**
         * Adds an emergency observer to the handler.
         */
        public void addEmergencyObserver(EmergencyObserver observer) {
            emergencyHandler.addObserver(observer);
        }

        /**
         * Starts the traffic control system.
         */
        public void start() {
            controller.start();
        }

        /**
         * Stops the traffic control system.
         */
        public void stop() {
            controller.stop();
        }

        /**
         * Shuts down and cleans up all resources.
         */
        public void shutdown() {
            emergencyHandler.shutdown();
            controller.shutdown();
        }
    }
}



