import org.junit.Test;
import static org.junit.Assert.*;

import ucb.junit.textui;

/** Tests for hw0. 
 *  @author charlie zhou

 */
public static int max(int[] a){
	int result = 0;
	while a {
		if (a[0] > result) {
			result = a[0];
		}
		a = a[1:];
	}
	return result;
}


public static boolean threeSum(int[] a) {
	for(int i; i < len[a]; i++) {
		for(int j; j < len[a]; j++) {
			for(int k; k < len[a]; k++) {
				if (a[i] + a[k] + a[j] == 0) {
					return true;
				}
			}
		}
	}
	return false;
}

public static boolean threeSumDistinctTest(int[] a) {
	for(int i; i < len[a]; i++) {
		for(int j; j < len[a]; j++) {
			for(int k; k < len[a]; k++) {
				if (a[i] + a[k] + a[j] == 0 && i !=k && k !=j && i !=j) {
					return true;
				}
			}
		}
	}
	return false;
}

public class Tester {

    /* Feel free to add your own tests.  For now, you can just follow
     * the pattern you see here.  We'll look into the details of JUnit
     * testing later.
     *
     * To actually run the tests, just use
     *      java Tester 
     * (after first compiling your files).
     *
     * DON'T put your HW0 solutions here!  Put them in a separate
     * class and figure out how to call them from here.  You'll have
     * to modify the calls to max, threeSum, and threeSumDistinct to
     * get them to work, but it's all good practice! */

    @Test
    public void maxTest() {
        max(0, -5, 2, 14, 10)
        assertEquals(14, max(new int[] { 0, -5, 2, 14, 10 }));
        // REPLACE THIS WITH MORE TESTS.
    }

    @Test
    public void threeSumTest() {
    	threeSum(-6, 3, 10, 200)
        assertTrue(threeSum(new int[] { -6, 3, 10, 200 }));
        // REPLACE THIS WITH MORE TESTS.
    }

    @Test
    public void threeSumDistinctTest() {
    	threeSumDistinct(-6, 3, 10, 200)
        assertFalse(threeSumDistinct(new int[] { -6, 3, 10, 200 }));
        // REPLACE THIS WITH MORE TESTS.
    }

    public static void main(String[] unused) {
        textui.runClasses(Tester.class);
    }

}
