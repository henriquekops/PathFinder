package algorithms.genetic;

// built-in dependencies
import org.javatuples.Pair;

import java.util.List;
import java.util.ArrayList;

public class Agent {

    /**
     * Coordinate variables
     */
    private int X;
    private int Y;

    /**
     * Genetic cycle variables
     */
    private int identifier;
    private int score;

    /**
     * Agent movements in maze
     */
    private final List<String> moves;

    /**
     *
     * @param x
     * @param y
     * @param identifier
     */
    public Agent(int x, int y, int identifier) {
        this.moves = new ArrayList<>();
        this.identifier = identifier;
        this.score = 0;
        this.X = x;
        this.Y = y;
    }

    // GETTERS

    /**
     *
     * @return
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     *
     * @return
     */
    public List<String> getMoves() {
        return moves;
    }

    /**
     *
     * @return
     */
    public int getX() {
        return X;
    }

    /**
     *
     * @return
     */
    public int getY() {
        return Y;
    }

    /**
     *
     * @return
     */
    public Pair<Integer, Integer> getCoordinates() {
       return Pair.with(this.X, this.Y);
    }

    // SETTERS

    /**
     *
     * @param identifier
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     *
     * @param coordinates
     */
    public void setCoordinate(Pair<Integer, Integer> coordinates) {
        this.X = coordinates.getValue0();
        this.Y = coordinates.getValue1();
    }

    // OTHERS

    /**
     *
     * @param score
     */
    public void updateScore(int score) {
        this.score += score;
    }

}
