import java.sql.Array;
import java.util.Arrays;
import java.util.Observable;
/**
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private boolean targetFound = false;
    private Maze maze;
    private int[] temp;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        temp = new int[m.N() * m.N()];
    }

    @Override
    public void solve() {

        solve_helper(temp, 0);
    }

    public void solve_helper(int[] i, int s) {
        if (marked[s]) {
            targetFound = true;
            edgeTo[s] = i[s];
            announce();
            int next = i[s];
            while (next != s) {
                edgeTo[next] = i[next];
                announce();
                next = i[next];
            }

        }

        if (targetFound) {
            return;
        }

        marked[s] = true;
        announce();

        for (int w : maze.adj(s)) {
            if (w != i[s]) {
                i[w] = s;
                solve_helper(i, w);
                if (targetFound) {
                    return;
                }
            }
        }

    }


}

