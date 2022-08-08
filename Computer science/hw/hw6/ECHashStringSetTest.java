import org.junit.Test;
import static org.junit.Assert.*;


import java.util.HashSet;
import java.util.LinkedList;


/**
 * Implementation of a BST based String Set.
 * @author Zhou
 */

public class ECHashStringSetTest {

    @Test
    public void testlinkedlist() {
        LinkedList<String>[] A = new LinkedList[5];
    }

    @Test
    public void testPut() {
        int N = 1000000;
        ECHashStringSet a1 = new ECHashStringSet();
        HashSet a2 = new HashSet();
        String s = "Zhou";
        for (int i = 0; i < N; i++) {
            s = StringUtils.nextString(s);
            a1.put(s);
            a2.add(s);
        }
        assertEquals("size does not match", a2.size(), a1.size());

        boolean contain = true;
        s = "Zhou";
        for (int i=0; i < N; i++) {
            s = StringUtils.nextString(s);
            if (!a1.contains(s)) {
                contain = false;
            }
        }
        assertEquals(true, contain);

        ECHashStringSet a3 = new ECHashStringSet();
        a3.put("1");
        a3.put("2");
        a3.put("3");
        a3.put("4");
        a3.put("5");
        a3.put("6");
        a3.put("7");

        assertEquals("size of hashset", 7, 7);
    }}


