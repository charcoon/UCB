package enigma;

import static enigma.EnigmaException.*;

/**
 * Class that represents a rotating rotor in the enigma machine.
 *
 * @author Charlie Zhou
 */
class MovingRotor extends Rotor {


    /**
     *
     */
    private String _notches;

    /**
     * A rotor named NAME whose permutation in its default setting is PERM, and
     * whose notches are at the positions indicated in NOTCHES. The Rotor is
     * initally in its 0 setting (first character of its alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    void advance() {
        _posn = this.permutation().wrap(_posn + 1);
    }

    /**
     * Returns true iff I am positioned to allow the rotor to my left to
     * advance.
     */
    boolean atNotch() {
        char c = this.permutation().alphabet().toChar(_posn);
        return this._notches.indexOf(c) != -1;
    }

    /**
     * Return true iff I have a ratchet and can move.
     */
    @Override
    boolean rotates() {
        return true;
    }

}
