/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
import java.util.Arrays;

public class Escape {
    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        // TODO
        int n = w.cells();
        if (n == 0) return new Result(new int[0], 0, 0);
        int[][] open = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbours : open) if (neighbours.length == 1) deadEnds++;
        
        boolean[] marked = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        int[] stackCell = new int[n];
        int[] stackDirection = new int[n];
        int depth = 1, visitedCount = 1;
        stackCell[0] = 0;
        marked[0] = true;

        while (depth > 0 && !marked[n - 1]) {
            int u = stackCell[depth - 1];
            int direction = stackDirection[depth - 1];
            if (direction == 4) {
                depth--; 
                continue;
            }
            stackDirection[depth - 1]++;
            int v = switch (direction) {
                case 0 -> u - w.cols(); 
                case 1 -> u + 1;        
                case 2 -> u + w.cols(); 
                default -> u - 1;       
            };
            
            boolean adjacent = false;
            for (int neighbor : open[u]) {
                if (neighbor == v) { adjacent = true; break; }
            }
            if (adjacent && !marked[v]) {
                marked[v] = true;
                visitedCount++;
                parent[v] = u;
                stackCell[depth] = v;
                stackDirection[depth] = 0;
                depth++;
            }
        }

        if (!marked[n - 1]) return new Result(new int[0], visitedCount, deadEnds);
        int length = 0;
        for (int u = n - 1; u != -1; u = parent[u]) length++;
        int[] path = new int[length];
        for (int u = n - 1, i = length - 1; u != -1; u = parent[u]) path[i--] = u;
        return new Result(path, visitedCount, deadEnds);
    }
}
