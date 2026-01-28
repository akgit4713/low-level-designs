/**
 * Monotonic Stack and Deque Implementation in C++
 */

#include <iostream>
#include <vector>
#include <stack>
#include <deque>
#include <climits>

class MonotonicStack {
public:
    // Next Greater Element
    static std::vector<int> nextGreaterElement(const std::vector<int>& nums) {
        int n = nums.size();
        std::vector<int> result(n, -1);
        std::stack<int> st;  // Indices
        
        for (int i = 0; i < n; i++) {
            while (!st.empty() && nums[st.top()] < nums[i]) {
                result[st.top()] = nums[i];
                st.pop();
            }
            st.push(i);
        }
        return result;
    }
    
    // Next Greater Element (Circular)
    static std::vector<int> nextGreaterCircular(const std::vector<int>& nums) {
        int n = nums.size();
        std::vector<int> result(n, -1);
        std::stack<int> st;
        
        for (int i = 0; i < 2 * n; i++) {
            int num = nums[i % n];
            while (!st.empty() && nums[st.top()] < num) {
                result[st.top()] = num;
                st.pop();
            }
            if (i < n) st.push(i);
        }
        return result;
    }
    
    // Daily Temperatures
    static std::vector<int> dailyTemperatures(const std::vector<int>& temps) {
        int n = temps.size();
        std::vector<int> result(n, 0);
        std::stack<int> st;
        
        for (int i = 0; i < n; i++) {
            while (!st.empty() && temps[st.top()] < temps[i]) {
                result[st.top()] = i - st.top();
                st.pop();
            }
            st.push(i);
        }
        return result;
    }
    
    // Largest Rectangle in Histogram
    static int largestRectangle(const std::vector<int>& heights) {
        int n = heights.size();
        int maxArea = 0;
        std::stack<int> st;
        
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            
            while (!st.empty() && heights[st.top()] > h) {
                int height = heights[st.top()];
                st.pop();
                int width = st.empty() ? i : i - st.top() - 1;
                maxArea = std::max(maxArea, height * width);
            }
            st.push(i);
        }
        return maxArea;
    }
    
    // Trapping Rain Water
    static int trapRainWater(const std::vector<int>& height) {
        int n = height.size();
        int water = 0;
        std::stack<int> st;
        
        for (int i = 0; i < n; i++) {
            while (!st.empty() && height[st.top()] < height[i]) {
                int bottom = st.top();
                st.pop();
                if (st.empty()) break;
                
                int left = st.top();
                int width = i - left - 1;
                int boundedHeight = std::min(height[left], height[i]) - height[bottom];
                water += width * boundedHeight;
            }
            st.push(i);
        }
        return water;
    }
};

class MonotonicDeque {
public:
    // Sliding Window Maximum
    static std::vector<int> maxSlidingWindow(const std::vector<int>& nums, int k) {
        int n = nums.size();
        std::vector<int> result;
        std::deque<int> dq;  // Indices
        
        for (int i = 0; i < n; i++) {
            // Remove elements outside window
            while (!dq.empty() && dq.front() < i - k + 1) {
                dq.pop_front();
            }
            
            // Remove smaller elements
            while (!dq.empty() && nums[dq.back()] < nums[i]) {
                dq.pop_back();
            }
            
            dq.push_back(i);
            
            if (i >= k - 1) {
                result.push_back(nums[dq.front()]);
            }
        }
        return result;
    }
    
    // Sliding Window Minimum
    static std::vector<int> minSlidingWindow(const std::vector<int>& nums, int k) {
        int n = nums.size();
        std::vector<int> result;
        std::deque<int> dq;
        
        for (int i = 0; i < n; i++) {
            while (!dq.empty() && dq.front() < i - k + 1) {
                dq.pop_front();
            }
            while (!dq.empty() && nums[dq.back()] > nums[i]) {
                dq.pop_back();
            }
            dq.push_back(i);
            
            if (i >= k - 1) {
                result.push_back(nums[dq.front()]);
            }
        }
        return result;
    }
};

int main() {
    // Next Greater Element
    std::vector<int> arr = {4, 5, 2, 25, 7, 8};
    std::cout << "Next Greater: ";
    for (int x : MonotonicStack::nextGreaterElement(arr)) std::cout << x << " ";
    std::cout << std::endl;
    
    // Largest Rectangle
    std::vector<int> heights = {2, 1, 5, 6, 2, 3};
    std::cout << "Largest Rectangle: " << MonotonicStack::largestRectangle(heights) << std::endl;
    
    // Sliding Window Maximum
    std::vector<int> nums = {1, 3, -1, -3, 5, 3, 6, 7};
    std::cout << "Sliding Window Max (k=3): ";
    for (int x : MonotonicDeque::maxSlidingWindow(nums, 3)) std::cout << x << " ";
    std::cout << std::endl;
    
    return 0;
}
