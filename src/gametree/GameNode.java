package gametree;

import java.util.ArrayList;
import java.util.Collections;


/**
 * This class encodes a node in a game tree with functions
 * to simulate into the future, store current heuristic value
 * etc.
 *
 */
public class GameNode {

    private ArrayList<GameNode> children = new ArrayList<>();
    private GameNode parent = null;
    private double heuristicEvaluation = 0;
    private double value = 0;
    private int monteWins = 0;
    private int monteSimulations = 0;
    private int monteValue = 0;
    private Object nodeState;
    private Object nodeMove;
    public TreeGame game;

    public Object getRootPlayer() {
        return rootPlayer;
    }

    public Object rootPlayer;

    public ArrayList<GameNode> getChildren() {
        return children;
    }

    public void addChildren(ArrayList<GameNode> children) {
        this.children.addAll(children);
    }

    public void addChild(GameNode child) {
        this.children.add(child);
    }

    public GameNode getParent() {
        return parent;
    }

    public void setParent(GameNode parent) {
        this.parent = parent;
    }

    public Object getNodeState() {
        return this.nodeState;
    }

    public Object getPlayer() {
        return game.getPlayer(getNodeState());
    }

    public Object getNodeMove() {
        return nodeMove;
    }

    public void setNodeState(Object state) {
        this.nodeState = state;
    }

    public TreeGame getGame() {
        return this.game;
    }

    public int getMonteWins() {
        return monteWins;
    }

    public int getMonteSimulations() {
        return monteSimulations;
    }

    public double getMonteValue() {
        return monteValue;
    }

    public GameNode(Object state, TreeGame game, Object rootPlayer) {
        this.nodeState = state;
        this.game = game;
        this.rootPlayer = rootPlayer;
    }

    public GameNode(Object state, Object move, GameNode parent, TreeGame game, Object rootPlayer) {
        this.nodeState = state;
        this.nodeMove = move;
        this.parent = parent;
        this.game = game;
        this.rootPlayer = rootPlayer;
    }

    /**
     * Will generate children to a certain depth by using legal moves, applying that move and then
     * recursively calling itself again with depth-1
     * @param depth
     */
    public void generateChildren(int depth) {
        if (depth == 0 || game.currentPlayerResult(nodeState) != 0) {
            return;
        } else {
            ArrayList<String> legalMoves = getGame().generateLegalMoves(this.getNodeState());
            for (Object move : legalMoves) {
                GameNode child = new GameNode(this.game.applyMove(this.getNodeState(), move), move,this, this.game, this.rootPlayer);
                this.addChild(child);
                child.generateChildren(depth - 1);
            }
        }
    }

    /**
     * Way of printing node state, number of children and depth from the bottom.
     * @param depth
     */
    public void printChildren(int depth) {
        System.out.println("---------- Node ----------");
        System.out.println("Depth: " + depth);
        System.out.println("No. children: " + getChildren().size());
        System.out.println("State: " + getNodeState());
        System.out.println("Monte simulations: " + getMonteSimulations());
        System.out.println("Monte wins: " + getMonteWins());
        System.out.println("Minimax value: " + getValue());
        if (depth == 0) {
            return;
        } else {
            for (GameNode child: getChildren()) {
                child.printChildren(depth - 1);
            }
        }
    }

    //  Minimax heuristic evaluations will be run through the variable heuristicEvaluation

    public double getHeuristicEvaluation() {
        return this.heuristicEvaluation;
    }

    public void generateHeuristicEvaluation() {
        this.heuristicEvaluation = game.combinedHeuristic(this.nodeState);
    }


    /**
     * Calls random simulation of current node
     * @return simulation result
     */
    public int monteSimulate() {
        return MonteCarlo.simulate(this, this.game, 200);
    }

    /**
     * Update phase of monte tree search algorithm. Will go through and update values for all parents
     * @param outcome
     */
    public void monteUpdate(int outcome) {
        if (getParent() == null) {
            this.monteSimulations += 1;
            this.monteWins += outcome;
            return;
        } else {
            this.monteSimulations += 1;
            this.monteWins += outcome;
            this.parent.monteUpdate(outcome);
        }
    }

    /**
     * Uses UCB algorithm with an alpha par. of 2 (balance between exploitation and expansion)
     * @return calculated UCB value
     */
    public double calculateMonteUCB() {
        return MonteCarlo.UCB(getMonteWins(), getMonteSimulations(), getParent().getMonteSimulations(), 1);
    }

    /**
     * Finds leaf node of game tree using UCB algorithm.
     * @return  leaf node of game tree
     */
    public GameNode findMonteLeaf() {
        if (getChildren().size() == 0) {
            return this;
        } else {
            ArrayList<Double> childrenUCB = new ArrayList<Double>();
            for (GameNode child : getChildren()) {
                if (child.getMonteSimulations() == 0) {
                    return child;
                } else {
                    childrenUCB.add(child.calculateMonteUCB());
                }
            }
            GameNode chosenChild = getChildren().get(childrenUCB.indexOf(Collections.max(childrenUCB)));
            return chosenChild.findMonteLeaf();
        }
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GameNode{" +
                "children=" + children +
                ", nodeState=" + nodeState +
                ", nodeMove=" + nodeMove +
                ", value=" + value +
                '}';
    }
}
