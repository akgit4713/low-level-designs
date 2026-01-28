package java;

import java.util.*;

/**
 * Heap / Priority Queue Implementation
 * 
 * Common Interview Problems:
 * - Kth Largest Element (Leetcode 215)
 * - Top K Frequent Elements (Leetcode 347)
 * - Merge K Sorted Lists (Leetcode 23)
 * - Find Median from Data Stream (Leetcode 295)
 * - Task Scheduler (Leetcode 621)
 */
public class Heap {
    
    private int[] heap;
    private int size;
    private boolean isMinHeap;
    
    public Heap(int capacity, boolean isMinHeap) {
        this.heap = new int[capacity];
        this.size = 0;
        this.isMinHeap = isMinHeap;
    }
    
    private int parent(int i) { return (i - 1) / 2; }
    private int leftChild(int i) { return 2 * i + 1; }
    private int rightChild(int i) { return 2 * i + 2; }
    
    private boolean compare(int a, int b) {
        return isMinHeap ? a < b : a > b;
    }
    
    public void insert(int val) {
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        heap[size] = val;
        siftUp(size);
        size++;
    }
    
    private void siftUp(int i) {
        while (i > 0 && compare(heap[i], heap[parent(i)])) {
            swap(i, parent(i));
            i = parent(i);
        }
    }
    
    public int peek() {
        if (size == 0) throw new NoSuchElementException();
        return heap[0];
    }
    
    public int poll() {
        if (size == 0) throw new NoSuchElementException();
        int result = heap[0];
        heap[0] = heap[size - 1];
        size--;
        siftDown(0);
        return result;
    }
    
    private void siftDown(int i) {
        int best = i;
        int left = leftChild(i);
        int right = rightChild(i);
        
        if (left < size && compare(heap[left], heap[best])) {
            best = left;
        }
        if (right < size && compare(heap[right], heap[best])) {
            best = right;
        }
        
        if (best != i) {
            swap(i, best);
            siftDown(best);
        }
    }
    
    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
    
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        // Min Heap
        Heap minHeap = new Heap(10, true);
        minHeap.insert(5);
        minHeap.insert(3);
        minHeap.insert(8);
        minHeap.insert(1);
        
        System.out.println("Min Heap extraction:");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");  // 1 3 5 8
        }
        System.out.println();
        
        // Find Median
        System.out.println("\n--- Median Finder ---");
        MedianFinder mf = new MedianFinder();
        mf.addNum(1);
        mf.addNum(2);
        System.out.println("Median after [1,2]: " + mf.findMedian());
        mf.addNum(3);
        System.out.println("Median after [1,2,3]: " + mf.findMedian());
        
        // Top K Frequent
        System.out.println("\n--- Top K Frequent ---");
        int[] nums = {1, 1, 1, 2, 2, 3};
        System.out.println("Top 2 frequent: " + Arrays.toString(topKFrequent(nums, 2)));
    }
    
    // Kth Largest Element
    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }
    
    // Top K Frequent Elements
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }
        
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            (a, b) -> freq.get(a) - freq.get(b)
        );
        
        for (int num : freq.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll();
        }
        return result;
    }
}

/**
 * Find Median from Data Stream (Leetcode 295)
 */
class MedianFinder {
    private PriorityQueue<Integer> maxHeap;  // Lower half
    private PriorityQueue<Integer> minHeap;  // Upper half
    
    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        maxHeap.offer(num);
        minHeap.offer(maxHeap.poll());
        
        if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}
