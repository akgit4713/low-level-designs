package concurrency.lrucache;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * ================================================================================
 * PROBLEM 3: THREAD-SAFE LRU CACHE
 * ================================================================================
 * 
 * Requirements:
 * 1. LRU eviction policy (remove least recently used when capacity exceeded)
 * 2. O(1) get and put operations
 * 3. Thread-safe for concurrent read/write operations
 * 4. High read concurrency
 * 
 * Key Design Decisions:
 * 1. LinkedHashMap with access-order for O(1) LRU operations
 * 2. ReadWriteLock for concurrent reads (multiple readers, single writer)
 * 3. Alternative: ConcurrentHashMap + custom doubly-linked list for better concurrency
 * 
 * Approach Comparison:
 * | Approach | Read Concurrency | Write Concurrency | Complexity |
 * |----------|------------------|-------------------|------------|
 * | synchronized | Low | Low | Simple |
 * | ReentrantLock | Low | Low | Simple |
 * | ReadWriteLock | High | Low | Medium |
 * | ConcurrentHashMap | High | Medium | Complex |
 * | Striped Locks | High | High | Very Complex |
 */
public class ThreadSafeLRUCache<K, V> {
    
    // ==================== Approach 1: ReadWriteLock + LinkedHashMap ====================
    
    private final int capacity;
    private final LinkedHashMap<K, V> cache;
    private final ReadWriteLock rwLock;
    private final Lock readLock;
    private final Lock writeLock;
    
    /**
     * Creates an LRU cache with ReadWriteLock for thread safety.
     * 
     * @param capacity Maximum number of entries
     */
    public ThreadSafeLRUCache(int capacity) {
        this.capacity = capacity;
        this.rwLock = new ReentrantReadWriteLock();
        this.readLock = rwLock.readLock();
        this.writeLock = rwLock.writeLock();
        
        // LinkedHashMap with accessOrder=true maintains LRU order
        // Most recently accessed entries move to the end
        this.cache = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }
    
