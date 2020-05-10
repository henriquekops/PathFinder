package algorithms.genetic;

// built-in dependencies
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Agent {

    private int X;
    private int Y;

    private int identifier;
    private int score;

    private List<String> moves;

    public Agent(int x, int y, int identifier) {
        this.moves = new ArrayList<>();
        this.identifier = identifier;
        this.score = 0;
        this.X = x;
        this.Y = y;
    }

    public int getIdentifier() {
        return identifier;
    }

    public int getScore() {
        return score;
    }

    public List<String> getMoves() {
        return moves;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    public void setX(int x, int mazeSize) {
        if (x > 0 && x < mazeSize) {
            X = x;
        }
    }

    public void setY(int y, int mazeSize) {
        if (y > 0 && y < mazeSize ) {
            Y = y;
        }
    }
}
