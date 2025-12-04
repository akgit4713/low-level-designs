package lrucache.listener;

/**
 * Observer interface for cache eviction events.
 * Allows external components to react to evictions.
 * 
 * Follows Observer Pattern - decouples cache from eviction handling logic.
 * Follows ISP - single method interface.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
@FunctionalInterface
public interface EvictionListener<K, V> {
    
    /**
     * Called when a key-value pair is evicted from the cache.
     *
     * @param key   the evicted key
     * @param value the evicted value
     */
    void onEviction(K key, V value);
}



