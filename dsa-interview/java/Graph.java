package java;

import java.util.*;

/**
 * Graph Algorithms Implementation
 * 
 * Essential algorithms for FAANG interviews:
 * - BFS, DFS (iterative and recursive)
 * - Dijkstra's Algorithm
 * - Bellman-Ford Algorithm
 * - Floyd-Warshall Algorithm
 * - Topological Sort (Kahn's and DFS)
 * - Cycle Detection
 * - Strongly Connected Components (Kosaraju's)
 * - Minimum Spanning Tree (Prim's, Kruskal's)
 */
public class Graph {
    
    private int vertices;
    private List<List<int[]>> adjList;  // {neighbor, weight}
    
    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }
    
    public void addEdge(int src, int dest, int weight) {
        adjList.get(src).add(new int[]{dest, weight});
    }
    
    public void addUndirectedEdge(int src, int dest, int weight) {
        adjList.get(src).add(new int[]{dest, weight});
        adjList.get(dest).add(new int[]{src, weight});
    }
    
    // ==================== BFS ====================
    public List<Integer> bfs(int start) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(start);
        visited[start] = true;
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            
            for (int[] neighbor : adjList.get(node)) {
                if (!visited[neighbor[0]]) {
                    visited[neighbor[0]] = true;
                    queue.offer(neighbor[0]);
                }
            }
        }
        return result;
    }
    
    // ==================== DFS ====================
    public List<Integer> dfs(int start) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[vertices];
        dfsHelper(start, visited, result);
        return result;
    }
    
    private void dfsHelper(int node, boolean[] visited, List<Integer> result) {
        visited[node] = true;
        result.add(node);
        
        for (int[] neighbor : adjList.get(node)) {
            if (!visited[neighbor[0]]) {
                dfsHelper(neighbor[0], visited, result);
            }
        }
    }
    
    // Iterative DFS
    public List<Integer> dfsIterative(int start) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[vertices];
        Deque<Integer> stack = new ArrayDeque<>();
        
        stack.push(start);
        
        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (!visited[node]) {
                visited[node] = true;
                result.add(node);
                
                for (int[] neighbor : adjList.get(node)) {
                    if (!visited[neighbor[0]]) {
                        stack.push(neighbor[0]);
                    }
                }
            }
        }
        return result;
    }
    
    // ==================== Dijkstra's Algorithm ====================
    public int[] dijkstra(int start) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        
        // {distance, node}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, start});
        
        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], node = curr[1];
            
            if (d > dist[node]) continue;  // Skip outdated entries
            
            for (int[] neighbor : adjList.get(node)) {
                int next = neighbor[0], weight = neighbor[1];
                if (dist[node] + weight < dist[next]) {
                    dist[next] = dist[node] + weight;
                    pq.offer(new int[]{dist[next], next});
                }
            }
        }
        return dist;
    }
    
    // ==================== Bellman-Ford Algorithm ====================
    // Handles negative weights, detects negative cycles
    public int[] bellmanFord(int start) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        
        // Relax all edges V-1 times
        for (int i = 0; i < vertices - 1; i++) {
            for (int u = 0; u < vertices; u++) {
                if (dist[u] == Integer.MAX_VALUE) continue;
                for (int[] neighbor : adjList.get(u)) {
                    int v = neighbor[0], w = neighbor[1];
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }
        
        // Check for negative cycle
        for (int u = 0; u < vertices; u++) {
            if (dist[u] == Integer.MAX_VALUE) continue;
            for (int[] neighbor : adjList.get(u)) {
                int v = neighbor[0], w = neighbor[1];
                if (dist[u] + w < dist[v]) {
                    throw new RuntimeException("Graph contains negative cycle");
                }
            }
        }
        
        return dist;
    }
    
    // ==================== Floyd-Warshall Algorithm ====================
    // All-pairs shortest paths
    public int[][] floydWarshall() {
        int[][] dist = new int[vertices][vertices];
        int INF = Integer.MAX_VALUE / 2;
        
        // Initialize
        for (int i = 0; i < vertices; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        
        // Add edges
        for (int u = 0; u < vertices; u++) {
            for (int[] neighbor : adjList.get(u)) {
                dist[u][neighbor[0]] = neighbor[1];
            }
        }
        
        // DP
        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                for (int j = 0; j < vertices; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        
        return dist;
    }
    
    // ==================== Topological Sort (Kahn's BFS) ====================
    public List<Integer> topologicalSort() {
        int[] inDegree = new int[vertices];
        for (int u = 0; u < vertices; u++) {
            for (int[] neighbor : adjList.get(u)) {
                inDegree[neighbor[0]]++;
            }
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            
            for (int[] neighbor : adjList.get(node)) {
                inDegree[neighbor[0]]--;
                if (inDegree[neighbor[0]] == 0) {
                    queue.offer(neighbor[0]);
                }
            }
        }
        
        if (result.size() != vertices) {
            throw new RuntimeException("Graph has a cycle - topological sort not possible");
        }
        
        return result;
    }
    
    // ==================== Cycle Detection ====================
    // For directed graph using DFS with colors
    public boolean hasCycleDirected() {
        int[] color = new int[vertices];  // 0: white, 1: gray, 2: black
        
        for (int i = 0; i < vertices; i++) {
            if (color[i] == 0) {
                if (hasCycleDFS(i, color)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasCycleDFS(int node, int[] color) {
        color[node] = 1;  // Mark as visiting
        
        for (int[] neighbor : adjList.get(node)) {
            if (color[neighbor[0]] == 1) {  // Back edge
                return true;
            }
            if (color[neighbor[0]] == 0 && hasCycleDFS(neighbor[0], color)) {
                return true;
            }
        }
        
        color[node] = 2;  // Mark as visited
        return false;
    }
    
    // For undirected graph using Union-Find (see UnionFind.java)
    
    // ==================== Prim's MST ====================
    public List<int[]> primMST() {
        List<int[]> mst = new ArrayList<>();
        boolean[] visited = new boolean[vertices];
        // {weight, from, to}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        
        // Start from vertex 0
        visited[0] = true;
        for (int[] neighbor : adjList.get(0)) {
            pq.offer(new int[]{neighbor[1], 0, neighbor[0]});
        }
        
        while (!pq.isEmpty() && mst.size() < vertices - 1) {
            int[] edge = pq.poll();
            int weight = edge[0], from = edge[1], to = edge[2];
            
            if (visited[to]) continue;
            
            visited[to] = true;
            mst.add(new int[]{from, to, weight});
            
            for (int[] neighbor : adjList.get(to)) {
                if (!visited[neighbor[0]]) {
                    pq.offer(new int[]{neighbor[1], to, neighbor[0]});
                }
            }
        }
        
        return mst;
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        // Create a graph
        Graph g = new Graph(6);
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 5);
        g.addEdge(2, 3, 8);
        g.addEdge(2, 4, 10);
        g.addEdge(3, 4, 2);
        g.addEdge(3, 5, 6);
        g.addEdge(4, 5, 3);
        
        System.out.println("BFS from 0: " + g.bfs(0));
        System.out.println("DFS from 0: " + g.dfs(0));
        
        // Dijkstra
        int[] dist = g.dijkstra(0);
        System.out.println("\nDijkstra from 0:");
        for (int i = 0; i < dist.length; i++) {
            System.out.println("  Distance to " + i + ": " + dist[i]);
        }
        
        // Topological Sort
        Graph dag = new Graph(6);
        dag.addEdge(5, 2, 1);
        dag.addEdge(5, 0, 1);
        dag.addEdge(4, 0, 1);
        dag.addEdge(4, 1, 1);
        dag.addEdge(2, 3, 1);
        dag.addEdge(3, 1, 1);
        
        System.out.println("\nTopological Sort: " + dag.topologicalSort());
        
        // Cycle detection
        System.out.println("DAG has cycle: " + dag.hasCycleDirected());
        
        Graph cyclic = new Graph(3);
        cyclic.addEdge(0, 1, 1);
        cyclic.addEdge(1, 2, 1);
        cyclic.addEdge(2, 0, 1);
        System.out.println("Cyclic graph has cycle: " + cyclic.hasCycleDirected());
        
        // MST
        Graph mstGraph = new Graph(5);
        mstGraph.addUndirectedEdge(0, 1, 2);
        mstGraph.addUndirectedEdge(0, 3, 6);
        mstGraph.addUndirectedEdge(1, 2, 3);
        mstGraph.addUndirectedEdge(1, 3, 8);
        mstGraph.addUndirectedEdge(1, 4, 5);
        mstGraph.addUndirectedEdge(2, 4, 7);
        mstGraph.addUndirectedEdge(3, 4, 9);
        
        System.out.println("\nPrim's MST edges:");
        for (int[] edge : mstGraph.primMST()) {
            System.out.println("  " + edge[0] + " - " + edge[1] + " : " + edge[2]);
        }
    }
}
