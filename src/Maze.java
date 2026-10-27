/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
import java.util.Arrays;
import java.util.PriorityQueue;

public class Maze {
    
    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        // TODO
        int n = w.cells();
        if (n == 0) return new int[0];
        DisjointSet dsu = new DisjointSet(n);
        int[] broken = new int[n - 1];
        int taken = 0;
        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                broken[taken++] = k;
                if (taken == n - 1) break;
            }
        }
        return Arrays.copyOf(broken, taken);
    }
    
    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        // TODO
        
        long[] sorted = new long[w.count()];
        for (int k = 0; k < sorted.length; k++) {
            sorted[k] = ((long) w.cost()[k] << 32) | (k & 0xffffffffL);
        }
        Arrays.sort(sorted);
        int n = w.cells();
        if (n == 0) return new int[0];
        DisjointSet dsu = new DisjointSet(n);
        int[] takenEdges = new int[n - 1];
        int taken = 0;
        for (long entry : sorted) {
            int k = (int) entry;
            if (dsu.union(w.a()[k], w.b()[k])) {
                takenEdges[taken++] = k;
                if (taken == n - 1) break;
            }
        }
        return Arrays.copyOf(takenEdges, taken);
    }

    private record Candidate(int vertex, int cost) {}
    
    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        // TODO
        int n = w.cells();
        if (n == 0) return 0;
        int m = w.count();
        
        int[] head = new int[n];
        Arrays.fill(head, -1);
        int[] to = new int[2 * m];
        int[] next = new int[2 * m];
        int[] weight = new int[2 * m];
        int arc = 0;
        for (int k = 0; k < m; k++) {
            int a = w.a()[k], b = w.b()[k], c = w.cost()[k];
            to[arc] = b; weight[arc] = c;
            next[arc] = head[a]; head[a] = arc++;
            to[arc] = a; weight[arc] = c;
            next[arc] = head[b]; head[b] = arc++;
        }

        boolean[] visited = new boolean[n];
        int[] best = new int[n];
        Arrays.fill(best, Integer.MAX_VALUE);
        PriorityQueue<Candidate> queue = new PriorityQueue<>(
            (a, b) -> Integer.compare(a.cost(), b.cost()));
        best[0] = 0;
        queue.offer(new Candidate(0, 0));
        int chosen = 0;
        long total = 0;
        while (!queue.isEmpty() && chosen < n) {
            Candidate item = queue.poll();
            int u = item.vertex();
            if (visited[u] || item.cost() != best[u]) continue;
            visited[u] = true;
            chosen++;
            total += item.cost();
            for (int e = head[u]; e != -1; e = next[e]) {
                int v = to[e];
                if (!visited[v] && weight[e] < best[v]) {
                    best[v] = weight[e];
                    queue.offer(new Candidate(v, weight[e]));
                }
            }
        }
        if (chosen != n) throw new IllegalArgumentException("Maze is disconnected");
        return total;
    }
}
