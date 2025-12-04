package pubsub.test;

import pubsub.impl.ConcurrentSubscriptionManager;
import pubsub.interfaces.Subscriber;
import pubsub.interfaces.SubscriptionManager;
import pubsub.models.Message;
import pubsub.models.Subscription;
import pubsub.models.Topic;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for SubscriptionManager implementation.
 */
public class SubscriptionManagerTest {
    
    // Helper to create a simple subscriber
    private Subscriber<String> createSubscriber(String id) {
        return new Subscriber<>() {
            @Override
            public void onMessage(Message<String> message) {
                // No-op for testing
            }
            
            @Override
            public String getId() {
                return id;
            }
        };
    }
    
    public void testAddSubscription() {
        System.out.println("Running: testAddSubscription");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        Topic topic = new Topic("test-topic");
        Subscriber<String> subscriber = createSubscriber("sub-1");
        
        Subscription<String> subscription = new Subscription<>(topic, subscriber);
        boolean added = manager.addSubscription(subscription);
        
        assert added : "Should add subscription successfully";
        assert manager.getSubscriptionCount() == 1 : "Should have 1 subscription";
        assert manager.hasSubscribers(topic) : "Topic should have subscribers";
        
        System.out.println("✓ testAddSubscription passed");
    }
    
    public void testRemoveSubscription() {
        System.out.println("Running: testRemoveSubscription");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        Topic topic = new Topic("test-topic");
        Subscriber<String> subscriber = createSubscriber("sub-1");
        
        Subscription<String> subscription = new Subscription<>(topic, subscriber);
        manager.addSubscription(subscription);
        
        var removed = manager.removeSubscription(subscription.getId());
        
        assert removed.isPresent() : "Should remove subscription";
        assert manager.getSubscriptionCount() == 0 : "Should have 0 subscriptions";
        assert !manager.hasSubscribers(topic) : "Topic should not have subscribers";
        
        System.out.println("✓ testRemoveSubscription passed");
    }
    
    public void testGetSubscriptionsBySubscriber() {
        System.out.println("Running: testGetSubscriptionsBySubscriber");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        Subscriber<String> subscriber = createSubscriber("multi-sub");
        
        manager.addSubscription(new Subscription<>(new Topic("topic1"), subscriber));
        manager.addSubscription(new Subscription<>(new Topic("topic2"), subscriber));
        manager.addSubscription(new Subscription<>(new Topic("topic3"), subscriber));
        
        Set<Subscription<String>> subs = manager.getSubscriptionsBySubscriber("multi-sub");
        assert subs.size() == 3 : "Subscriber should have 3 subscriptions";
        
        System.out.println("✓ testGetSubscriptionsBySubscriber passed");
    }
    
    public void testRemoveSubscriberFromAll() {
        System.out.println("Running: testRemoveSubscriberFromAll");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        Subscriber<String> subscriber = createSubscriber("to-remove");
        
        manager.addSubscription(new Subscription<>(new Topic("t1"), subscriber));
        manager.addSubscription(new Subscription<>(new Topic("t2"), subscriber));
        manager.addSubscription(new Subscription<>(new Topic("t3"), subscriber));
        
        Set<Subscription<String>> removed = manager.removeSubscriberFromAll("to-remove");
        
        assert removed.size() == 3 : "Should remove all 3 subscriptions";
        assert manager.getSubscriptionCount() == 0 : "Should have no subscriptions left";
        
        System.out.println("✓ testRemoveSubscriberFromAll passed");
    }
    
    public void testActiveTopics() {
        System.out.println("Running: testActiveTopics");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        
        manager.addSubscription(new Subscription<>(new Topic("active1"), createSubscriber("s1")));
        manager.addSubscription(new Subscription<>(new Topic("active2"), createSubscriber("s2")));
        
        Set<Topic> activeTopics = manager.getActiveTopics();
        assert activeTopics.size() == 2 : "Should have 2 active topics";
        
        System.out.println("✓ testActiveTopics passed");
    }
    
    public void testConcurrentOperations() throws InterruptedException {
        System.out.println("Running: testConcurrentOperations");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        int threadCount = 10;
        int operationsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        Topic topic = new Topic("concurrent-topic");
        
        // Multiple threads adding and removing subscriptions
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        Subscriber<String> sub = createSubscriber("sub-" + threadId + "-" + i);
                        Subscription<String> subscription = new Subscription<>(topic, sub);
                        
                        manager.addSubscription(subscription);
                        Thread.sleep(1); // Small delay to increase contention
                        manager.removeSubscription(subscription.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        assert completed : "All threads should complete";
        assert manager.getSubscriptionCount() == 0 : "All subscriptions should be removed";
        
        System.out.println("✓ testConcurrentOperations passed");
    }
    
    public void testClear() {
        System.out.println("Running: testClear");
        
        SubscriptionManager<String> manager = new ConcurrentSubscriptionManager<>();
        
        for (int i = 0; i < 10; i++) {
            manager.addSubscription(new Subscription<>(
                    new Topic("topic-" + i),
                    createSubscriber("sub-" + i)
            ));
        }
        
        assert manager.getSubscriptionCount() == 10 : "Should have 10 subscriptions";
        
        manager.clear();
        
        assert manager.getSubscriptionCount() == 0 : "Should have 0 subscriptions after clear";
        assert manager.getActiveTopics().isEmpty() : "Should have no active topics";
        
        System.out.println("✓ testClear passed");
    }
    
    public void runAllTests() throws InterruptedException {
        System.out.println("\n========== SUBSCRIPTION MANAGER TESTS ==========\n");
        
        testAddSubscription();
        testRemoveSubscription();
        testGetSubscriptionsBySubscriber();
        testRemoveSubscriberFromAll();
        testActiveTopics();
        testConcurrentOperations();
        testClear();
        
        System.out.println("\n========== ALL TESTS PASSED ==========\n");
    }
    
    public static void main(String[] args) throws InterruptedException {
        new SubscriptionManagerTest().runAllTests();
    }
}



