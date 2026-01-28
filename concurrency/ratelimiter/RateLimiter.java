package concurrency.ratelimiter;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * ================================================================================
 * PROBLEM 10: RATE LIMITER
 * ================================================================================
 * 
 * Requirements:
 * 1. Control rate of job/request execution
 * 2. Ensure jobs are not executed more frequently than specified limit
 * 3. Thread-safe for concurrent access
 * 4. Support different rate limiting strategies
 * 
 * Common Algorithms:
 * 1. Token Bucket - Allows bursts, most common
 * 2. Leaky Bucket - Constant output rate
 * 3. Fixed Window Counter - Simple but has boundary issues
 * 4. Sliding Window Log - Accurate but memory intensive
 * 5. Sliding Window Counter - Balanced approach
 * 
 * Real-World Use Cases:
 * - API rate limiting (e.g., 100 requests/minute)
 * - DDoS protection
 * - Resource fair usage
 * - Cost control for paid APIs
 */

// ==================== APPROACH 1: TOKEN BUCKET (MOST COMMON) ====================

/**
 * Token Bucket Algorithm:
 * 
 * - Bucket holds tokens up to maximum capacity
 * - Tokens are added at a fixed rate (e.g., 10 tokens/second)
 * - Each request consumes one token
 * - If no tokens available, request is rejected/delayed
 * 
 * Advantages:
 * - Allows bursts up to bucket capacity
 * - Simple to implement
 * - Smooth rate limiting
 * 
 * Parameters:
 * - Bucket capacity: max tokens (allows bursts)
 * - Refill rate: tokens added per second
 */
public class RateLimiter {
    
    private final long capacity;        // Max tokens
    private final double refillRate;    // Tokens per second
    private double tokens;              // Current tokens
    private long lastRefillTime;        // Last time tokens were added
    private final Lock lock;
    
    /**
     * Creates a Token Bucket rate limiter.
     * 
     * @param capacity Maximum tokens (burst capacity)
     * @param refillRate Tokens added per second
     */
    public RateLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;  // Start with full bucket
        this.lastRefillTime = System.nanoTime();
        this.lock = new ReentrantLock();
    }
    
    /**
     * Try to acquire a permit (consume one token).
     * 
     * @return true if permit acquired, false if rate limited
     */
    public boolean tryAcquire() {
        return tryAcquire(1);
    }
    
    /**
     * Try to acquire multiple permits.
     * 
     * @param permits Number of tokens to consume
     * @return true if permits acquired, false if rate limited
     */
    public boolean tryAcquire(int permits) {
        lock.lock();
        try {
            refill();
            
            if (tokens >= permits) {
                tokens -= permits;
                return true;
            }
            return false;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Acquire a permit, blocking if necessary.
     * 
     * @throws InterruptedException if interrupted while waiting
     */
    public void acquire() throws InterruptedException {
        acquire(1);
    }
    
    /**
     * Acquire multiple permits, blocking if necessary.
     */
    public void acquire(int permits) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                refill();
                
                if (tokens >= permits) {
                    tokens -= permits;
                    return;
                }
                
                // Calculate wait time for required tokens
                double tokensNeeded = permits - tokens;
                long waitNanos = (long) (tokensNeeded / refillRate * 1_000_000_000);
                
                // Brief unlock to allow other operations
                lock.unlock();
                TimeUnit.NANOSECONDS.sleep(Math.min(waitNanos, 10_000_000));  // Max 10ms wait
                lock.lock();
                
            } finally {
                lock.unlock();
            }
        }
    }
    
    /**
     * Refill tokens based on elapsed time.
     */
    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
        double tokensToAdd = elapsedSeconds * refillRate;
        
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillTime = now;
    }
    
    /**
     * Get current available tokens (for monitoring).
     */
    public double getAvailableTokens() {
        lock.lock();
        try {
            refill();
            return tokens;
        } finally {
            lock.unlock();
        }
    }
}

// ==================== APPROACH 2: SLIDING WINDOW COUNTER ====================

