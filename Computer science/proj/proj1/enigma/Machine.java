package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static enigma.EnigmaException.error;

/**
 * Class that represents a complete enigma machine.
 *
 * @author Charlie Zhou
 */
class Machine {

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;
    /**
     *
     */
    private ArrayList<Rotor> _selectRotorList = new ArrayList<Rotor>();
    /**
     *
     */
    private int _nRotor;
    /**
     *
     */
    private int _nMoveRotor;
    /**
     *
     */
    private ArrayList<Rotor> _rotorList;
    /**
     *
     */
    private Permutation _plugboard;

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots, and
     * 0 <= PAWLS < NUMROTORS pawls. ALLROTORS contains all the available
     * rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _nRotor = numRotors;
        _nMoveRotor = pawls;
        if (allRotors != null) {
            _rotorList = new ArrayList<Rotor>(allRotors);
        }
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _nRotor;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _nMoveRotor;
    }

    /**
     * @param name   name
     * @param config config
     * @param cycle  cycle
     */
    void addRotorFromConfig(String name, String config, String cycle) {
        if (!name.matches("[a-zA-Z0-9]+")) {
            throw error("illegal character in name field");
        }
        if (!config.matches("[a-zA-Z0-9]+")) {
            throw error("illegal character in config field");
        }
        if (cycle.isEmpty()) {
            throw error("illegal character in cycle field");
        }


        Rotor rotor;
        switch (config.charAt(0)) {
        case 'R':
            rotor = new Reflector(name, new Permutation(cycle, _alphabet));
            break;
        case 'N':
            rotor = new FixedRotor(name, new Permutation(cycle, _alphabet));
            break;
        case 'M':
            rotor = new MovingRotor(name, new Permutation(cycle, _alphabet),
                        config.substring(1));
            break;
        default:
            throw error("illegal character in config field");
        }
        _rotorList.add(rotor);
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of available
     * rotors (ROTORS[0] names the reflector). Initially, all rotors are set at
     * their 0 setting.
     */
    void insertRotors(String[] rotors) {

    }
    /**
     *   mment here.
     * @param extra 43
     * @param setting */
    void configExtra(String extra, String setting) {
        if (extra == null) {
            for (int i = 0; i < setting.length(); i++) {
                this._selectRotorList.get(i + 1).offset = 0;
            }
        } else {
            for (int i = 0; i < setting.length(); i++) {
                this._selectRotorList.get(i + 1).offset =
                        (int) extra.charAt(i) - 'A';
            }
        }
    }


    /**
     * @param rotors rotors
     */
    void setSlot(String[] rotors) {

        _selectRotorList.clear();
        int j = 0;
        HashSet<String> nameSet = new HashSet<>();
        for (int i = 0; i < rotors.length; i++) {

            if (nameSet.contains(rotors[i])) {
                throw error("A rotor might be repeated in the setting line");
            }
            nameSet.add(rotors[i]);

            for (j = 0; j < _rotorList.size(); j++) {
                if (_rotorList.get(j).name().equalsIgnoreCase(rotors[i])) {
                    break;
                }
            }
            if (j == _rotorList.size()) {

                throw error("The rotors might be misnamed");
            }

            _selectRotorList.add(_rotorList.get(j));
        }

        if (!_selectRotorList.get(0).getClass().equals(Reflector.class)) {
            throw error("The first rotor might not be a reflector");
        }

    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers to the
     * leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        /* check rotor number */
        if (setting.length() != numRotors() - 1) {
            throw error("initial positions string might be the wrong "
                    + "length or contain characters not in the alphabet.");
        }

        for (int i = 1; i < _selectRotorList.size(); i++) {

            char c = setting.charAt(i - 1);
            if (!this._alphabet.contains(c)) {
                throw error("initial positions string might be the wrong "
                        + "length or contain characters not in the alphabet.");
            }

            _selectRotorList.get(i).set(c);
        }

    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        this._plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an index in
     * the range 0..alphabet size - 1), after first advancing
     * <p>
     * the machine.
     */
    int convert(int c) {
        if (this._selectRotorList.size() != this._nRotor) {
            throw error("rotor number wrong");
        }

        boolean[] advance = new boolean[this._nRotor];
        advance[this._nRotor - 1] = true;

        int moveStartIndex = this._nRotor - this._nMoveRotor;
        if (this._nMoveRotor == 1) {
            moveStartIndex = moveStartIndex;
        } else {
            if (this._selectRotorList.get(moveStartIndex + 1).atNotch()) {
                advance[moveStartIndex] = true;
            }

            for (int i = moveStartIndex + 1; i < this._nRotor - 1; i++) {

                if (this._selectRotorList.get(i).atNotch()
                        || this._selectRotorList.get(i + 1).atNotch()) {
                    advance[i] = true;
                }
            }
        }

        for (int i = moveStartIndex; i < this._nRotor; i++) {
            if (advance[i]) {
                this._selectRotorList.get(i).advance();
            }
        }
        int temp = this._plugboard.permute(c);
        for (int i = this._selectRotorList.size() - 1; i >= 0; i--) {
            Rotor r = this._selectRotorList.get(i);
            if (i == this._selectRotorList.size() - 1) {
                temp = r.convertForward(temp);
            } else {
                temp = r.convertForward(temp);
            }
        }

        for (int i = 1; i < this._selectRotorList.size(); i++) {
            Rotor r = this._selectRotorList.get(i);

            if (i == this._selectRotorList.size() - 1) {
                temp = r.convertBackward(temp);
            } else {
                temp = r.convertBackward(temp);
            }
        }

        temp = this._plugboard.permute(temp);
        return temp;

    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of the rotors
     * accordingly.
     */
    String convert(String msg) {
        String s = "";
        for (int i = 0; i < msg.length(); i++) {

            int ori = this._alphabet.toInt(msg.charAt(i));
            int crypt = convert(ori);

            s += this._alphabet.toChar(crypt);
        }

        return s;
    }

}
