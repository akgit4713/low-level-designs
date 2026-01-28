package lrucache.policy;

import lrucache.cache.DoublyLinkedList;
import lrucache.cache.Node;

import java.util.Map;

public class LFUEvictionPolicy<K, V> implements EvictionPolicy<K, V>{

    private final Map<Integer, DoublyLinkedList<K,V>> frequencyMap;
    private int minFrequency;

    public LFUEvictionPolicy(Map<Integer, DoublyLinkedList<K, V>> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }

    @Override
    public void recordAccess(Node<K, V> node) {
        if (node == null) return;
        int freq = node.getFrequency();
        DoublyLinkedList<K, V> oldList = frequencyMap.get(freq);

        if (oldList != null) {
            oldList.remove(node);
            if (oldList.isEmpty()) {
                frequencyMap.remove(freq);  // Clean up empty buckets
                if (freq == minFrequency) {
                    minFrequency++;
                }
            }
        }
        int newFreq = freq + 1;
        node.setFrequency(newFreq);
        frequencyMap.computeIfAbsent(newFreq, k -> new DoublyLinkedList<>()).addToHead(node);
    }

    @Override
    public void recordInsertion(Node<K, V> node) {
        node.setFrequency(1);
        frequencyMap.computeIfAbsent(1, k -> new DoublyLinkedList<>()).addToHead(node);
        minFrequency = 1;
    }

    @Override
    public Node<K, V> getEvictionCandidate() {
        DoublyLinkedList<K,V> minFreqList = frequencyMap.get(minFrequency);
        return minFreqList.peekTail();
    }

    @Override
    public void remove(Node<K, V> node) {
        int freq = node.getFrequency();
        if(frequencyMap.containsKey(freq)){
            DoublyLinkedList<K,V> list = frequencyMap.get(freq);
            list.remove(node);
            if(list.isEmpty() && freq == minFrequency){
                // Update minFrequency if needed
                minFrequency++;
            }
        }
    }

    @Override
    public void clear() {
        frequencyMap.clear();
        minFrequency = 0;
    }

    @Override
    public String getName() {
        return "";
    }
}
