package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Katrina Sharonin
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */

    Reflector(String name, Permutation perm) {
        super(name, perm);
        _setting = 0;
    }


    @Override
    boolean atNotch() {
        return false;
    }

    @Override
    String notches() {
        return "";
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    @Override
    void set(char cposn) {
        throw error("char ver: reflector has only one position");
    }

    /** Setting for reflector instance. */
    private int _setting;

}