/**
 * Sliding Window Counter Algorithm:
 * 
 * Combines fixed window and sliding window log:
 * - Maintains counters for current and previous window
 * - Weighted average based on position in current window
 * 
 * Example: 100 requests/minute limit
 * - Previous window (0:00-1:00): 80 requests
 * - Current window (1:00-2:00): 30 requests so far
 * - Current time: 1:15 (25% into current window)
 * - Weighted count: 80 * 0.75 + 30 = 90 (within limit)
 * 
 * Advantages:
 * - Memory efficient (only 2 counters)
 * - Smooth rate limiting at window boundaries
 */
class SlidingWindowRateLimiter {
    
    private final int maxRequests;
    private final long windowSizeMs;
    private final AtomicInteger previousCount;
    private final AtomicInteger currentCount;
    private final AtomicLong windowStart;
    private final Lock lock;
    
    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.previousCount = new AtomicInteger(0);
        this.currentCount = new AtomicInteger(0);
        this.windowStart = new AtomicLong(System.currentTimeMillis());
        this.lock = new ReentrantLock();
    }
    
    public boolean tryAcquire() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            long currentWindowStart = windowStart.get();
            
            // Check if we've moved to a new window
            if (now - currentWindowStart >= windowSizeMs) {
                // Slide the window
                previousCount.set(currentCount.get());
                currentCount.set(0);
                windowStart.set(currentWindowStart + windowSizeMs);
                
                // Handle multiple windows passed
                while (now - windowStart.get() >= windowSizeMs) {
                    previousCount.set(0);
                    windowStart.addAndGet(windowSizeMs);
                }
            }
            
            // Calculate weighted count
            long elapsedInWindow = now - windowStart.get();
            double previousWeight = 1.0 - (double) elapsedInWindow / windowSizeMs;
            double weightedCount = previousCount.get() * previousWeight + currentCount.get();
            
            if (weightedCount < maxRequests) {
                currentCount.incrementAndGet();
                return true;
            }
            
            return false;
            
        } finally {
            lock.unlock();
        }
    }
}

// ==================== APPROACH 3: LEAKY BUCKET ====================

/**
 * Leaky Bucket Algorithm:
 * 
 * - Requests enter the bucket
 * - Bucket "leaks" at a constant rate
 * - If bucket overflows, requests are rejected
 * 
 * Difference from Token Bucket:
 * - Token Bucket: allows bursts
 * - Leaky Bucket: constant output rate (smoothing)
 * 
 * Use case: When you need constant, predictable rate
 */
class LeakyBucketRateLimiter {
    
    private final int capacity;
    private final double leakRate;  // Requests processed per second
    private double water;           // Current water level (pending requests)
    private long lastLeakTime;
    private final Lock lock;
    
    public LeakyBucketRateLimiter(int capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.water = 0;
        this.lastLeakTime = System.nanoTime();
        this.lock = new ReentrantLock();
    }
    
    public boolean tryAcquire() {
        lock.lock();
        try {
            leak();
            
            if (water < capacity) {
                water++;
                return true;
            }
            return false;  // Bucket overflow
            
        } finally {
            lock.unlock();
        }
    }
    
    private void leak() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastLeakTime) / 1_000_000_000.0;
        double leaked = elapsedSeconds * leakRate;
        
        water = Math.max(0, water - leaked);
        lastLeakTime = now;
    }
}

// ==================== APPROACH 4: FIXED WINDOW COUNTER ====================

/**
 * Fixed Window Counter Algorithm:
 * 
 * - Divide time into fixed windows (e.g., per minute)
 * - Count requests in each window
 * - Reset counter at window boundary
 * 
 * Simple but has boundary problem:
 * - Limit: 100 requests/minute
 * - 100 requests at 0:59
 * - 100 requests at 1:01
 * - 200 requests in 2 seconds! (crosses window boundary)
 */
class FixedWindowRateLimiter {
    
    private final int maxRequests;
    private final long windowSizeMs;
    private final AtomicInteger counter;
    private final AtomicLong windowStart;
    
