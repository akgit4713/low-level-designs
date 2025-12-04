package lrucache.policy;

import lrucache.cache.DoublyLinkedList;
import lrucache.cache.Node;

/**
 * FIFO (First-In-First-Out) eviction policy implementation.
 * Demonstrates extensibility of the EvictionPolicy interface.
 * 
 * Unlike LRU, FIFO does not update order on access - 
 * only insertion order matters.
 *
 * All operations are O(1):
 * - recordAccess: no-op (access doesn't change order)
 * - recordInsertion: adds node to head
 * - getEvictionCandidate: returns tail (oldest entry)
 * - remove: removes node from list
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class FIFOEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    
    private static final String POLICY_NAME = "FIFO";
    
    private final DoublyLinkedList<K, V> insertionOrder;
    
    public FIFOEvictionPolicy() {
        this.insertionOrder = new DoublyLinkedList<>();
    }
    
    @Override
    public void recordAccess(Node<K, V> node) {
        // FIFO ignores access - order is based on insertion time only
        // No operation needed
    }
    
    @Override
    public void recordInsertion(Node<K, V> node) {
        if (node == null) {
            return;
        }
        // Add to head (newest)
        insertionOrder.addToHead(node);
    }
    
    @Override
    public Node<K, V> getEvictionCandidate() {
        // Return tail (oldest - first inserted)
        return insertionOrder.peekTail();
    }
    
    @Override
    public void remove(Node<K, V> node) {
        if (node == null) {
            return;
        }
        insertionOrder.remove(node);
    }
    
    @Override
    public void clear() {
        insertionOrder.clear();
    }
    
    @Override
    public String getName() {
        return POLICY_NAME;
    }
}



