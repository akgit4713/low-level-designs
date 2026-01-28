/**
 * Segment Tree Implementation in C++
 * 
 * Time Complexity:
 * - Build: O(n)
 * - Query: O(log n)
 * - Update: O(log n)
 * 
 * Space Complexity: O(n)
 */

#include <iostream>
#include <vector>
#include <climits>
#include <functional>

// ==================== Generic Segment Tree ====================
template<typename T>
class SegmentTree {
private:
    std::vector<T> tree;
    std::vector<T> lazy;
    int n;
    T identity;
    std::function<T(T, T)> combine;
    
    void build(const std::vector<T>& nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
        } else {
            int mid = start + (end - start) / 2;
            build(nums, 2 * node + 1, start, mid);
            build(nums, 2 * node + 2, mid + 1, end);
            tree[node] = combine(tree[2 * node + 1], tree[2 * node + 2]);
        }
    }
    
    void updateHelper(int node, int start, int end, int idx, T val) {
        if (start == end) {
            tree[node] = val;
        } else {
            int mid = start + (end - start) / 2;
            if (idx <= mid) {
                updateHelper(2 * node + 1, start, mid, idx, val);
            } else {
                updateHelper(2 * node + 2, mid + 1, end, idx, val);
            }
            tree[node] = combine(tree[2 * node + 1], tree[2 * node + 2]);
        }
    }
    
    T queryHelper(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return identity;
        if (l <= start && end <= r) return tree[node];
        
        int mid = start + (end - start) / 2;
        return combine(
            queryHelper(2 * node + 1, start, mid, l, r),
            queryHelper(2 * node + 2, mid + 1, end, l, r)
        );
    }

public:
    SegmentTree(const std::vector<T>& nums, T identity, std::function<T(T, T)> combine)
        : n(nums.size()), identity(identity), combine(combine) {
        tree.resize(4 * n);
        lazy.resize(4 * n, identity);
        build(nums, 0, 0, n - 1);
    }
    
    void update(int idx, T val) {
        updateHelper(0, 0, n - 1, idx, val);
    }
    
    T query(int l, int r) {
        return queryHelper(0, 0, n - 1, l, r);
    }
};

// ==================== Segment Tree with Lazy Propagation ====================
class LazySegmentTree {
private:
    std::vector<long long> tree;
    std::vector<long long> lazy;
    int n;
    
    void pushDown(int node, int start, int end) {
        if (lazy[node] != 0) {
            tree[node] += (end - start + 1) * lazy[node];
            if (start != end) {
                lazy[2 * node + 1] += lazy[node];
                lazy[2 * node + 2] += lazy[node];
            }
            lazy[node] = 0;
        }
    }
    
    void build(const std::vector<int>& nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
        } else {
            int mid = start + (end - start) / 2;
            build(nums, 2 * node + 1, start, mid);
            build(nums, 2 * node + 2, mid + 1, end);
            tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
        }
    }
    
    void rangeUpdateHelper(int node, int start, int end, int l, int r, long long val) {
        pushDown(node, start, end);
        
        if (r < start || end < l) return;
        
        if (l <= start && end <= r) {
            tree[node] += (end - start + 1) * val;
            if (start != end) {
                lazy[2 * node + 1] += val;
                lazy[2 * node + 2] += val;
            }
            return;
        }
        
        int mid = start + (end - start) / 2;
        rangeUpdateHelper(2 * node + 1, start, mid, l, r, val);
        rangeUpdateHelper(2 * node + 2, mid + 1, end, l, r, val);
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }
    
    long long queryHelper(int node, int start, int end, int l, int r) {
        pushDown(node, start, end);
        
        if (r < start || end < l) return 0;
        if (l <= start && end <= r) return tree[node];
        
        int mid = start + (end - start) / 2;
        return queryHelper(2 * node + 1, start, mid, l, r) +
               queryHelper(2 * node + 2, mid + 1, end, l, r);
    }

