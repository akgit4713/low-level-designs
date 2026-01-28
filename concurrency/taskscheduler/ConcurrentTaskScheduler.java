package concurrency.taskscheduler;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ================================================================================
 * PROBLEM 4: CONCURRENT TASK SCHEDULER
 * ================================================================================
 * 
 * Requirements:
 * 1. Execute multiple tasks concurrently
 * 2. Limit maximum number of parallel executions
 * 3. Support task submission and completion tracking
 * 
 * Key Design Decisions:
 * 1. Semaphore to limit concurrent executions (acts as a permit system)
 * 2. ExecutorService for thread management
 * 3. CompletableFuture for async composition and result handling
 * 
 * Why Semaphore?
 * - Perfect for "N permits" scenarios (N concurrent tasks allowed)
 * - acquire() blocks when no permits available
 * - release() returns permit to pool
 * 
 * Time Complexity:
 * - Submit task: O(1)
 * - acquire/release: O(1) amortized
 */
public class ConcurrentTaskScheduler {
    
    private final int maxConcurrency;
    private final Semaphore semaphore;
    private final ExecutorService executor;
    private final AtomicInteger activeTaskCount;
    private final AtomicInteger completedTaskCount;
    private volatile boolean isShutdown;
    
    /**
     * Creates a task scheduler with limited concurrency.
     * 
     * @param maxConcurrency Maximum number of tasks that can run simultaneously
     */
    public ConcurrentTaskScheduler(int maxConcurrency) {
        if (maxConcurrency <= 0) {
            throw new IllegalArgumentException("maxConcurrency must be positive");
        }
        
        this.maxConcurrency = maxConcurrency;
        // Semaphore with N permits - only N tasks can run at once
        // fair=true ensures FIFO order for waiting threads
        this.semaphore = new Semaphore(maxConcurrency, true);
        this.executor = Executors.newCachedThreadPool();  // Elastic thread pool
        this.activeTaskCount = new AtomicInteger(0);
        this.completedTaskCount = new AtomicInteger(0);
        this.isShutdown = false;
    }
    
