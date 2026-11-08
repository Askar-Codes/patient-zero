import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. The tree is rooted in person 0.
 */
public class Outbreak {

    // Filled by the DFS, one value per person
    private static int[] cheapestCostIfPersonIsTested;
    private static int[] cheapestCostIfPersonIsNotTested;
    private static int[] parentOfPerson;

    /**
     * Chooses people to test so that every link has at least one tested person, with the smallest total cost.
     * Returns isTested[v] = true if person v is tested.
     */
    public static boolean[] cheapestTests(int numberOfPeople, int[][] neighbours, int[] testingCost) {
        cheapestCostIfPersonIsTested = new int[numberOfPeople];
        cheapestCostIfPersonIsNotTested = new int[numberOfPeople];
        parentOfPerson = new int[numberOfPeople];
        parentOfPerson[0] = -1;

        // Step 1: fill both numbers for everybody, from the leaves up to the root
        fillCostsForSubtree(0, -1, neighbours, testingCost);

        // Step 2: walk down from the root and decide who is tested
        boolean[] isTested = new boolean[numberOfPeople];
        boolean rootIsTested = cheapestCostIfPersonIsTested[0] <= cheapestCostIfPersonIsNotTested[0];
        decideWhoIsTested(0, rootIsTested, neighbours, isTested);
        return isTested;
    }

    /** DFS: computes both costs for person and everybody below it. */
    private static void fillCostsForSubtree(int person, int parent, int[][] neighbours, int[] testingCost) {
        parentOfPerson[person] = parent;

        int costIfTested = testingCost[person];  // we pay for this person, children are free to choose
        int costIfNotTested = 0;                 // we do not pay, but every child MUST be tested

        for (int child : neighbours[person]) {
            if (child == parent) {
                continue;  // do not go back to the person who infected this one
            }
            fillCostsForSubtree(child, person, neighbours, testingCost);

            // If person is tested, each child takes the cheaper of its two options
            costIfTested += Math.min(cheapestCostIfPersonIsTested[child], cheapestCostIfPersonIsNotTested[child]);
            // If person is not tested, each child has to be tested
            costIfNotTested += cheapestCostIfPersonIsTested[child];
        }
        cheapestCostIfPersonIsTested[person] = costIfTested;
        cheapestCostIfPersonIsNotTested[person] = costIfNotTested;
    }

    /** Walks down the tree and writes the choice for every person. */
    private static void decideWhoIsTested(int person, boolean thisPersonIsTested, int[][] neighbours, boolean[] isTested) {
        isTested[person] = thisPersonIsTested;
        for (int child : neighbours[person]) {
            if (child == parentOfPerson[person]) {
                continue;
            }
            boolean childIsTested;
            if (!thisPersonIsTested) {
                childIsTested = true;  // parent is not tested, so the child must be tested
            } else {
                // parent is tested, so the child takes the cheaper option
                childIsTested = cheapestCostIfPersonIsTested[child] <= cheapestCostIfPersonIsNotTested[child];
            }
            decideWhoIsTested(child, childIsTested, neighbours, isTested);
        }
    }

    // The best chain found so far while the DFS runs
    private static int longestChainFoundSoFar;

    /**
     * Number of links on the longest path in the tree.
     * For every person: take the two longest ways down; the best chain through the person is their sum.
     */
    public static int longestChain(int numberOfPeople, int[][] neighbours) {
        longestChainFoundSoFar = 0;
        findLongestWayDown(0, -1, neighbours);
        return longestChainFoundSoFar;
    }

    /** Returns the number of links on the longest way down from person. */
    private static int findLongestWayDown(int person, int parent, int[][] neighbours) {
        int longestWayDown = 0;
        int secondLongestWayDown = 0;

        for (int child : neighbours[person]) {
            if (child == parent) {
                continue;
            }
            int wayDownThroughThisChild = findLongestWayDown(child, person, neighbours) + 1;  // +1 for the link to the child

            if (wayDownThroughThisChild > longestWayDown) {
                secondLongestWayDown = longestWayDown;
                longestWayDown = wayDownThroughThisChild;
            } else if (wayDownThroughThisChild > secondLongestWayDown) {
                secondLongestWayDown = wayDownThroughThisChild;
            }
        }

        // Best chain that goes through this person: up one branch and down another
        longestChainFoundSoFar = Math.max(longestChainFoundSoFar, longestWayDown + secondLongestWayDown);
        return longestWayDown;
    }
}