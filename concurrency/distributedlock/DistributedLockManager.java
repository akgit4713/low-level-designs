package concurrency.distributedlock;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.*;

/**
 * ================================================================================
 * PROBLEM 9: DISTRIBUTED LOCK MANAGER
 * ================================================================================
 * 
 * Requirements:
 * 1. Coordinate job execution across multiple nodes
 * 2. Prevent concurrent execution of conflicting jobs
 * 3. Handle node failures gracefully
 * 4. Support lock timeout and automatic release
 * 
 * Key Design Considerations:
 * 1. Lock granularity (resource-level vs global)
 * 2. Deadlock prevention/detection
 * 3. Fault tolerance (what if lock holder crashes?)
 * 4. Fencing tokens (prevent split-brain)
 * 
 * Real-World Implementations:
 * - Redis: SETNX + TTL + Lua scripts (Redlock algorithm)
 * - ZooKeeper: Ephemeral sequential nodes
 * - etcd: Lease-based locking
 * - Database: SELECT FOR UPDATE with timeout
 * 
 * This implementation simulates distributed behavior in-memory for interviews.
 */
public class DistributedLockManager {
    
    // ==================== Lock Abstraction ====================
    
    public interface DistributedLock {
        /**
         * Acquire the lock. Blocks until lock is acquired or timeout.
         * 
         * @param timeout Maximum time to wait
         * @param unit Time unit
         * @return Fencing token if acquired, -1 if timeout
         * @throws InterruptedException if interrupted while waiting
         */
        long tryLock(long timeout, TimeUnit unit) throws InterruptedException;
        
        /**
         * Release the lock.
         * 
         * @param fencingToken The token received when lock was acquired
         * @return true if released successfully
         */
        boolean unlock(long fencingToken);
        
        /**
         * Check if lock is currently held.
         */
        boolean isLocked();
        
        /**
         * Get the current lock holder (node ID).
         */
        String getHolder();
    }
    
    // ==================== Lock Implementation ====================
    
    /**
     * In-memory distributed lock simulation.
     * In production, this would be backed by Redis/ZooKeeper/etcd.
     */
    public static class InMemoryDistributedLock implements DistributedLock {
        
        private final String lockName;
        private final ReentrantLock lock;
        private final Condition available;
        private final long ttlMs;  // Lock auto-expiry time
        
        // Lock state
        private volatile String holder;  // Node ID of lock holder
        private volatile long fencingToken;  // Monotonically increasing token
        private volatile long expirationTime;  // When lock auto-expires
        
        // Fencing token generator (should be global in real distributed system)
        private static final AtomicLong tokenGenerator = new AtomicLong(0);
        
        public InMemoryDistributedLock(String lockName, long ttlMs) {
            this.lockName = lockName;
            this.lock = new ReentrantLock(true);  // Fair lock
            this.available = lock.newCondition();
            this.ttlMs = ttlMs;
            this.holder = null;
            this.fencingToken = -1;
            this.expirationTime = 0;
        }
        
        @Override
        public long tryLock(long timeout, TimeUnit unit) throws InterruptedException {
            String nodeId = getNodeId();
            long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
            
            lock.lock();
            try {
                while (true) {
                    // Check if lock is available (not held or expired)
                    if (!isLocked() || isExpired()) {
                        // Acquire the lock
                        this.holder = nodeId;
                        this.fencingToken = tokenGenerator.incrementAndGet();
                        this.expirationTime = System.currentTimeMillis() + ttlMs;
                        
                        System.out.println("[" + lockName + "] Lock acquired by " + nodeId + 
                                         " (token: " + fencingToken + ", expires in: " + ttlMs + "ms)");
                        
                        return fencingToken;
                    }
                    
                    // Calculate remaining wait time
                    long remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) {
                        System.out.println("[" + lockName + "] Lock acquisition timeout for " + nodeId);
                        return -1;  // Timeout
                    }
                    
                    // Wait for lock to become available
                    available.await(Math.min(remaining, 100), TimeUnit.MILLISECONDS);
                }
            } finally {
                lock.unlock();
            }
        }
        
