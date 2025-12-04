package lrucache.cache;

import lrucache.listener.EvictionListener;
import lrucache.policy.EvictionPolicy;
import lrucache.policy.LRUEvictionPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe LRU Cache implementation with O(1) operations.
 * 
 * Design Highlights:
 * - Uses HashMap for O(1) key lookups
 * - Uses EvictionPolicy (Strategy Pattern) for flexible eviction algorithms
 * - Uses ReadWriteLock for concurrent read access
 * - Supports optional EvictionListener (Observer Pattern)
 * - Built with Builder Pattern for flexible configuration
 *
 * Thread Safety:
 * - Read operations (get, containsKey, size) acquire read lock
 * - Write operations (put, remove, clear) acquire write lock
 * - Multiple readers can access concurrently
 * - Writers have exclusive access
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public class LRUCache<K, V> implements Cache<K, V> {
    
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final EvictionPolicy<K, V> evictionPolicy;
    private final EvictionListener<K, V> evictionListener;
    
    // ReadWriteLock for concurrent access
    private final ReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    
    /**
     * Private constructor - use Builder for instantiation.
     */
    private LRUCache(Builder<K, V> builder) {
        this.capacity = builder.capacity;
        this.evictionPolicy = builder.evictionPolicy;
        this.evictionListener = builder.evictionListener;
        this.cache = new HashMap<>(capacity);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    @Override
    public Optional<V> get(K key) {
        validateKey(key);
        
        writeLock.lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) {
                return Optional.empty();
            }
            
            // Record access for LRU tracking
            evictionPolicy.recordAccess(node);
            return Optional.ofNullable(node.getValue());
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public void put(K key, V value) {
        validateKey(key);
        
        writeLock.lock();
        try {
            Node<K, V> existingNode = cache.get(key);
            
            if (existingNode != null) {
                // Update existing entry
                existingNode.setValue(value);
                evictionPolicy.recordAccess(existingNode);
            } else {
                // Evict if at capacity
                evictIfNecessary();
                
                // Insert new entry
                Node<K, V> newNode = new Node<>(key, value);
                cache.put(key, newNode);
                evictionPolicy.recordInsertion(newNode);
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public Optional<V> remove(K key) {
        validateKey(key);
        
        writeLock.lock();
        try {
            Node<K, V> node = cache.remove(key);
            if (node == null) {
                return Optional.empty();
            }
            
            evictionPolicy.remove(node);
            return Optional.ofNullable(node.getValue());
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public int size() {
        readLock.lock();
        try {
            return cache.size();
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public int capacity() {
        return capacity;
    }
    
    @Override
    public void clear() {
        writeLock.lock();
        try {
            cache.clear();
            evictionPolicy.clear();
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        
        readLock.lock();
        try {
            return cache.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * Evicts the least recently used entry if cache is at capacity.
     * Must be called while holding write lock.
     */
    private void evictIfNecessary() {
        if (cache.size() >= capacity) {
            Node<K, V> victim = evictionPolicy.getEvictionCandidate();
            if (victim != null) {
                cache.remove(victim.getKey());
                evictionPolicy.remove(victim);
                
                // Notify listener if present
                if (evictionListener != null) {
                    evictionListener.onEviction(victim.getKey(), victim.getValue());
                }
            }
        }
    }
    
    /**
     * Validates that the key is not null.
     */
    private void validateKey(K key) {
        Objects.requireNonNull(key, "Cache key cannot be null");
    }
    
    /**
     * Returns the eviction policy name for debugging.
     */
    public String getEvictionPolicyName() {
        return evictionPolicy.getName();
    }
    
    @Override
    public String toString() {
        readLock.lock();
        try {
            return String.format("LRUCache{capacity=%d, size=%d, policy=%s}", 
                    capacity, cache.size(), evictionPolicy.getName());
        } finally {
            readLock.unlock();
        }
    }
    
    // ==================== Builder Pattern ====================
    
    /**
     * Builder for constructing LRUCache instances with flexible configuration.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    public static class Builder<K, V> {
        
        private final int capacity;
        private EvictionPolicy<K, V> evictionPolicy;
        private EvictionListener<K, V> evictionListener;
        
        /**
         * Creates a builder with the specified capacity.
         *
         * @param capacity the maximum number of entries the cache can hold
         * @throws IllegalArgumentException if capacity is not positive
         */
        public Builder(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Cache capacity must be positive: " + capacity);
            }
            this.capacity = capacity;
            this.evictionPolicy = new LRUEvictionPolicy<>();  // Default
        }
        
        /**
         * Sets a custom eviction policy.
         * Enables Open/Closed Principle - extend with new policies without modifying cache.
         *
         * @param policy the eviction policy to use
         * @return this builder
         */
        public Builder<K, V> evictionPolicy(EvictionPolicy<K, V> policy) {
            this.evictionPolicy = Objects.requireNonNull(policy, "Eviction policy cannot be null");
            return this;
        }
        
        /**
         * Sets an eviction listener for monitoring evictions.
         *
         * @param listener the listener to notify on evictions
         * @return this builder
         */
        public Builder<K, V> evictionListener(EvictionListener<K, V> listener) {
            this.evictionListener = listener;
            return this;
        }
        
        /**
         * Builds and returns the configured LRUCache instance.
         *
         * @return a new LRUCache instance
         */
        public LRUCache<K, V> build() {
            return new LRUCache<>(this);
        }
    }
    
    /**
     * Convenience factory method for creating a simple LRU cache.
     *
     * @param capacity the cache capacity
     * @param <K>      key type
     * @param <V>      value type
     * @return a new LRUCache instance
     */
    public static <K, V> LRUCache<K, V> create(int capacity) {
        return new Builder<K, V>(capacity).build();
    }
}



