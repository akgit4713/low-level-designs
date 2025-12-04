package pubsub;

import pubsub.impl.DefaultPublisher;
import pubsub.interfaces.Publisher;
import pubsub.interfaces.Subscriber;
import pubsub.models.Message;
import pubsub.models.Subscription;
import pubsub.subscribers.CallbackSubscriber;
import pubsub.subscribers.CollectingSubscriber;
import pubsub.subscribers.FilteringSubscriber;
import pubsub.subscribers.LoggingSubscriber;

import java.util.concurrent.TimeUnit;

/**
 * Demonstration of the Pub-Sub system.
 * Shows various usage patterns and features.
 */
public class Main {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           PUB-SUB SYSTEM DEMONSTRATION                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        
        // Create the broker (central coordinator)
        PubSubBroker<String> broker = new PubSubBroker<>();
        
        // ==================== Example 1: Basic Pub-Sub ====================
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 1: Basic Publish-Subscribe");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        // Create logging subscribers for different topics
        LoggingSubscriber<String> newsSubscriber = new LoggingSubscriber<>("📰 News Reader");
        LoggingSubscriber<String> sportsSubscriber = new LoggingSubscriber<>("⚽ Sports Fan");
        LoggingSubscriber<String> techSubscriber = new LoggingSubscriber<>("💻 Tech Enthusiast");
        
        // Subscribe to topics
        broker.subscribe("news", newsSubscriber);
        broker.subscribe("sports", sportsSubscriber);
        broker.subscribe("technology", techSubscriber);
        broker.subscribe("news", sportsSubscriber); // One subscriber can listen to multiple topics
        
        // Publish messages
        broker.publish("news", "Breaking: New programming language released!");
        broker.publish("sports", "Team Alpha wins the championship!");
        broker.publish("technology", "AI breakthrough announced");
        
        TimeUnit.MILLISECONDS.sleep(100); // Allow async delivery
        
        // ==================== Example 2: Using Publisher Interface ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 2: Using Publisher Interface");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        Publisher<String> weatherService = new DefaultPublisher<>("weather-service", broker);
        Publisher<String> alertService = new DefaultPublisher<>("alert-service", broker);
        
        LoggingSubscriber<String> weatherSubscriber = new LoggingSubscriber<>("🌤️ Weather Watcher");
        broker.subscribe("weather", weatherSubscriber);
        broker.subscribe("alerts", weatherSubscriber);
        
        weatherService.publish("weather", "Sunny, 25°C");
        alertService.publish("alerts", "Heat wave warning for tomorrow!");
        
        TimeUnit.MILLISECONDS.sleep(100);
        
        // ==================== Example 3: Collecting Subscriber ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 3: Collecting Messages for Batch Processing");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        CollectingSubscriber<String> collector = new CollectingSubscriber<>();
        broker.subscribe("orders", collector);
        
        for (int i = 1; i <= 5; i++) {
            broker.publish("orders", "Order #" + i + " received");
        }
        
        TimeUnit.MILLISECONDS.sleep(100);
        
        System.out.println("Collected " + collector.getMessageCount() + " orders:");
        collector.getMessages().forEach(msg -> 
            System.out.println("  → " + msg.getPayload())
        );
        
        // ==================== Example 4: Callback Subscriber ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 4: Callback-based Subscription (Lambda)");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        CallbackSubscriber<String> callbackSub = new CallbackSubscriber<>(
            msg -> System.out.println("🔔 Notification: " + msg.getPayload())
        );
        
        broker.subscribe("notifications", callbackSub);
        broker.publish("notifications", "You have 3 new messages");
        broker.publish("notifications", "Your package has been delivered");
        
        TimeUnit.MILLISECONDS.sleep(100);
        
        // ==================== Example 5: Filtering Subscriber ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 5: Filtered Subscriptions");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        LoggingSubscriber<String> urgentLogger = new LoggingSubscriber<>("🚨 Urgent Handler");
        
        // Only receive messages containing "URGENT"
        FilteringSubscriber<String> urgentFilter = new FilteringSubscriber<>(
            urgentLogger,
            msg -> msg.getPayload().contains("URGENT")
        );
        
        broker.subscribe("logs", urgentFilter);
        
        broker.publish("logs", "INFO: System started");
        broker.publish("logs", "URGENT: Database connection lost!");
        broker.publish("logs", "DEBUG: Cache cleared");
        broker.publish("logs", "URGENT: Memory usage at 95%!");
        
        TimeUnit.MILLISECONDS.sleep(100);
        
        // ==================== Example 6: Unsubscription ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 6: Unsubscription");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        LoggingSubscriber<String> tempSubscriber = new LoggingSubscriber<>("🔄 Temporary Sub");
        Subscription<String> subscription = broker.subscribe("events", tempSubscriber);
        
        broker.publish("events", "Event 1 - Should be received");
        TimeUnit.MILLISECONDS.sleep(50);
        
        System.out.println("\n>>> Unsubscribing...\n");
        broker.unsubscribe(subscription);
        
        broker.publish("events", "Event 2 - Should NOT be received");
        TimeUnit.MILLISECONDS.sleep(50);
        
        // ==================== Example 7: Multiple Publishers ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("EXAMPLE 7: Multiple Publishers to Same Topic");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        Publisher<String> sensor1 = new DefaultPublisher<>("sensor-1", broker);
        Publisher<String> sensor2 = new DefaultPublisher<>("sensor-2", broker);
        Publisher<String> sensor3 = new DefaultPublisher<>("sensor-3", broker);
        
        LoggingSubscriber<String> monitor = new LoggingSubscriber<>("📊 Monitor");
        broker.subscribe("sensor-data", monitor);
        
        sensor1.publish("sensor-data", "Temperature: 22°C");
        sensor2.publish("sensor-data", "Humidity: 65%");
        sensor3.publish("sensor-data", "Pressure: 1013 hPa");
        
        TimeUnit.MILLISECONDS.sleep(100);
        
        // ==================== Statistics ====================
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("SYSTEM STATISTICS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
        
        System.out.println("📈 Messages Published: " + broker.getMessagesPublishedCount());
        System.out.println("📤 Total Deliveries: " + broker.getMessagesDeliveredCount());
        System.out.println("📋 Active Subscriptions: " + broker.getTotalSubscriptionCount());
        System.out.println("📁 Active Topics: " + broker.getActiveTopics().size());
        
        // Shutdown
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("Shutting down broker gracefully...");
        broker.shutdown();
        System.out.println("✅ Broker shutdown complete");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }
}