public:
    LazySegmentTree(const std::vector<int>& nums) : n(nums.size()) {
        tree.resize(4 * n, 0);
        lazy.resize(4 * n, 0);
        build(nums, 0, 0, n - 1);
    }
    
    void rangeUpdate(int l, int r, long long val) {
        rangeUpdateHelper(0, 0, n - 1, l, r, val);
    }
    
    long long query(int l, int r) {
        return queryHelper(0, 0, n - 1, l, r);
    }
};

// ==================== Merge Sort Tree (for K-th smallest in range) ====================
class MergeSortTree {
private:
    std::vector<std::vector<int>> tree;
    int n;
    
    void build(const std::vector<int>& nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = {nums[start]};
        } else {
            int mid = start + (end - start) / 2;
            build(nums, 2 * node + 1, start, mid);
            build(nums, 2 * node + 2, mid + 1, end);
            
            // Merge sorted arrays
            std::merge(tree[2 * node + 1].begin(), tree[2 * node + 1].end(),
                      tree[2 * node + 2].begin(), tree[2 * node + 2].end(),
                      std::back_inserter(tree[node]));
        }
    }
    
    // Count elements <= val in range [l, r]
    int countLessOrEqual(int node, int start, int end, int l, int r, int val) {
        if (r < start || end < l) return 0;
        if (l <= start && end <= r) {
            return std::upper_bound(tree[node].begin(), tree[node].end(), val) - tree[node].begin();
        }
        
        int mid = start + (end - start) / 2;
        return countLessOrEqual(2 * node + 1, start, mid, l, r, val) +
               countLessOrEqual(2 * node + 2, mid + 1, end, l, r, val);
    }

public:
    MergeSortTree(const std::vector<int>& nums) : n(nums.size()) {
        tree.resize(4 * n);
        build(nums, 0, 0, n - 1);
    }
    
    // Find k-th smallest element in range [l, r] (1-indexed k)
    int kthSmallest(int l, int r, int k) {
        int lo = INT_MIN, hi = INT_MAX;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (countLessOrEqual(0, 0, n - 1, l, r, mid) < k) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
    
    int countInRange(int l, int r, int minVal, int maxVal) {
        return countLessOrEqual(0, 0, n - 1, l, r, maxVal) - 
               countLessOrEqual(0, 0, n - 1, l, r, minVal - 1);
    }
};

int main() {
    // Sum Segment Tree
    std::vector<int> nums = {1, 3, 5, 7, 9, 11};
    SegmentTree<int> sumTree(nums, 0, [](int a, int b) { return a + b; });
    
    std::cout << "Sum of range [1, 3]: " << sumTree.query(1, 3) << std::endl; // 15
    std::cout << "Sum of range [0, 5]: " << sumTree.query(0, 5) << std::endl; // 36
    
    sumTree.update(1, 10);
    std::cout << "After update, sum of range [1, 3]: " << sumTree.query(1, 3) << std::endl; // 22
    
    // Min Segment Tree
    SegmentTree<int> minTree(nums, INT_MAX, [](int a, int b) { return std::min(a, b); });
    std::cout << "Min in range [0, 3]: " << minTree.query(0, 3) << std::endl;
    
    // Lazy Segment Tree
    std::cout << "\n--- Lazy Segment Tree ---" << std::endl;
    LazySegmentTree lazyTree({1, 2, 3, 4, 5});
    std::cout << "Initial sum [0, 4]: " << lazyTree.query(0, 4) << std::endl; // 15
    lazyTree.rangeUpdate(1, 3, 10);  // Add 10 to indices 1, 2, 3
    std::cout << "After +10 to [1,3], sum [0, 4]: " << lazyTree.query(0, 4) << std::endl; // 45
    
    // Merge Sort Tree
    std::cout << "\n--- Merge Sort Tree ---" << std::endl;
    MergeSortTree mst({3, 1, 4, 1, 5, 9, 2, 6});
    std::cout << "2nd smallest in [0, 4]: " << mst.kthSmallest(0, 4, 2) << std::endl; // 1
    std::cout << "3rd smallest in [2, 6]: " << mst.kthSmallest(2, 6, 3) << std::endl; // 4
    
    return 0;
}
