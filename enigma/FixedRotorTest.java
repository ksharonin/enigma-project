package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** J-unit tests for fixedrotor */

public class FixedRotorTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    @Test (expected = EnigmaException.class)
    public void advanceFailTest() {
        Alphabet alphy = new Alphabet("(ABCD)");
        Permutation permy = new Permutation("(AD)(B)", alphy);
        FixedRotor rotory = new FixedRotor("VII", permy);
        rotory.advance();
    }

}
