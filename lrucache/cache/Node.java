package lrucache.cache;

/**
 * Represents a node in the doubly linked list used for LRU tracking.
 * Package-private for encapsulation.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class Node<K, V> {
    
    private final K key;
    private V value;
    private Node<K, V> prev;
    private Node<K, V> next;
    
    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    // Sentinel node constructor (for head/tail)
    Node() {
        this.key = null;
        this.value = null;
    }
    
    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
    
    public void setValue(V value) {
        this.value = value;
    }
    
    public Node<K, V> getPrev() {
        return prev;
    }
    
    public void setPrev(Node<K, V> prev) {
        this.prev = prev;
    }
    
    public Node<K, V> getNext() {
        return next;
    }
    
    public void setNext(Node<K, V> next) {
        this.next = next;
    }
    
    @Override
    public String toString() {
        return String.format("Node{key=%s, value=%s}", key, value);
    }
}