        @Override
        public boolean unlock(long providedToken) {
            lock.lock();
            try {
                // Validate fencing token
                if (providedToken != this.fencingToken) {
                    System.out.println("[" + lockName + "] Invalid fencing token! " +
                                     "Expected: " + fencingToken + ", Got: " + providedToken);
                    return false;
                }
                
                String oldHolder = this.holder;
                this.holder = null;
                this.fencingToken = -1;
                this.expirationTime = 0;
                
                System.out.println("[" + lockName + "] Lock released by " + oldHolder);
                
                available.signalAll();
                return true;
                
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Renew lock lease (extend expiration).
         * Important for long-running operations.
         */
        public boolean renew(long providedToken, long additionalMs) {
            lock.lock();
            try {
                if (providedToken != this.fencingToken) {
                    return false;
                }
                
                this.expirationTime = System.currentTimeMillis() + additionalMs;
                System.out.println("[" + lockName + "] Lock renewed until " + expirationTime);
                return true;
                
            } finally {
                lock.unlock();
            }
        }
        
        @Override
        public boolean isLocked() {
            return holder != null && !isExpired();
        }
        
        @Override
        public String getHolder() {
            return holder;
        }
        
        private boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
        
        private String getNodeId() {
            // In real system, this would be actual node identifier
            return "node-" + Thread.currentThread().getId();
        }
    }
    
    // ==================== Lock Manager ====================
    
    private final ConcurrentHashMap<String, InMemoryDistributedLock> locks;
    private final long defaultTtlMs;
    private final ScheduledExecutorService expirationChecker;
    
    public DistributedLockManager(long defaultTtlMs) {
        this.locks = new ConcurrentHashMap<>();
        this.defaultTtlMs = defaultTtlMs;
        this.expirationChecker = Executors.newSingleThreadScheduledExecutor();
        
        // Periodically check for expired locks
        expirationChecker.scheduleAtFixedRate(
            this::checkExpiredLocks, 100, 100, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Get or create a lock for the given resource.
     */
    public InMemoryDistributedLock getLock(String resourceName) {
        return locks.computeIfAbsent(resourceName, 
            name -> new InMemoryDistributedLock(name, defaultTtlMs));
    }
    
    /**
     * Execute a task while holding the lock.
     * Automatically acquires and releases the lock.
     */
    public <T> T executeWithLock(String resourceName, long timeout, TimeUnit unit, 
                                  Callable<T> task) throws Exception {
        InMemoryDistributedLock lock = getLock(resourceName);
        long token = lock.tryLock(timeout, unit);
        
        if (token == -1) {
            throw new TimeoutException("Could not acquire lock for: " + resourceName);
        }
        
        try {
            return task.call();
        } finally {
            lock.unlock(token);
        }
    }
    
    private void checkExpiredLocks() {
        for (InMemoryDistributedLock lock : locks.values()) {
            // Expired locks are automatically released on next acquire attempt
            // This is just for logging/monitoring
        }
    }
    
    public void shutdown() {
        expirationChecker.shutdown();
    }
}

/**
 * ================================================================================
 * SIMULATION: Redis-like Distributed Lock (Redlock Algorithm Simplified)
 * ================================================================================
 */
class RedisLockSimulation {
    
    // Simulates multiple Redis instances
    private final Map<Integer, Map<String, LockEntry>> redisInstances;
    private final int numInstances;
    private final int quorum;
    
    static class LockEntry {
        final String value;  // Unique lock value (for safe release)
        final long expirationTime;
        
        LockEntry(String value, long ttlMs) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMs;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
    
    public RedisLockSimulation(int numInstances) {
        this.numInstances = numInstances;
        this.quorum = numInstances / 2 + 1;
        this.redisInstances = new ConcurrentHashMap<>();
        
        for (int i = 0; i < numInstances; i++) {
            redisInstances.put(i, new ConcurrentHashMap<>());
        }
    }
    
    /**
     * Acquire lock using Redlock algorithm:
     * 1. Get current time
     * 2. Try to acquire lock on N instances
     * 3. Lock is acquired if majority (N/2+1) agree AND time elapsed < TTL
     */
    public String tryLock(String lockName, long ttlMs) {
        String lockValue = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        int successCount = 0;
        
        // Try to acquire on all instances
        for (int i = 0; i < numInstances; i++) {
            if (trySetOnInstance(i, lockName, lockValue, ttlMs)) {
                successCount++;
            }
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        long validityTime = ttlMs - elapsed;
        
        // Check if we got quorum within validity time
        if (successCount >= quorum && validityTime > 0) {
            System.out.println("[Redlock] Lock acquired on " + successCount + "/" + 
                             numInstances + " instances");
            return lockValue;
        }
        
        // Failed - release all locks
        for (int i = 0; i < numInstances; i++) {
            releaseOnInstance(i, lockName, lockValue);
        }
        
        System.out.println("[Redlock] Failed to acquire lock (got " + successCount + 
                         "/" + quorum + " required)");
        return null;
    }
    
    public void unlock(String lockName, String lockValue) {
        for (int i = 0; i < numInstances; i++) {
            releaseOnInstance(i, lockName, lockValue);
        }
        System.out.println("[Redlock] Lock released");
    }
    
    // Simulates Redis SETNX (SET if Not eXists)
    private boolean trySetOnInstance(int instance, String key, String value, long ttlMs) {
        Map<String, LockEntry> redis = redisInstances.get(instance);
        
        synchronized (redis) {
            LockEntry existing = redis.get(key);
            if (existing == null || existing.isExpired()) {
                redis.put(key, new LockEntry(value, ttlMs));
                return true;
            }
            return false;
        }
    }
    
    // Simulates Redis DELETE with value check (Lua script in real Redis)
    private void releaseOnInstance(int instance, String key, String value) {
        Map<String, LockEntry> redis = redisInstances.get(instance);
        
        synchronized (redis) {
            LockEntry existing = redis.get(key);
            if (existing != null && existing.value.equals(value)) {
                redis.remove(key);
            }
        }
    }
}

/**
 * ================================================================================
 * SIMULATION: ZooKeeper-like Distributed Lock
 * ================================================================================
 */
class ZooKeeperLockSimulation {
    
    // Simulates ZooKeeper znodes
    private final TreeMap<String, String> znodes;  // path -> data
    private final ReentrantLock zkLock;
    private final AtomicLong sequenceCounter;
    
    public ZooKeeperLockSimulation() {
        this.znodes = new TreeMap<>();
        this.zkLock = new ReentrantLock();
        this.sequenceCounter = new AtomicLong(0);
    }
    
    /**
     * ZooKeeper lock acquisition:
     * 1. Create ephemeral sequential node: /lock/lock-000000001
     * 2. Get children of /lock and sort them
     * 3. If our node is smallest, we have the lock
     * 4. Otherwise, watch the node just before ours
     */
    public String acquireLock(String lockPath, long timeout, TimeUnit unit) 
            throws InterruptedException {
        
        String nodeId = Thread.currentThread().getName();
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        
        // Create sequential ephemeral node
        String myNode;
        zkLock.lock();
        try {
            long seq = sequenceCounter.incrementAndGet();
            myNode = lockPath + "/lock-" + String.format("%010d", seq);
            znodes.put(myNode, nodeId);
        } finally {
            zkLock.unlock();
        }
        
        System.out.println("[ZK] Created node: " + myNode);
        
        // Wait until we have the lock
        while (System.currentTimeMillis() < deadline) {
            zkLock.lock();
            try {
                // Get all children
                SortedMap<String, String> children = znodes.subMap(lockPath + "/", lockPath + "0");
                
                if (children.isEmpty() || children.firstKey().equals(myNode)) {
                    System.out.println("[ZK] Lock acquired: " + myNode);
                    return myNode;
                }
                
                // Watch the node before ours
                String nodeToWatch = null;
                for (String node : children.keySet()) {
                    if (node.equals(myNode)) break;
                    nodeToWatch = node;
                }
                
                System.out.println("[ZK] Waiting for: " + nodeToWatch);
                
            } finally {
                zkLock.unlock();
            }
            
            Thread.sleep(50);  // Polling (real ZK uses watches)
        }
        
        // Timeout - clean up our node
        zkLock.lock();
        try {
            znodes.remove(myNode);
        } finally {
            zkLock.unlock();
        }
        
        return null;
    }
    
    public void releaseLock(String myNode) {
        zkLock.lock();
        try {
            znodes.remove(myNode);
            System.out.println("[ZK] Released: " + myNode);
        } finally {
            zkLock.unlock();
        }
    }
}

/**
 * ================================================================================
 * TEST CLASS
 * ================================================================================
 */
class DistributedLockManagerTest {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Distributed Lock Manager Test ===\n");
        
        // Test 1: Basic lock/unlock
        System.out.println("Test 1: Basic lock operations");
        DistributedLockManager manager = new DistributedLockManager(5000);
        
        String result = manager.executeWithLock("resource-1", 1, TimeUnit.SECONDS, () -> {
            System.out.println("  Executing critical section...");
            Thread.sleep(100);
            return "Success!";
        });
        System.out.println("  Result: " + result);
        
        // Test 2: Concurrent access
        System.out.println("\nTest 2: Concurrent access");
        CountDownLatch latch = new CountDownLatch(3);
        
        for (int i = 0; i < 3; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    DistributedLockManager.InMemoryDistributedLock lock = 
                        manager.getLock("shared-resource");
                    long token = lock.tryLock(5, TimeUnit.SECONDS);
                    
                    if (token != -1) {
                        System.out.println("  Thread " + threadId + " acquired lock");
                        Thread.sleep(200);
                        lock.unlock(token);
                        System.out.println("  Thread " + threadId + " released lock");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        latch.await();
        
        // Test 3: Lock expiration
        System.out.println("\nTest 3: Lock expiration (TTL)");
        DistributedLockManager shortTtlManager = new DistributedLockManager(500);
        DistributedLockManager.InMemoryDistributedLock expiringLock = 
            shortTtlManager.getLock("expiring-resource");
        
        long token = expiringLock.tryLock(1, TimeUnit.SECONDS);
        System.out.println("  Lock acquired, waiting for expiration...");
        Thread.sleep(700);
        System.out.println("  Is locked after TTL: " + expiringLock.isLocked() + " (should be false)");
        
        // Test 4: Redlock simulation
        System.out.println("\nTest 4: Redlock algorithm simulation");
        RedisLockSimulation redlock = new RedisLockSimulation(5);
        String lockValue = redlock.tryLock("my-resource", 10000);
        if (lockValue != null) {
            System.out.println("  Redlock acquired with value: " + lockValue);
            redlock.unlock("my-resource", lockValue);
        }
        
        manager.shutdown();
        shortTtlManager.shutdown();
        
        System.out.println("\nAll tests completed!");
    }
}
