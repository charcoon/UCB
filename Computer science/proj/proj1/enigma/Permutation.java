package enigma;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author Charlie Zhou
 */
class Permutation {

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;
    /**
     *
     */
    private  HashMap<Character, Character> _permuteMap;
    /**
     *
     */
    private  HashMap<Character, Character> _invertMap;

    /**
     * Set this Permutation to that specified by CYCLES, a string in the form
     * "(cccc) (cc) ..." where the c's are characters in ALPHABET, which is
     * interpreted as a permutation in cycle notation. Characters in the
     * alphabet that are not included in any cycle map to themselves. Whitespace
     * is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _permuteMap = new HashMap<>();
        _invertMap = new HashMap<>();
        cycles = cycles.trim();
        Pattern pattern = Pattern.compile("\\((\\w+)\\)");
        Matcher m = pattern.matcher(cycles);
        while (m.find()) {
            String cycle = m.group(1);
            addCycle(cycle);
        }
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {

        int i = 0;
        for (i = 0; i < cycle.length() - 1; i++) {
            this._permuteMap.put(cycle.charAt(i), cycle.charAt(i + 1));
            this._invertMap.put(cycle.charAt(i + 1), cycle.charAt(i));
        }
        this._permuteMap.put(cycle.charAt(i), cycle.charAt(0));
        this._invertMap.put(cycle.charAt(0), cycle.charAt(i));
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the alphabet
     * size.
     */
    int permute(int p) {
        char t = this._alphabet.toChar(wrap(p));
        return this._alphabet.toInt(this.permute(t));
    }

    /**
     * Return the result of applying the inverse of this permutation to C modulo
     * the alphabet size.
     */
    int invert(int c) {
        char t = this._alphabet.toChar(wrap(c));
        return this._alphabet.toInt(this.invert(t));
    }

    /**
     * Return the result of applying this permutation to the index of P in
     * ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        char ret;
        if (this._permuteMap.containsKey(p)) {
            ret = this._permuteMap.get(p);
        } else {
            ret = p;
        }
        return ret;
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        char ret;
        if (this._invertMap.containsKey(c)) {
            ret = this._invertMap.get(c);
        } else {
            ret = c;
        }
        return ret;
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a permutation
     * for which no value maps to itself).
     */
    boolean derangement() {
        for (Character c : this._permuteMap.keySet()) {
            if (c.equals(this._permuteMap.get(c))) {
                return false;
            }
        }
        return true;
    }
}
