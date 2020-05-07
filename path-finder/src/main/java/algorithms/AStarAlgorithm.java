package algorithms;

import java.util.ArrayList;
import java.util.List;

public class AStarAlgorithm {

    private static class Cell {

        private int sourceCost;
        private int targetCost;
        private final int totalCost;

        private final int X;
        private final int Y;

        private Cell previousCell;

        public Cell(int x, int y, int sourceCost, int targetCost) {
            this.sourceCost = sourceCost;
            this.targetCost = targetCost;
            this.totalCost = sourceCost + targetCost;
            this.previousCell = null;
            this.X = x;
            this.Y = y;
        }
    }

    private final int sourceX;
    private final int sourceY;

    private final int targetX;
    private final int targetY;

    private final List<Cell> freeCells, closedCells;

    private final int[][] neighbourCells = new int[][] {
            {0,+1}, {+1,0}, {-1,0}, {0,-1},
            {+1,+1},{-1,-1}, {-1,+1}, {+1,-1}
    };

    public AStarAlgorithm(int[] source, int[] target) {
        this.sourceX = source[0];
        this.sourceY = source[1];
        this.targetX = target[0];
        this.targetY = target[1];
        this.freeCells = new ArrayList<>();
        this.closedCells = new ArrayList<>();
    }

    public List<Cell> findPath(int[][] maze) {

        List<Cell> bestMazePath = new ArrayList<>();
        Cell sourceCell = createCell(this.sourceX,this.sourceY);
        Cell targetCell = createCell(this.targetX, this.targetY);

        freeCells.add(sourceCell);

        while (!freeCells.isEmpty()) {
            Cell currCell = freeCells.get(0);

            for (Cell freeCell: freeCells) {
                if (freeCell.totalCost <= currCell.totalCost) {
                    if (freeCell.targetCost < currCell.targetCost) {
                        currCell = freeCell;
                    }
                }
            }

            freeCells.remove(currCell);
            closedCells.add(currCell);

            if (currCell == targetCell) { break; }

            List<Cell> neighbourCells = getCellNeighbours(currCell);

            for (Cell neighbour: neighbourCells) {
                if (maze[neighbour.X][neighbour.Y] == '0' && !closedCells.contains(neighbour)) {
                    int newNeighbourSourceCost = currCell.sourceCost + heuristicFunction(
                            currCell.X, currCell.Y, neighbour.X, neighbour.Y
                    );

                    if (newNeighbourSourceCost < neighbour.sourceCost || !this.freeCells.contains(neighbour)) {
                        neighbour.sourceCost = newNeighbourSourceCost;
                        neighbour.targetCost = heuristicFunction(neighbour.X,neighbour.Y, targetCell.X, targetCell.Y);
                        neighbour.previousCell = currCell;

                        if (!freeCells.contains(neighbour)) {
                            freeCells.add(neighbour);
                        }
                    }
                }
            }
        }
        return retrievePath(targetCell);
    }

    public Cell createCell(int x, int y) {
        int distSource = heuristicFunction(x, y, this.sourceX, this.sourceY);
        int distTarget = heuristicFunction(x, y, this.targetX, this.targetY);
        return new Cell(x, y, distSource, distTarget);
    }

    public List<Cell> getCellNeighbours(Cell cell) {
        int neighbourX;
        int neighbourY;

        List<Cell> neighbours = new ArrayList<>();

        for (int[] neighbourCoordinates: this.neighbourCells) {
            neighbourX = cell.X + neighbourCoordinates[0];
            neighbourY = cell.Y + neighbourCoordinates[1];
            Cell neighbour = createCell(neighbourX, neighbourY);
            neighbours.add(neighbour);
        }

        return neighbours;
    }

    public List<Cell> retrievePath(Cell currCell) {
        Cell previousCell = currCell;
        List<Cell> bestMazePath = new ArrayList<>();

        while (true) {
            bestMazePath.add(currCell);

            if (previousCell != null) {
                previousCell = currCell.previousCell;
            }
            else {
                break;
            }
        }
        return bestMazePath;
    }

    public int heuristicFunction(int fromX, int fromY, int toX, int toY) {
        int dstX = Math.abs(fromX - toX);
        int dstY = Math.abs(fromY - toY);
        return dstX + dstY;

//        if (dstX > dstY)
//            return 14*dstY + 10* (dstX-dstY);
//        return 14*dstX + 10 * (dstY-dstX);
    }
}
