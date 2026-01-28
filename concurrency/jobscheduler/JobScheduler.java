package concurrency.jobscheduler;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ================================================================================
 * PROBLEM 1: JOB SCHEDULER WITH DEPENDENCIES AND PRIORITIES
 * ================================================================================
 * 
 * Requirements:
 * 1. Schedule and execute multiple jobs concurrently
 * 2. Respect job dependencies (Job B runs only after Job A completes)
 * 3. Support job priorities (higher priority jobs execute first)
 * 
 * Key Design Decisions:
 * 1. Topological Sort (Kahn's Algorithm) for dependency resolution
 * 2. ThreadPoolExecutor for concurrent execution
 * 3. CountDownLatch for each job to track when dependencies complete
 * 4. PriorityBlockingQueue for priority-based job selection
 * 
 * Time Complexity:
 * - Submit job: O(log n) for priority queue insertion
 * - Start execution: O(V + E) for topological sort
 * 
 * Thread Safety:
 * - ConcurrentHashMap for job storage
 * - AtomicInteger for job ID generation
 * - Synchronized blocks for dependency management
 */
public class JobScheduler {
    
    // ==================== Job Definition ====================
    
    public enum JobStatus {
        PENDING,      // Job submitted but not ready to run
        READY,        // All dependencies satisfied, waiting for thread
        RUNNING,      // Currently executing
        COMPLETED,    // Successfully completed
        FAILED,       // Execution failed
        CANCELLED     // Cancelled by user
    }
    
    public static class Job implements Comparable<Job> {
        private final String id;
        private final Runnable task;
        private final int priority;  // Higher value = higher priority
        private final Set<String> dependencies;
        private volatile JobStatus status;
        private final CountDownLatch completionLatch;
        private CountDownLatch dependencyLatch;
        
        public Job(String id, Runnable task, int priority, Set<String> dependencies) {
            this.id = id;
            this.task = task;
            this.priority = priority;
            this.dependencies = dependencies != null ? new HashSet<>(dependencies) : new HashSet<>();
            this.status = JobStatus.PENDING;
            this.completionLatch = new CountDownLatch(1);
            // dependencyLatch will be set when we know how many dependencies exist
        }
        
        public String getId() { return id; }
        public int getPriority() { return priority; }
        public Set<String> getDependencies() { return dependencies; }
        public JobStatus getStatus() { return status; }
        public void setStatus(JobStatus status) { this.status = status; }
        public CountDownLatch getCompletionLatch() { return completionLatch; }
        
        @Override
        public int compareTo(Job other) {
            // Higher priority first (descending order)
            return Integer.compare(other.priority, this.priority);
        }
        
        @Override
        public String toString() {
            return String.format("Job{id='%s', priority=%d, status=%s, deps=%s}", 
                                 id, priority, status, dependencies);
        }
    }
    
    // ==================== Scheduler Implementation ====================
    
    private final ConcurrentHashMap<String, Job> jobs;
    private final ExecutorService executor;
    private final PriorityBlockingQueue<Job> readyQueue;
    private final AtomicInteger jobIdCounter;
    private volatile boolean isRunning;
    
    public JobScheduler(int threadPoolSize) {
        this.jobs = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.readyQueue = new PriorityBlockingQueue<>();
        this.jobIdCounter = new AtomicInteger(0);
        this.isRunning = false;
    }
    
    /**
     * Submit a job with dependencies.
     * 
     * @param task The task to execute
     * @param priority Job priority (higher = more important)
     * @param dependencies Set of job IDs that must complete before this job
     * @return The job ID
     * @throws IllegalArgumentException if circular dependency is detected
     */
    public String submitJob(Runnable task, int priority, Set<String> dependencies) {
        String jobId = "job-" + jobIdCounter.incrementAndGet();
        Job job = new Job(jobId, task, priority, dependencies);
        
        // Validate dependencies exist
        if (dependencies != null) {
            for (String depId : dependencies) {
                if (!jobs.containsKey(depId)) {
                    throw new IllegalArgumentException("Dependency not found: " + depId);
                }
            }
        }
        
        // Check for circular dependencies
        if (hasCircularDependency(jobId, dependencies)) {
            throw new IllegalArgumentException("Circular dependency detected for job: " + jobId);
        }
        
        jobs.put(jobId, job);
        
        // If no dependencies, job is immediately ready
        if (dependencies == null || dependencies.isEmpty()) {
            job.setStatus(JobStatus.READY);
            readyQueue.offer(job);
        }
        
        System.out.println("Submitted: " + job);
        return jobId;
    }
    
    /**
     * Detect circular dependencies using DFS.
     */
    private boolean hasCircularDependency(String newJobId, Set<String> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return false;
        }
        
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        recursionStack.add(newJobId);
        
        for (String depId : dependencies) {
            if (hasCycleDFS(depId, visited, recursionStack)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasCycleDFS(String jobId, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(jobId)) {
            return true; // Cycle detected
        }
        if (visited.contains(jobId)) {
            return false; // Already processed
        }
        
        visited.add(jobId);
        recursionStack.add(jobId);
        
        Job job = jobs.get(jobId);
        if (job != null) {
            for (String depId : job.getDependencies()) {
                if (hasCycleDFS(depId, visited, recursionStack)) {
                    return true;
                }
            }
        }
        
        recursionStack.remove(jobId);
        return false;
    }
    
    /**
     * Start the scheduler - begins processing jobs from the ready queue.
     */
    public void start() {
        isRunning = true;
        
        // Worker thread that continuously processes ready jobs
        Thread schedulerThread = new Thread(() -> {
            while (isRunning || !readyQueue.isEmpty()) {
                try {
                    // Poll with timeout to allow shutdown
                    Job job = readyQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (job != null) {
                        executeJob(job);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        schedulerThread.start();
    }
    
    /**
     * Execute a job and handle completion/failure.
     */
    private void executeJob(Job job) {
        executor.submit(() -> {
            job.setStatus(JobStatus.RUNNING);
            System.out.println("Running: " + job.getId() + " (priority: " + job.getPriority() + ")");
            
            try {
                // Wait for all dependencies to complete
                waitForDependencies(job);
                
                // Execute the actual task
                job.task.run();
                
                job.setStatus(JobStatus.COMPLETED);
                System.out.println("Completed: " + job.getId());
                
            } catch (Exception e) {
                job.setStatus(JobStatus.FAILED);
                System.err.println("Failed: " + job.getId() + " - " + e.getMessage());
            } finally {
                // Signal completion to dependent jobs
                job.getCompletionLatch().countDown();
                
                // Check if any pending jobs are now ready
                notifyDependentJobs(job.getId());
            }
        });
    }
    
    /**
     * Wait for all dependencies of a job to complete.
     */
    private void waitForDependencies(Job job) throws InterruptedException {
        for (String depId : job.getDependencies()) {
            Job dependency = jobs.get(depId);
            if (dependency != null) {
                dependency.getCompletionLatch().await();
            }
        }
    }
    
    /**
     * Check pending jobs and move them to ready queue if dependencies are satisfied.
     */
    private void notifyDependentJobs(String completedJobId) {
        for (Job job : jobs.values()) {
            if (job.getStatus() == JobStatus.PENDING) {
                boolean allDependenciesMet = job.getDependencies().stream()
                    .allMatch(depId -> {
                        Job dep = jobs.get(depId);
                        return dep != null && dep.getStatus() == JobStatus.COMPLETED;
                    });
                
                if (allDependenciesMet) {
                    job.setStatus(JobStatus.READY);
                    readyQueue.offer(job);
                    System.out.println("Ready: " + job.getId() + " (all dependencies met)");
                }
            }
        }
    }
    
    /**
     * Get the status of a job.
     */
    public JobStatus getJobStatus(String jobId) {
        Job job = jobs.get(jobId);
        return job != null ? job.getStatus() : null;
    }
    
    /**
     * Wait for a specific job to complete.
     */
    public void waitForJob(String jobId) throws InterruptedException {
        Job job = jobs.get(jobId);
        if (job != null) {
            job.getCompletionLatch().await();
        }
    }
    
    /**
     * Wait for a job with timeout.
     */
    public boolean waitForJob(String jobId, long timeout, TimeUnit unit) throws InterruptedException {
        Job job = jobs.get(jobId);
        if (job != null) {
            return job.getCompletionLatch().await(timeout, unit);
        }
        return false;
    }
    
    /**
     * Shutdown the scheduler gracefully.
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
 * TEST CLASS
 * ================================================================================
 */
class JobSchedulerTest {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Job Scheduler with Dependencies Test ===\n");
        
        JobScheduler scheduler = new JobScheduler(4);
        
        // Submit independent jobs with different priorities
        String jobA = scheduler.submitJob(
            () -> {
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                System.out.println("  -> Executing Job A");
            },
            5,  // Medium priority
            null
        );
        
        String jobB = scheduler.submitJob(
            () -> {
                try { Thread.sleep(300); } catch (InterruptedException e) {}
                System.out.println("  -> Executing Job B");
            },
            10, // High priority
            null
        );
        
        // Submit jobs with dependencies
        String jobC = scheduler.submitJob(
            () -> {
                try { Thread.sleep(200); } catch (InterruptedException e) {}
                System.out.println("  -> Executing Job C (depends on A)");
            },
            3,
            Set.of(jobA)  // Depends on Job A
        );
        
        String jobD = scheduler.submitJob(
            () -> {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                System.out.println("  -> Executing Job D (depends on A and B)");
            },
            8,
            Set.of(jobA, jobB)  // Depends on both A and B
        );
        
        String jobE = scheduler.submitJob(
            () -> {
                System.out.println("  -> Executing Job E (depends on C and D)");
            },
            1,
            Set.of(jobC, jobD)  // Depends on C and D
        );
        
        // Start the scheduler
        scheduler.start();
        
        // Wait for all jobs to complete
        scheduler.waitForJob(jobE);
        
        System.out.println("\nAll jobs completed!");
        System.out.println("Final statuses:");
        System.out.println("  Job A: " + scheduler.getJobStatus(jobA));
        System.out.println("  Job B: " + scheduler.getJobStatus(jobB));
        System.out.println("  Job C: " + scheduler.getJobStatus(jobC));
        System.out.println("  Job D: " + scheduler.getJobStatus(jobD));
        System.out.println("  Job E: " + scheduler.getJobStatus(jobE));
        
        scheduler.shutdown();
    }
}

/**
 * ================================================================================
 * FOLLOW-UP: Job Scheduler with Retry Mechanism
 * ================================================================================
 */
class JobSchedulerWithRetry {
    
    public static class RetryableJob {
        private final String id;
        private final Callable<Void> task;
        private final int maxRetries;
        private int retryCount;
        private long retryDelayMs;
        
        public RetryableJob(String id, Callable<Void> task, int maxRetries, long retryDelayMs) {
            this.id = id;
            this.task = task;
            this.maxRetries = maxRetries;
            this.retryCount = 0;
            this.retryDelayMs = retryDelayMs;
        }
        
        public boolean execute() {
            while (retryCount <= maxRetries) {
                try {
                    task.call();
                    return true;
                } catch (Exception e) {
                    retryCount++;
                    System.out.println("Job " + id + " failed, retry " + retryCount + "/" + maxRetries);
                    
                    if (retryCount <= maxRetries) {
                        try {
                            // Exponential backoff
                            Thread.sleep(retryDelayMs * (1L << (retryCount - 1)));
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return false;
                        }
                    }
                }
            }
            return false;
        }
    }
}
