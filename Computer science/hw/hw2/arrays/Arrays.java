package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        /* *Replace this body with the solution. */
        int[] result = new int[A.length + B.length];
        System.arraycopy(A, 0, result, 0, A.length);
        System.arraycopy(B, 0, result, A.length, B.length);
        return result;
    }


    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        /* *Replace this body with the solution. */
        int answer[] = new int[len];
        int count = 0;
        int curr = 0;
        int last = 0;
        last = start;
        while (count < len) {
            answer[curr] = A[last];
            count ++;
            curr ++;
            last ++;
        }
        return answer;
    }



    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
   static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        for (int i = 1; i < A.length; i ++) {
            if (A[i - 1] > A[i]) {
                int[][] temp1 = {Utils.subarray(A, 0, i)};
                int[][] temp2 = naturalRuns(remove(A, 0, i));
                int[][] result = new int[1 + temp2.length][];
                System.arraycopy(temp1, 0, result, 0, 1 );
                System.arraycopy(temp2, 0, result, 1, temp2.length);
                return result;
            }
        }
        int[][] result = {A};
        return result;
    }

}

