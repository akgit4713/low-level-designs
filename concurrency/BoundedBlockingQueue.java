package concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe Bounded Blocking Queue Implementation
 * 
 * This implementation uses:
 * - ReentrantLock for mutual exclusion
 * - Two Condition variables:
 *   - notFull: signals when queue has space (producers wait on this)
 *   - notEmpty: signals when queue has elements (consumers wait on this)
 * 
 * Key Design Decisions:
 * 1. Using Lock/Condition instead of synchronized/wait/notify for better control
 * 2. LinkedList as underlying data structure for O(1) enqueue/dequeue
 * 3. Separate conditions for producers and consumers to avoid spurious wakeups
 */
public class BoundedBlockingQueue {
    
    private final Queue<Integer> queue;
    private final int capacity;
    private final Lock lock;
    private final Condition notFull;   // Condition for producers - wait when queue is full
    private final Condition notEmpty;  // Condition for consumers - wait when queue is empty
    
    /**
     * Constructor initializes the queue with a maximum capacity.
     * @param capacity maximum number of elements the queue can hold
     */
    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }
    
    /**
     * Adds an element to the front of the queue.
     * If the queue is full, the calling thread is blocked until space is available.
     * 
     * @param element the element to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            // Wait while queue is full
            // Using while loop to handle spurious wakeups
            while (queue.size() == capacity) {
                notFull.await();
            }
            
            // Add element to the queue
            queue.offer(element);
            
            // Signal consumers that queue is no longer empty
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns and removes the element at the rear of the queue.
     * If the queue is empty, the calling thread is blocked until an element is available.
     * 
     * @return the element at the rear of the queue
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            // Wait while queue is empty
            // Using while loop to handle spurious wakeups
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            
            // Remove and return element from the queue
            int element = queue.poll();
            
            // Signal producers that queue is no longer full
            notFull.signal();
            
            return element;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns the number of elements currently in the queue.
     * Thread-safe read operation.
     * 
     * @return current size of the queue
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}

/**
 * Alternative implementation using synchronized/wait/notify
 * (For comparison and learning purposes)
 */
class BoundedBlockingQueueWithSynchronized {
    
    private final int[] buffer;
    private int head;  // Points to next position to dequeue from
    private int tail;  // Points to next position to enqueue to
    private int count; // Current number of elements
    private final int capacity;
    
    public BoundedBlockingQueueWithSynchronized(int capacity) {
        this.capacity = capacity;
        this.buffer = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }
    
    public synchronized void enqueue(int element) throws InterruptedException {
        // Wait while queue is full
        while (count == capacity) {
            wait();
        }
        
        // Add element at tail position
        buffer[tail] = element;
        tail = (tail + 1) % capacity;
        count++;
        
        // Notify waiting consumers
        notifyAll();
    }
    
    public synchronized int dequeue() throws InterruptedException {
        // Wait while queue is empty
        while (count == 0) {
            wait();
        }
        
        // Remove element from head position
        int element = buffer[head];
        head = (head + 1) % capacity;
        count--;
        
        // Notify waiting producers
        notifyAll();
        
        return element;
    }
    
    public synchronized int size() {
        return count;
    }
}

/**
 * ================================================================================
 * SEMAPHORE-BASED IMPLEMENTATION
 * ================================================================================
 * 
 * What is a Semaphore?
 * --------------------
 * A Semaphore is a synchronization primitive that maintains a COUNT of permits:
 * 
 *   - acquire(): Decrements permit count. BLOCKS if count is 0.
 *   - release(): Increments permit count. Wakes up waiting threads.
 * 
 * Types of Semaphores:
 *   1. Binary Semaphore (permits = 1): Acts like a mutex/lock
 *   2. Counting Semaphore (permits > 1): Controls access to a pool of N resources
 * 
 * Key Difference from Lock:
 *   - Lock: Only the thread that acquired can release it
 *   - Semaphore: ANY thread can release a permit (not tied to ownership)
 * 
 * Classic Producer-Consumer Solution using 3 Semaphores:
 * -------------------------------------------------------
 *   1. emptySlots (counting): Tracks available empty slots. Init = capacity
 *   2. fullSlots (counting):  Tracks filled slots with data. Init = 0
 *   3. mutex (binary):        Protects critical section.     Init = 1
 * 
 * Flow:
 *   Producer:                          Consumer:
 *   ---------                          ---------
 *   emptySlots.acquire()  // Wait      fullSlots.acquire()   // Wait
 *   mutex.acquire()       // Lock      mutex.acquire()       // Lock
 *   [add to buffer]                    [remove from buffer]
 *   mutex.release()       // Unlock    mutex.release()       // Unlock
 *   fullSlots.release()   // Signal    emptySlots.release()  // Signal
 */
