package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Collection;
import java.util.NoSuchElementException;

import ucb.util.CommandArgs;
import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Katrina Sharonin
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine currMachine = readConfig();

        try {
            while (_input.hasNextLine()) {

                String currLine = _input.nextLine();

                if (currLine.contains("*")) {
                    setUp(currMachine, currLine);
                } else {

                    String converted = currMachine.convert(currLine);

                    printMessageLine(converted);

                }
            }
        } catch (ArrayIndexOutOfBoundsException excp) {
            throw error("process: no setting line given");
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */

    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());

            int s;
            int p;

            if (!_config.hasNextLine()) {
                throw new EnigmaException("missing an int line");
            }

            if (!_config.hasNextInt()) {
                throw new EnigmaException("need 1 of two ints");
            }
            s = _config.nextInt();

            if (!_config.hasNextInt()) {
                throw new EnigmaException("need two ints");
            }

            p = _config.nextInt();


            if (s <= p | p <= 0) {
                System.out.println("error will be faced");
                throw new EnigmaException("s p etc violated");
            }


            if (_config.hasNextInt()) {
                throw new EnigmaException("only 2 ints allowed");
            }


            Collection<Rotor> allRotors = new ArrayList<Rotor>();

            while (_config.hasNext()) {
                Rotor addToList = readRotor();
                allRotors.add(addToList);
            }

            return new Machine(_alphabet, s, p, allRotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    public ArrayList<Character> arrayMaker(String input) {

        ArrayList<Character> result = new ArrayList();
        for (int i = 0; i < input.length(); i++) {
            char curChar = input.charAt(i);
            result.add(curChar);
        }

        return result;
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor expecty;

            String namey = _config.next();
            Permutation permy;
            String permcycles = "";
            String potentialNotches = _config.next();

            Pattern hopeThisWork = Pattern.compile("\\([^*]+");

            while (_config.hasNext(hopeThisWork)) {
                String theNextCheck = _config.next();

                ArrayList arrayP = arrayMaker(theNextCheck);

                if (arrayP.get(0).equals('(')
                        && arrayP.get(arrayP.size() - 1).equals(')')) {
                    permcycles = permcycles +  theNextCheck;

                } else {
                    throw new EnigmaException("end ) missing");
                }
            }


            permy = new Permutation(permcycles, _alphabet);

            if (potentialNotches.charAt(0) == 'M') {
                String notchesPassed = potentialNotches.substring(1);
                expecty = new MovingRotor(namey, permy, notchesPassed);
            } else if (potentialNotches.charAt(0) == 'N') {
                expecty = new FixedRotor(namey, permy);
            } else {
                expecty = new Reflector(namey, permy);
            }

            return expecty;

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner settingsHandler = new Scanner(settings);
        settingsHandler.next();

        int sizetoCallNextForNames = M.numRotors();
        String[] arrayForInsertRotors =
                new String[sizetoCallNextForNames];

        for (int i = 0; i < arrayForInsertRotors.length; i++) {
            if (settingsHandler.hasNext()) {
                String currNamey = settingsHandler.next();
                arrayForInsertRotors[i] = currNamey;

            } else {
                throw new EnigmaException("mistmatch setting size");
            }
        }

        M.insertRotors(arrayForInsertRotors);

        if (!settingsHandler.hasNext()) {
            throw new EnigmaException("missing a setting");
        }

        String settingtopass = settingsHandler.next();
        M.setRotors(settingtopass);


        if (settingsHandler.hasNext()) {
            String plugBoardStringPass = "";

            while (settingsHandler.hasNext()) {
                String currentConcat = settingsHandler.next();
                plugBoardStringPass =
                        plugBoardStringPass + currentConcat;
            }
            Permutation needed =
                    new Permutation(plugBoardStringPass, M.alphabet());
            M.setPlugboard(needed);

        } else {
            String haveAnywaysPlug = "";
            Permutation neededNot =
                    new Permutation(haveAnywaysPlug, M.alphabet());
            M.setPlugboard(neededNot);
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {

        String[] stringprocess = msg.split("");

        for (int i = 0; i < stringprocess.length; i++) {
            if ((i > 0) && (i % 5 == 0)) {
                _output.print(" ");
            }
            _output.print(stringprocess[i]);
        }
        _output.print("\n");

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
