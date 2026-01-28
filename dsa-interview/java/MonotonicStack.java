package java;

import java.util.*;

/**
 * Monotonic Stack and Monotonic Deque Implementations
 * 
 * Time Complexity: O(n) for processing n elements
 * Space Complexity: O(n)
 * 
 * Common Interview Problems:
 * - Next Greater Element I/II/III (Leetcode 496, 503, 556)
 * - Daily Temperatures (Leetcode 739)
 * - Largest Rectangle in Histogram (Leetcode 84)
 * - Maximal Rectangle (Leetcode 85)
 * - Trapping Rain Water (Leetcode 42)
 * - Sliding Window Maximum (Leetcode 239)
 * - Stock Span Problem (Leetcode 901)
 * - Sum of Subarray Minimums (Leetcode 907)
 */
public class MonotonicStack {
    
    // ==================== Next Greater Element ====================
    /**
     * Find the next greater element for each element in the array.
     * Returns -1 if no greater element exists.
     */
    public static int[] nextGreaterElement(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Deque<Integer> stack = new ArrayDeque<>();  // Stores indices
        
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        
        return result;
    }
    
    // ==================== Next Greater Element (Circular) ====================
    /**
     * Next Greater Element II - Circular array
     */
    public static int[] nextGreaterElementCircular(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        // Iterate twice for circular behavior
        for (int i = 0; i < 2 * n; i++) {
            int num = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] < num) {
                result[stack.pop()] = num;
            }
            if (i < n) {
                stack.push(i);
            }
        }
        
        return result;
    }
    
    // ==================== Previous Smaller Element ====================
    public static int[] previousSmallerElement(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                result[i] = nums[stack.peek()];
            }
            stack.push(i);
        }
        
        return result;
    }
    
    // ==================== Daily Temperatures ====================
    /**
     * How many days until a warmer temperature?
     */
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
                int prevDay = stack.pop();
                result[prevDay] = i - prevDay;
            }
            stack.push(i);
        }
        
        return result;
    }
    
    // ==================== Largest Rectangle in Histogram ====================
    public static int largestRectangleInHistogram(int[] heights) {
        int n = heights.length;
        int maxArea = 0;
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            
            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        
        return maxArea;
    }
    
    // ==================== Trapping Rain Water ====================
    public static int trapRainWater(int[] height) {
        int n = height.length;
        int water = 0;
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
                int bottom = stack.pop();
                if (stack.isEmpty()) break;
                
                int left = stack.peek();
                int width = i - left - 1;
                int boundedHeight = Math.min(height[left], height[i]) - height[bottom];
                water += width * boundedHeight;
            }
            stack.push(i);
        }
        
        return water;
    }
    
    // ==================== Sum of Subarray Minimums ====================
    public static int sumSubarrayMins(int[] arr) {
        int MOD = 1_000_000_007;
        int n = arr.length;
        long result = 0;
        
        // For each element, find how many subarrays it's the minimum of
        int[] left = new int[n];   // Distance to previous smaller element
        int[] right = new int[n];  // Distance to next smaller or equal element
        
        Deque<Integer> stack = new ArrayDeque<>();
        
        // Find previous smaller
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                stack.pop();
            }
            left[i] = stack.isEmpty() ? i + 1 : i - stack.peek();
            stack.push(i);
        }
        
        stack.clear();
        
        // Find next smaller or equal (to handle duplicates correctly)
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
                stack.pop();
            }
            right[i] = stack.isEmpty() ? n - i : stack.peek() - i;
            stack.push(i);
        }
        
        // Calculate contribution of each element
        for (int i = 0; i < n; i++) {
            result = (result + (long) arr[i] * left[i] * right[i]) % MOD;
        }
        
        return (int) result;
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        // Next Greater Element
        int[] arr = {4, 5, 2, 25, 7, 8};
        System.out.println("Array: " + Arrays.toString(arr));
        System.out.println("Next Greater: " + Arrays.toString(nextGreaterElement(arr)));
        // Expected: [5, 25, 25, -1, 8, -1]
        
        // Next Greater Circular
        int[] circular = {1, 2, 1};
        System.out.println("\nCircular Array: " + Arrays.toString(circular));
        System.out.println("Next Greater Circular: " + Arrays.toString(nextGreaterElementCircular(circular)));
        // Expected: [2, -1, 2]
        
        // Daily Temperatures
        int[] temps = {73, 74, 75, 71, 69, 72, 76, 73};
        System.out.println("\nTemperatures: " + Arrays.toString(temps));
        System.out.println("Days until warmer: " + Arrays.toString(dailyTemperatures(temps)));
        // Expected: [1, 1, 4, 2, 1, 1, 0, 0]
        
        // Largest Rectangle in Histogram
        int[] heights = {2, 1, 5, 6, 2, 3};
        System.out.println("\nHistogram: " + Arrays.toString(heights));
        System.out.println("Largest Rectangle: " + largestRectangleInHistogram(heights));
        // Expected: 10
        
        // Trapping Rain Water
        int[] elevation = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        System.out.println("\nElevation: " + Arrays.toString(elevation));
        System.out.println("Trapped Water: " + trapRainWater(elevation));
        // Expected: 6
        
        // Sum of Subarray Minimums
        int[] subArr = {3, 1, 2, 4};
        System.out.println("\nArray: " + Arrays.toString(subArr));
        System.out.println("Sum of Subarray Minimums: " + sumSubarrayMins(subArr));
        // Expected: 17
        
        // Monotonic Deque Demo
        System.out.println("\n--- Sliding Window Maximum ---");
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        System.out.println("Array: " + Arrays.toString(nums) + ", k=" + k);
        System.out.println("Max in each window: " + Arrays.toString(MonotonicDeque.maxSlidingWindow(nums, k)));
        // Expected: [3, 3, 5, 5, 6, 7]
    }
}

/**
 * Monotonic Deque for Sliding Window problems
 */
class MonotonicDeque {
    
    /**
     * Sliding Window Maximum (Leetcode 239)
     */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        Deque<Integer> deque = new ArrayDeque<>();  // Stores indices
        
        for (int i = 0; i < n; i++) {
            // Remove elements outside the window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            
            // Remove smaller elements (they can never be max)
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            
            deque.offerLast(i);
            
            // Add to result once we have a full window
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        
        return result;
    }
    
    /**
     * Sliding Window Minimum
     */
    public static int[] minSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            
            while (!deque.isEmpty() && nums[deque.peekLast()] > nums[i]) {
                deque.pollLast();
            }
            
            deque.offerLast(i);
            
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        
        return result;
    }
    
    /**
     * Shortest Subarray with Sum at Least K (Leetcode 862)
     * Uses monotonic deque on prefix sums
     */
    public static int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }
        
        int result = Integer.MAX_VALUE;
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int i = 0; i <= n; i++) {
            // Check if current prefix - some previous prefix >= k
            while (!deque.isEmpty() && prefix[i] - prefix[deque.peekFirst()] >= k) {
                result = Math.min(result, i - deque.pollFirst());
            }
            
            // Maintain monotonic increasing deque of prefix sums
            while (!deque.isEmpty() && prefix[deque.peekLast()] >= prefix[i]) {
                deque.pollLast();
            }
            
            deque.offerLast(i);
        }
        
        return result == Integer.MAX_VALUE ? -1 : result;
    }
}