class BoundedBlockingQueueWithSemaphore {
    
    private final Queue<Integer> queue;
    private final int capacity;
    
    // Counting semaphore: tracks empty slots (producer waits when 0)
    private final Semaphore emptySlots;
    
    // Counting semaphore: tracks filled slots (consumer waits when 0)
    private final Semaphore fullSlots;
    
    // Binary semaphore: mutual exclusion for buffer access
    private final Semaphore mutex;
    
    // For thread-safe size() without holding mutex
    private final AtomicInteger size;
    
    public BoundedBlockingQueueWithSemaphore(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        
        // Initially all slots are empty
        this.emptySlots = new Semaphore(capacity);
        
        // Initially no slots are filled
        this.fullSlots = new Semaphore(0);
        
        // Binary semaphore for mutual exclusion (1 permit = mutex)
        this.mutex = new Semaphore(1);
        
        this.size = new AtomicInteger(0);
    }
    
    /**
     * Adds element to queue. Blocks if queue is full.
     * 
     * Semaphore flow:
     * 1. Acquire empty slot (blocks if queue full)
     * 2. Acquire mutex (enter critical section)
     * 3. Add element
     * 4. Release mutex (exit critical section)
     * 5. Signal that a slot is now full
     */
    public void enqueue(int element) throws InterruptedException {
        // Step 1: Wait for an empty slot (blocks if count = 0, i.e., queue full)
        emptySlots.acquire();
        
        // Step 2: Acquire mutex to access shared buffer
        mutex.acquire();
        try {
            // CRITICAL SECTION - only one thread can be here
            queue.offer(element);
            size.incrementAndGet();
        } finally {
            // Step 3: Release mutex
            mutex.release();
        }
        
        // Step 4: Signal consumers that a new item is available
        fullSlots.release();
    }
    
    /**
     * Removes and returns element from queue. Blocks if queue is empty.
     * 
     * Semaphore flow:
     * 1. Acquire full slot (blocks if queue empty)
     * 2. Acquire mutex (enter critical section)
     * 3. Remove element
     * 4. Release mutex (exit critical section)
     * 5. Signal that a slot is now empty
     */
    public int dequeue() throws InterruptedException {
        // Step 1: Wait for a full slot (blocks if count = 0, i.e., queue empty)
        fullSlots.acquire();
        
        int element;
        // Step 2: Acquire mutex to access shared buffer
        mutex.acquire();
        try {
            // CRITICAL SECTION - only one thread can be here
            element = queue.poll();
            size.decrementAndGet();
        } finally {
            // Step 3: Release mutex
            mutex.release();
        }
        
        // Step 4: Signal producers that a slot is now available
        emptySlots.release();
        
        return element;
    }
    
    /**
     * Returns current queue size.
     * Uses AtomicInteger for lock-free read.
     */
    public int size() {
        return size.get();
    }
}

/**
 * Comparison of all three approaches:
 * 
 * | Approach          | Pros                           | Cons                          |
 * |-------------------|--------------------------------|-------------------------------|
 * | ReentrantLock     | Fine-grained control,          | More verbose                  |
 * | + Condition       | Separate conditions for P/C    |                               |
 * |-------------------|--------------------------------|-------------------------------|
 * | synchronized      | Simple, built-in               | notifyAll() wakes all threads |
 * | + wait/notify     |                                | Only one wait condition       |
 * |-------------------|--------------------------------|-------------------------------|
 * | Semaphore         | Classic OS solution,           | Requires careful ordering     |
 * |                   | Decoupled waiting & locking    | of acquire/release            |
 * 
 * Can multiple threads enter critical section?
 * - ReentrantLock: NO (only one thread holds lock)
 * - synchronized:  NO (only one thread in synchronized block)
 * - Semaphore(1):  NO (binary semaphore acts as mutex)
 * - Semaphore(N):  YES, up to N threads (if used with N > 1 permits)
 */

