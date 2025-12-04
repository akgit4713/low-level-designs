package lrucache.cache;

/**
 * A doubly linked list optimized for LRU cache operations.
 * Provides O(1) operations for:
 * - Adding to head (most recently used)
 * - Removing from tail (least recently used)
 * - Moving a node to head
 * - Removing an arbitrary node
 *
 * Uses sentinel nodes (dummy head and tail) to simplify edge case handling.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class DoublyLinkedList<K, V> {
    
    private final Node<K, V> head;  // Sentinel head (dummy)
    private final Node<K, V> tail;  // Sentinel tail (dummy)
    private int size;
    
    public DoublyLinkedList() {
        this.head = new Node<>();
        this.tail = new Node<>();
        head.setNext(tail);
        tail.setPrev(head);
        this.size = 0;
    }
    
    /**
     * Adds a node right after the head (most recently used position).
     * Time Complexity: O(1)
     *
     * @param node the node to add
     */
    public void addToHead(Node<K, V> node) {
        node.setPrev(head);
        node.setNext(head.getNext());
        head.getNext().setPrev(node);
        head.setNext(node);
        size++;
    }
    
    /**
     * Removes a node from the list.
     * Time Complexity: O(1)
     *
     * @param node the node to remove
     */
    public void remove(Node<K, V> node) {
        if (node == null || node == head || node == tail) {
            return;
        }
        
        Node<K, V> prev = node.getPrev();
        Node<K, V> next = node.getNext();
        
        if (prev != null) {
            prev.setNext(next);
        }
        if (next != null) {
            next.setPrev(prev);
        }
        
        node.setPrev(null);
        node.setNext(null);
        size--;
    }
    
    /**
     * Moves an existing node to the head (most recently used position).
     * Time Complexity: O(1)
     *
     * @param node the node to move
     */
    public void moveToHead(Node<K, V> node) {
        remove(node);
        addToHead(node);
    }
    
    /**
     * Removes and returns the tail node (least recently used).
     * Time Complexity: O(1)
     *
     * @return the removed tail node, or null if list is empty
     */
    public Node<K, V> removeTail() {
        if (isEmpty()) {
            return null;
        }
        
        Node<K, V> tailNode = tail.getPrev();
        remove(tailNode);
        return tailNode;
    }
    
    /**
     * Returns the tail node without removing it.
     * Time Complexity: O(1)
     *
     * @return the tail node, or null if list is empty
     */
    public Node<K, V> peekTail() {
        if (isEmpty()) {
            return null;
        }
        return tail.getPrev();
    }
    
    /**
     * Returns the head node without removing it.
     * Time Complexity: O(1)
     *
     * @return the head node, or null if list is empty
     */
    public Node<K, V> peekHead() {
        if (isEmpty()) {
            return null;
        }
        return head.getNext();
    }
    
    /**
     * Returns the number of nodes in the list.
     *
     * @return the size of the list
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns true if the list is empty.
     *
     * @return true if no nodes are present
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Removes all nodes from the list.
     */
    public void clear() {
        head.setNext(tail);
        tail.setPrev(head);
        size = 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<K, V> current = head.getNext();
        while (current != tail) {
            sb.append(current.getKey()).append("=").append(current.getValue());
            if (current.getNext() != tail) {
                sb.append(" -> ");
            }
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }
}



