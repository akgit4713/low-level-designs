package lrucache.cache;

import java.util.Optional;

/**
 * Generic cache interface defining core cache operations.
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public interface Cache<K, V> {
    
    /**
     * Returns the value associated with the given key.
     * Updates the access order for LRU tracking.
     *
     * @param key the key whose associated value is to be returned
     * @return an Optional containing the value, or empty if key not found
     * @throws IllegalArgumentException if key is null
     */
    Optional<V> get(K key);
    
    /**
     * Associates the specified value with the specified key.
     * If the cache is at capacity, the least recently used entry is evicted.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @throws IllegalArgumentException if key is null
     */
    void put(K key, V value);
    
    /**
     * Removes the mapping for a key from this cache if present.
     *
     * @param key the key whose mapping is to be removed
     * @return an Optional containing the previous value, or empty if there was no mapping
     * @throws IllegalArgumentException if key is null
     */
    Optional<V> remove(K key);
    
    /**
     * Returns the number of key-value mappings in this cache.
     *
     * @return the number of entries in the cache
     */
    int size();
    
    /**
     * Returns the maximum capacity of this cache.
     *
     * @return the maximum number of entries the cache can hold
     */
    int capacity();
    
    /**
     * Removes all mappings from this cache.
     */
    void clear();
    
    /**
     * Returns true if this cache contains a mapping for the specified key.
     * Does not update access order.
     *
     * @param key the key to check
     * @return true if the cache contains the key
     */
    boolean containsKey(K key);
    
    /**
     * Returns true if this cache contains no key-value mappings.
     *
     * @return true if the cache is empty
     */
    default boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * Returns true if this cache is at maximum capacity.
     *
     * @return true if size equals capacity
     */
    default boolean isFull() {
        return size() >= capacity();
    }
}