/**
 * Test class to demonstrate the BoundedBlockingQueue
 * 
 * Constraints:
 * - 1 <= Number of Producers <= 8
 * - 1 <= Number of Consumers <= 8
 * - 1 <= size (capacity) <= 30
 * - 0 <= element <= 20
 * - enqueue calls >= dequeue calls
 * - At most 40 total calls to enqueue, dequeue, and size
 */
class BoundedBlockingQueueTest {
    
    public static void main(String[] args) throws InterruptedException {
        // Test 1: Basic single producer-consumer
        System.out.println("=== Test 1: Basic Producer-Consumer (ReentrantLock) ===");
        testBasic();
        
        // Test 2: Multiple producers and consumers (matching constraints)
        System.out.println("\n=== Test 2: Multi-Producer Multi-Consumer ===");
        testMultiProducerConsumer();
        
        // Test 3: Edge case - capacity 1
        System.out.println("\n=== Test 3: Capacity 1 ===");
        testCapacityOne();
        
        // Test 4: Semaphore-based implementation
        System.out.println("\n=== Test 4: Semaphore-Based Implementation ===");
        testSemaphoreImplementation();
    }
    
    private static void testSemaphoreImplementation() throws InterruptedException {
        BoundedBlockingQueueWithSemaphore queue = new BoundedBlockingQueueWithSemaphore(3);
        
        // Multiple producers
        Thread producer1 = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                    System.out.println("[Semaphore] P1 enqueued: " + i + " | size: " + queue.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread producer2 = new Thread(() -> {
            try {
                for (int i = 10; i < 15; i++) {
                    queue.enqueue(i);
                    System.out.println("[Semaphore] P2 enqueued: " + i + " | size: " + queue.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    int val = queue.dequeue();
                    System.out.println("[Semaphore] Consumer dequeued: " + val + " | size: " + queue.size());
                    Thread.sleep(50); // Slower consumer
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer1.start();
        producer2.start();
        consumer.start();
        
        producer1.join();
        producer2.join();
        consumer.join();
        
        System.out.println("[Semaphore] Final size: " + queue.size());
    }
    
    private static void testBasic() throws InterruptedException {
        BoundedBlockingQueue queue = new BoundedBlockingQueue(5);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i <= 10; i++) {
                    queue.enqueue(i % 21); // elements 0-20
                    System.out.println("Produced: " + (i % 21));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    int value = queue.dequeue();
                    System.out.println("Consumed: " + value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        
        System.out.println("Final size: " + queue.size());
    }
    
    private static void testMultiProducerConsumer() throws InterruptedException {
        // Capacity between 1-30
        BoundedBlockingQueue queue = new BoundedBlockingQueue(10);
        
        int numProducers = 4;  // Up to 8 producers
        int numConsumers = 3;  // Up to 8 consumers
        int itemsPerProducer = 5;
        int totalItems = numProducers * itemsPerProducer;
        int itemsPerConsumer = totalItems / numConsumers;
        
        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];
        
        // Create producers
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        int element = (producerId * itemsPerProducer + j) % 21; // 0-20
                        queue.enqueue(element);
                        System.out.println("Producer-" + producerId + " enqueued: " + element);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Create consumers
        for (int i = 0; i < numConsumers; i++) {
            final int consumerId = i;
            final int itemsToConsume = (i == numConsumers - 1) 
                ? totalItems - (itemsPerConsumer * (numConsumers - 1)) 
                : itemsPerConsumer;
            
            consumers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < itemsToConsume; j++) {
                        int value = queue.dequeue();
                        System.out.println("Consumer-" + consumerId + " dequeued: " + value);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Start all threads
        for (Thread p : producers) p.start();
        for (Thread c : consumers) c.start();
        
        // Wait for completion
        for (Thread p : producers) p.join();
        for (Thread c : consumers) c.join();
        
        System.out.println("Final size: " + queue.size());
    }
    
    private static void testCapacityOne() throws InterruptedException {
        BoundedBlockingQueue queue = new BoundedBlockingQueue(1);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                    System.out.println("Produced: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    int value = queue.dequeue();
                    System.out.println("Consumed: " + value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        
        System.out.println("Final size: " + queue.size());
    }
}
