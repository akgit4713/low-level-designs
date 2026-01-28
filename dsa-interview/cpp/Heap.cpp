/**
 * Heap / Priority Queue Implementation in C++
 */

#include <iostream>
#include <vector>
#include <queue>
#include <unordered_map>
#include <algorithm>

template<typename T, typename Compare = std::less<T>>
class Heap {
private:
    std::vector<T> heap;
    Compare comp;
    
    int parent(int i) { return (i - 1) / 2; }
    int leftChild(int i) { return 2 * i + 1; }
    int rightChild(int i) { return 2 * i + 2; }
    
    void siftUp(int i) {
        while (i > 0 && comp(heap[i], heap[parent(i)])) {
            std::swap(heap[i], heap[parent(i)]);
            i = parent(i);
        }
    }
    
    void siftDown(int i) {
        int best = i;
        int left = leftChild(i);
        int right = rightChild(i);
        
        if (left < heap.size() && comp(heap[left], heap[best])) {
            best = left;
        }
        if (right < heap.size() && comp(heap[right], heap[best])) {
            best = right;
        }
        
        if (best != i) {
            std::swap(heap[i], heap[best]);
            siftDown(best);
        }
    }

public:
    void push(T val) {
        heap.push_back(val);
        siftUp(heap.size() - 1);
    }
    
    T top() const {
        return heap[0];
    }
    
    void pop() {
        heap[0] = heap.back();
        heap.pop_back();
        if (!heap.empty()) siftDown(0);
    }
    
    int size() const { return heap.size(); }
    bool empty() const { return heap.empty(); }
};

// Find Median from Data Stream
class MedianFinder {
private:
    std::priority_queue<int> maxHeap;  // Lower half
    std::priority_queue<int, std::vector<int>, std::greater<int>> minHeap;  // Upper half

public:
    void addNum(int num) {
        maxHeap.push(num);
        minHeap.push(maxHeap.top());
        maxHeap.pop();
        
        if (minHeap.size() > maxHeap.size()) {
            maxHeap.push(minHeap.top());
            minHeap.pop();
        }
    }
    
    double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.top();
        }
        return (maxHeap.top() + minHeap.top()) / 2.0;
    }
};

// Top K Frequent Elements
std::vector<int> topKFrequent(std::vector<int>& nums, int k) {
    std::unordered_map<int, int> freq;
    for (int num : nums) freq[num]++;
    
    auto cmp = [&freq](int a, int b) { return freq[a] > freq[b]; };
    std::priority_queue<int, std::vector<int>, decltype(cmp)> minHeap(cmp);
    
    for (auto& [num, count] : freq) {
        minHeap.push(num);
        if (minHeap.size() > k) minHeap.pop();
    }
    
    std::vector<int> result;
    while (!minHeap.empty()) {
        result.push_back(minHeap.top());
        minHeap.pop();
    }
    return result;
}

// Kth Largest Element
int findKthLargest(std::vector<int>& nums, int k) {
    std::priority_queue<int, std::vector<int>, std::greater<int>> minHeap;
    for (int num : nums) {
        minHeap.push(num);
        if (minHeap.size() > k) minHeap.pop();
    }
    return minHeap.top();
}

int main() {
    // Custom Heap
    Heap<int> minHeap;
    minHeap.push(5);
    minHeap.push(3);
    minHeap.push(8);
    minHeap.push(1);
    
    std::cout << "Min Heap extraction: ";
    while (!minHeap.empty()) {
        std::cout << minHeap.top() << " ";
        minHeap.pop();
    }
    std::cout << std::endl;
    
    // Median Finder
    std::cout << "\n--- Median Finder ---\n";
    MedianFinder mf;
    mf.addNum(1);
    mf.addNum(2);
    std::cout << "Median after [1,2]: " << mf.findMedian() << std::endl;
    mf.addNum(3);
    std::cout << "Median after [1,2,3]: " << mf.findMedian() << std::endl;
    
    // Top K Frequent
    std::cout << "\n--- Top K Frequent ---\n";
    std::vector<int> nums = {1, 1, 1, 2, 2, 3};
    auto result = topKFrequent(nums, 2);
    std::cout << "Top 2 frequent: ";
    for (int x : result) std::cout << x << " ";
    std::cout << std::endl;
    
    return 0;
}
