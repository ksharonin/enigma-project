package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Katrina Sharonin
 */


class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */

    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        _stringcycles = new String(cycles);
        _stringcycles =
                _stringcycles.replaceAll("\\s", "");
        _stringcycles =
                _stringcycles.replaceAll("\\(", "");
        _stringcycles =
                _stringcycles.replaceAll("\\)", "");

        char[] checky = _stringcycles.toCharArray();
        Map<Character, Integer> map = new HashMap<>();
        for (char c : checky) {
            if (map.containsKey(c)) {
                int counter = map.get(c);
                map.put(c, ++counter);
            } else {
                map.put(c, 1);
            }
        }
        for (char c : map.keySet()) {
            if (map.get(c) > 1) {
                throw new EnigmaException("Duplicate chars");
            }
        }
        _cycles = new ArrayList<ArrayList<Character>>();
        cycles = cycles.replaceAll("\\s", "");

        if (cycles.startsWith("(")) {
            if (!cycles.endsWith(")")) {
                throw new EnigmaException("Missing )");
            }

            int tracker = 0;
            for (int i = 0; i < cycles.length(); i++) {
                if (cycles.charAt(i) == '(') {
                    if (cycles.charAt(i + 1) == ')') {
                        throw new EnigmaException("Invalid perm");
                    }

                    ArrayList anotherCycle = new ArrayList<>();
                    _cycles.add(anotherCycle);

                    i += 1;
                    while (cycles.charAt(i) != ')') {
                        _cycles.get(tracker).add(cycles.charAt(i));
                        i += 1;
                    }
                    tracker += 1;

                }

            }

        }

    }

    /** Add the cycle c0->c1->...->cm->c0
     * to the permutation, where CYCLE is
     *  c0c1...cm. */

    private void addCycle(String cycle) {
        cycle = cycle.replaceAll("\\s", "");
        cycle = cycle.replaceAll("\\(", "");
        cycle = cycle.replaceAll("\\)", "");
        ArrayList anotherCycle = new ArrayList<>();
        _cycles.add(anotherCycle);

        int indexofTwo = _cycles.size();
        for (int i = 0; i < cycle.length(); i++) {
            _cycles.get(indexofTwo).add(cycle.charAt(i));
        }


    }

    ArrayList<ArrayList<Character>> arrcycles() {
        return _cycles;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {

        p = wrap(p);
        if (p >= _alphabet.size()) {
            throw new EnigmaException("out of bounds +");

        }

        char applyTo = _alphabet.toChar(p);


        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).size(); j++) {
                if (_cycles.get(i).get(j).equals(applyTo)) {

                    if (j + 1 >= _cycles.get(i).size()) {
                        applyTo = _cycles.get(i).get(0);
                    } else {
                        applyTo = _cycles.get(i).get(j + 1);
                    }

                    int permutedIndex = _alphabet.toInt(applyTo);
                    return permutedIndex;

                }

            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {

        c = wrap(c);

        if (c >= _alphabet.size()) {
            throw new EnigmaException("out of bounds");

        }

        char applyToy = _alphabet.toChar(c);


        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).size(); j++) {
                if (_cycles.get(i).get(j).equals(applyToy)) {
                    if (j - 1 < 0) {
                        applyToy = _cycles.get(i).get
                                (_cycles.get(i).size() - 1);
                    } else {
                        applyToy = _cycles.get(i).get(j - 1);
                    }

                    int permutedIndex = _alphabet.toInt(applyToy);
                    return permutedIndex;

                }

            }
        }
        return c;

    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {

        if (!_alphabet.contains(p)) {
            throw new EnigmaException("character dne in the given alphabet");
        }

        int transformed = _alphabet.toInt(p);
        int result = permute(transformed);
        char changedResult = _alphabet.toChar(result);
        return changedResult;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("char DNE");
        }

        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).size(); j++) {

                if (_cycles.get(i).get(j).equals(c)) {
                    char getcharwantedc;

                    if (j - 1 < 0) {
                        getcharwantedc =
                                _cycles.get(i).get(_cycles.get(i).size() - 1);
                    } else {
                        getcharwantedc =
                                _cycles.get(i).get(j - 1);
                    }

                    return getcharwantedc;

                }
            }
        }

        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */

    boolean derangement() {
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Storage of cycles for permutation. */
    private ArrayList<ArrayList<Character>> _cycles;

    /** String form of cycles. */
    private String _stringcycles;
}
