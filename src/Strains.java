/**
 * Missions 1 and 2. Compare two strains with dynamic programming.
 */
public class Strains {

    /**
     * Mission 1. Edit distance.
     * table[i][j] = the smallest number of changes that turn the FIRST i letters of strainA
     * into the FIRST j letters of strainB.
     */
    public static int editDistance(String strainA, String strainB) {
        int lengthOfA = strainA.length();
        int lengthOfB = strainB.length();

        int[][] table = new int[lengthOfA + 1][lengthOfB + 1];

        // First column: turn i letters of A into an empty word -> delete all i letters
        for (int i = 0; i <= lengthOfA; i++) {
            table[i][0] = i;
        }
        // First row: turn an empty word into j letters of B -> insert all j letters
        for (int j = 0; j <= lengthOfB; j++) {
            table[0][j] = j;
        }

        for (int i = 1; i <= lengthOfA; i++) {
            for (int j = 1; j <= lengthOfB; j++) {
                char lastLetterOfA = strainA.charAt(i - 1);
                char lastLetterOfB = strainB.charAt(j - 1);

                int costOfReplace;
                if (lastLetterOfA == lastLetterOfB) {
                    costOfReplace = table[i - 1][j - 1];      // same letter: no change needed
                } else {
                    costOfReplace = table[i - 1][j - 1] + 1;  // different letter: replace it
                }
                int costOfDelete = table[i - 1][j] + 1;       // delete last letter of A
                int costOfInsert = table[i][j - 1] + 1;       // insert last letter of B

                table[i][j] = Math.min(costOfReplace, Math.min(costOfDelete, costOfInsert));
            }
        }
        return table[lengthOfA][lengthOfB];
    }

    /**
     * Mission 2. Longest common subsequence.
     * table[i][j] = length of the longest common subsequence of the FIRST i letters of strainA
     * and the FIRST j letters of strainB.
     */
    public static int lcsLength(String strainA, String strainB) {
        int lengthOfA = strainA.length();
        int lengthOfB = strainB.length();

        // First row and first column stay 0: an empty word has nothing in common with anything
        int[][] table = new int[lengthOfA + 1][lengthOfB + 1];

        for (int i = 1; i <= lengthOfA; i++) {
            for (int j = 1; j <= lengthOfB; j++) {
                char lastLetterOfA = strainA.charAt(i - 1);
                char lastLetterOfB = strainB.charAt(j - 1);

                if (lastLetterOfA == lastLetterOfB) {
                    table[i][j] = table[i - 1][j - 1] + 1;    // letters match: extend the diagonal
                } else {
                    table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]); // take the better neighbour
                }
            }
        }
        return table[lengthOfA][lengthOfB];
    }
}