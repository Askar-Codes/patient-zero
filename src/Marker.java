import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. Prefix function.
     * result[i] = length of the longest proper prefix of marker[0..i] that is also its suffix.
     */
    public static int[] prefixFunction(String marker) {
        int markerLength = marker.length();
        int[] prefixValues = new int[markerLength];

        int currentBorderLength = 0;  // length of the border of the part we have already looked at

        for (int i = 1; i < markerLength; i++) {
            // While the next letter does not continue the border, fall back to a shorter border
            while (currentBorderLength > 0 && marker.charAt(i) != marker.charAt(currentBorderLength)) {
                currentBorderLength = prefixValues[currentBorderLength - 1];
            }
            // If the letter continues the border, the border becomes one letter longer
            if (marker.charAt(i) == marker.charAt(currentBorderLength)) {
                currentBorderLength++;
            }
            prefixValues[i] = currentBorderLength;
        }
        return prefixValues;
    }

    /**
     * Mission 3b. KMP search. Returns all start positions of marker in genome (overlaps count).
     */
    public static int[] findAll(String genome, String marker) {
        int markerLength = marker.length();
        int[] prefixValues = prefixFunction(marker);
        List<Integer> foundPositions = new ArrayList<>();

        int matchedLetters = 0;  // how many letters of the marker match right now

        // The position in the genome only moves forward, it never goes back
        for (int positionInGenome = 0; positionInGenome < genome.length(); positionInGenome++) {
            // On a mismatch, fall back using the prefix function (do not restart from 0)
            while (matchedLetters > 0 && genome.charAt(positionInGenome) != marker.charAt(matchedLetters)) {
                matchedLetters = prefixValues[matchedLetters - 1];
            }
            if (genome.charAt(positionInGenome) == marker.charAt(matchedLetters)) {
                matchedLetters++;
            }
            // Whole marker matched: remember where it started
            if (matchedLetters == markerLength) {
                foundPositions.add(positionInGenome - markerLength + 1);
                // The next match may overlap this one, so fall back to the longest border
                matchedLetters = prefixValues[matchedLetters - 1];
            }
        }

        int[] result = new int[foundPositions.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = foundPositions.get(i);
        }
        return result;
    }

    // Numbers for the rolling hash
    private static final long HASH_BASE = 131;
    private static final long HASH_MODULO = 1_000_000_007L;

    /**
     * Mission 4. Rabin-Karp with a rolling hash.
     * Counts positions i in strainA such that the word strainA[i..i+wordLength-1] occurs in strainB.
     */
    public static int sharedWords(String strainA, String strainB, int wordLength) {
        if (strainA.length() < wordLength || strainB.length() < wordLength) {
            return 0;
        }

        // HASH_BASE^(wordLength-1): needed to remove the first letter of the window
        long weightOfFirstLetter = 1;
        for (int i = 0; i < wordLength - 1; i++) {
            weightOfFirstLetter = (weightOfFirstLetter * HASH_BASE) % HASH_MODULO;
        }

        // Step 1: hash of every window of strainB -> list of start positions with this hash
        Map<Long, List<Integer>> startsOfWindowsInB = new HashMap<>();
        long hashOfWindowInB = hashOfFirstWindow(strainB, wordLength);
        for (int start = 0; start + wordLength <= strainB.length(); start++) {
            startsOfWindowsInB.computeIfAbsent(hashOfWindowInB, key -> new ArrayList<>()).add(start);
            if (start + wordLength < strainB.length()) {
                hashOfWindowInB = moveWindowOneStep(hashOfWindowInB, strainB.charAt(start),
                        strainB.charAt(start + wordLength), weightOfFirstLetter);
            }
        }

        // Step 2: slide over strainA, look up the hash, and COMPARE THE LETTERS when the hash is found
        int positionsWithSharedWord = 0;
        long hashOfWindowInA = hashOfFirstWindow(strainA, wordLength);
        for (int start = 0; start + wordLength <= strainA.length(); start++) {
            List<Integer> candidates = startsOfWindowsInB.get(hashOfWindowInA);
            if (candidates != null) {
                for (int startInB : candidates) {
                    if (windowsHaveSameLetters(strainA, start, strainB, startInB, wordLength)) {
                        positionsWithSharedWord++;
                        break;  // one match is enough, count this position once
                    }
                }
            }
            if (start + wordLength < strainA.length()) {
                hashOfWindowInA = moveWindowOneStep(hashOfWindowInA, strainA.charAt(start),
                        strainA.charAt(start + wordLength), weightOfFirstLetter);
            }
        }
        return positionsWithSharedWord;
    }

    /** Number for a letter: A=1, C=2, G=3, T=4. */
    private static long numberOfLetter(char letter) {
        if (letter == 'A') return 1;
        if (letter == 'C') return 2;
        if (letter == 'G') return 3;
        return 4;
    }

    /** Hash of the first wordLength letters of the text. */
    private static long hashOfFirstWindow(String text, int wordLength) {
        long hash = 0;
        for (int i = 0; i < wordLength; i++) {
            hash = (hash * HASH_BASE + numberOfLetter(text.charAt(i))) % HASH_MODULO;
        }
        return hash;
    }

    /** Rolling step in O(1): remove the old first letter, shift, add the new last letter. */
    private static long moveWindowOneStep(long oldHash, char letterLeaving, char letterEntering, long weightOfFirstLetter) {
        long hashWithoutLeavingLetter = oldHash - numberOfLetter(letterLeaving) * weightOfFirstLetter % HASH_MODULO;
        hashWithoutLeavingLetter = (hashWithoutLeavingLetter + HASH_MODULO) % HASH_MODULO;  // avoid a negative number
        return (hashWithoutLeavingLetter * HASH_BASE + numberOfLetter(letterEntering)) % HASH_MODULO;
    }

    /** Compares two windows letter by letter. */
    private static boolean windowsHaveSameLetters(String firstText, int firstStart, String secondText, int secondStart, int wordLength) {
        for (int i = 0; i < wordLength; i++) {
            if (firstText.charAt(firstStart + i) != secondText.charAt(secondStart + i)) {
                return false;
            }
        }
        return true;
    }
}