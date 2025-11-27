# GraphX Utility Library

GraphX is a lightweight Java utility that lets you treat arbitrary labels (ints or custom tuples) as graph nodes, then run unweighted BFS or non-negative weighted Dijkstra searches on top of them. The library is self-contained in `graphx.java`, so you can drop the file into any competitive-programming solution or larger project and use the classes as black-box helpers.

## Getting Started

```bash
# Compile the single source file
javac graphx.java

# Run the demo driver (reads from stdin)
java graphx < input.txt
```

The bundled `main` demonstrates how to build an undirected unit-weight graph from stdin and prints both the BFS hop count and the Dijkstra weighted distance between two nodes. You can remove or adapt the `main` method when embedding the utility elsewhere.

## Class Overview

### Graph

Responsible for storing adjacency lists and mapping arbitrary vertex labels to dense integer ids.

| Method | Description |
| --- | --- |
| `Graph()` / `Graph(boolean directed)` / `Graph(int expectedNodes, boolean directed)` | Create an empty graph (directed by default). `expectedNodes` optionally pre-sizes the adjacency list. |
| `intern(int/Tuple2/Tuple3 node)` | Registers the label (if new) and returns its internal id. Automatically grows adjacency storage. |
| `find(int/Tuple2/Tuple3 node)` | Returns the internal id if the label exists, otherwise `-1`. Does **not** insert. |
| `addEdge(u, v)` | Adds a unit-weight edge between two labels (directed or undirected depending on constructor). |
| `addEdge(u, v, weight)` | Adds a non‑negative weighted edge. Throws if `weight < 0`. |
| `neighbors(int id)` | Package-private helper that returns the adjacency list for an already interned node. |
| `nodeCount()` | Returns the current number of interned nodes. |

**Node labels** – You can supply an `int`, a `Tuple2(int a, int b)`, or a `Tuple3(int a, int b, int c)` anywhere the API accepts a node. All tuple classes implement `equals`/`hashCode`, so your labels behave like composite keys.

### BFS

Performs classic breadth-first search on the graph, treating every edge as cost 1.

| Method | Description |
| --- | --- |
| `BFS(Graph graph)` | Reuses the provided graph reference. |
| `run(source)` | Accepts `int`, `Tuple2`, or `Tuple3`. Interns the source label and explores all reachable nodes. Calls automatically reset internal buffers. |
| `minDist(target)` | Returns the minimum hop count from the last `run` source to `target`, or `-1` if unreachable/unknown. Does not insert new labels (returns `-1` for unseen nodes). |

**Complexity** – `O(V + E)` per run. Memory is linear in the number of interned nodes.

### Dijkstra

Computes shortest paths with non-negative weights.

| Method | Description |
| --- | --- |
| `Dijkstra(Graph graph)` | Reuses the graph reference. |
| `run(source)` | Accepts any supported label type. Resets internal distance array, then runs a binary-heap Dijkstra. |
| `minDist(target)` | Returns the shortest distance from the last `run` source to `target`, or `-1` if unreachable. |

**Complexity** – `O(E log V)` due to the priority queue. Edge weights must be non-negative.

## Example Usage

```java
Graph g = new Graph(false); // undirected

// Use ints for building ids
g.addEdge(101, 202);
g.addEdge(202, 303, 4);

// Mix in tuple labels (e.g., coordinates)
Tuple2 a = new Tuple2(0, 0);
Tuple2 b = new Tuple2(0, 1);
Tuple2 c = new Tuple2(1, 1);
g.addEdge(a, b);      // unit weight
g.addEdge(b, c, 2);   // weighted edge

BFS bfs = new BFS(g);
bfs.run(101);
System.out.println(bfs.minDist(303)); // -> hop count if reachable

Dijkstra dij = new Dijkstra(g);
dij.run(a);
System.out.println(dij.minDist(c)); // -> weighted distance (int)
```

## Input/Output Contract of the Demo Driver

The provided `main` expects the following stdin format:

```
N M
u1 v1
u2 v2
...
uM vM
start end
```

- `N` – optional hint for the number of nodes (used only for initial sizing).
- `M` – number of edges (each treated as undirected with weight 1 in the demo).
- `ui vi` – endpoints of each edge (integers).
- `start end` – query pair for BFS/Dijkstra.

Sample:

```
5 4
1 2
2 3
3 4
1 5
1 4
```

Output:

```
3   # BFS hops from 1 to 4 (1-2-3-4)
3   # Dijkstra distance (same because all weights = 1)
```

## Tips

- Call `intern` sparingly—most helpers (adding edges, running searches) call it for you.
- Use `find` before querying `minDist` when you are unsure whether a node was ever inserted.
- Store and reuse single instances of `BFS`/`Dijkstra` per graph; they reset themselves each run to avoid reallocating arrays.
- If you only need weighted shortest paths, you can skip constructing `BFS`, and vice versa.

That’s it—drop `graphx.java` into your project, construct a `Graph`, and treat the traversal classes as black boxes for shortest-path queries.
