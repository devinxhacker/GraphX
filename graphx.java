import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Objects;
import java.util.Comparator;

// --- Helper Classes (Java 8/11 compatible) ---

//---------------BLACKBOX CODE START-------------

class Pair {
    final int a;
    final int b;

    public Pair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int a() { return a; }
    public int b() { return b; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return a == pair.a && b == pair.b;
    }

    @Override
    public int hashCode() { return Objects.hash(a, b); }

    @Override
    public String toString() { return "(" + a + ", " + b + ")"; }
}

class Tuple2 {
    final int a;
    final int b;

    public Tuple2(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int a() { return a; }
    public int b() { return b; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2 tuple2 = (Tuple2) o;
        return a == tuple2.a && b == tuple2.b;
    }

    @Override
    public int hashCode() { return Objects.hash(a, b); }

    @Override
    public String toString() { return "(" + a + ", " + b + ")"; }
}

class Tuple3 {
    final int a;
    final int b;
    final int c;

    public Tuple3(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int a() { return a; }
    public int b() { return b; }
    public int c() { return c; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple3 tuple3 = (Tuple3) o;
        return a == tuple3.a && b == tuple3.b && c == tuple3.c;
    }

    @Override
    public int hashCode() { return Objects.hash(a, b, c); }

    @Override
    public String toString() { return "(" + a + ", " + b + ", " + c + ")"; }
}

// --- Hash Class ---
class Hash {
    private final Map<Tuple3, Integer> hashTable;

    public Hash() {
        this.hashTable = new HashMap<>();
    }

    public int intern(int x) { return intern(new Tuple3(x, 0, 0)); }
    public int intern(Tuple2 x) { return intern(new Tuple3(x.a(), x.b(), 0)); }
    public int intern(Tuple3 x) {
        Integer existing = hashTable.get(x);
        if (existing != null) return existing;
        int id = hashTable.size();
        hashTable.put(x, id);
        return id;
    }

    public int find(int x) { return find(new Tuple3(x, 0, 0)); }
    public int find(Tuple2 x) { return find(new Tuple3(x.a(), x.b(), 0)); }
    public int find(Tuple3 x) {
        Integer existing = hashTable.get(x);
        return existing == null ? -1 : existing;
    }

    public int size() { return hashTable.size(); }
}

// --- Graph Class ---
class Graph {
    private final boolean directed;
    private final Hash hash;
    private final List<List<Pair>> adj;

    public Graph() { this(true); }

    public Graph(boolean directed) { this(0, directed); }

    public Graph(int expectedNodes, boolean directed) {
        this.directed = directed;
        this.hash = new Hash();
        this.adj = new ArrayList<>(Math.max(0, expectedNodes));
        for (int i = 0; i < expectedNodes; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public int nodeCount() { return adj.size(); }
    public boolean isDirected() { return directed; }

    public int intern(int node) { return ensureCapacity(hash.intern(node)); }
    public int intern(Tuple2 node) { return ensureCapacity(hash.intern(node)); }
    public int intern(Tuple3 node) { return ensureCapacity(hash.intern(node)); }

    public int find(int node) { return hash.find(node); }
    public int find(Tuple2 node) { return hash.find(node); }
    public int find(Tuple3 node) { return hash.find(node); }

    public void addEdge(int u, int v) { addEdge(u, v, 1); }
    public void addEdge(Tuple2 u, Tuple2 v) { addEdge(u, v, 1); }
    public void addEdge(Tuple3 u, Tuple3 v) { addEdge(u, v, 1); }

    public void addEdge(int u, int v, int weight) {
        int uId = intern(u);
        int vId = intern(v);
        addEdgeInternal(uId, vId, weight);
    }

    public void addEdge(Tuple2 u, Tuple2 v, int weight) {
        int uId = intern(u);
        int vId = intern(v);
        addEdgeInternal(uId, vId, weight);
    }

    public void addEdge(Tuple3 u, Tuple3 v, int weight) {
        int uId = intern(u);
        int vId = intern(v);
        addEdgeInternal(uId, vId, weight);
    }

    List<Pair> neighbors(int id) { return adj.get(id); }

    private void addEdgeInternal(int u, int v, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Negative weights are not supported");
        }
        adj.get(u).add(new Pair(v, weight));
        if (!directed) {
            adj.get(v).add(new Pair(u, weight));
        }
    }

    private int ensureCapacity(int id) {
        while (adj.size() <= id) {
            adj.add(new ArrayList<>());
        }
        return id;
    }
}

// --- BFS Class ---
class BFS {
    private final Graph graph;
    private int[] distance;
    private boolean[] visited;

