package pubsub.test;

import pubsub.PubSubBroker;
import pubsub.impl.ConcurrentSubscriptionManager;
import pubsub.impl.SyncMessageDispatcher;
import pubsub.impl.TopicRegistry;
import pubsub.interfaces.Subscriber;
import pubsub.models.Message;
import pubsub.models.Subscription;
import pubsub.models.Topic;
import pubsub.subscribers.CollectingSubscriber;
import pubsub.subscribers.FilteringSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for the Pub-Sub system.
 * Uses sync dispatcher for predictable testing.
 */
public class PubSubBrokerTest {
    
    // Using sync dispatcher for predictable test execution
    private PubSubBroker<String> createSyncBroker() {
        return new PubSubBroker<>(
                new TopicRegistry(),
                new ConcurrentSubscriptionManager<>(),
                new SyncMessageDispatcher<>()
        );
    }
    
    // ==================== Test Methods ====================
    
    public void testBasicPublishSubscribe() {
        System.out.println("Running: testBasicPublishSubscribe");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> subscriber = new CollectingSubscriber<>();
        
        broker.subscribe("test-topic", subscriber);
        broker.publish("test-topic", "Hello, World!");
        
        assert subscriber.getMessageCount() == 1 : "Expected 1 message";
        assert subscriber.getLastMessage().getPayload().equals("Hello, World!") : "Payload mismatch";
        
        System.out.println("✓ testBasicPublishSubscribe passed");
    }
    
    public void testMultipleSubscribers() {
        System.out.println("Running: testMultipleSubscribers");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> sub1 = new CollectingSubscriber<>();
        CollectingSubscriber<String> sub2 = new CollectingSubscriber<>();
        CollectingSubscriber<String> sub3 = new CollectingSubscriber<>();
        
        broker.subscribe("news", sub1);
        broker.subscribe("news", sub2);
        broker.subscribe("news", sub3);
        
        broker.publish("news", "Breaking News!");
        
        assert sub1.getMessageCount() == 1 : "Sub1 should receive 1 message";
        assert sub2.getMessageCount() == 1 : "Sub2 should receive 1 message";
        assert sub3.getMessageCount() == 1 : "Sub3 should receive 1 message";
        
        System.out.println("✓ testMultipleSubscribers passed");
    }
    
    public void testMultipleTopics() {
        System.out.println("Running: testMultipleTopics");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> sportsSubscriber = new CollectingSubscriber<>();
        CollectingSubscriber<String> weatherSubscriber = new CollectingSubscriber<>();
        
        broker.subscribe("sports", sportsSubscriber);
        broker.subscribe("weather", weatherSubscriber);
        
        broker.publish("sports", "Team A wins!");
        broker.publish("weather", "Sunny today");
        broker.publish("sports", "Team B scores!");
        
        assert sportsSubscriber.getMessageCount() == 2 : "Sports subscriber should have 2 messages";
        assert weatherSubscriber.getMessageCount() == 1 : "Weather subscriber should have 1 message";
        
        System.out.println("✓ testMultipleTopics passed");
    }
    
    public void testUnsubscribe() {
        System.out.println("Running: testUnsubscribe");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> subscriber = new CollectingSubscriber<>();
        
        Subscription<String> subscription = broker.subscribe("updates", subscriber);
        
        broker.publish("updates", "Message 1");
        assert subscriber.getMessageCount() == 1 : "Should receive before unsubscribe";
        
        broker.unsubscribe(subscription);
        
        broker.publish("updates", "Message 2");
        assert subscriber.getMessageCount() == 1 : "Should not receive after unsubscribe";
        
        System.out.println("✓ testUnsubscribe passed");
    }
    
    public void testUnsubscribeAll() {
        System.out.println("Running: testUnsubscribeAll");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> subscriber = new CollectingSubscriber<>();
        
        broker.subscribe("topic1", subscriber);
        broker.subscribe("topic2", subscriber);
        broker.subscribe("topic3", subscriber);
        
        assert broker.getTotalSubscriptionCount() == 3 : "Should have 3 subscriptions";
        
        int removed = broker.unsubscribeAll(subscriber.getId());
        assert removed == 3 : "Should remove 3 subscriptions";
        assert broker.getTotalSubscriptionCount() == 0 : "Should have 0 subscriptions";
        
        System.out.println("✓ testUnsubscribeAll passed");
    }
    
    public void testFilteringSubscriber() {
        System.out.println("Running: testFilteringSubscriber");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> collector = new CollectingSubscriber<>();
        
        // Only accept messages containing "important"
        FilteringSubscriber<String> filtered = new FilteringSubscriber<>(
                collector,
                msg -> msg.getPayload().contains("important")
        );
        
        broker.subscribe("alerts", filtered);
        
        broker.publish("alerts", "This is important");
        broker.publish("alerts", "This is not");
        broker.publish("alerts", "Another important message");
        
        assert collector.getMessageCount() == 2 : "Should only receive 2 filtered messages";
        
        System.out.println("✓ testFilteringSubscriber passed");
    }
    