    public FixedWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
        this.counter = new AtomicInteger(0);
        this.windowStart = new AtomicLong(System.currentTimeMillis());
    }
    
    public boolean tryAcquire() {
        long now = System.currentTimeMillis();
        
        // Reset window if needed
        if (now - windowStart.get() >= windowSizeMs) {
            synchronized (this) {
                if (now - windowStart.get() >= windowSizeMs) {
                    counter.set(0);
                    windowStart.set(now);
                }
            }
        }
        
        // Try to increment counter
        while (true) {
            int current = counter.get();
            if (current >= maxRequests) {
                return false;
            }
            if (counter.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }
}

// ==================== APPROACH 5: PER-KEY RATE LIMITER ====================

/**
 * Rate limiter that supports per-key (per-user, per-IP) limiting.
 * Each key has its own rate limit bucket.
 */
class PerKeyRateLimiter {
    
    private final ConcurrentHashMap<String, RateLimiter> limiters;
    private final long capacity;
    private final double refillRate;
    
    public PerKeyRateLimiter(long capacity, double refillRate) {
        this.limiters = new ConcurrentHashMap<>();
        this.capacity = capacity;
        this.refillRate = refillRate;
    }
    
    public boolean tryAcquire(String key) {
        RateLimiter limiter = limiters.computeIfAbsent(key, 
            k -> new RateLimiter(capacity, refillRate));
        return limiter.tryAcquire();
    }
    
    /**
     * Cleanup old limiters (call periodically).
     */
    public void cleanup() {
        // Remove limiters that haven't been used recently
        // In production, track last access time
        limiters.entrySet().removeIf(entry -> 
            entry.getValue().getAvailableTokens() >= capacity);
    }
}

// ==================== DISTRIBUTED RATE LIMITER (Redis-based concept) ====================

/**
 * Conceptual distributed rate limiter using Redis.
 * 
 * In production, you would use Redis with Lua scripts for atomicity.
 * 
 * Redis commands (Token Bucket):
 * ```lua
 * local tokens = tonumber(redis.call('GET', key) or capacity)
 * local lastRefill = tonumber(redis.call('GET', key..':time') or now)
 * local elapsed = now - lastRefill
 * tokens = math.min(capacity, tokens + elapsed * refillRate)
 * if tokens >= 1 then
 *     redis.call('SET', key, tokens - 1)
 *     redis.call('SET', key..':time', now)
 *     return 1  -- allowed
 * end
 * return 0  -- rate limited
 * ```
 */
class DistributedRateLimiterConcept {
    
    // Simulated Redis storage
    private final ConcurrentHashMap<String, Double> tokenStore;
    private final ConcurrentHashMap<String, Long> timeStore;
    private final long capacity;
    private final double refillRate;
    private final Lock lock;
    
    public DistributedRateLimiterConcept(long capacity, double refillRate) {
        this.tokenStore = new ConcurrentHashMap<>();
        this.timeStore = new ConcurrentHashMap<>();
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.lock = new ReentrantLock();
    }
    
    /**
     * This simulates a Lua script that would run atomically in Redis.
     */
    public boolean tryAcquire(String key) {
        lock.lock();  // In Redis, Lua script provides atomicity
        try {
            long now = System.nanoTime();
            
            // Get current state
            double tokens = tokenStore.getOrDefault(key, (double) capacity);
            long lastRefill = timeStore.getOrDefault(key, now);
            
            // Refill tokens
            double elapsed = (now - lastRefill) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            
            if (tokens >= 1) {
                // Consume token
                tokenStore.put(key, tokens - 1);
                timeStore.put(key, now);
                return true;
            }
            
            // Store updated token count even on failure (for accuracy)
            tokenStore.put(key, tokens);
            timeStore.put(key, now);
            return false;
            
        } finally {
            lock.unlock();
        }
    }
}

// ==================== TEST CLASS ====================

class RateLimiterTest {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Rate Limiter Test ===\n");
        
        // Test 1: Token Bucket
        System.out.println("Test 1: Token Bucket (10 tokens, 5 tokens/sec refill)");
        RateLimiter tokenBucket = new RateLimiter(10, 5);
        
        System.out.print("  Burst of 15 requests: ");
        int allowed = 0;
        for (int i = 0; i < 15; i++) {
            if (tokenBucket.tryAcquire()) {
                allowed++;
            }
        }
        System.out.println(allowed + " allowed, " + (15 - allowed) + " rejected");
        System.out.println("  Tokens remaining: " + String.format("%.2f", tokenBucket.getAvailableTokens()));
        
        System.out.println("  Waiting 2 seconds for refill...");
        Thread.sleep(2000);
        System.out.println("  Tokens after wait: " + String.format("%.2f", tokenBucket.getAvailableTokens()));
        
        // Test 2: Sliding Window
        System.out.println("\nTest 2: Sliding Window (10 requests per 1000ms)");
        SlidingWindowRateLimiter slidingWindow = new SlidingWindowRateLimiter(10, 1000);
        
        allowed = 0;
        for (int i = 0; i < 15; i++) {
            if (slidingWindow.tryAcquire()) {
                allowed++;
            }
        }
        System.out.println("  Initial burst: " + allowed + " allowed");
        
        Thread.sleep(500);
        allowed = 0;
        for (int i = 0; i < 5; i++) {
            if (slidingWindow.tryAcquire()) {
                allowed++;
            }
        }
        System.out.println("  After 500ms: " + allowed + " more allowed (sliding window effect)");
        
        // Test 3: Per-Key Rate Limiter
        System.out.println("\nTest 3: Per-Key Rate Limiter (5 tokens per key)");
        PerKeyRateLimiter perKeyLimiter = new PerKeyRateLimiter(5, 2);
        
        System.out.print("  User 'alice' (8 requests): ");
        allowed = 0;
        for (int i = 0; i < 8; i++) {
            if (perKeyLimiter.tryAcquire("alice")) allowed++;
        }
        System.out.println(allowed + " allowed");
        
        System.out.print("  User 'bob' (8 requests): ");
        allowed = 0;
        for (int i = 0; i < 8; i++) {
            if (perKeyLimiter.tryAcquire("bob")) allowed++;
        }
        System.out.println(allowed + " allowed");
        
        // Test 4: Concurrent access
        System.out.println("\nTest 4: Concurrent access (100 threads, 20 token bucket)");
        RateLimiter concurrentLimiter = new RateLimiter(20, 10);
        AtomicInteger totalAllowed = new AtomicInteger(0);
        AtomicInteger totalRejected = new AtomicInteger(0);
        
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                if (concurrentLimiter.tryAcquire()) {
                    totalAllowed.incrementAndGet();
                } else {
                    totalRejected.incrementAndGet();
                }
                latch.countDown();
            }).start();
        }
        
        latch.await();
        System.out.println("  Allowed: " + totalAllowed.get() + ", Rejected: " + totalRejected.get());
        
        // Test 5: Rate over time
        System.out.println("\nTest 5: Sustained rate test (5 tokens/sec for 3 seconds)");
        RateLimiter sustainedLimiter = new RateLimiter(5, 5);
        
        long startTime = System.currentTimeMillis();
        int totalRequests = 0;
        int successfulRequests = 0;
        
        while (System.currentTimeMillis() - startTime < 3000) {
            totalRequests++;
            if (sustainedLimiter.tryAcquire()) {
                successfulRequests++;
            }
            Thread.sleep(50);  // ~20 requests per second attempted
        }
        
        System.out.println("  Attempted: " + totalRequests + ", Successful: " + successfulRequests);
        System.out.println("  Expected ~15 successful (5/sec * 3 sec + initial burst)");
        
        System.out.println("\nAll tests completed!");
    }
}

/**
 * ================================================================================
 * COMPARISON TABLE
 * ================================================================================
 * 
 * | Algorithm           | Burst | Memory | Accuracy | Use Case |
 * |---------------------|-------|--------|----------|----------|
 * | Token Bucket        | Yes   | O(1)   | Good     | API rate limiting |
 * | Leaky Bucket        | No    | O(1)   | Good     | Traffic shaping |
 * | Fixed Window        | Yes*  | O(1)   | Poor     | Simple use cases |
 * | Sliding Window Log  | No    | O(n)   | Excellent| When accuracy matters |
 * | Sliding Window Counter | Limited | O(1) | Good | Balanced approach |
 * 
 * * Fixed window has boundary burst problem
 * 
 * Production Recommendations:
 * 1. Single server: Token Bucket or Sliding Window Counter
 * 2. Distributed: Redis + Lua scripts
 * 3. API Gateway: Built-in rate limiters (Kong, NGINX)
 */
