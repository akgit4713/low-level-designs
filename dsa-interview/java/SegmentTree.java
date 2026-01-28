package java;

/**
 * Segment Tree Implementation
 * 
 * Time Complexity:
 * - Build: O(n)
 * - Query: O(log n)
 * - Update (point): O(log n)
 * - Update (range with lazy): O(log n)
 * 
 * Space Complexity: O(n)
 * 
 * Common Interview Problems:
 * - Range Sum Query - Mutable (Leetcode 307)
 * - Range Minimum Query
 * - Count of Smaller Numbers After Self (Leetcode 315)
 * - Range Sum Query 2D - Mutable (Leetcode 308)
 * - My Calendar I/II/III (Leetcode 729, 731, 732)
 */
public class SegmentTree {
    
    private int[] tree;
    private int[] lazy;  // For lazy propagation
    private int n;
    
    public SegmentTree(int[] nums) {
        if (nums == null || nums.length == 0) return;
        
        n = nums.length;
        tree = new int[4 * n];  // 4*n to be safe
        lazy = new int[4 * n];
        build(nums, 0, 0, n - 1);
    }
    
    private void build(int[] nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
        } else {
            int mid = start + (end - start) / 2;
            int leftChild = 2 * node + 1;
            int rightChild = 2 * node + 2;
            
            build(nums, leftChild, start, mid);
            build(nums, rightChild, mid + 1, end);
            
            tree[node] = tree[leftChild] + tree[rightChild];  // Sum query
        }
    }
    
    /**
     * Point update: Update value at index idx to val.
     */
    public void update(int idx, int val) {
        update(0, 0, n - 1, idx, val);
    }
    
    private void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;
        } else {
            int mid = start + (end - start) / 2;
            int leftChild = 2 * node + 1;
            int rightChild = 2 * node + 2;
            
            if (idx <= mid) {
                update(leftChild, start, mid, idx, val);
            } else {
                update(rightChild, mid + 1, end, idx, val);
            }
            
            tree[node] = tree[leftChild] + tree[rightChild];
        }
    }
    
    /**
     * Range sum query from l to r (inclusive).
     */
    public int query(int l, int r) {
        return query(0, 0, n - 1, l, r);
    }
    
    private int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) {
            return 0;  // Out of range
        }
        
        if (l <= start && end <= r) {
            return tree[node];  // Completely in range
        }
        
        int mid = start + (end - start) / 2;
        int leftChild = 2 * node + 1;
        int rightChild = 2 * node + 2;
        
        int leftSum = query(leftChild, start, mid, l, r);
        int rightSum = query(rightChild, mid + 1, end, l, r);
        
        return leftSum + rightSum;
    }
    
    // ==================== Range Update with Lazy Propagation ====================
    
    /**
     * Range update: Add val to all elements in range [l, r].
     */
    public void rangeUpdate(int l, int r, int val) {
        rangeUpdate(0, 0, n - 1, l, r, val);
    }
    
    private void rangeUpdate(int node, int start, int end, int l, int r, int val) {
        // Push down any pending lazy updates
        pushDown(node, start, end);
        
        if (r < start || end < l) {
            return;  // Out of range
        }
        
        if (l <= start && end <= r) {
            // Completely in range - apply update lazily
            tree[node] += (end - start + 1) * val;
            if (start != end) {
                lazy[2 * node + 1] += val;
                lazy[2 * node + 2] += val;
            }
            return;
        }
        
        int mid = start + (end - start) / 2;
        int leftChild = 2 * node + 1;
        int rightChild = 2 * node + 2;
        
        rangeUpdate(leftChild, start, mid, l, r, val);
        rangeUpdate(rightChild, mid + 1, end, l, r, val);
        
        tree[node] = tree[leftChild] + tree[rightChild];
    }
    
    private void pushDown(int node, int start, int end) {
        if (lazy[node] != 0) {
            tree[node] += (end - start + 1) * lazy[node];
            if (start != end) {
                lazy[2 * node + 1] += lazy[node];
                lazy[2 * node + 2] += lazy[node];
            }
            lazy[node] = 0;
        }
    }
    
    /**
     * Query with lazy propagation.
     */
    public int queryLazy(int l, int r) {
        return queryLazy(0, 0, n - 1, l, r);
    }
    
    private int queryLazy(int node, int start, int end, int l, int r) {
        pushDown(node, start, end);
        
        if (r < start || end < l) {
            return 0;
        }
        
        if (l <= start && end <= r) {
            return tree[node];
        }
        
        int mid = start + (end - start) / 2;
        return queryLazy(2 * node + 1, start, mid, l, r) + 
               queryLazy(2 * node + 2, mid + 1, end, l, r);
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 7, 9, 11};
        SegmentTree st = new SegmentTree(nums);
        
        // Sum queries
        System.out.println("Sum of range [1, 3]: " + st.query(1, 3)); // 3+5+7 = 15
        System.out.println("Sum of range [0, 5]: " + st.query(0, 5)); // 1+3+5+7+9+11 = 36
        
        // Point update
        st.update(1, 10);  // Change index 1 from 3 to 10
        System.out.println("After update, sum of range [1, 3]: " + st.query(1, 3)); // 10+5+7 = 22
        
        // Range Minimum Query example
        System.out.println("\n--- Range Minimum Query ---");
        RMQSegmentTree rmq = new RMQSegmentTree(new int[]{2, 5, 1, 4, 9, 3});
        System.out.println("Min in range [1, 4]: " + rmq.query(1, 4)); // 1
        System.out.println("Min in range [3, 5]: " + rmq.query(3, 5)); // 3
    }
}

/**
 * Range Minimum Query Segment Tree
 */
class RMQSegmentTree {
    private int[] tree;
    private int n;
    
    public RMQSegmentTree(int[] nums) {
        n = nums.length;
        tree = new int[4 * n];
        java.util.Arrays.fill(tree, Integer.MAX_VALUE);
        build(nums, 0, 0, n - 1);
    }
    
    private void build(int[] nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
        } else {
            int mid = start + (end - start) / 2;
            build(nums, 2 * node + 1, start, mid);
            build(nums, 2 * node + 2, mid + 1, end);
            tree[node] = Math.min(tree[2 * node + 1], tree[2 * node + 2]);
        }
    }
    
    public int query(int l, int r) {
        return query(0, 0, n - 1, l, r);
    }
    
    private int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return Integer.MAX_VALUE;
        if (l <= start && end <= r) return tree[node];
        
        int mid = start + (end - start) / 2;
        return Math.min(
            query(2 * node + 1, start, mid, l, r),
            query(2 * node + 2, mid + 1, end, l, r)
        );
    }
    
    public void update(int idx, int val) {
        update(0, 0, n - 1, idx, val);
    }
    
    private void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;
        } else {
            int mid = start + (end - start) / 2;
            if (idx <= mid) {
                update(2 * node + 1, start, mid, idx, val);
            } else {
                update(2 * node + 2, mid + 1, end, idx, val);
            }
            tree[node] = Math.min(tree[2 * node + 1], tree[2 * node + 2]);
        }
    }
}
