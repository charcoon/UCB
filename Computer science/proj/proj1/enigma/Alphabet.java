package enigma;

import java.util.ArrayList;

/**
 * An alphabet of encodable characters. Provides a mapping from characters to
 * and from indices into the alphabet.
 *
 * @author Charlie Zhou
 */
class Alphabet {

    /**
     *
     */
    private ArrayList<Character> charList = new ArrayList<>();

    /**
     * A new alphabet containing CHARS. Character number #k has index K
     * (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        chars = chars.trim();
        for (int i = 0; i < chars.length(); i++) {
            charList.add(chars.charAt(i));
        }
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return charList.size();
    }

    /**
     * Returns true if preprocess(CH) is in this alphabet.
     */
    boolean contains(char ch) {

        return toInt(ch) >= 0;
    }

    /**
     * Returns character number INDEX in the alphabet,
     * size().
     */
    char toChar(int index) {
        return charList.get(index);
    }

    /**
     * Returns the index of character preprocess(CH), which must be in the
     * alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {

        return charList.indexOf(ch);
    }

}
