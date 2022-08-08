import java.util.LinkedList;
import java.util.Observable;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *  @author Josh Hug
 */

public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int a;
    private int b;
    private boolean targetFound = false;
    private Maze maze;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        a = maze.xyTo1D(sourceX, sourceY);
        b = maze.xyTo1D(targetX, targetY);
        distTo[a] = 0;
        edgeTo[a] = a;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs(int v) {
        // TODO: Your code here. Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        marked[v] = true;
        announce();

        Queue<Integer> q = new LinkedList<>();
        q.add(v);

        while (!q.isEmpty()) {
            int i = q.remove();
            for (int w: maze.adj(i)) {
                if (i == b) { targetFound = true; }
                if (targetFound) { return; }
                if (!marked[w]) {
                    edgeTo[w] = i;
                    announce();
                    distTo[w] = distTo[i] + 1;
                    announce();
                    marked[w] = true;
                    announce();
                    q.add(w);
                }
            }
        }

    }


    @Override
    public void solve() {
        bfs(a);
    }
}

