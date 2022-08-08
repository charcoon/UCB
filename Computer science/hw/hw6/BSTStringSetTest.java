import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;


/**
 * Implementation of a BST based String Set.
 * @author zhou
 */

public class BSTStringSetTest {
    @Test
    public void testPut() {
        BSTStringSet A1 = new BSTStringSet();
        A1.put("HELLO");
        A1.put("WORLD");
        A1.put("THIS");
        A1.put("IS");
        A1.put("ZHOU");

        ArrayList<String> A2 = new ArrayList<String>();
        A2.add("HELLO");
        A2.add("IS");
        A2.add("THIS");
        A2.add("WORLD");
        A2.add("ZHOU");

        assertEquals(A1.asList(), A2);
    }

}