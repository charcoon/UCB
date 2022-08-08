package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     */

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.  
    @Test
    public void testNaturalRuns () {
        IntList e = new IntList(1, null);
        IntList a = IntList.list(1, 3, 7, 5);
        IntList b = IntList.list(1, 3, 7);
        IntList c = IntList.list(5);
        IntListList d = IntListList.list(b,c);
        assertEquals(Lists.naturalRuns(e), new IntListList(new IntList(1, null), null));
        assertEquals(d, Lists.naturalRuns(a));
    }



    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
