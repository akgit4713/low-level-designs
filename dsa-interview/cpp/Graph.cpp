/**
 * Graph Algorithms Implementation in C++
 * 
 * Essential algorithms for FAANG interviews
 */

#include <iostream>
#include <vector>
#include <queue>
#include <stack>
#include <climits>
#include <algorithm>
#include <functional>

class Graph {
private:
    int vertices;
    std::vector<std::vector<std::pair<int, int>>> adjList;  // {neighbor, weight}

public:
    Graph(int v) : vertices(v), adjList(v) {}
    
    void addEdge(int src, int dest, int weight = 1) {
        adjList[src].push_back({dest, weight});
    }
    
    void addUndirectedEdge(int src, int dest, int weight = 1) {
        adjList[src].push_back({dest, weight});
        adjList[dest].push_back({src, weight});
    }
    
    // ==================== BFS ====================
    std::vector<int> bfs(int start) {
        std::vector<int> result;
        std::vector<bool> visited(vertices, false);
        std::queue<int> q;
        
        q.push(start);
        visited[start] = true;
        
        while (!q.empty()) {
            int node = q.front();
            q.pop();
            result.push_back(node);
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    q.push(neighbor);
                }
            }
        }
        return result;
    }
    
    // BFS for shortest path in unweighted graph
    std::vector<int> bfsShortestPath(int start, int end) {
        std::vector<int> parent(vertices, -1);
        std::vector<bool> visited(vertices, false);
        std::queue<int> q;
        
        q.push(start);
        visited[start] = true;
        
        while (!q.empty()) {
            int node = q.front();
            q.pop();
            
            if (node == end) break;
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = node;
                    q.push(neighbor);
                }
            }
        }
        
        // Reconstruct path
        std::vector<int> path;
        for (int node = end; node != -1; node = parent[node]) {
            path.push_back(node);
        }
        std::reverse(path.begin(), path.end());
        
        if (path[0] != start) return {};  // No path exists
        return path;
    }
    
    // ==================== DFS ====================
    std::vector<int> dfs(int start) {
        std::vector<int> result;
        std::vector<bool> visited(vertices, false);
        
        std::function<void(int)> dfsHelper = [&](int node) {
            visited[node] = true;
            result.push_back(node);
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (!visited[neighbor]) {
                    dfsHelper(neighbor);
                }
            }
        };
        
        dfsHelper(start);
        return result;
    }
    
    // ==================== Dijkstra ====================
    std::vector<int> dijkstra(int start) {
        std::vector<int> dist(vertices, INT_MAX);
        dist[start] = 0;
        
        // {distance, node}
        std::priority_queue<std::pair<int, int>, 
                           std::vector<std::pair<int, int>>,
                           std::greater<>> pq;
        pq.push({0, start});
        
        while (!pq.empty()) {
            auto [d, node] = pq.top();
            pq.pop();
            
            if (d > dist[node]) continue;
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (dist[node] + weight < dist[neighbor]) {
                    dist[neighbor] = dist[node] + weight;
                    pq.push({dist[neighbor], neighbor});
                }
            }
        }
        return dist;
    }
    
    // Dijkstra with path reconstruction
    std::pair<int, std::vector<int>> dijkstraWithPath(int start, int end) {
        std::vector<int> dist(vertices, INT_MAX);
        std::vector<int> parent(vertices, -1);
        dist[start] = 0;
        
        std::priority_queue<std::pair<int, int>,
                           std::vector<std::pair<int, int>>,
                           std::greater<>> pq;
        pq.push({0, start});
        
        while (!pq.empty()) {
            auto [d, node] = pq.top();
            pq.pop();
            
            if (d > dist[node]) continue;
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (dist[node] + weight < dist[neighbor]) {
                    dist[neighbor] = dist[node] + weight;
                    parent[neighbor] = node;
                    pq.push({dist[neighbor], neighbor});
                }
            }
        }
        
        // Reconstruct path
        std::vector<int> path;
        for (int node = end; node != -1; node = parent[node]) {
            path.push_back(node);
        }
        std::reverse(path.begin(), path.end());
        
        return {dist[end], path};
    }
    
    // ==================== Bellman-Ford ====================
    std::vector<int> bellmanFord(int start) {
        std::vector<int> dist(vertices, INT_MAX);
        dist[start] = 0;
        
        for (int i = 0; i < vertices - 1; i++) {
            for (int u = 0; u < vertices; u++) {
                if (dist[u] == INT_MAX) continue;
                for (auto& [v, w] : adjList[u]) {
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }
        
        // Check for negative cycle
        for (int u = 0; u < vertices; u++) {
            if (dist[u] == INT_MAX) continue;
            for (auto& [v, w] : adjList[u]) {
                if (dist[u] + w < dist[v]) {
                    throw std::runtime_error("Negative cycle detected");
                }
            }
        }
        
        return dist;
    }
    
    // ==================== Floyd-Warshall ====================
    std::vector<std::vector<int>> floydWarshall() {
        const int INF = INT_MAX / 2;
        std::vector<std::vector<int>> dist(vertices, std::vector<int>(vertices, INF));
        
        for (int i = 0; i < vertices; i++) {
            dist[i][i] = 0;
        }
        
        for (int u = 0; u < vertices; u++) {
            for (auto& [v, w] : adjList[u]) {
                dist[u][v] = w;
            }
        }
        
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
    
    // ==================== Topological Sort (Kahn's) ====================
    std::vector<int> topologicalSort() {
        std::vector<int> inDegree(vertices, 0);
        for (int u = 0; u < vertices; u++) {
            for (auto& [v, w] : adjList[u]) {
                inDegree[v]++;
            }
        }
        
        std::queue<int> q;
        for (int i = 0; i < vertices; i++) {
            if (inDegree[i] == 0) {
                q.push(i);
            }
        }
        
        std::vector<int> result;
        while (!q.empty()) {
            int node = q.front();
            q.pop();
            result.push_back(node);
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (--inDegree[neighbor] == 0) {
                    q.push(neighbor);
                }
            }
        }
        
        if (result.size() != vertices) {
            throw std::runtime_error("Graph has a cycle");
        }
        
        return result;
    }
    
    // ==================== Cycle Detection ====================
    bool hasCycleDirected() {
        std::vector<int> color(vertices, 0);  // 0: white, 1: gray, 2: black
        
        std::function<bool(int)> dfs = [&](int node) -> bool {
            color[node] = 1;
            
            for (auto& [neighbor, weight] : adjList[node]) {
                if (color[neighbor] == 1) return true;  // Back edge
                if (color[neighbor] == 0 && dfs(neighbor)) return true;
            }
            
            color[node] = 2;
            return false;
        };
        
        for (int i = 0; i < vertices; i++) {
            if (color[i] == 0 && dfs(i)) return true;
        }
        return false;
    }
    
    // ==================== Prim's MST ====================
    std::vector<std::tuple<int, int, int>> primMST() {
        std::vector<std::tuple<int, int, int>> mst;
        std::vector<bool> visited(vertices, false);
        
        // {weight, from, to}
        std::priority_queue<std::tuple<int, int, int>,
                           std::vector<std::tuple<int, int, int>>,
                           std::greater<>> pq;
        
        visited[0] = true;
        for (auto& [neighbor, weight] : adjList[0]) {
            pq.push({weight, 0, neighbor});
        }
        
        while (!pq.empty() && mst.size() < vertices - 1) {
            auto [w, from, to] = pq.top();
            pq.pop();
            
            if (visited[to]) continue;
            
            visited[to] = true;
            mst.push_back({from, to, w});
            
            for (auto& [neighbor, weight] : adjList[to]) {
                if (!visited[neighbor]) {
                    pq.push({weight, to, neighbor});
                }
            }
        }
        
        return mst;
    }
    
    // ==================== Bipartite Check ====================
    bool isBipartite() {
        std::vector<int> color(vertices, -1);
        
        for (int start = 0; start < vertices; start++) {
            if (color[start] != -1) continue;
            
            std::queue<int> q;
            q.push(start);
            color[start] = 0;
            
            while (!q.empty()) {
                int node = q.front();
                q.pop();
                
                for (auto& [neighbor, weight] : adjList[node]) {
                    if (color[neighbor] == -1) {
                        color[neighbor] = 1 - color[node];
                        q.push(neighbor);
                    } else if (color[neighbor] == color[node]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
};

int main() {
    Graph g(6);
    g.addEdge(0, 1, 4);
    g.addEdge(0, 2, 2);
    g.addEdge(1, 2, 1);
    g.addEdge(1, 3, 5);
    g.addEdge(2, 3, 8);
    g.addEdge(2, 4, 10);
    g.addEdge(3, 4, 2);
    g.addEdge(3, 5, 6);
    g.addEdge(4, 5, 3);
    
    std::cout << "BFS from 0: ";
    for (int x : g.bfs(0)) std::cout << x << " ";
    std::cout << std::endl;
    
    std::cout << "DFS from 0: ";
    for (int x : g.dfs(0)) std::cout << x << " ";
    std::cout << std::endl;
    
    std::cout << "\nDijkstra from 0:\n";
    auto dist = g.dijkstra(0);
    for (int i = 0; i < dist.size(); i++) {
        std::cout << "  Distance to " << i << ": " << dist[i] << std::endl;
    }
    
    // Topological Sort
    Graph dag(6);
    dag.addEdge(5, 2);
    dag.addEdge(5, 0);
    dag.addEdge(4, 0);
    dag.addEdge(4, 1);
    dag.addEdge(2, 3);
    dag.addEdge(3, 1);
    
    std::cout << "\nTopological Sort: ";
    for (int x : dag.topologicalSort()) std::cout << x << " ";
    std::cout << std::endl;
    
    std::cout << "DAG has cycle: " << (dag.hasCycleDirected() ? "true" : "false") << std::endl;
    
    // MST
    Graph mstGraph(5);
    mstGraph.addUndirectedEdge(0, 1, 2);
    mstGraph.addUndirectedEdge(0, 3, 6);
    mstGraph.addUndirectedEdge(1, 2, 3);
    mstGraph.addUndirectedEdge(1, 3, 8);
    mstGraph.addUndirectedEdge(1, 4, 5);
    mstGraph.addUndirectedEdge(2, 4, 7);
    mstGraph.addUndirectedEdge(3, 4, 9);
    
    std::cout << "\nPrim's MST edges:\n";
    for (auto& [from, to, weight] : mstGraph.primMST()) {
        std::cout << "  " << from << " - " << to << " : " << weight << std::endl;
    }
    
    return 0;
}
