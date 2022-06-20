package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Katrina Sharonin
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);

        if (notches.startsWith("M")) {
            _notches = notches.substring(1);
        } else {
            _notches = notches;
        }

    }

    @Override
    void advance() {
        int advancement = (setting() + 1) % alphabet().size();
        this.set(advancement);

    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    boolean atNotch() {


        int settingA = setting() % (alphabet().size());
        boolean alternate = notches().contains(String.valueOf
                (super.permutation().alphabet().toChar(settingA)));

        return alternate;

    }


    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean reflecting() {
        return false;
    }

    /** Notches of instance of moving Rotor.*/
    private String _notches;

}
