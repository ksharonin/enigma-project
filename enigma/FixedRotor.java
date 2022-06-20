package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Katrina Sharonin
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);

    }

    @Override
    String notches() {
        throw new EnigmaException("FixedRotor: no notches allowed");
    }

    @Override
    void advance() {
        throw new EnigmaException("FixedRotor: no advance");
    }

    @Override
    boolean rotates() {
        return false;
    }

}