    public void testMessageOrdering() {
        System.out.println("Running: testMessageOrdering");
        
        PubSubBroker<Integer> broker = new PubSubBroker<>(
                new TopicRegistry(),
                new ConcurrentSubscriptionManager<>(),
                new SyncMessageDispatcher<>()
        );
        
        CollectingSubscriber<Integer> subscriber = new CollectingSubscriber<>();
        broker.subscribe("sequence", subscriber);
        
        for (int i = 1; i <= 5; i++) {
            broker.publish("sequence", i);
        }
        
        List<Message<Integer>> messages = subscriber.getMessages();
        for (int i = 0; i < 5; i++) {
            assert messages.get(i).getPayload() == (i + 1) : "Messages should be in order";
        }
        
        System.out.println("✓ testMessageOrdering passed");
    }
    
    public void testSubscriberErrorHandling() {
        System.out.println("Running: testSubscriberErrorHandling");
        
        PubSubBroker<String> broker = createSyncBroker();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        // Subscriber that throws on every message
        Subscriber<String> failingSubscriber = new Subscriber<>() {
            @Override
            public void onMessage(Message<String> message) {
                throw new RuntimeException("Simulated failure");
            }
            
            @Override
            public String getId() {
                return "failing-subscriber";
            }
            
            @Override
            public void onError(Message<String> message, Throwable error) {
                errorCount.incrementAndGet();
            }
        };
        
        // Normal subscriber
        Subscriber<String> normalSubscriber = new Subscriber<>() {
            @Override
            public void onMessage(Message<String> message) {
                successCount.incrementAndGet();
            }
            
            @Override
            public String getId() {
                return "normal-subscriber";
            }
        };
        
        broker.subscribe("errors", failingSubscriber);
        broker.subscribe("errors", normalSubscriber);
        
        broker.publish("errors", "Test message");
        
        assert errorCount.get() == 1 : "Error handler should be called";
        assert successCount.get() == 1 : "Normal subscriber should still receive message";
        
        System.out.println("✓ testSubscriberErrorHandling passed");
    }
    
    public void testTopicCreation() {
        System.out.println("Running: testTopicCreation");
        
        PubSubBroker<String> broker = createSyncBroker();
        
        Topic topic = broker.createTopic("new-topic");
        assert topic.getName().equals("new-topic") : "Topic name mismatch";
        assert broker.topicExists("new-topic") : "Topic should exist";
        
        // Creating same topic again should return same instance
        Topic sameTopic = broker.createTopic("new-topic");
        assert topic == sameTopic : "Should return same topic instance";
        
        System.out.println("✓ testTopicCreation passed");
    }
    
    public void testStatistics() {
        System.out.println("Running: testStatistics");
        
        PubSubBroker<String> broker = createSyncBroker();
        CollectingSubscriber<String> sub1 = new CollectingSubscriber<>();
        CollectingSubscriber<String> sub2 = new CollectingSubscriber<>();
        
        broker.subscribe("stats-topic", sub1);
        broker.subscribe("stats-topic", sub2);
        
        broker.publish("stats-topic", "Message 1");
        broker.publish("stats-topic", "Message 2");
        
        assert broker.getMessagesPublishedCount() == 2 : "Should have 2 messages published";
        assert broker.getMessagesDeliveredCount() == 4 : "Should have 4 deliveries (2 msgs × 2 subs)";
        assert broker.getSubscriberCount("stats-topic") == 2 : "Should have 2 subscribers";
        
        System.out.println("✓ testStatistics passed");
    }
    
    public void testConcurrentSubscribers() throws InterruptedException {
        System.out.println("Running: testConcurrentSubscribers");
        
        // Use default async broker for concurrent test
        PubSubBroker<String> broker = new PubSubBroker<>();
        
        int subscriberCount = 10;
        int messageCount = 100;
        CountDownLatch latch = new CountDownLatch(subscriberCount * messageCount);
        AtomicInteger totalReceived = new AtomicInteger(0);
        
        // Create multiple subscribers
        for (int i = 0; i < subscriberCount; i++) {
            final int subId = i;
            broker.subscribe("concurrent-topic", new Subscriber<String>() {
                @Override
                public void onMessage(Message<String> message) {
                    totalReceived.incrementAndGet();
                    latch.countDown();
                }
                
                @Override
                public String getId() {
                    return "sub-" + subId;
                }
            });
        }
        
        // Publish messages
        for (int i = 0; i < messageCount; i++) {
            broker.publish("concurrent-topic", "Message " + i);
        }
        
        // Wait for all deliveries
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assert completed : "All messages should be delivered within timeout";
        assert totalReceived.get() == subscriberCount * messageCount : 
                "Expected " + (subscriberCount * messageCount) + " but got " + totalReceived.get();
        
        broker.shutdown();
        System.out.println("✓ testConcurrentSubscribers passed");
    }
    
    // ==================== Test Runner ====================
    
    public void runAllTests() throws InterruptedException {
        System.out.println("\n========== PUB-SUB SYSTEM TESTS ==========\n");
        
        testBasicPublishSubscribe();
        testMultipleSubscribers();
        testMultipleTopics();
        testUnsubscribe();
        testUnsubscribeAll();
        testFilteringSubscriber();
        testMessageOrdering();
        testSubscriberErrorHandling();
        testTopicCreation();
        testStatistics();
        testConcurrentSubscribers();
        
        System.out.println("\n========== ALL TESTS PASSED ==========\n");
    }
    
    public static void main(String[] args) throws InterruptedException {
        new PubSubBrokerTest().runAllTests();
    }
}



