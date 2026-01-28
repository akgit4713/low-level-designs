package concurrency.jobqueue;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * ================================================================================
 * PROBLEM 2: THREAD-SAFE JOB QUEUE
 * ================================================================================
 * 
 * Requirements:
 * 1. Add jobs (enqueue)
 * 2. Remove jobs (dequeue)
 * 3. Retrieve next job to execute
 * 4. Thread-safe for multiple producers and consumers
 * 
 * Key Design Decisions:
 * 1. ReentrantLock for mutual exclusion (better than synchronized)
 * 2. Two Condition variables: notFull (for producers) and notEmpty (for consumers)
 * 3. LinkedList for O(1) enqueue/dequeue operations
 * 
 * Why ReentrantLock over synchronized?
 * - Separate conditions for producers and consumers (avoids unnecessary wakeups)
 * - tryLock() with timeout support
 * - Fair locking option
 * - Interruptible lock acquisition
 * 
 * Time Complexity:
 * - enqueue: O(1)
 * - dequeue: O(1)
 * - peek: O(1)
 * - remove specific job: O(n)
 */
public class ThreadSafeJobQueue<T> {
    
    private final Queue<T> queue;
    private final int capacity;
    private final Lock lock;
    private final Condition notFull;   // Producers wait on this when queue is full
    private final Condition notEmpty;  // Consumers wait on this when queue is empty
    
    /**
     * Creates a bounded job queue with specified capacity.
     * 
     * @param capacity Maximum number of jobs the queue can hold
     */
    public ThreadSafeJobQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();  // Can use ReentrantLock(true) for fair ordering
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }
    
    /**
     * Adds a job to the queue. Blocks if the queue is full.
     * 
     * @param job The job to add
     * @throws InterruptedException if interrupted while waiting
     */
    public void enqueue(T job) throws InterruptedException {
        lock.lock();
        try {
            // Wait while queue is full (use while loop to handle spurious wakeups)
            while (queue.size() == capacity) {
                System.out.println(Thread.currentThread().getName() + " waiting - queue full");
                notFull.await();
            }
            
            queue.offer(job);
            System.out.println(Thread.currentThread().getName() + " enqueued: " + job + " | size: " + queue.size());
            
            // Signal ONE waiting consumer that queue is not empty
            notEmpty.signal();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Adds a job with timeout. Returns false if unable to add within timeout.
     * 
     * @param job The job to add
     * @param timeout Maximum time to wait
     * @param unit Time unit for timeout
     * @return true if job was added, false if timeout expired
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean offer(T job, long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        
        lock.lock();
        try {
            while (queue.size() == capacity) {
                if (nanos <= 0) {
                    return false;  // Timeout expired
                }
                nanos = notFull.awaitNanos(nanos);
            }
            
            queue.offer(job);
            notEmpty.signal();
            return true;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Removes and returns the next job. Blocks if the queue is empty.
     * 
     * @return The next job to execute
     * @throws InterruptedException if interrupted while waiting
     */
    public T dequeue() throws InterruptedException {
        lock.lock();
        try {
            // Wait while queue is empty
            while (queue.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " waiting - queue empty");
                notEmpty.await();
            }
            
            T job = queue.poll();
            System.out.println(Thread.currentThread().getName() + " dequeued: " + job + " | size: " + queue.size());
            
            // Signal ONE waiting producer that queue is not full
            notFull.signal();
            
            return job;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Removes and returns the next job with timeout.
     * 
     * @param timeout Maximum time to wait
     * @param unit Time unit for timeout
     * @return The next job, or null if timeout expired
     * @throws InterruptedException if interrupted while waiting
     */
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        
        lock.lock();
        try {
            while (queue.isEmpty()) {
                if (nanos <= 0) {
                    return null;  // Timeout expired
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            
            T job = queue.poll();
            notFull.signal();
            return job;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns the next job without removing it. Blocks if empty.
     * 
     * @return The next job (does not remove)
     * @throws InterruptedException if interrupted while waiting
     */
    public T peek() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Non-blocking peek. Returns null if queue is empty.
     */
    public T peekNow() {
        lock.lock();
        try {
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Removes a specific job from the queue.
     * 
     * @param job The job to remove
     * @return true if job was found and removed
     */
    public boolean remove(T job) {
        lock.lock();
        try {
            boolean removed = queue.remove(job);
            if (removed) {
                notFull.signal();  // Space is now available
            }
            return removed;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns the current size of the queue.
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Checks if queue is empty.
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Checks if queue is full.
     */
    public boolean isFull() {
        lock.lock();
        try {
            return queue.size() == capacity;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Clears all jobs from the queue.
     */
    public void clear() {
        lock.lock();
        try {
            queue.clear();
            notFull.signalAll();  // Wake up all waiting producers
        } finally {
            lock.unlock();
        }
    }
}

/**
 * ================================================================================
 * ALTERNATIVE: Priority Job Queue
 * ================================================================================
 */
class PriorityJobQueue<T extends Comparable<T>> {
    
    private final PriorityQueue<T> queue;
    private final int capacity;
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    
    public PriorityJobQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new PriorityQueue<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }
    
    public void enqueue(T job) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.offer(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public T dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            T job = queue.poll();
            notFull.signal();
            return job;
        } finally {
            lock.unlock();
        }
    }
}

/**
 * ================================================================================
 * TEST CLASS
 * ================================================================================
 */
class ThreadSafeJobQueueTest {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread-Safe Job Queue Test ===\n");
        
        ThreadSafeJobQueue<String> queue = new ThreadSafeJobQueue<>(3);
        
        // Multiple producers
        Thread producer1 = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.enqueue("P1-Job-" + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer-1");
        
        Thread producer2 = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.enqueue("P2-Job-" + i);
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer-2");
        
        // Multiple consumers
        Thread consumer1 = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.dequeue();
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-1");
        
        Thread consumer2 = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.dequeue();
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-2");
        
        // Start all threads
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
        
        // Wait for completion
        producer1.join();
        producer2.join();
        consumer1.join();
        consumer2.join();
        
        System.out.println("\nFinal queue size: " + queue.size());
        System.out.println("Test completed!");
    }
}

/**
 * ================================================================================
 * FOLLOW-UP: Job Queue with Cancellation Support
 * ================================================================================
 */
class CancellableJobQueue<T> {
    
    public static class Job<T> {
        private final T task;
        private volatile boolean cancelled;
        
        public Job(T task) {
            this.task = task;
            this.cancelled = false;
        }
        
        public T getTask() { return task; }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { this.cancelled = true; }
    }
    
    private final LinkedList<Job<T>> queue;
    private final Lock lock;
    private final Condition notEmpty;
    
    public CancellableJobQueue() {
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
    }
    
    public Job<T> submit(T task) {
        lock.lock();
        try {
            Job<T> job = new Job<>(task);
            queue.offer(job);
            notEmpty.signal();
            return job;  // Return handle for cancellation
        } finally {
            lock.unlock();
        }
    }
    
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                while (queue.isEmpty()) {
                    notEmpty.await();
                }
                
                Job<T> job = queue.poll();
                if (job != null && !job.isCancelled()) {
                    return job.getTask();
                }
                // Skip cancelled jobs
            }
        } finally {
            lock.unlock();
        }
    }
}
