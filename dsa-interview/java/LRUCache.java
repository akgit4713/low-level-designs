package dsainterview.java;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) Cache Implementation
 * 
 * Time Complexity:
 * - Get: O(1)
 * - Put: O(1)
 * 
 * Space Complexity: O(capacity)
 * 
 * Implementation: HashMap + Doubly Linked List
 * 
 * Common Interview Problems:
 * - LRU Cache (Leetcode 146)
 * - LFU Cache (Leetcode 460)
 * - Design In-Memory File System
 * - Design Twitter
 */
public class LRUCache {
    
    private class Node {
        int key, value;
        Node prev, next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head, tail;  // Dummy nodes
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // Initialize dummy head and tail
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        
        Node node = cache.get(key);
        moveToHead(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addToHead(newNode);
            
            if (cache.size() > capacity) {
                Node removed = removeTail();
                cache.remove(removed.key);
            }
        }
    }
    
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    private Node removeTail() {
        Node node = tail.prev;
        removeNode(node);
        return node;
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println("Get 1: " + cache.get(1));       // 1
        
        cache.put(3, 3);    // Evicts key 2
        System.out.println("Get 2: " + cache.get(2));       // -1 (not found)
        
        cache.put(4, 4);    // Evicts key 1
        System.out.println("Get 1: " + cache.get(1));       // -1 (not found)
        System.out.println("Get 3: " + cache.get(3));       // 3
        System.out.println("Get 4: " + cache.get(4));       // 4
        
        // LFU Cache demo
        System.out.println("\n--- LFU Cache Demo ---");
        LFUCache lfuCache = new LFUCache(2);
        lfuCache.put(1, 1);
        lfuCache.put(2, 2);
        System.out.println("LFU Get 1: " + lfuCache.get(1)); // 1
        lfuCache.put(3, 3);  // Evicts key 2 (LFU)
        System.out.println("LFU Get 2: " + lfuCache.get(2)); // -1
        System.out.println("LFU Get 3: " + lfuCache.get(3)); // 3
    }
}

/**
 * LFU (Least Frequently Used) Cache
 * 
 * Time Complexity: O(1) for both get and put
 */
class LFUCache {
    
    private class Node {
        int key, value, freq;
        Node prev, next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }
    
    private class DoublyLinkedList {
        Node head, tail;
        int size;
        
        DoublyLinkedList() {
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
            size = 0;
        }
        
        void addFirst(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }
        
        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }
        
        Node removeLast() {
            if (size == 0) return null;
            Node last = tail.prev;
            remove(last);
            return last;
        }
        
        boolean isEmpty() {
            return size == 0;
        }
    }
    
    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> keyToNode;
    private final Map<Integer, DoublyLinkedList> freqToList;
    
    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToNode = new HashMap<>();
        this.freqToList = new HashMap<>();
    }
    
    public int get(int key) {
        if (!keyToNode.containsKey(key)) {
            return -1;
        }
        
        Node node = keyToNode.get(key);
        updateFreq(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        if (capacity == 0) return;
        
        if (keyToNode.containsKey(key)) {
            Node node = keyToNode.get(key);
            node.value = value;
            updateFreq(node);
        } else {
            if (keyToNode.size() >= capacity) {
                // Evict LFU node
                DoublyLinkedList minFreqList = freqToList.get(minFreq);
                Node toRemove = minFreqList.removeLast();
                keyToNode.remove(toRemove.key);
            }
            
            Node newNode = new Node(key, value);
            keyToNode.put(key, newNode);
            freqToList.computeIfAbsent(1, k -> new DoublyLinkedList()).addFirst(newNode);
            minFreq = 1;
        }
    }
    
    private void updateFreq(Node node) {
        int freq = node.freq;
        freqToList.get(freq).remove(node);
        
        if (freq == minFreq && freqToList.get(freq).isEmpty()) {
            minFreq++;
        }
        
        node.freq++;
        freqToList.computeIfAbsent(node.freq, k -> new DoublyLinkedList()).addFirst(node);
    }
}
