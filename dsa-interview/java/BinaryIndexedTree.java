package java;

/**
 * Binary Indexed Tree (Fenwick Tree) Implementation
 * 
 * Time Complexity:
 * - Build: O(n log n)
 * - Update: O(log n)
 * - Query (prefix sum): O(log n)
 * - Range query: O(log n)
 * 
 * Space Complexity: O(n)
 * 
 * Advantages over Segment Tree:
 * - More memory efficient (n vs 4n)
 * - Simpler to implement
 * - Faster constant factors
 * 
 * Common Interview Problems:
 * - Range Sum Query - Mutable (Leetcode 307)
 * - Count of Smaller Numbers After Self (Leetcode 315)
 * - Count of Range Sum (Leetcode 327)
 * - Reverse Pairs (Leetcode 493)
 * - Create Sorted Array through Instructions (Leetcode 1649)
 */
public class BinaryIndexedTree {
    
    private int[] tree;
    private int n;
    
    public BinaryIndexedTree(int n) {
        this.n = n;
        this.tree = new int[n + 1];  // 1-indexed
    }
    
    public BinaryIndexedTree(int[] nums) {
        this.n = nums.length;
        this.tree = new int[n + 1];
        
        // Build in O(n) instead of O(n log n)
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
    
    /**
     * Add delta to index i (0-indexed in original array).
     */
    public void update(int i, int delta) {
        i++;  // Convert to 1-indexed
        while (i <= n) {
            tree[i] += delta;
            i += i & (-i);  // Move to parent
        }
    }
    
    /**
     * Set index i to value val.
     */
    public void set(int i, int val, int[] nums) {
        int delta = val - nums[i];
        nums[i] = val;
        update(i, delta);
    }
    
    /**
     * Get prefix sum [0, i] (0-indexed).
     */
    public int prefixSum(int i) {
        int sum = 0;
        i++;  // Convert to 1-indexed
        while (i > 0) {
            sum += tree[i];
            i -= i & (-i);  // Move to parent
        }
        return sum;
    }
    
    /**
     * Get sum in range [l, r] (0-indexed, inclusive).
     */
    public int rangeSum(int l, int r) {
        return prefixSum(r) - (l > 0 ? prefixSum(l - 1) : 0);
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 7, 9, 11};
        BinaryIndexedTree bit = new BinaryIndexedTree(nums);
        
        System.out.println("Prefix sum [0, 2]: " + bit.prefixSum(2));  // 1+3+5 = 9
        System.out.println("Range sum [1, 3]: " + bit.rangeSum(1, 3)); // 3+5+7 = 15
        System.out.println("Range sum [0, 5]: " + bit.rangeSum(0, 5)); // 36
        
        // Update: add 10 to index 2
        bit.update(2, 10);
        System.out.println("After adding 10 to index 2:");
        System.out.println("Range sum [1, 3]: " + bit.rangeSum(1, 3)); // 3+15+7 = 25
        
        // Example: Count of Smaller Numbers After Self
        System.out.println("\n--- Count Smaller Numbers After Self ---");
        int[] input = {5, 2, 6, 1};
        int[] result = countSmaller(input);
        System.out.print("Result: ");
        for (int x : result) System.out.print(x + " ");  // [2, 1, 1, 0]
        System.out.println();
    }
    
    /**
     * Count of Smaller Numbers After Self (Leetcode 315)
     */
    public static int[] countSmaller(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        // Coordinate compression
        int[] sorted = nums.clone();
        java.util.Arrays.sort(sorted);
        java.util.Map<Integer, Integer> ranks = new java.util.HashMap<>();
        int rank = 0;
        for (int num : sorted) {
            if (!ranks.containsKey(num)) {
                ranks.put(num, rank++);
            }
        }
        
        // BIT to count elements
        BinaryIndexedTree bit = new BinaryIndexedTree(rank);
        
        // Process from right to left
        for (int i = n - 1; i >= 0; i--) {
            int r = ranks.get(nums[i]);
            result[i] = r > 0 ? bit.prefixSum(r - 1) : 0;
            bit.update(r, 1);
        }
        
        return result;
    }
}

/**
 * 2D Binary Indexed Tree for 2D range queries
 */
class BinaryIndexedTree2D {
    private int[][] tree;
    private int rows, cols;
    
    public BinaryIndexedTree2D(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tree = new int[rows + 1][cols + 1];
    }
    
    public void update(int row, int col, int delta) {
        row++; col++;
        for (int i = row; i <= rows; i += i & (-i)) {
            for (int j = col; j <= cols; j += j & (-j)) {
                tree[i][j] += delta;
            }
        }
    }
    
    public int prefixSum(int row, int col) {
        int sum = 0;
        row++; col++;
        for (int i = row; i > 0; i -= i & (-i)) {
            for (int j = col; j > 0; j -= j & (-j)) {
                sum += tree[i][j];
            }
        }
        return sum;
    }
    
    public int rangeSum(int row1, int col1, int row2, int col2) {
        return prefixSum(row2, col2)
             - prefixSum(row1 - 1, col2)
             - prefixSum(row2, col1 - 1)
             + prefixSum(row1 - 1, col1 - 1);
    }
}

/**
 * BIT supporting range update and point query
 */
class RangeUpdateBIT {
    private long[] tree;
    private int n;
    
    public RangeUpdateBIT(int n) {
        this.n = n;
        this.tree = new long[n + 2];
    }
    
    private void add(int i, long delta) {
        i++;
        while (i <= n + 1) {
            tree[i] += delta;
            i += i & (-i);
        }
    }
    
    // Add delta to range [l, r]
    public void rangeAdd(int l, int r, long delta) {
        add(l, delta);
        add(r + 1, -delta);
    }
    
    // Get value at index i
    public long get(int i) {
        long sum = 0;
        i++;
        while (i > 0) {
            sum += tree[i];
            i -= i & (-i);
        }
        return sum;
    }
}

/**
 * BIT supporting both range update and range query
 */
class RangeUpdateRangeQueryBIT {
    private long[] tree1, tree2;
    private int n;
    
    public RangeUpdateRangeQueryBIT(int n) {
        this.n = n;
        this.tree1 = new long[n + 2];
        this.tree2 = new long[n + 2];
    }
    
    private void add(long[] tree, int i, long delta) {
        i++;
        while (i <= n + 1) {
            tree[i] += delta;
            i += i & (-i);
        }
    }
    
    private long sum(long[] tree, int i) {
        long s = 0;
        i++;
        while (i > 0) {
            s += tree[i];
            i -= i & (-i);
        }
        return s;
    }
    
    // Add delta to range [l, r]
    public void rangeAdd(int l, int r, long delta) {
        add(tree1, l, delta);
        add(tree1, r + 1, -delta);
        add(tree2, l, delta * (l - 1));
        add(tree2, r + 1, -delta * r);
    }
    
    // Prefix sum [0, i]
    private long prefixSum(int i) {
        return sum(tree1, i) * i - sum(tree2, i);
    }
    
    // Range sum [l, r]
    public long rangeSum(int l, int r) {
        return prefixSum(r) - (l > 0 ? prefixSum(l - 1) : 0);
    }
}
