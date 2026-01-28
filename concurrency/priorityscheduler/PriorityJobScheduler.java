package concurrency.priorityscheduler;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/**
 * ================================================================================
 * PROBLEM 8: PRIORITY JOB SCHEDULER (WITHOUT PriorityBlockingQueue)
 * ================================================================================
 * 
 * Requirements:
 * 1. Schedule jobs based on priority (higher priority executes first)
 * 2. Support dynamic priority changes for queued/running jobs
 * 3. Thread-safe for concurrent job submissions
 * 
 * Constraint: Cannot use PriorityBlockingQueue - must implement manually
 * 
 * Key Design Decisions:
 * 1. Custom Min-Heap (as Max-Heap via comparator) for priority ordering
 * 2. ReentrantLock + Condition for thread-safe blocking operations
 * 3. HashMap for O(1) job lookup for priority updates
 * 4. Heap re-heapify for dynamic priority updates
 * 
 * Time Complexity:
 * - Submit: O(log n) - heap insert
 * - Take highest priority: O(log n) - heap extract
 * - Update priority: O(n) - find in heap + O(log n) heapify = O(n)
 * - Cancel: O(n) - find + remove
 */
public class PriorityJobScheduler {
    
    // ==================== Job Definition ====================
    
    public enum JobStatus {
        QUEUED,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    public static class PriorityJob {
        private final String id;
        private final Runnable task;
        private volatile int priority;  // Higher = more important
        private volatile JobStatus status;
        private final long submissionTime;
        private int heapIndex;  // Track position in heap for O(log n) updates
        
        public PriorityJob(String id, Runnable task, int priority) {
            this.id = id;
            this.task = task;
            this.priority = priority;
            this.status = JobStatus.QUEUED;
            this.submissionTime = System.nanoTime();
            this.heapIndex = -1;
        }
        
        public String getId() { return id; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public JobStatus getStatus() { return status; }
        public void setStatus(JobStatus status) { this.status = status; }
        public Runnable getTask() { return task; }
        public long getSubmissionTime() { return submissionTime; }
        public int getHeapIndex() { return heapIndex; }
        public void setHeapIndex(int index) { this.heapIndex = index; }
        
        /**
         * Compare by priority (higher first), then by submission time (earlier first - FIFO).
         */
        public int compareTo(PriorityJob other) {
            // Higher priority first
            int priorityCompare = Integer.compare(other.priority, this.priority);
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            // If same priority, FIFO order
            return Long.compare(this.submissionTime, other.submissionTime);
        }
        
        @Override
        public String toString() {
            return String.format("Job{id='%s', priority=%d, status=%s}", id, priority, status);
        }
    }
    
    // ==================== Thread-Safe Priority Heap ====================
    
    /**
     * Custom thread-safe max-heap implementation.
     * 
     * Heap Property: Parent has higher priority than children
     * 
     * Array representation:
     * - Parent of node i: (i - 1) / 2
     * - Left child of node i: 2 * i + 1
     * - Right child of node i: 2 * i + 2
     */
    private static class ThreadSafePriorityHeap {
        
        private final ArrayList<PriorityJob> heap;
        private final Lock lock;
        private final Condition notEmpty;  // Signal when heap becomes non-empty
        
        public ThreadSafePriorityHeap() {
            this.heap = new ArrayList<>();
            this.lock = new ReentrantLock();
            this.notEmpty = lock.newCondition();
        }
        
        /**
         * Insert a job into the heap. O(log n)
         */
        public void offer(PriorityJob job) {
            lock.lock();
            try {
                // Add at the end
                job.setHeapIndex(heap.size());
                heap.add(job);
                
                // Bubble up to maintain heap property
                siftUp(heap.size() - 1);
                
                // Signal waiting consumers
                notEmpty.signal();
                
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Remove and return the highest priority job. Blocks if empty. O(log n)
         */
        public PriorityJob take() throws InterruptedException {
            lock.lock();
            try {
                // Wait while heap is empty
                while (heap.isEmpty()) {
                    notEmpty.await();
                }
                
                return extractMax();
                
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Remove and return highest priority job with timeout.
         */
        public PriorityJob poll(long timeout, TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            
            lock.lock();
            try {
                while (heap.isEmpty()) {
                    if (nanos <= 0) {
                        return null;
                    }
                    nanos = notEmpty.awaitNanos(nanos);
                }
                
                return extractMax();
                
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Peek at highest priority job without removing.
         */
        public PriorityJob peek() {
            lock.lock();
            try {
                return heap.isEmpty() ? null : heap.get(0);
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Remove a specific job from the heap. O(n) find + O(log n) heapify
         */
        public boolean remove(PriorityJob job) {
            lock.lock();
            try {
                int index = job.getHeapIndex();
                
                // Validate index
                if (index < 0 || index >= heap.size() || heap.get(index) != job) {
                    // Index invalid, need linear search
                    index = -1;
                    for (int i = 0; i < heap.size(); i++) {
                        if (heap.get(i) == job) {
                            index = i;
                            break;
                        }
                    }
                }
                
                if (index == -1) {
                    return false;
                }
                
                removeAt(index);
                return true;
                
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Update priority of a job in the heap. O(log n) if index tracked, O(n) otherwise
         */
        public boolean updatePriority(PriorityJob job, int newPriority) {
            lock.lock();
            try {
                int index = job.getHeapIndex();
                
                // Validate index
                if (index < 0 || index >= heap.size() || heap.get(index) != job) {
                    return false;
                }
                
                int oldPriority = job.getPriority();
                job.setPriority(newPriority);
                
                // Re-heapify based on priority change direction
                if (newPriority > oldPriority) {
                    // Priority increased, might need to bubble up
                    siftUp(index);
                } else if (newPriority < oldPriority) {
                    // Priority decreased, might need to bubble down
                    siftDown(index);
                }
                
                return true;
                
            } finally {
                lock.unlock();
            }
        }
        
        public int size() {
            lock.lock();
            try {
                return heap.size();
            } finally {
                lock.unlock();
            }
        }
        
        public boolean isEmpty() {
            lock.lock();
            try {
                return heap.isEmpty();
            } finally {
                lock.unlock();
            }
        }
        
        // ==================== Heap Internal Operations ====================
        
        /**
         * Extract the maximum (root) element from heap.
         * Must be called while holding lock.
         */
        private PriorityJob extractMax() {
            PriorityJob max = heap.get(0);
            max.setHeapIndex(-1);
            
            int lastIndex = heap.size() - 1;
            
            if (lastIndex == 0) {
                // Only one element
                heap.remove(0);
            } else {
                // Move last element to root and sift down
                PriorityJob last = heap.remove(lastIndex);
                heap.set(0, last);
                last.setHeapIndex(0);
                siftDown(0);
            }
            
            return max;
        }
        
        /**
         * Remove element at given index.
         * Must be called while holding lock.
         */
        private void removeAt(int index) {
            int lastIndex = heap.size() - 1;
            
            if (index == lastIndex) {
                PriorityJob removed = heap.remove(lastIndex);
                removed.setHeapIndex(-1);
            } else {
                // Replace with last element
                PriorityJob removed = heap.get(index);
                removed.setHeapIndex(-1);
                
                PriorityJob last = heap.remove(lastIndex);
                heap.set(index, last);
                last.setHeapIndex(index);
                
                // Restore heap property - might need to go up or down
                siftUp(index);
                siftDown(index);
            }
        }
        
        /**
         * Bubble up element at given index until heap property is restored.
         * Used after insert or priority increase.
         * Must be called while holding lock.
         */
        private void siftUp(int index) {
            while (index > 0) {
                int parentIndex = (index - 1) / 2;
                PriorityJob current = heap.get(index);
                PriorityJob parent = heap.get(parentIndex);
                
                // If current has higher priority than parent, swap
                if (current.compareTo(parent) < 0) {
                    swap(index, parentIndex);
                    index = parentIndex;
                } else {
                    break;
                }
            }
        }
        
        /**
         * Bubble down element at given index until heap property is restored.
         * Used after extract or priority decrease.
         * Must be called while holding lock.
         */
        private void siftDown(int index) {
            int size = heap.size();
            
            while (true) {
                int leftChild = 2 * index + 1;
                int rightChild = 2 * index + 2;
                int highest = index;
                
                // Compare with left child
                if (leftChild < size && 
                    heap.get(leftChild).compareTo(heap.get(highest)) < 0) {
                    highest = leftChild;
                }
                
                // Compare with right child
                if (rightChild < size && 
                    heap.get(rightChild).compareTo(heap.get(highest)) < 0) {
                    highest = rightChild;
                }
                
                // If current is highest priority, heap property is satisfied
                if (highest == index) {
                    break;
                }
                
                // Swap with higher priority child and continue
                swap(index, highest);
                index = highest;
            }
        }
        
        /**
         * Swap elements at two indices.
         */
        private void swap(int i, int j) {
            PriorityJob jobI = heap.get(i);
            PriorityJob jobJ = heap.get(j);
            
            heap.set(i, jobJ);
            heap.set(j, jobI);
            
            jobI.setHeapIndex(j);
            jobJ.setHeapIndex(i);
        }
    }
    
    // ==================== Scheduler Implementation ====================
    
    private final ThreadSafePriorityHeap jobQueue;
    private final ConcurrentHashMap<String, PriorityJob> jobRegistry;
    private final ExecutorService executor;
    private final AtomicInteger jobIdCounter;
    private volatile boolean isRunning;
    private final int workerThreads;
    
    public PriorityJobScheduler(int workerThreads) {
        this.jobQueue = new ThreadSafePriorityHeap();
        this.jobRegistry = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(workerThreads);
        this.jobIdCounter = new AtomicInteger(0);
        this.isRunning = false;
        this.workerThreads = workerThreads;
    }
    
    /**
     * Submit a job with given priority.
     * 
     * @param task The task to execute
     * @param priority Job priority (higher = more important)
     * @return Job ID for tracking and priority updates
     */
    public String submit(Runnable task, int priority) {
        String jobId = "job-" + jobIdCounter.incrementAndGet();
        PriorityJob job = new PriorityJob(jobId, task, priority);
        
        jobRegistry.put(jobId, job);
        jobQueue.offer(job);
        
        System.out.println("Submitted: " + job);
        return jobId;
    }
    
    /**
     * Update priority of a queued job.
     * 
     * Uses heap index tracking for O(log n) update instead of O(n).
     * 
     * @param jobId The job ID
     * @param newPriority The new priority value
     * @return true if priority was updated, false if job not found or already running
     */
    public boolean updatePriority(String jobId, int newPriority) {
        PriorityJob job = jobRegistry.get(jobId);
        
        if (job == null) {
            System.out.println("Job not found: " + jobId);
            return false;
        }
        
        if (job.getStatus() != JobStatus.QUEUED) {
            System.out.println("Cannot update priority of " + job.getStatus() + " job: " + jobId);
            return false;
        }
        
        int oldPriority = job.getPriority();
        boolean updated = jobQueue.updatePriority(job, newPriority);
        
        if (updated) {
            System.out.println("Updated priority: " + jobId + " from " + oldPriority + " to " + newPriority);
        }
        
        return updated;
    }
    
    /**
     * Start the scheduler - begins processing jobs by priority.
     */
    public void start() {
        isRunning = true;
        
        // Worker threads that continuously process jobs
        for (int i = 0; i < workerThreads; i++) {
            final int workerId = i;
            executor.submit(() -> {
                System.out.println("Worker-" + workerId + " started");
                
                while (isRunning || !jobQueue.isEmpty()) {
                    try {
                        // Take highest priority job (blocks if empty)
                        PriorityJob job = jobQueue.poll(100, TimeUnit.MILLISECONDS);
                        
                        if (job != null && job.getStatus() == JobStatus.QUEUED) {
                            executeJob(job, workerId);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                System.out.println("Worker-" + workerId + " stopped");
            });
        }
    }
    
    private void executeJob(PriorityJob job, int workerId) {
        job.setStatus(JobStatus.RUNNING);
        System.out.println("Worker-" + workerId + " executing: " + job);
        
        try {
            job.getTask().run();
            job.setStatus(JobStatus.COMPLETED);
            System.out.println("Worker-" + workerId + " completed: " + job.getId());
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            System.err.println("Worker-" + workerId + " failed: " + job.getId() + " - " + e.getMessage());
        }
    }
    
    /**
     * Cancel a job if it's still queued.
     */
    public boolean cancel(String jobId) {
        PriorityJob job = jobRegistry.get(jobId);
        if (job != null && job.getStatus() == JobStatus.QUEUED) {
            job.setStatus(JobStatus.CANCELLED);
            jobQueue.remove(job);
            System.out.println("Cancelled: " + jobId);
            return true;
        }
        return false;
    }
    
    /**
     * Get job status.
     */
    public JobStatus getStatus(String jobId) {
        PriorityJob job = jobRegistry.get(jobId);
        return job != null ? job.getStatus() : null;
    }
    
    /**
     * Get current queue size.
     */
    public int getQueueSize() {
        return jobQueue.size();
    }
    
    /**
     * Shutdown the scheduler.
     */
    public void shutdown() {
        isRunning = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * ================================================================================
 * FOLLOW-UP: Priority Scheduler with Aging (Prevents Starvation)
 * ================================================================================
 * 
 * Problem: Low-priority jobs may starve if high-priority jobs keep arriving
 * Solution: Aging - gradually increase priority of waiting jobs
 */
class PrioritySchedulerWithAging {
    
    static class AgingJob {
        final String id;
        final Runnable task;
        int basePriority;
        final long submissionTime;
        int heapIndex = -1;
        
        AgingJob(String id, Runnable task, int priority) {
            this.id = id;
            this.task = task;
            this.basePriority = priority;
            this.submissionTime = System.currentTimeMillis();
        }
        
        // Effective priority increases with age (1 priority per second waiting)
        int getEffectivePriority() {
            long ageMs = System.currentTimeMillis() - submissionTime;
            int agingBonus = (int) (ageMs / 1000);
            return basePriority + agingBonus;
        }
        
        int compareTo(AgingJob other) {
            return Integer.compare(other.getEffectivePriority(), this.getEffectivePriority());
        }
    }
    
    private final ArrayList<AgingJob> heap;
    private final Lock lock;
    private final Condition notEmpty;
    private final ScheduledExecutorService reorderService;
    
    public PrioritySchedulerWithAging() {
        this.heap = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.reorderService = Executors.newSingleThreadScheduledExecutor();
        
        // Periodically re-heapify to account for aging
        reorderService.scheduleAtFixedRate(this::reheapify, 1, 1, TimeUnit.SECONDS);
    }
    
    private void reheapify() {
        lock.lock();
        try {
            // Rebuild heap from scratch (O(n) heapify)
            int n = heap.size();
            for (int i = n / 2 - 1; i >= 0; i--) {
                siftDownAging(i, n);
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void siftDownAging(int index, int size) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int highest = index;
            
            if (left < size && heap.get(left).compareTo(heap.get(highest)) < 0) {
                highest = left;
            }
            if (right < size && heap.get(right).compareTo(heap.get(highest)) < 0) {
                highest = right;
            }
            
            if (highest == index) break;
            
            Collections.swap(heap, index, highest);
            index = highest;
        }
    }
    
    public void submit(String id, Runnable task, int priority) {
        lock.lock();
        try {
            AgingJob job = new AgingJob(id, task, priority);
            job.heapIndex = heap.size();
            heap.add(job);
            siftUpAging(heap.size() - 1);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    private void siftUpAging(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parent)) < 0) {
                Collections.swap(heap, index, parent);
                heap.get(index).heapIndex = index;
                heap.get(parent).heapIndex = parent;
                index = parent;
            } else {
                break;
            }
        }
    }
    
    public AgingJob take() throws InterruptedException {
        lock.lock();
        try {
            while (heap.isEmpty()) {
                notEmpty.await();
            }
            
            AgingJob max = heap.get(0);
            AgingJob last = heap.remove(heap.size() - 1);
            
            if (!heap.isEmpty()) {
                heap.set(0, last);
                last.heapIndex = 0;
                siftDownAging(0, heap.size());
            }
            
            return max;
        } finally {
            lock.unlock();
        }
    }
    
    public void shutdown() {
        reorderService.shutdown();
    }
}

/**
 * ================================================================================
 * FOLLOW-UP: Multi-Level Feedback Queue (MLFQ)
 * ================================================================================
 */
class MultiLevelFeedbackQueue {
    
    private static final int NUM_LEVELS = 4;
    private final List<LinkedList<Runnable>> queues;
    private final int[] timeSlices;
    private final Lock lock;
    private final Condition notEmpty;
    
    public MultiLevelFeedbackQueue() {
        this.queues = new ArrayList<>();
        this.timeSlices = new int[]{10, 20, 40, 80};
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        
        for (int i = 0; i < NUM_LEVELS; i++) {
            queues.add(new LinkedList<>());
        }
    }
    
    // New jobs start at highest priority (level 0)
    public void submit(Runnable task) {
        lock.lock();
        try {
            queues.get(0).addLast(task);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    // Get next job from highest priority non-empty queue
    public Runnable getNext() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                for (int i = 0; i < NUM_LEVELS; i++) {
                    if (!queues.get(i).isEmpty()) {
                        return queues.get(i).removeFirst();
                    }
                }
                notEmpty.await();
            }
        } finally {
            lock.unlock();
        }
    }
    
    // Demote job to lower priority
    public void demote(Runnable task, int currentLevel) {
        lock.lock();
        try {
            int newLevel = Math.min(currentLevel + 1, NUM_LEVELS - 1);
            queues.get(newLevel).addLast(task);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    // Boost all jobs to highest priority (prevents starvation)
    public void boostAll() {
        lock.lock();
        try {
            for (int i = 1; i < NUM_LEVELS; i++) {
                queues.get(0).addAll(queues.get(i));
                queues.get(i).clear();
            }
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
class PriorityJobSchedulerTest {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Priority Job Scheduler Test (Custom Heap) ===\n");
        
        PriorityJobScheduler scheduler = new PriorityJobScheduler(2);
        
        // Submit jobs with different priorities
        String lowJob1 = scheduler.submit(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            System.out.println("  -> Low priority job 1 (priority 1) executed");
        }, 1);
        
        String lowJob2 = scheduler.submit(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            System.out.println("  -> Low priority job 2 (priority 1) executed");
        }, 1);
        
        String highJob = scheduler.submit(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            System.out.println("  -> HIGH priority job (priority 10) executed");
        }, 10);
        
        String mediumJob = scheduler.submit(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
            System.out.println("  -> Medium priority job (priority 5) executed");
        }, 5);
        
        System.out.println("\nQueue size before start: " + scheduler.getQueueSize());
        
        // Dynamic priority update
        System.out.println("\nUpdating lowJob2 priority from 1 to 15 (now highest!)...");
        boolean updated = scheduler.updatePriority(lowJob2, 15);
        System.out.println("Update successful: " + updated);
        
        // Start scheduler
        System.out.println("\nStarting scheduler...\n");
        scheduler.start();
        
        // Wait for all jobs
        Thread.sleep(3000);
        
        System.out.println("\nFinal statuses:");
        System.out.println("  Low job 1 (original priority 1): " + scheduler.getStatus(lowJob1));
        System.out.println("  Low job 2 (boosted to 15): " + scheduler.getStatus(lowJob2));
        System.out.println("  High job (priority 10): " + scheduler.getStatus(highJob));
        System.out.println("  Medium job (priority 5): " + scheduler.getStatus(mediumJob));
        
        scheduler.shutdown();
        
        System.out.println("\nExpected order: lowJob2(15) -> highJob(10) -> mediumJob(5) -> lowJob1(1)");
        System.out.println("\nAll tests completed!");
    }
}
