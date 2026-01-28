package java;

/**
 * Union-Find (Disjoint Set Union) Implementation
 * 
 * Time Complexity (with path compression + union by rank):
 * - Find: O(α(n)) ≈ O(1) amortized (inverse Ackermann function)
 * - Union: O(α(n)) ≈ O(1) amortized
 * 
 * Space Complexity: O(n)
 * 
 * Common Interview Problems:
 * - Number of Connected Components (Leetcode 323)
 * - Redundant Connection (Leetcode 684)
 * - Accounts Merge (Leetcode 721)
 * - Number of Islands II (Leetcode 305)
 * - Smallest String With Swaps (Leetcode 1202)
 * - Satisfiability of Equality Equations (Leetcode 990)
 * - Kruskal's MST Algorithm
 */
public class UnionFind {
    
    private int[] parent;
    private int[] rank;
    private int[] size;  // Size of each component
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        size = new int[n];
        components = n;
        
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
            size[i] = 1;
        }
    }
    
    /**
     * Find with path compression.
     * Path compression flattens the tree during find operations.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    /**
     * Union by rank.
     * Returns true if union was performed (elements were in different sets).
     */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) {
            return false; // Already in same set
        }
        
        // Union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
            size[rootX] += size[rootY];
        }
        
        components--;
        return true;
    }
    
    /**
     * Check if two elements are in the same set.
     */
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    
    /**
     * Get number of disjoint sets.
     */
    public int getComponents() {
        return components;
    }
    
    /**
     * Get size of the component containing x.
     */
    public int getComponentSize(int x) {
        return size[find(x)];
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        UnionFind uf = new UnionFind(10);
        
        // Create some unions
        uf.union(0, 1);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(6, 7);
        uf.union(8, 9);
        
        System.out.println("Components after initial unions: " + uf.getComponents()); // 5
        
        uf.union(1, 3);  // Merge {0,1} and {2,3}
        uf.union(5, 7);  // Merge {4,5} and {6,7}
        
        System.out.println("Components after more unions: " + uf.getComponents()); // 3
        
        System.out.println("0 and 3 connected: " + uf.connected(0, 3)); // true
        System.out.println("0 and 4 connected: " + uf.connected(0, 4)); // false
        System.out.println("Component size of 0: " + uf.getComponentSize(0)); // 4
        
        // Example: Number of Islands problem approach
        System.out.println("\n--- Number of Islands Example ---");
        int[][] grid = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1}
        };
        System.out.println("Number of islands: " + countIslands(grid)); // 3
    }
    
    /**
     * Example: Count number of islands using Union-Find
     */
    public static int countIslands(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int rows = grid.length;
        int cols = grid[0].length;
        
        UnionFind uf = new UnionFind(rows * cols);
        int waterCount = 0;
        
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0) {
                    waterCount++;
                } else {
                    for (int[] dir : directions) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && grid[ni][nj] == 1) {
                            uf.union(i * cols + j, ni * cols + nj);
                        }
                    }
                }
            }
        }
        
        return uf.getComponents() - waterCount;
    }
}
