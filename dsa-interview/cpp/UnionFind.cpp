/**
 * Union-Find (Disjoint Set Union) Implementation in C++
 * 
 * Time Complexity (with path compression + union by rank):
 * - Find: O(α(n)) ≈ O(1) amortized
 * - Union: O(α(n)) ≈ O(1) amortized
 * 
 * Space Complexity: O(n)
 */

#include <iostream>
#include <vector>
#include <numeric>

class UnionFind {
private:
    std::vector<int> parent;
    std::vector<int> rank_;
    std::vector<int> size_;
    int components;

public:
    UnionFind(int n) : parent(n), rank_(n, 0), size_(n, 1), components(n) {
        std::iota(parent.begin(), parent.end(), 0); // parent[i] = i
    }
    
    // Find with path compression
    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    // Union by rank, returns true if union was performed
    bool unite(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) return false;
        
        if (rank_[rootX] < rank_[rootY]) {
            parent[rootX] = rootY;
            size_[rootY] += size_[rootX];
        } else if (rank_[rootX] > rank_[rootY]) {
            parent[rootY] = rootX;
            size_[rootX] += size_[rootY];
        } else {
            parent[rootY] = rootX;
            rank_[rootX]++;
            size_[rootX] += size_[rootY];
        }
        
        components--;
        return true;
    }
    
    bool connected(int x, int y) {
        return find(x) == find(y);
    }
    
    int getComponents() const {
        return components;
    }
    
    int getComponentSize(int x) {
        return size_[find(x)];
    }
};

// ==================== Weighted Union-Find ====================
// Useful for problems like "Evaluate Division" (Leetcode 399)
class WeightedUnionFind {
private:
    std::vector<int> parent;
    std::vector<double> weight; // weight[i] = value of i / value of parent[i]

public:
    WeightedUnionFind(int n) : parent(n), weight(n, 1.0) {
        std::iota(parent.begin(), parent.end(), 0);
    }
    
    // Returns pair<root, weight_to_root>
    std::pair<int, double> find(int x) {
        if (parent[x] != x) {
            auto [root, w] = find(parent[x]);
            parent[x] = root;
            weight[x] *= w;
        }
        return {parent[x], weight[x]};
    }
    
    // Union x and y with x/y = value
    void unite(int x, int y, double value) {
        auto [rootX, weightX] = find(x);
        auto [rootY, weightY] = find(y);
        
        if (rootX == rootY) return;
        
        parent[rootX] = rootY;
        // x/rootX = weightX, y/rootY = weightY
        // x/y = value => rootX/rootY = value * weightY / weightX
        weight[rootX] = value * weightY / weightX;
    }
    
    // Get x/y, returns -1.0 if not computable
    double query(int x, int y) {
        auto [rootX, weightX] = find(x);
        auto [rootY, weightY] = find(y);
        
        if (rootX != rootY) return -1.0;
        return weightX / weightY;
    }
};

int main() {
    UnionFind uf(10);
    
    uf.unite(0, 1);
    uf.unite(2, 3);
    uf.unite(4, 5);
    uf.unite(6, 7);
    uf.unite(8, 9);
    
    std::cout << "Components after initial unions: " << uf.getComponents() << std::endl;
    
    uf.unite(1, 3);
    uf.unite(5, 7);
    
    std::cout << "Components after more unions: " << uf.getComponents() << std::endl;
    std::cout << "0 and 3 connected: " << (uf.connected(0, 3) ? "true" : "false") << std::endl;
    std::cout << "0 and 4 connected: " << (uf.connected(0, 4) ? "true" : "false") << std::endl;
    std::cout << "Component size of 0: " << uf.getComponentSize(0) << std::endl;
    
    // Weighted Union-Find demo (Evaluate Division style)
    std::cout << "\n--- Weighted Union-Find Demo ---" << std::endl;
    WeightedUnionFind wuf(4);
    // Let's say: a=0, b=1, c=2, d=3
    // a/b = 2.0
    wuf.unite(0, 1, 2.0);
    // b/c = 3.0
    wuf.unite(1, 2, 3.0);
    
    std::cout << "a/c = " << wuf.query(0, 2) << std::endl; // Should be 6.0
    std::cout << "c/a = " << wuf.query(2, 0) << std::endl; // Should be 1/6 ≈ 0.167
    std::cout << "a/d = " << wuf.query(0, 3) << std::endl; // Should be -1.0 (not connected)
    
    return 0;
}
