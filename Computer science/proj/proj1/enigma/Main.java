package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.error;

/**
 * Enigma simulator.
 *
 * @author Charlie Zhou
 */
public final class Main {

    /**
     * Machine.
     */
    private Machine _machine;
    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Process a sequence of encryptions and decryptions, as specified by ARGS,
     * where 1 <= ARGS.length <= 3. ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file containing
     * messages. Otherwise, input comes from the standard input. ARGS[2] is
     * optional; when present, it names an output file for processed messages.
     * Otherwise, output goes to the standard output. Exits normally if there
     * are no errors in the input; otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (NoSuchElementException | EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration file
     * _config and apply it to the messages in _input, sending the results to
     * _output.
     */
    private void process() {

        _machine = readConfig();
        readInput();

    }

    /**
     *
     * @param config config
     * @param contentList contentList
     */
    void encrypt(String config, ArrayList<String> contentList) {
        Scanner sc = new Scanner(config);
        sc.next("[^ \t\n\r]+");

        String[] rotorsName = new String[this._machine.numRotors()];
        for (int i = 0; i < this._machine.numRotors(); i++) {
            rotorsName[i] = sc.next("[^ \t\n\r]+");
        }
        String setting = sc.next("[^ \t\n\r]+");
        String extra = null;
        if (!sc.hasNext("(\\([a-zA-Z0-9]+\\))+") && sc.hasNext()) {
            extra = sc.next();
        }


        String plugBoardString = "";
        while (sc.hasNext("[^ \t\n\r]+")) {
            String line = sc.next("[^ \t\n\r]+");
            if (!line.matches("(\\([a-zA-Z0-9]+\\))+")) {
                throw error("plug board error");
            }
            plugBoardString += line;
        }


        _machine.setSlot(rotorsName);


        _machine.setRotors(setting);


        _machine.setPlugboard(new Permutation(plugBoardString, _alphabet));
        _machine.configExtra(extra, setting);

        for (String content : contentList) {

            String filterContent = "";
            for (int i = 0; i < content.length(); i++) {

                if (this._alphabet.contains(content.charAt(i))) {
                    filterContent += content.charAt(i);
                }
            }

            String s = this._machine.convert(filterContent);


            for (int i = 0; i < s.length(); i++) {
                if (i > 0 && i % 5 == 0) {
                    _output.print(' ');
                }
                _output.print(s.charAt(i));
            }

            _output.println();
        }

    }

    /**
     * read input.
     */
    void readInput() {
        try {

            ArrayList<String> lineList = new ArrayList<>();
            while (_input.hasNextLine()) {
                lineList.add(_input.nextLine().trim());
            }

            if (!lineList.get(0).startsWith("*")) {
                throw error("The input might not start with a setting.");
            }

            String config = "";
            ArrayList<String> contentList = new ArrayList<>();
            int index = 0;
            while (index < lineList.size()) {
                config = lineList.get(index);
                index++;
                boolean showNewline = false;
                while (index < lineList.size()
                        && !lineList.get(index).startsWith("*")) {
                    if (lineList.get(index).length() == 0) {
                        showNewline = true;
                    } else {
                        contentList.add(lineList.get(index));
                    }
                    index++;
                }
                encrypt(config, contentList);
                if (showNewline) {
                    _output.println("");
                }
                contentList.clear();
            }
        } catch (NoSuchElementException | EnigmaException e) {
            throw error("input file truncated");
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            int nRotor, nMoveRotor;

            String line;

            line = this._config.next("\\S+");
            _alphabet = new Alphabet(line);

            nRotor = _config.nextInt();
            nMoveRotor = _config.nextInt();
            Machine machine = new Machine(_alphabet, nRotor, nMoveRotor,
                    new ArrayList<Rotor>());

            ArrayList<String> tokens = new ArrayList<>();
            while (_config.hasNext("[\\S]+")) {
                line = _config.next("[\\S]+");
                tokens.add(line);


            }

            String name = "";
            String config = "";
            String cycle = "";
            int index = 0;
            while (index < tokens.size()) {
                name = tokens.get(index);
                index++;
                config = tokens.get(index);
                index++;

                while (index < tokens.size() && tokens.get(index).
                        matches("(\\(\\S+\\))+")) {
                    cycle += tokens.get(index);
                    index++;
                }
                machine.addRotorFromConfig(name, config, cycle);
                cycle = "";
            }

            return machine;
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            return null;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS, which must have
     * the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {

    }

    /**
     * Print MSG in groups of five (except that the last group may have fewer
     * letters).
     */
    private void printMessageLine(String msg) {

    }

}
