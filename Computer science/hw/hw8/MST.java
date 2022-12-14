import java.util.Arrays;
import java.util.Comparator;

/** Minimal spanning tree utility.
 *  @author
 */
public class MST {

    /** Given an undirected, weighted, connected graph whose vertices are
     *  numbered 1 to V, and an array E of edges, returns a list of edges
     *  in E that form a minimal spanning tree of the input graph.
     *  Each edge in E is a three-element int array of the form (u, v, w),
     *  where 0 < u < v <= V are vertex numbers, and 0 <= w is the weight
     *  of the edge. The result is an array containing edges from E.
     *  Neither E nor the arrays in it may be modified.  There may be
     *  multiple edges between vertices.  The objects in the returned array
     *  are a subset of those in E (they do not include copies of the
     *  original edges, just the original edges themselves.) */
    public static int[][] mst(int A, int[][] B) {
        UnionFind S;
        int[][] EdgeSet = new int[A-1][];

        S = new UnionFind(A);
        int[][] E0 = Arrays.copyOf(B, B.length);
        Arrays.sort(E0, EDGE_WEIGHT_COMPARATOR);

        int l = 0;
        for (int[] e : E0) {
            if (!S.samePartition(e[0], e[1])) {
                EdgeSet[l] = e;
                S.union(e[0], e[1]);
                l += 1;
            }
        }
        return EdgeSet;
    }

    /** An ordering of edges by weight. */
    private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
            new Comparator<int[]>() {
                @Override
                public int compare(int[] e0, int[] e1) {
                    return e0[2] - e1[2];
                }
            };

}