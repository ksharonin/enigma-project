package enigma;
import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {

    @Test
    public void constructorTestAndMore() {
        Alphabet test = new Alphabet("ABCD");
        assertEquals(4, test.size());
        assertTrue(test.contains('A'));
        assertFalse(test.contains('a'));
        assertFalse(test.contains('Z'));

        assertEquals(0, test.toInt('A'));
        assertEquals(3, test.toInt('D'));


        assertEquals('B', test.toChar(1));
        assertEquals('C', test.toChar(2));

    }

    @Test
    public void testComplex() {
        String testcomplex = "abedf1092";
        Alphabet testcomply = new Alphabet(testcomplex);
        assertEquals(9, testcomply.size());

        for (int i = 0; i < testcomplex.length(); i++) {
            char curr = testcomplex.charAt(i);
            assertEquals(curr, testcomply.toChar(i));
            assertEquals(i, testcomply.toInt(curr));
            assertTrue(testcomply.contains(curr));

        }
        assertFalse(testcomply.contains('A'));
    }
}
