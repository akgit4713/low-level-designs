/**
 * Binary Indexed Tree (Fenwick Tree) Implementation in C++
 * 
 * Time Complexity:
 * - Build: O(n)
 * - Update: O(log n)
 * - Query: O(log n)
 * 
 * Space Complexity: O(n)
 */

#include <iostream>
#include <vector>
#include <algorithm>
#include <unordered_map>

class BinaryIndexedTree {
private:
    std::vector<int> tree;
    int n;

public:
    BinaryIndexedTree(int n) : n(n), tree(n + 1, 0) {}
    
    // O(n) construction
    BinaryIndexedTree(const std::vector<int>& nums) : n(nums.size()), tree(nums.size() + 1, 0) {
        for (int i = 0; i < n; i++) {
            tree[i + 1] = nums[i];
        }
        for (int i = 1; i <= n; i++) {
            int parent = i + (i & (-i));
            if (parent <= n) {
                tree[parent] += tree[i];
            }
        }
    }
    
    // Add delta to index i (0-indexed)
    void update(int i, int delta) {
        i++;
        while (i <= n) {
            tree[i] += delta;
            i += i & (-i);
        }
    }
    
    // Prefix sum [0, i] (0-indexed)
    int prefixSum(int i) {
        int sum = 0;
        i++;
        while (i > 0) {
            sum += tree[i];
            i -= i & (-i);
        }
        return sum;
    }
    
    // Range sum [l, r] (0-indexed, inclusive)
    int rangeSum(int l, int r) {
        return prefixSum(r) - (l > 0 ? prefixSum(l - 1) : 0);
    }
};

// ==================== 2D BIT ====================
class BinaryIndexedTree2D {
private:
    std::vector<std::vector<int>> tree;
    int rows, cols;

public:
    BinaryIndexedTree2D(int rows, int cols) 
        : rows(rows), cols(cols), tree(rows + 1, std::vector<int>(cols + 1, 0)) {}
    
    void update(int row, int col, int delta) {
        row++; col++;
        for (int i = row; i <= rows; i += i & (-i)) {
            for (int j = col; j <= cols; j += j & (-j)) {
                tree[i][j] += delta;
            }
        }
    }
    
    int prefixSum(int row, int col) {
        int sum = 0;
        row++; col++;
        for (int i = row; i > 0; i -= i & (-i)) {
            for (int j = col; j > 0; j -= j & (-j)) {
                sum += tree[i][j];
            }
        }
        return sum;
    }
    
    int rangeSum(int row1, int col1, int row2, int col2) {
        return prefixSum(row2, col2)
             - prefixSum(row1 - 1, col2)
             - prefixSum(row2, col1 - 1)
             + prefixSum(row1 - 1, col1 - 1);
    }
};

// ==================== Range Update BIT ====================
class RangeUpdateBIT {
private:
    std::vector<long long> tree;
    int n;
    
    void add(int i, long long delta) {
        i++;
        while (i <= n + 1) {
            tree[i] += delta;
            i += i & (-i);
        }
    }

public:
    RangeUpdateBIT(int n) : n(n), tree(n + 2, 0) {}
    
    // Add delta to range [l, r]
    void rangeAdd(int l, int r, long long delta) {
        add(l, delta);
        add(r + 1, -delta);
    }
    
    // Get value at index i
    long long get(int i) {
        long long sum = 0;
        i++;
        while (i > 0) {
            sum += tree[i];
            i -= i & (-i);
        }
        return sum;
    }
};

// ==================== Range Update Range Query BIT ====================
class RangeUpdateRangeQueryBIT {
private:
    std::vector<long long> tree1, tree2;
    int n;
    
    void add(std::vector<long long>& tree, int i, long long delta) {
        i++;
        while (i <= n + 1) {
            tree[i] += delta;
            i += i & (-i);
        }
    }
    
    long long sum(std::vector<long long>& tree, int i) {
        long long s = 0;
        i++;
        while (i > 0) {
            s += tree[i];
            i -= i & (-i);
        }
        return s;
    }
    
    long long prefixSum(int i) {
        return sum(tree1, i) * i - sum(tree2, i);
    }

public:
    RangeUpdateRangeQueryBIT(int n) : n(n), tree1(n + 2, 0), tree2(n + 2, 0) {}
    
    void rangeAdd(int l, int r, long long delta) {
        add(tree1, l, delta);
        add(tree1, r + 1, -delta);
        add(tree2, l, delta * (l - 1));
        add(tree2, r + 1, -delta * r);
    }
    
    long long rangeSum(int l, int r) {
        return prefixSum(r) - (l > 0 ? prefixSum(l - 1) : 0);
    }
};

// Count of Smaller Numbers After Self using BIT
std::vector<int> countSmaller(std::vector<int>& nums) {
    int n = nums.size();
    std::vector<int> result(n);
    
    // Coordinate compression
    std::vector<int> sorted = nums;
    std::sort(sorted.begin(), sorted.end());
    sorted.erase(std::unique(sorted.begin(), sorted.end()), sorted.end());
    
    std::unordered_map<int, int> ranks;
    for (int i = 0; i < sorted.size(); i++) {
        ranks[sorted[i]] = i;
    }
    
    BinaryIndexedTree bit(sorted.size());
    
    for (int i = n - 1; i >= 0; i--) {
        int rank = ranks[nums[i]];
        result[i] = rank > 0 ? bit.prefixSum(rank - 1) : 0;
        bit.update(rank, 1);
    }
    
    return result;
}

int main() {
    std::vector<int> nums = {1, 3, 5, 7, 9, 11};
    BinaryIndexedTree bit(nums);
    
    std::cout << "Prefix sum [0, 2]: " << bit.prefixSum(2) << std::endl;  // 9
    std::cout << "Range sum [1, 3]: " << bit.rangeSum(1, 3) << std::endl; // 15
    std::cout << "Range sum [0, 5]: " << bit.rangeSum(0, 5) << std::endl; // 36
    
    bit.update(2, 10);
    std::cout << "After adding 10 to index 2:" << std::endl;
    std::cout << "Range sum [1, 3]: " << bit.rangeSum(1, 3) << std::endl; // 25
    
    // Count Smaller Numbers
    std::cout << "\n--- Count Smaller Numbers After Self ---" << std::endl;
    std::vector<int> input = {5, 2, 6, 1};
    auto result = countSmaller(input);
    std::cout << "Result: ";
    for (int x : result) std::cout << x << " ";  // 2 1 1 0
    std::cout << std::endl;
    
    // 2D BIT
    std::cout << "\n--- 2D BIT Demo ---" << std::endl;
    BinaryIndexedTree2D bit2d(3, 3);
    bit2d.update(0, 0, 1);
    bit2d.update(1, 1, 2);
    bit2d.update(2, 2, 3);
    std::cout << "Range sum [0,0] to [2,2]: " << bit2d.rangeSum(0, 0, 2, 2) << std::endl; // 6
    
    return 0;
}
