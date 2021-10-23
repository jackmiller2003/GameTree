package gametree;

import java.util.ArrayList;
import java.util.Random;


/**
 * Class containing algorithms and functions associated with Monte Carlo tree search.
 *
 */
public class MonteCarlo {

    /**
     * Completes a random simulation up to a cap of 500 moves and determines the outcome.
     * @param node
     * @param game
     * @param simCap
     * @return A value of 0 or 1 depending on the outcome (0 for loss, 1 for win and a random
     * sample of these for simulation cap and tie)
     */
    public static int simulate(GameNode node, TreeGame game, int simCap) {
        Object originalPlayer = node.getRootPlayer();
        Object nodeState = node.getNodeState();
        Random rand = new Random();
        int i = 0;
        while ((game.currentPlayerResult(nodeState) == 0) && i < simCap) {
            ArrayList<Object> currentMoves = game.generateLegalMoves(nodeState);
            Object move = currentMoves.get(rand.nextInt(currentMoves.size()));
            nodeState = game.applyMove(nodeState, move);

            i += 1;

        }

        if ((game.currentPlayerResult(nodeState) == 1) && node.getPlayer().equals(originalPlayer)
            || (game.currentPlayerResult(nodeState) == 2) && !node.getPlayer().equals(originalPlayer)) {
            return 1;
        } else if (game.currentPlayerResult(nodeState) == 0 || game.currentPlayerResult(nodeState) == 3) {
            return rand.nextInt() % 2;
        } else {
            return 0;
        }
    }

    public static double UCB(int nodeWins, int nodeSimulations, int parentSimulations, double alpha) {
        return (nodeWins/nodeSimulations) + alpha*(Math.log(parentSimulations) / nodeSimulations);
    }
}
