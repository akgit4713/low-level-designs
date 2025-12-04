package lrucache.policy;

import lrucache.cache.Node;

/**
 * Strategy interface for cache eviction policies.
 * Allows different eviction algorithms (LRU, LFU, FIFO, etc.) 
 * to be plugged into the cache implementation.
 *
 * Follows Strategy Pattern - encapsulates eviction algorithm.
 * Follows ISP - minimal interface with only necessary operations.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public interface EvictionPolicy<K, V> {
    
    /**
     * Records an access to the given node.
     * Called when a key is accessed via get() operation.
     * Implementations should update their internal state accordingly.
     *
     * @param node the node that was accessed
     */
    void recordAccess(Node<K, V> node);
    
    /**
     * Records the insertion of a new node.
     * Called when a new key-value pair is added to the cache.
     *
     * @param node the newly inserted node
     */
    void recordInsertion(Node<K, V> node);
    
    /**
     * Returns the node that should be evicted next.
     * Does not remove the node from internal structures.
     *
     * @return the candidate node for eviction, or null if no candidate exists
     */
    Node<K, V> getEvictionCandidate();
    
    /**
     * Removes a node from the eviction policy's tracking.
     * Called when a node is removed from the cache.
     *
     * @param node the node to remove from tracking
     */
    void remove(Node<K, V> node);
    
    /**
     * Clears all internal state.
     */
    void clear();
    
    /**
     * Returns the name of this eviction policy.
     *
     * @return the policy name for logging/debugging
     */
    String getName();
}



