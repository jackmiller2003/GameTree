package gametree;

import java.util.ArrayList;

/**
 * Interface for abstracting away from a particular game as the AI system will work on
 * any game provided so long as there are the set of functions specified.
 * @param <State>
 * @param <Move>
 */
public interface TreeGame<State, Move> {

    /**
     * Generates a list of all possible legal moves without repetition
     * @param state
     * @return that above list
     */
    abstract public ArrayList generateLegalMoves(State state);

    /**
     * Function for applying a move to a state
     * @param state an object which represents the current state of the game
     * @param move an object which represents a move on the state
     */
    abstract public Object applyMove(State state, Move move);

    /**
     * Function for finding a winning side
     * @param state
     * @return a:
     *  - 1 if the side is the player whose turn it is
     *  - 2 if it is the other player
     *  - 3 if it is a draw
     *  - 0 otherwise
     */
    abstract public int currentPlayerResult(State state);

    /**
     * This function should return whose turn it is
     * @param state
     * @return the object which describes the player
     */
    abstract public Object getPlayer(State state);

    /**
     * This combined heuristic function produces a float value from -1 to 1 defining how good
     * the state is based on the first player's perspective.
     * @param state
     * @return
     */
    public double combinedHeuristic(State state);
}