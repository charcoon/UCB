import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** HW #7, Sorting ranges.
 *  @author Charlie Zhou
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        // REPLACE WITH APPROPRIATE STATEMENTS.
        intervals.sort((arr1, arr2) -> arr1[0] - arr2[0]);

        int result = 0;
        int start = Integer.MIN_VALUE;
        int end = Integer.MIN_VALUE;
        for (int x = 0; x < intervals.size(); x++) {
            if (intervals.get(x)[0] > end) {
                result += end - start;
                start = intervals.get(x)[0];
                end = intervals.get(x)[1];
            } else if (intervals.get(x)[0] <= end && intervals.get(x)[1] > end) {
                end = intervals.get(x)[1];
            }
        }
        return result + end - start;
    }

    /** Test intervals. */
    static final int[][] INTERVALS = {
        {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
    };
    /** Covered length of INTERVALS. */
    static final int CORRECT = 23;

    /** Performs a basic functionality test on the coveredLength method. */
    @Test
    public void basicTest() {
        assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}
