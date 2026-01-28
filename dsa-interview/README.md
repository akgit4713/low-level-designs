# DSA Interview Implementations

Comprehensive data structure implementations in **Java** and **C++** for FAANG Senior Software Engineer technical interviews.

## Data Structures Included

| Data Structure | Java | C++ | Key Problems |
|----------------|------|-----|--------------|
| **Trie** | `Trie.java` | `Trie.cpp` | Autocomplete, Word Search II, Maximum XOR |
| **Union-Find** | `UnionFind.java` | `UnionFind.cpp` | Connected Components, Redundant Connection, Accounts Merge |
| **Segment Tree** | `SegmentTree.java` | `SegmentTree.cpp` | Range Sum/Min Query, Count Smaller Numbers |
| **Binary Indexed Tree** | `BinaryIndexedTree.java` | `BinaryIndexedTree.cpp` | Range Sum Query, Count Inversions |
| **LRU/LFU Cache** | `LRUCache.java` | `LRUCache.cpp` | LRU Cache, LFU Cache |
| **Graph Algorithms** | `Graph.java` | `Graph.cpp` | Dijkstra, Bellman-Ford, Topological Sort, MST |
| **Monotonic Stack/Deque** | `MonotonicStack.java` | `MonotonicStack.cpp` | Next Greater Element, Sliding Window Maximum |
| **Heap** | `Heap.java` | `Heap.cpp` | Kth Largest, Median Finder, Top K Frequent |

## Quick Reference

### Trie (Prefix Tree)
```
Insert/Search/StartsWith: O(m) where m = word length
Space: O(ALPHABET_SIZE × m × n)
```

### Union-Find (DSU)
```
Find/Union: O(α(n)) ≈ O(1) amortized
Space: O(n)
```

### Segment Tree
```
Build: O(n)
Query/Update: O(log n)
Range Update (Lazy): O(log n)
Space: O(4n)
```

### Binary Indexed Tree (Fenwick)
```
Build: O(n)
Query/Update: O(log n)
Space: O(n) - more efficient than Segment Tree
```

### LRU/LFU Cache
```
Get/Put: O(1)
Space: O(capacity)
```

### Graph Algorithms
```
BFS/DFS: O(V + E)
Dijkstra: O((V + E) log V)
Bellman-Ford: O(V × E)
Floyd-Warshall: O(V³)
Topological Sort: O(V + E)
```

### Monotonic Stack
```
Next Greater/Smaller Element: O(n)
Largest Rectangle in Histogram: O(n)
Trapping Rain Water: O(n)
```

## Common Interview Patterns

### 1. Range Query Problems
- Use **Segment Tree** for complex operations (min/max/sum with updates)
- Use **BIT** for simpler sum queries (more memory efficient)

### 2. Connected Components / Graph Union
- Use **Union-Find** for dynamic connectivity
- Use **DFS/BFS** for static graphs

### 3. Prefix/Autocomplete
- Use **Trie** for string prefix operations
- XOR Trie for bit manipulation problems

### 4. Sliding Window + Extrema
- Use **Monotonic Deque** for O(1) max/min in window
- Use **Monotonic Stack** for next greater/smaller problems

### 5. Top K / Median
- Use **Heap** for top K elements
- Use **Two Heaps** for running median

## How to Compile

### Java
```bash
cd java
javac *.java
java Trie  # or any other class
```

### C++
```bash
cd cpp
g++ -std=c++17 -o trie Trie.cpp && ./trie
```

## Tips for Interviews

1. **Start with brute force**, then optimize
2. **Clarify constraints** - size of input, range of values
3. **Think about edge cases** - empty input, single element, duplicates
4. **Consider time/space tradeoffs**
5. **Practice writing clean, bug-free code**

## Leetcode Problems by Data Structure

### Trie
- 208: Implement Trie
- 211: Design Add and Search Words
- 212: Word Search II
- 421: Maximum XOR of Two Numbers

### Union-Find
- 200: Number of Islands
- 323: Number of Connected Components
- 684: Redundant Connection
- 721: Accounts Merge
- 990: Satisfiability of Equality Equations

### Segment Tree / BIT
- 307: Range Sum Query - Mutable
- 315: Count of Smaller Numbers After Self
- 327: Count of Range Sum
- 493: Reverse Pairs

### Monotonic Stack
- 84: Largest Rectangle in Histogram
- 239: Sliding Window Maximum
- 496: Next Greater Element I
- 503: Next Greater Element II
- 739: Daily Temperatures
- 907: Sum of Subarray Minimums

### Graph
- 743: Network Delay Time (Dijkstra)
- 787: Cheapest Flights Within K Stops
- 207: Course Schedule (Topological Sort)
- 210: Course Schedule II
- 1584: Min Cost to Connect All Points (MST)
