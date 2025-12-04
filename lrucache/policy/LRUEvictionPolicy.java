package lrucache.policy;

import lrucache.cache.DoublyLinkedList;
import lrucache.cache.Node;

/**
 * LRU (Least Recently Used) eviction policy implementation.
 * 
 * Maintains access order using a doubly linked list:
 * - Head = Most Recently Used (MRU)
 * - Tail = Least Recently Used (LRU) - eviction candidate
 *
 * All operations are O(1):
 * - recordAccess: moves node to head
 * - recordInsertion: adds node to head
 * - getEvictionCandidate: returns tail
 * - remove: removes node from list
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class LRUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    
    private static final String POLICY_NAME = "LRU";
    
    private final DoublyLinkedList<K, V> accessOrder;
    
    public LRUEvictionPolicy() {
        this.accessOrder = new DoublyLinkedList<>();
    }
    
    @Override
    public void recordAccess(Node<K, V> node) {
        if (node == null) {
            return;
        }
        // Move to head (most recently used)
        accessOrder.moveToHead(node);
    }
    
    @Override
    public void recordInsertion(Node<K, V> node) {
        if (node == null) {
            return;
        }
        // Add to head (most recently used)
        accessOrder.addToHead(node);
    }
    
    @Override
    public Node<K, V> getEvictionCandidate() {
        // Return tail (least recently used)
        return accessOrder.peekTail();
    }
    
    @Override
    public void remove(Node<K, V> node) {
        if (node == null) {
            return;
        }
        accessOrder.remove(node);
    }
    
    @Override
    public void clear() {
        accessOrder.clear();
    }
    
    @Override
    public String getName() {
        return POLICY_NAME;
    }
    
    /**
     * Returns the access order list for debugging purposes.
     *
     * @return string representation of access order (MRU -> LRU)
     */
    public String getAccessOrder() {
        return accessOrder.toString();
    }
}



