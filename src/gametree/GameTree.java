package gametree;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Game tree class which stores Game Nodes in a large tree. This class will also
 * include the functions which work on the entire tree such as the minimax or
 * Monte Carlo algorithms
 */
public class GameTree<State, Move> {
    final private GameNode rootNode;

    public TreeGame<State, Move> game;

    public TreeGame<State, Move> getGame() {
        return this.game;
    }

    public GameTree(State originalState, TreeGame<State, Move> game) {
        this.rootNode = new GameNode(originalState, game, game.getPlayer(originalState));
        this.game = game;
    }

    /**
     * Game tree generation with a given depth, root state and depth.
     * @param originalState the original state of the game
     * @param depth the depth that the tree is to be generated to
     * @param game the type of game the tree is modeling
     */
    public GameTree(State originalState, int depth, TreeGame<State, Move> game) {
        this.rootNode = new GameNode(originalState, game, game.getPlayer(originalState));
        this.rootNode.generateChildren(depth);
    }

    public void printTree(int depth) {
        this.rootNode.printChildren(depth);
    }

    /**
     * This iterates through the monte carlo algorithm and produces values for children of the root node
     * @param iterations number of iterations that the algorithm is meant to iterate
     */
    public void monteIterate(int iterations) {

//      Prints out the number of iterations
        if (iterations % 10 == 0) {
            System.out.println("No. of iterations " + iterations);
        }

//      If there are no more iterations to complete, the function returns.
        if (iterations == 0) {
            return;
        }

//      If the root node is empty it will then to the first step of expansion manually
        if (rootNode.getChildren().size() == 0) {
            rootNode.generateChildren(1);
            GameNode current = rootNode.getChildren().get(0);
            current.monteUpdate(current.monteSimulate());
            monteIterate(iterations - 1);
            return;
        }

//      Finds leaf according to monte choice algorithm
        GameNode current = rootNode.findMonteLeaf();

//      If the leaf has been simulated it will generate children and choose the first of these
        if (current.getMonteSimulations() != 0) {
            current.generateChildren(1);
            if (current.getChildren().size() == 0) {
                current.monteSimulate();
            }
        }

//      If there hasn't been a simulation, it will simulate
        current.monteUpdate(current.monteSimulate());

//      Iterate again
        monteIterate(iterations - 1);

    }

    /**
     * This is called after the monte carlo simulation has been run for a certain number of iterations
     * @return the best move according to monte carlo
     */
    public Object monteMove() {
        ArrayList<Double> childValues = new ArrayList<>();

//      Runs through list of children and gets their value
        for (GameNode child : rootNode.getChildren()) {
            if (child.getMonteSimulations() != 0) {
                childValues.add((double) child.getMonteWins() / child.getMonteSimulations());
            } else {
                childValues.add((double) 0);
            }
        }

        GameNode highestValueChild = rootNode.getChildren().get(childValues.indexOf(Collections.max(childValues)));
        return highestValueChild.getNodeMove();
    }

    /**
     * This is a typical implementation of minimax with alpha beta pruning written in the language of
     * the GameTree package/library we developed for Cublino.
     * @param position the node from which the minimax value is to be calculated
     * @param depth the depth the minimax algorithm should go to
     * @param alpha starting alpha pruning value for recursion
     * @param beta starting beta pruning value for recursion
     * @return the minimax value for a particle position node
     */
    public double minimaxValue (GameNode position, int depth, double alpha, double beta, Player originalPlayer) {
        Object maximizingPlayer = position.getPlayer();
        if (depth == 0) {
            position.generateHeuristicEvaluation();

            if (originalPlayer.equals(Player.PLAYER1)) {
                return position.getHeuristicEvaluation();
            } else {
                return (-1) * position.getHeuristicEvaluation();
            }
        }

        position.generateChildren(1);

        if (maximizingPlayer == originalPlayer) {
            double maxEval = -1000000000;

            for (GameNode child : position.getChildren()) {
                double eval = minimaxValue(child, depth - 1,alpha,beta, originalPlayer);
                maxEval = Math.max(maxEval,eval);
                alpha = Math.max(alpha,eval);
                if (beta <= alpha) break;
            }
            position.setValue(maxEval);
            return maxEval;
        } else {
            double minEval = 1000000000;
            for (GameNode child : position.getChildren()) {
                double eval = minimaxValue(child, depth - 1, alpha,beta, originalPlayer);
                minEval = Math.min(minEval,eval);
                beta = Math.min(beta,eval);
                if (beta <= alpha) break;
            }
            position.setValue(minEval);
            return minEval;
        }
    }

    /**
     * @return best move according to minimax for the current root node.
     */
    public Object minimaxMove() {
        double minimaxResult = minimaxValue(this.rootNode,3,-1000000000,
                1000000000, (Player) this.rootNode.getPlayer());

        if (rootNode.getChildren().size() == 0) {
        } else {
            for (GameNode candidateMove : rootNode.getChildren()) {
                if (candidateMove.getValue() == minimaxResult) {
                    return candidateMove.getNodeMove();
                }
            }
        }
        return null;
    }

}
