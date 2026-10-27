/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.Arrays;

public class Zones {
    
    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        // TODO
        int n = s.n();
        int[][] outgoing = Graphs.slidesFrom(s);
        boolean[] seen = new boolean[n];
        int[] finish = new int[n];
        int filled = 0;
        int[] vertexStack = new int[n];
        int[] edgePosition = new int[n];
        
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int depth = 1;
            vertexStack[0] = start;
            edgePosition[0] = 0;
            seen[start] = true;
            while (depth > 0) {
                int u = vertexStack[depth - 1];
                int pos = edgePosition[depth - 1];
                if (pos == outgoing[u].length) {
                    finish[filled++] = u;
                    depth--;
                } else {
                    edgePosition[depth - 1]++;
                    int v = s.to()[outgoing[u][pos]];
                    if (!seen[v]) {
                        seen[v] = true;
                        vertexStack[depth] = v;
                        edgePosition[depth] = 0;
                        depth++;
                    }
                }
            }
        }
        
        int[][] incoming = Graphs.slidesInto(s);
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zone = 0;
        for (int k = filled - 1; k >= 0; k--) {
            int start = finish[k];
            if (zoneOf[start] != -1) continue;
            int depth = 1;
            vertexStack[0] = start;
            zoneOf[start] = zone;
            while (depth > 0) {
                int u = vertexStack[--depth];
                for (int edge : incoming[u]) {
                    int v = s.from()[edge];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zone;
                        vertexStack[depth++] = v;
                    }
                }
            }
            zone++;
        }
        return zoneOf;
    }
    
    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        // TODO
        if (zoneCount == 0) return new long[]{0, 0};
        int[] head = new int[zoneCount];
        int[] next = new int[s.count()];
        Arrays.fill(head, -1);
        
        for (int k = 0; k < s.count(); k++) {
            int fromZone = zoneOf[s.from()[k]];
            int toZone = zoneOf[s.to()[k]];
            if (fromZone == toZone) continue;
            next[k] = head[fromZone];
            head[fromZone] = k;
        }

        long[] stages = new long[zoneCount];
        Arrays.fill(stages, 1); 
        final long INF = Long.MAX_VALUE / 4;
        long[] distance = new long[zoneCount];
        Arrays.fill(distance, INF);
        distance[zoneOf[0]] = 0;
        long longest = 1;
        for (int fromZone = 0; fromZone < zoneCount; fromZone++) {
            longest = Math.max(longest, stages[fromZone]);
            for (int k = head[fromZone]; k != -1; k = next[k]) {
                int toZone = zoneOf[s.to()[k]];
                stages[toZone] = Math.max(stages[toZone], stages[fromZone] + 1);
                if (distance[fromZone] != INF) {
                    distance[toZone] = Math.min(distance[toZone], distance[fromZone] + s.time()[k]);
                }
            }
        }
        
        return new long[]{longest, distance[zoneOf[s.n() - 1]]};
    }
}