    /**
     * Submit a task for execution. Blocks if max concurrency reached.
     * 
     * @param task The task to execute
     * @return CompletableFuture for tracking completion
     */
    public CompletableFuture<Void> submit(Runnable task) {
        if (isShutdown) {
            throw new RejectedExecutionException("Scheduler is shutdown");
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                // Acquire permit (blocks if none available)
                semaphore.acquire();
                activeTaskCount.incrementAndGet();
                
                String threadName = Thread.currentThread().getName();
                System.out.println("[" + threadName + "] Task started. Active: " + activeTaskCount.get());
                
                try {
                    // Execute the actual task
                    task.run();
                } finally {
                    // Always release permit
                    activeTaskCount.decrementAndGet();
                    completedTaskCount.incrementAndGet();
                    semaphore.release();
                    
                    System.out.println("[" + threadName + "] Task completed. Active: " + activeTaskCount.get());
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task interrupted", e);
            }
        }, executor);
    }
    
    /**
     * Submit a task with a result.
     * 
     * @param task The callable task
     * @return CompletableFuture with the result
     */
    public <T> CompletableFuture<T> submit(Callable<T> task) {
        if (isShutdown) {
            throw new RejectedExecutionException("Scheduler is shutdown");
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                semaphore.acquire();
                activeTaskCount.incrementAndGet();
                
                try {
                    return task.call();
                } finally {
                    activeTaskCount.decrementAndGet();
                    completedTaskCount.incrementAndGet();
                    semaphore.release();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task interrupted", e);
            } catch (Exception e) {
                throw new RuntimeException("Task failed", e);
            }
        }, executor);
    }
    
    /**
     * Submit a task with timeout. Fails if cannot start within timeout.
     * 
     * @param task The task to execute
     * @param timeout Maximum time to wait for a slot
     * @param unit Time unit
     * @return CompletableFuture, or failed future if timeout
     */
    public CompletableFuture<Void> submitWithTimeout(Runnable task, long timeout, TimeUnit unit) {
        if (isShutdown) {
            return CompletableFuture.failedFuture(new RejectedExecutionException("Scheduler is shutdown"));
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                // Try to acquire with timeout
                boolean acquired = semaphore.tryAcquire(timeout, unit);
                if (!acquired) {
                    throw new TimeoutException("Could not acquire slot within timeout");
                }
                
                activeTaskCount.incrementAndGet();
                
                try {
                    task.run();
                } finally {
                    activeTaskCount.decrementAndGet();
                    completedTaskCount.incrementAndGet();
                    semaphore.release();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task interrupted", e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    /**
     * Submit multiple tasks and wait for all to complete.
     * 
     * @param tasks List of tasks to execute
     * @return CompletableFuture that completes when all tasks finish
     */
    public CompletableFuture<Void> submitAll(List<Runnable> tasks) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (Runnable task : tasks) {
            futures.add(submit(task));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    /**
     * Get current number of active (running) tasks.
     */
    public int getActiveTaskCount() {
        return activeTaskCount.get();
    }
    
    /**
     * Get total number of completed tasks.
     */
    public int getCompletedTaskCount() {
        return completedTaskCount.get();
    }
    
    /**
     * Get number of available slots.
     */
    public int getAvailableSlots() {
        return semaphore.availablePermits();
    }
    
    /**
     * Get number of tasks waiting for a slot.
     */
    public int getQueueLength() {
        return semaphore.getQueueLength();
    }
    
    /**
     * Shutdown the scheduler gracefully.
     */
    public void shutdown() {
        isShutdown = true;
        executor.shutdown();
    }
    
    /**
     * Shutdown and wait for completion.
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        shutdown();
        return executor.awaitTermination(timeout, unit);
    }
}

/**
 * ================================================================================
 * ALTERNATIVE: Task Scheduler with Rate Limiting
 * ================================================================================
 */
class RateLimitedTaskScheduler {
    
    private final Semaphore concurrencySemaphore;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;
    private final long minIntervalMs;
    private long lastSubmitTime;
    private final Object timeLock = new Object();
    
    /**
     * Creates a scheduler with both concurrency and rate limiting.
     * 
     * @param maxConcurrency Max parallel tasks
     * @param maxTasksPerSecond Max tasks submitted per second
     */
    public RateLimitedTaskScheduler(int maxConcurrency, double maxTasksPerSecond) {
        this.concurrencySemaphore = new Semaphore(maxConcurrency);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.executor = Executors.newCachedThreadPool();
        this.minIntervalMs = (long) (1000.0 / maxTasksPerSecond);
        this.lastSubmitTime = 0;
    }
    
    public CompletableFuture<Void> submit(Runnable task) {
        // Rate limiting
        synchronized (timeLock) {
            long now = System.currentTimeMillis();
            long elapsed = now - lastSubmitTime;
            
            if (elapsed < minIntervalMs) {
                try {
                    Thread.sleep(minIntervalMs - elapsed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            lastSubmitTime = System.currentTimeMillis();
        }
        
        // Concurrency limiting
        return CompletableFuture.runAsync(() -> {
            try {
                concurrencySemaphore.acquire();
                try {
                    task.run();
                } finally {
                    concurrencySemaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, executor);
    }
    
    public void shutdown() {
        scheduler.shutdown();
        executor.shutdown();
    }
}

/**
 * ================================================================================
 * ALTERNATIVE: Task Scheduler with Work Stealing (ForkJoinPool)
 * ================================================================================
 */
class WorkStealingTaskScheduler {
    
    private final ForkJoinPool pool;
    private final Semaphore concurrencySemaphore;
    
    public WorkStealingTaskScheduler(int maxConcurrency) {
        // ForkJoinPool uses work-stealing algorithm for better CPU utilization
        this.pool = new ForkJoinPool(maxConcurrency);
        this.concurrencySemaphore = new Semaphore(maxConcurrency);
    }
    
    public CompletableFuture<Void> submit(Runnable task) {
        return CompletableFuture.runAsync(() -> {
            try {
                concurrencySemaphore.acquire();
                try {
                    task.run();
                } finally {
                    concurrencySemaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, pool);
    }
    
    /**
     * Submit a recursive task (good for divide-and-conquer algorithms).
     */
    public <T> T invoke(ForkJoinTask<T> task) {
        return pool.invoke(task);
    }
    
    public void shutdown() {
        pool.shutdown();
    }
}

/**
 * ================================================================================
 * TEST CLASS
 * ================================================================================
 */
class ConcurrentTaskSchedulerTest {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Concurrent Task Scheduler Test ===\n");
        
        // Test with max 3 concurrent tasks
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(3);
        
        System.out.println("Submitting 10 tasks with max concurrency of 3...\n");
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            CompletableFuture<Void> future = scheduler.submit(() -> {
                System.out.println("  Task " + taskId + " executing...");
                try {
                    Thread.sleep(500 + (int)(Math.random() * 500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("  Task " + taskId + " done!");
            });
            futures.add(future);
        }
        
        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        System.out.println("\nAll tasks completed!");
        System.out.println("Total completed: " + scheduler.getCompletedTaskCount());
        System.out.println("Available slots: " + scheduler.getAvailableSlots());
        
        scheduler.shutdown();
        
        // Test with callable (returning values)
        System.out.println("\n--- Test with Callable tasks ---\n");
        
        ConcurrentTaskScheduler scheduler2 = new ConcurrentTaskScheduler(2);
        
        List<CompletableFuture<Integer>> resultFutures = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            CompletableFuture<Integer> future = scheduler2.submit(() -> {
                Thread.sleep(300);
                return num * num;
            });
            resultFutures.add(future);
        }
        
        System.out.println("Results:");
        for (int i = 0; i < resultFutures.size(); i++) {
            System.out.println("  Task " + (i + 1) + " result: " + resultFutures.get(i).get());
        }
        
        scheduler2.shutdown();
    }
}
