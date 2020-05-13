package algorithms.genetic;

// external dependencies
import org.javatuples.Pair;

// built-in dependencies
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Agent implements Serializable {

    private int X;
    private int Y;

    private int identifier;
    private int score;

    private boolean foundWayOut;

    private final List<String> moves;

    public Agent(int x, int y, int identifier) {
        this.moves = new ArrayList<>();
        this.identifier = identifier;
        this.score = 0;
        this.X = x;
        this.Y = y;
        this.foundWayOut = false;
    }

    // GETTERS

    public int getIdentifier() {
        return identifier;
    }

    public int getScore() {
        return score;
    }

    public List<String> getMoves() {
        return moves;
    }

    public Pair<Integer, Integer> getCoordinates() {
       return Pair.with(X, Y);
    }

    public boolean foundWayOut() {
        return foundWayOut;
    }

    // SETTERS

    public void setFoundWayOut(boolean foundWayOut) {
        this.foundWayOut = foundWayOut;
    }

    public void updateScore(int score) {
        this.score += score;
    }

    public void setCoordinate(Pair<Integer, Integer> coordinates) {
        this.X = coordinates.getValue0();
        this.Y = coordinates.getValue1();
    }

    public void reset() {
        this.identifier = 0;
        this.score = 0;
        this.foundWayOut = false;
    }

    // OTHERS

    public Pair<Integer, Integer> mapNextPosition(String move) {

        int nextXCoordinate = 0;
        int nextYCoordinate = 0;

        switch (move) {
            case "N":
                nextXCoordinate = X - 1;
                break;
            case "S":
                nextXCoordinate = X + 1;
                break;
            case "E":
                nextYCoordinate = Y + 1;
                break;
            case "W":
                nextYCoordinate = Y - 1;
                break;
            case "NE":
                nextXCoordinate = X - 1;
                nextYCoordinate = Y + 1;
                break;
            case "NW":
                nextXCoordinate = X - 1;
                nextYCoordinate = Y - 1;
                break;
            case "SE":
                nextXCoordinate = X + 1;
                nextYCoordinate = Y + 1;
                break;
            case "SW":
                nextXCoordinate = X + 1;
                nextYCoordinate = Y - 1;
                break;
        }

        return Pair.with(nextXCoordinate, nextYCoordinate);
    }

    @Override
    public String toString() {
        return "Agent{\n" +
                "\tidentifier=" + identifier + ",\n" +
                "\tscore=" + score + ",\n" +
                "\tmoves=" + moves + ",\n" +
                '}';
    }
}
