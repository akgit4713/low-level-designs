package lrucache.test;

import lrucache.cache.Cache;
import lrucache.cache.LRUCache;
import lrucache.policy.FIFOEvictionPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Comprehensive unit tests for LRU Cache implementation.
 * Tests cover:
 * - Basic operations (get, put, remove)
 * - LRU eviction behavior
 * - Edge cases
 * - Thread safety
 * - Custom eviction policies
 * - Eviction listeners
 */
public class LRUCacheTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    /**
     * Functional interface that allows test methods to throw exceptions.
     */
    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           LRU CACHE UNIT TESTS                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Basic Operations
        runTest("testPutAndGet", LRUCacheTest::testPutAndGet);
        runTest("testGetNonExistentKey", LRUCacheTest::testGetNonExistentKey);
        runTest("testUpdateExistingKey", LRUCacheTest::testUpdateExistingKey);
        runTest("testRemove", LRUCacheTest::testRemove);
        runTest("testClear", LRUCacheTest::testClear);
        runTest("testContainsKey", LRUCacheTest::testContainsKey);
        runTest("testSizeAndCapacity", LRUCacheTest::testSizeAndCapacity);
        
        // LRU Eviction Behavior
        runTest("testEvictionAtCapacity", LRUCacheTest::testEvictionAtCapacity);
        runTest("testAccessUpdatesLRUOrder", LRUCacheTest::testAccessUpdatesLRUOrder);
        runTest("testEvictionOrder", LRUCacheTest::testEvictionOrder);
        
        // Edge Cases
        runTest("testNullKeyThrowsException", LRUCacheTest::testNullKeyThrowsException);
        runTest("testNullValueAllowed", LRUCacheTest::testNullValueAllowed);
        runTest("testSingleCapacity", LRUCacheTest::testSingleCapacity);
        runTest("testInvalidCapacity", LRUCacheTest::testInvalidCapacity);
        
        // Thread Safety
        runTest("testConcurrentReads", LRUCacheTest::testConcurrentReads);
        runTest("testConcurrentWrites", LRUCacheTest::testConcurrentWrites);
        runTest("testConcurrentReadWrite", LRUCacheTest::testConcurrentReadWrite);
        
        // Custom Eviction Policy (Extensibility)
        runTest("testFIFOEvictionPolicy", LRUCacheTest::testFIFOEvictionPolicy);
        
        // Eviction Listener
        runTest("testEvictionListener", LRUCacheTest::testEvictionListener);
        
        // Print Summary
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.printf("║  RESULTS: %d passed, %d failed, %d total                    ║%n", 
                testsPassed, testsFailed, testsRun);
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        if (testsFailed > 0) {
            System.exit(1);
        }
    }
    
    private static void runTest(String testName, ThrowingRunnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("✅ " + testName);
        } catch (AssertionError | Exception e) {
            testsFailed++;
            System.out.println("❌ " + testName + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== Basic Operations ====================
    
    static void testPutAndGet() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        
        assertEqual(Optional.of(1), cache.get("a"));
        assertEqual(Optional.of(2), cache.get("b"));
        assertEqual(Optional.of(3), cache.get("c"));
    }
    
    static void testGetNonExistentKey() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        
        assertEqual(Optional.empty(), cache.get("nonexistent"));
    }
    
    static void testUpdateExistingKey() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        cache.put("a", 100);  // Update
        
        assertEqual(Optional.of(100), cache.get("a"));
        assertEqual(1, cache.size());
    }
    
    static void testRemove() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        cache.put("b", 2);
        
        Optional<Integer> removed = cache.remove("a");
        
        assertEqual(Optional.of(1), removed);
        assertEqual(Optional.empty(), cache.get("a"));
        assertEqual(1, cache.size());
    }
    
    static void testClear() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        cache.put("b", 2);
        cache.clear();
        
        assertEqual(0, cache.size());
        assertTrue(cache.isEmpty());
        assertEqual(Optional.empty(), cache.get("a"));
    }
    
    static void testContainsKey() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        
        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
    }
    
    static void testSizeAndCapacity() {
        Cache<String, Integer> cache = LRUCache.create(5);
        
        assertEqual(0, cache.size());
        assertEqual(5, cache.capacity());
        assertTrue(cache.isEmpty());
        assertFalse(cache.isFull());
        
        for (int i = 0; i < 5; i++) {
            cache.put("key" + i, i);
        }
        
        assertEqual(5, cache.size());
        assertFalse(cache.isEmpty());
        assertTrue(cache.isFull());
    }
    
    // ==================== LRU Eviction Behavior ====================
    
    static void testEvictionAtCapacity() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);  // Should evict "a"
        
        assertEqual(3, cache.size());
        assertEqual(Optional.empty(), cache.get("a"));  // Evicted
        assertEqual(Optional.of(4), cache.get("d"));    // Present
    }
    
    static void testAccessUpdatesLRUOrder() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("a", 1);  // Order: a
        cache.put("b", 2);  // Order: b, a
        cache.put("c", 3);  // Order: c, b, a
        
        cache.get("a");     // Access "a" -> Order: a, c, b
        
        cache.put("d", 4);  // Should evict "b" (least recently used)
        
        assertEqual(Optional.empty(), cache.get("b"));  // Evicted
        assertEqual(Optional.of(1), cache.get("a"));    // Still present (was accessed)
    }
    
    static void testEvictionOrder() {
        Cache<Integer, String> cache = LRUCache.create(3);
        
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        
        // Access order: 3, 2, 1 (1 is LRU)
        cache.get(2);  // Order: 2, 3, 1
        cache.get(3);  // Order: 3, 2, 1
        
        cache.put(4, "four");  // Evicts 1
        cache.put(5, "five");  // Evicts 2
        
        assertEqual(Optional.empty(), cache.get(1));
        assertEqual(Optional.empty(), cache.get(2));
        assertEqual(Optional.of("three"), cache.get(3));
    }
    
    // ==================== Edge Cases ====================
    
    static void testNullKeyThrowsException() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        try {
            cache.put(null, 1);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        
        try {
            cache.get(null);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }
    
    static void testNullValueAllowed() {
        Cache<String, Integer> cache = LRUCache.create(3);
        
        cache.put("null-value", null);
        
        assertTrue(cache.containsKey("null-value"));
        assertEqual(Optional.empty(), cache.get("null-value"));
    }
    
    static void testSingleCapacity() {
        Cache<String, Integer> cache = LRUCache.create(1);
        
        cache.put("a", 1);
        assertEqual(Optional.of(1), cache.get("a"));
        
        cache.put("b", 2);  // Evicts "a"
        
        assertEqual(Optional.empty(), cache.get("a"));
        assertEqual(Optional.of(2), cache.get("b"));
        assertEqual(1, cache.size());
    }
    
    static void testInvalidCapacity() {
        try {
            LRUCache.create(0);
            throw new AssertionError("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            LRUCache.create(-5);
            throw new AssertionError("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
    
    // ==================== Thread Safety ====================
    
    static void testConcurrentReads() throws Exception {
        Cache<Integer, String> cache = LRUCache.create(100);
        
        // Pre-populate
        for (int i = 0; i < 100; i++) {
            cache.put(i, "value" + i);
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Boolean>> futures = new ArrayList<>();
        
        // 100 concurrent reads
        for (int i = 0; i < 100; i++) {
            final int key = i % 100;
            futures.add(executor.submit(() -> {
                Optional<String> value = cache.get(key);
                return value.isPresent() && value.get().equals("value" + key);
            }));
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        for (Future<Boolean> future : futures) {
            assertTrue(future.get());
        }
    }
    
    static void testConcurrentWrites() throws Exception {
        Cache<Integer, Integer> cache = LRUCache.create(50);
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // 100 concurrent writes
        for (int i = 0; i < 100; i++) {
            final int val = i;
            executor.submit(() -> {
                cache.put(val, val);
                successCount.incrementAndGet();
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEqual(100, successCount.get());
        assertTrue(cache.size() <= 50);  // Never exceeds capacity
    }
    
    static void testConcurrentReadWrite() throws Exception {
        Cache<Integer, Integer> cache = LRUCache.create(100);
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        AtomicInteger operations = new AtomicInteger(0);
        
        // Mix of reads and writes
        for (int i = 0; i < 1000; i++) {
            final int key = i % 100;
            if (i % 2 == 0) {
                executor.submit(() -> {
                    cache.put(key, key);
                    operations.incrementAndGet();
                });
            } else {
                executor.submit(() -> {
                    cache.get(key);
                    operations.incrementAndGet();
                });
            }
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEqual(1000, operations.get());
        assertTrue(cache.size() <= 100);
    }
    
    // ==================== Custom Eviction Policy ====================
    
    static void testFIFOEvictionPolicy() {
        LRUCache<String, Integer> cache = new LRUCache.Builder<String, Integer>(3)
                .evictionPolicy(new FIFOEvictionPolicy<>())
                .build();
        
        cache.put("a", 1);  // First in
        cache.put("b", 2);
        cache.put("c", 3);
        
        cache.get("a");  // Access doesn't change FIFO order
        
        cache.put("d", 4);  // Evicts "a" (first in)
        
        assertEqual(Optional.empty(), cache.get("a"));  // Evicted (FIFO)
        assertEqual(Optional.of(2), cache.get("b"));    // Still present
    }
    
    // ==================== Eviction Listener ====================
    
    static void testEvictionListener() {
        List<String> evictedKeys = new ArrayList<>();
        
        LRUCache<String, Integer> cache = new LRUCache.Builder<String, Integer>(2)
                .evictionListener((key, value) -> evictedKeys.add(key))
                .build();
        
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);  // Evicts "a"
        cache.put("d", 4);  // Evicts "b"
        
        assertEqual(2, evictedKeys.size());
        assertEqual("a", evictedKeys.get(0));
        assertEqual("b", evictedKeys.get(1));
    }
    
    // ==================== Assertion Helpers ====================
    
    private static <T> void assertEqual(T expected, T actual) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(
                    String.format("Expected: %s, Actual: %s", expected, actual));
        }
    }
    
    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }
    
    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }
}

