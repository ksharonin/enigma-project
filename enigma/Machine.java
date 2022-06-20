package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Katrina Sharonin
 */

class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;

        ArrayList<Rotor> takeInAllRotors = new ArrayList<>();
        takeInAllRotors.addAll(allRotors);
        _allRotors = takeInAllRotors;

        _rotorslots = new ArrayList<>();


    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        int suby = _numRotors;
        if (suby < 1) {
            throw new EnigmaException("Machine: slots must be > 1");
        }
        return suby;
    }


    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;

    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {

        return _rotorslots.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        try {
            if (_rotorslots.size() == 0) {
                for (int i = 0; i < rotors.length; i++) {
                    String currname = rotors[i];
                    for (int j = 0; j < _allRotors.size(); j++) {
                        String nameofrotorfromcollec = _allRotors.get(j).name();
                        if (nameofrotorfromcollec.equals(currname)) {
                            _allRotors.get(j).set(0);
                            Rotor needed = _allRotors.get(j);
                            _rotorslots.add(needed);

                        }
                    }
                }
            } else {
                for (int i = 0; i < rotors.length; i++) {
                    String currnamee = rotors[i];
                    for (int j = 0; j < _allRotors.size(); j++) {
                        String nameofrotorfromc = _allRotors.get(j).name();
                        if (nameofrotorfromc.equals(currnamee)) {
                            _allRotors.get(j).set(0);
                            _rotorslots.set(i, _allRotors.get(j));

                        }
                    }
                }
            }

            int pawls = numPawls();
            int counter = 0;

            for (int i = 0; i < _rotorslots.size(); i++) {
                Rotor curr = _rotorslots.get(i);

                if (curr.rotates()) {
                    counter += 1;
                }
            }

            if (counter != pawls) {
                throw new EnigmaException("wrong num of pawls vs rotors");
            }

            Rotor checkifreflector = _rotorslots.get(0);
            if (!checkifreflector.reflecting()) {
                throw new EnigmaException("insertRotors: R missing at in 0");
            }

        } catch (IndexOutOfBoundsException excp) {
            throw error("insert Rotors: invalid setting");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {

        if (setting.length() != this.numRotors() - 1) {
            throw new EnigmaException("setRotors: length wrong");
        }


        int processingsettingstring = 0;

        try {
            for (int i = 1; i < _rotorslots.size(); i++) {
                if (processingsettingstring >= setting.length()) {
                    break;
                }

                Rotor curr = _rotorslots.get(i);
                curr.set(setting.charAt(processingsettingstring));
                processingsettingstring += 1;
            }
        } catch (IndexOutOfBoundsException excp) {
            throw error("setRotors: invalid setting");
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        ArrayList sub = plugboard.arrcycles();
        for (int i = 0; i < sub.size(); i++) {
            ArrayList look = (ArrayList) sub.get(i);
            if (look.size() > 2) {
                throw new EnigmaException("plug cycles should only be 2");
            }

        }

        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            int num = numRotors();
            for (int r = 1; r < num; r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] advancebnarralst = new boolean[_rotorslots.size()];
        advancebnarralst[_rotorslots.size() - 1] = true;


        for (int i = _rotorslots.size() - 1; i > 0; i--) {
            if (_rotorslots.get(i).atNotch()
                    && _rotorslots.get(i - 1).rotates()) {
                advancebnarralst[i] = true;
                advancebnarralst[i - 1] = true;
            }
        }

        for (int k = 1; k < _rotorslots.size(); k++) {
            if (advancebnarralst[k]) {
                _rotorslots.get(k).advance();
            }
        }

    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {


        for (int i = _rotorslots.size() - 1; i >= 0; i -= 1) {
            Rotor curr = _rotorslots.get(i);
            c = curr.convertForward(c);
        }


        for (int i = 1; i < _rotorslots.size(); i++) {
            Rotor curry = _rotorslots.get(i);
            c = curry.convertBackward(c);

        }

        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {

        if (msg.equals("")) {
            return "";
        }

        msg = msg.replaceAll("\\s", "");
        String result = "";

        for (int i = 0; i < msg.length(); i++) {
            char currChar = msg.charAt(i);
            int currCtoInt = _alphabet.toInt(currChar);
            int indexConverted = convert(currCtoInt);
            char toAdd = _alphabet.toChar(indexConverted);
            result = result + toAdd;
        }

        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** num of rotors. */
    private Integer _numRotors;

    /** num of pawls. */
    private Integer _pawls;

    /** all rotors available. */
    private ArrayList<Rotor> _allRotors;

    /** Plugboard. */
    private Permutation _plugboard;

    /** Exisiting rotors. */
    private ArrayList<Rotor> _rotorslots;

}