    public BFS(Graph graph) {
        this.graph = graph;
    }

    public void run(int source) { runInternal(graph.intern(source)); }
    public void run(Tuple2 source) { runInternal(graph.intern(source)); }
    public void run(Tuple3 source) { runInternal(graph.intern(source)); }

    public int minDist(int target) { return readDistance(graph.find(target)); }
    public int minDist(Tuple2 target) { return readDistance(graph.find(target)); }
    public int minDist(Tuple3 target) { return readDistance(graph.find(target)); }

    private void runInternal(int sourceId) {
        prepareState();
        Queue<Integer> queue = new ArrayDeque<>();
        distance[sourceId] = 0;
        visited[sourceId] = true;
        queue.add(sourceId);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            int nextDistance = distance[node] + 1;
            for (Pair edge : graph.neighbors(node)) {
                int neighbor = edge.a();
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = nextDistance;
                    queue.add(neighbor);
                }
            }
        }
    }

    private void prepareState() {
        int n = graph.nodeCount();
        if (distance == null || distance.length != n) {
            distance = new int[n];
            visited = new boolean[n];
        }
        Arrays.fill(distance, -1);
        Arrays.fill(visited, false);
    }

    private int readDistance(int nodeId) {
        if (nodeId < 0 || distance == null || nodeId >= distance.length) {
            return -1;
        }
        return distance[nodeId];
    }
}

// --- Dijkstra Class ---
class Dijkstra {
    private final Graph graph;
    private int[] distance;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    public void run(int source) { runInternal(graph.intern(source)); }
    public void run(Tuple2 source) { runInternal(graph.intern(source)); }
    public void run(Tuple3 source) { runInternal(graph.intern(source)); }

    public int minDist(int target) { return readDistance(graph.find(target)); }
    public int minDist(Tuple2 target) { return readDistance(graph.find(target)); }
    public int minDist(Tuple3 target) { return readDistance(graph.find(target)); }

    private void runInternal(int sourceId) {
        prepareState();
        PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::b));
        distance[sourceId] = 0;
        pq.add(new Pair(sourceId, 0));

        while (!pq.isEmpty()) {
            Pair top = pq.poll();
            int node = top.a();
            int currentDist = top.b();
            if (currentDist > distance[node]) {
                continue;
            }
            for (Pair edge : graph.neighbors(node)) {
                int neighbor = edge.a();
                int weight = edge.b();
                int candidate = currentDist + weight;
                if (distance[neighbor] == -1 || candidate < distance[neighbor]) {
                    distance[neighbor] = candidate;
                    pq.add(new Pair(neighbor, candidate));
                }
            }
        }
    }

    private void prepareState() {
        int n = graph.nodeCount();
        if (distance == null || distance.length != n) {
            distance = new int[n];
        }
        Arrays.fill(distance, -1);
    }

    private int readDistance(int nodeId) {
        if (nodeId < 0 || distance == null || nodeId >= distance.length) {
            return -1;
        }
        return distance[nodeId];
    }
}

// ---------------BLACKBOX CODE END---------------


// --- Main Class (Driver Code) ---
public class graphx {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) {
            sc.close();
            return;
        }

        int n = sc.nextInt();
        int m = sc.nextInt();
        Graph graph = new Graph(n, false);

        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            graph.addEdge(u, v, 1);
        }

        int start = sc.nextInt();
        int end = sc.nextInt();

        BFS bfs = new BFS(graph);
        bfs.run(start);
        int hopDistance = bfs.minDist(end);

        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.run(start);
        int weightedDistance = dijkstra.minDist(end);

        if (hopDistance == -1) {
            System.out.println(0);
        } else {
            System.out.println(hopDistance);
        }

        if (weightedDistance == -1) {
            System.out.println(0);
        } else {
            System.out.println(weightedDistance);
        }

        sc.close();
    }
}