    /**
     * Get value for key. Updates access order (marks as recently used).
     * Uses read lock BUT needs to upgrade to write lock for access order update.
     * 
     * Note: In pure LinkedHashMap, get() modifies the access order,
     * so we need write lock for thread safety.
     */
    public V get(K key) {
        // For LinkedHashMap with access-order, get() modifies structure
        // So we need write lock (not read lock)
        writeLock.lock();
        try {
            return cache.get(key);
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Put key-value pair. Evicts LRU entry if capacity exceeded.
     */
    public void put(K key, V value) {
        writeLock.lock();
        try {
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Remove a specific key.
     */
    public V remove(K key) {
        writeLock.lock();
        try {
            return cache.remove(key);
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Check if key exists (without updating access order).
     * Can use read lock since containsKey doesn't modify structure.
     */
    public boolean containsKey(K key) {
        readLock.lock();
        try {
            return cache.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Get current size.
     */
    public int size() {
        readLock.lock();
        try {
            return cache.size();
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Clear the cache.
     */
    public void clear() {
        writeLock.lock();
        try {
            cache.clear();
        } finally {
            writeLock.unlock();
        }
    }
}

/**
 * ================================================================================
 * APPROACH 2: High-Concurrency LRU with ConcurrentHashMap + Custom Linked List
 * ================================================================================
 * 
 * This approach provides better read concurrency by:
 * 1. Using ConcurrentHashMap for O(1) lookups without blocking
 * 2. Custom doubly-linked list for LRU tracking
 * 3. Fine-grained locking only for list operations
 */
class HighConcurrencyLRUCache<K, V> {
    
    // Node for doubly-linked list
    private static class Node<K, V> {
        final K key;
        volatile V value;
        volatile Node<K, V> prev;
        volatile Node<K, V> next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> cache;
    private final Node<K, V> head;  // Dummy head (MRU side)
    private final Node<K, V> tail;  // Dummy tail (LRU side)
    private final Lock listLock;    // Only for linked list operations
    
    public HighConcurrencyLRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>();
        this.listLock = new ReentrantLock();
        
        // Initialize dummy head and tail
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }
    
    /**
     * Get value - uses ConcurrentHashMap for O(1) non-blocking lookup.
     * List update (move to head) is done with fine-grained lock.
     */
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) {
            return null;
        }
        
        // Move to head (mark as recently used)
        moveToHead(node);
        return node.value;
    }
    
    /**
     * Put key-value pair.
     */
    public void put(K key, V value) {
        // Check if key already exists
        Node<K, V> existingNode = cache.get(key);
        
        if (existingNode != null) {
            // Update existing node
            existingNode.value = value;
            moveToHead(existingNode);
            return;
        }
        
        // Create new node
        Node<K, V> newNode = new Node<>(key, value);
        
        // Use compute to atomically check-and-put
        cache.compute(key, (k, oldNode) -> {
            if (oldNode != null) {
                // Another thread added it, update value
                oldNode.value = value;
                moveToHead(oldNode);
                return oldNode;
            }
            
            // Add new node to list
            addToHead(newNode);
            return newNode;
        });
        
        // Evict if over capacity
        evictIfNecessary();
    }
    
    /**
     * Add node right after head (MRU position).
     */
    private void addToHead(Node<K, V> node) {
        listLock.lock();
        try {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        } finally {
            listLock.unlock();
        }
    }
    
    /**
     * Remove node from its current position.
     */
    private void removeNode(Node<K, V> node) {
        listLock.lock();
        try {
            if (node.prev != null && node.next != null) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        } finally {
            listLock.unlock();
        }
    }
    
    /**
     * Move existing node to head (mark as recently used).
     */
    private void moveToHead(Node<K, V> node) {
        listLock.lock();
        try {
            // Remove from current position
            if (node.prev != null && node.next != null) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            
            // Add to head
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        } finally {
            listLock.unlock();
        }
    }
    
    /**
     * Evict LRU entries if over capacity.
     */
    private void evictIfNecessary() {
        while (cache.size() > capacity) {
            listLock.lock();
            try {
                Node<K, V> lru = tail.prev;
                if (lru != head) {
                    // Remove from list
                    lru.prev.next = tail;
                    tail.prev = lru.prev;
                    
                    // Remove from map
                    cache.remove(lru.key);
                }
            } finally {
                listLock.unlock();
            }
        }
    }
    
    public int size() {
        return cache.size();
    }
    
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }
}

/**
 * ================================================================================
 * APPROACH 3: LRU Cache with TTL (Time-To-Live)
 * ================================================================================
 */
class LRUCacheWithTTL<K, V> {
    
    private static class TimedEntry<V> {
        final V value;
        final long expirationTime;
        
        TimedEntry(V value, long ttlMs) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMs;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
    
    private final int capacity;
    private final long defaultTtlMs;
    private final LinkedHashMap<K, TimedEntry<V>> cache;
    private final Lock lock;
    private final ScheduledExecutorService cleanupExecutor;
    
    public LRUCacheWithTTL(int capacity, long defaultTtlMs) {
        this.capacity = capacity;
        this.defaultTtlMs = defaultTtlMs;
        this.lock = new ReentrantLock();
        
        this.cache = new LinkedHashMap<K, TimedEntry<V>>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, TimedEntry<V>> eldest) {
                return size() > capacity;
            }
        };
        
        // Background cleanup thread
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpired, 
            defaultTtlMs, defaultTtlMs, TimeUnit.MILLISECONDS);
    }
    
    public V get(K key) {
        lock.lock();
        try {
            TimedEntry<V> entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key);
                return null;
            }
            return entry.value;
        } finally {
            lock.unlock();
        }
    }
    
    public void put(K key, V value) {
        put(key, value, defaultTtlMs);
    }
    
    public void put(K key, V value, long ttlMs) {
        lock.lock();
        try {
            cache.put(key, new TimedEntry<>(value, ttlMs));
        } finally {
            lock.unlock();
        }
    }
    
    private void cleanupExpired() {
        lock.lock();
        try {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        } finally {
            lock.unlock();
        }
    }
    
    public void shutdown() {
        cleanupExecutor.shutdown();
    }
}

/**
 * ================================================================================
 * TEST CLASS
 * ================================================================================
 */
class ThreadSafeLRUCacheTest {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread-Safe LRU Cache Test ===\n");
        
        // Test 1: Basic LRU behavior
        System.out.println("Test 1: Basic LRU behavior");
        ThreadSafeLRUCache<Integer, String> cache = new ThreadSafeLRUCache<>(3);
        
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        System.out.println("After adding 1,2,3: size = " + cache.size());
        
        cache.get(1);  // Access 1, making it recently used
        cache.put(4, "Four");  // This should evict 2 (LRU)
        
        System.out.println("After get(1) and put(4):");
        System.out.println("  Contains 1: " + cache.containsKey(1));
        System.out.println("  Contains 2: " + cache.containsKey(2) + " (should be evicted)");
        System.out.println("  Contains 3: " + cache.containsKey(3));
        System.out.println("  Contains 4: " + cache.containsKey(4));
        
        // Test 2: Concurrent access
        System.out.println("\nTest 2: Concurrent access");
        HighConcurrencyLRUCache<Integer, Integer> concurrentCache = new HighConcurrencyLRUCache<>(100);
        
        int numThreads = 10;
        int operationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            new Thread(() -> {
                for (int i = 0; i < operationsPerThread; i++) {
                    int key = (threadId * operationsPerThread + i) % 150;
                    if (i % 2 == 0) {
                        concurrentCache.put(key, key * 10);
                    } else {
                        concurrentCache.get(key);
                    }
                }
                latch.countDown();
            }).start();
        }
        
        latch.await();
        System.out.println("Concurrent test completed. Final size: " + concurrentCache.size());
        
        // Test 3: TTL Cache
        System.out.println("\nTest 3: TTL Cache");
        LRUCacheWithTTL<String, String> ttlCache = new LRUCacheWithTTL<>(10, 1000);
        
        ttlCache.put("key1", "value1", 500);  // 500ms TTL
        System.out.println("Immediately after put: " + ttlCache.get("key1"));
        
        Thread.sleep(600);
        System.out.println("After 600ms: " + ttlCache.get("key1") + " (should be null - expired)");
        
        ttlCache.shutdown();
        
        System.out.println("\nAll tests completed!");
    }
}
