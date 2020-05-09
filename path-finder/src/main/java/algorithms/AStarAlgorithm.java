package algorithms;

// built-in dependencies
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

// external dependencies
import org.javatuples.Pair;

/**
 * A star algorithm class
 */
public class AStarAlgorithm {

    /**
     * Cell class for maze cell representation
     */
    private static class Cell {

        /**
         * Heuristic (distance) costs
         */
        private int sourceCost;
        private final int targetCost;
        private final int totalCost;

        /**
         * Coordinates
         */
        private final int X;
        private final int Y;

        /**
         * Previous cell for path linking
         */
        private Cell previousCell;

        /**
         * Cell class constructor
         * @param x X coordinate (line)
         * @param y Y coordinate (column)
         * @param sourceCost Heuristic cost to source (distance)
         * @param targetCost Heuristic cost to target (distance)
         */
        public Cell(int x, int y, int sourceCost, int targetCost) {
            this.sourceCost = sourceCost;
            this.targetCost = targetCost;
            this.totalCost = sourceCost + targetCost;
            this.previousCell = null;
            this.X = x;
            this.Y = y;
        }

        /**
         * Get cell coordinates
         * @return Coordinate pair (x, y)
         */
        public Pair<Integer, Integer> getCoordinates() {
            return Pair.with(this.X, this.Y);
        }

    }

    /**
     * Source coordinates
     */
    private final int sourceX;
    private final int sourceY;

    /**
     * Target coordinates
     */
    private final int targetX;
    private final int targetY;

    /**
     * Cell controllers
     */
    private final List<Cell> walkableCells, chosenCells;
    private final List<Cell> clearCellCache = Collections.emptyList();

    /**
     * Neighbour cells coordinates for neighbour mapping
     */
    private final int[][] neighbourCellsCoordinates = new int[][] {
            {0,+1}, {+1,0}, {-1,0}, {0,-1},
            {+1,+1},{-1,-1}, {-1,+1}, {+1,-1}
    };

    /**
     * A star algorithm constructor
     * @param source Source coordinates
     * @param target Target coordinates
     */
    public AStarAlgorithm(int[] source, int[] target) {
        this.sourceX = source[0];
        this.sourceY = source[1];
        this.targetX = target[0];
        this.targetY = target[1];
        this.walkableCells = new ArrayList<>();
        this.chosenCells = new ArrayList<>();
    }

    /**
     * Applies the a star algorithm for maze's best path finding
     * @param maze Maze that contains a path to be found
     * @param mazeSize Maze's size
     * @return List of pairs of coordinates
     */
    public void findPath(char[][] maze, int mazeSize) {

        // create source and target cells
        Cell sourceCell = createCell(this.sourceX,this.sourceY);
        Cell targetCell = createCell(this.targetX, this.targetY);
        Cell currCell = null;

        // source is a free cell
        this.walkableCells.add(sourceCell);

        // while there are free cells to walk through
        while (!walkableCells.isEmpty()) {
//            showCells("Free", this.walkableCells); // TODO: remove
//            showCells("Closed", this.chosenCells); // TODO: remove

            // get best walkable cell using first as pivot
            currCell = getBestFreeCell(this.walkableCells.get(0));

            if (currCell.getCoordinates().equals(targetCell.getCoordinates())) {
                break;
            }

//            System.out.println("Current = " + currCell.getCoordinates() + "\n"); // TODO: remove

            // choose the best cell to be part of the path
            walkableCells.remove(currCell);
            chosenCells.add(currCell);

            // get current cell's neighbours
            List<Cell> neighbourCells = getCellNeighbours(currCell, mazeSize);

//            showCells("Neighbours", neighbourCells); // TODO: remove

            // for every neighbour
            for (Cell neighbour: neighbourCells) {

                // if it wasn't already chosen
                if (!this.chosenCells.contains(neighbour)) {
                    char mazeObjectAtNeighbour = maze[neighbour.X][neighbour.Y];

                    // if its walkable
                    if ( mazeObjectAtNeighbour == '0' || mazeObjectAtNeighbour == 'S') {
                        int newNeighbourSourceCost = currCell.sourceCost + heuristicFunction(currCell.X, currCell.Y, neighbour.X, neighbour.Y);

                        // if path to current neighbour is shorter or neighbour is not considered walkable yet
                        if (newNeighbourSourceCost < neighbour.sourceCost || !this.walkableCells.contains(neighbour)) {

                            // consider current neighbour in solution
                            neighbour.sourceCost = newNeighbourSourceCost;
                            neighbour.previousCell = currCell;

                            // if not considered as walkable, do it
                            if (!isAlreadyWalkable(neighbour)) {
//                                System.out.println("Adding neighbour " + neighbour.getCoordinates() + " (Parent: "+ neighbour.previousCell.getCoordinates() + ")");  // TODO: remove
                                this.walkableCells.add(neighbour);
                            }
                        }
                    }
                }
            }
            neighbourCells.removeAll(clearCellCache);
        }
        // return path
        retrievePath(currCell);
    }

    /**
     * Get the less distant cell from both source and target
     * @param pivotCell Pivot cell to consider in next cell selection
     * @return Next cell to be evaluated
     */
    public Cell getBestFreeCell(Cell pivotCell) {
        // for each free cell
        for (Cell currCell: this.walkableCells) {

            // if current walkable cell is less distant then pivot
            if (currCell.totalCost <= pivotCell.totalCost) {

                // and its closer to the target
                if (currCell.targetCost < pivotCell.targetCost) {

                    // set it as the pivot
                    pivotCell = currCell;
                }
            }
        }
        return pivotCell;
    }

    public void showCells(String prefix, List<Cell> cells) {
        System.out.print(prefix + " cells: ");
        for (Cell cell: cells) {
            System.out.print(cell.getCoordinates() + " ");
        }
        System.out.println("\n");
    }

    public boolean isAlreadyWalkable(Cell unknownCell) {
        for (Cell cell : this.walkableCells) {
            if (cell.getCoordinates().equals(unknownCell.getCoordinates())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a new cell using heuristic function
     * @param x New cell's X coordinate (line)
     * @param y New cell's Y coordinate (column)
     * @return New configured cell object
     */
    public Cell createCell(int x, int y) {
        // calculate heuristics
        int distSource = heuristicFunction(x, y, this.sourceX, this.sourceY);
        int distTarget = heuristicFunction(x, y, this.targetX, this.targetY);

        // return cell object
        return new Cell(x, y, distSource, distTarget);
    }

    /**
     * Get cell's neighbours in all directions
     * @param cell Cell object to retrieve neighbours from
     * @param mazeSize Maze's size
     * @return List of valid (in-bounds) neighbours
     */
    public List<Cell> getCellNeighbours(Cell cell, int mazeSize) {
        int neighbourX;
        int neighbourY;

        List<Cell> neighbours = new ArrayList<>();

        // for each cell around the current
        for (int[] neighbourCoordinates: this.neighbourCellsCoordinates) {
            neighbourX = cell.X + neighbourCoordinates[0];
            neighbourY = cell.Y + neighbourCoordinates[1];

            // check neighbour boundaries and create neighbour using heuristic function
            if ((neighbourX >= 0 && neighbourX < mazeSize) && (neighbourY >= 0 && neighbourY < mazeSize)) {
                Cell neighbour = createCell(neighbourX, neighbourY);
                neighbours.add(neighbour);
            }
        }

        // return valid neighbours
        return neighbours;
    }

    /**
     * Retrieve found maze's path
     * @param cell Any mapped cell (normally the target)
     * @return List of coordinates that represents the found path
     */
    public void retrievePath(Cell cell) { // List<Pair<Integer, Integer>>
        List<Cell> bestMazePath = new ArrayList<>();
        bestMazePath.add(cell);

        while (cell.previousCell != null) {
            cell = cell.previousCell;
            bestMazePath.add(cell);
        }
        Collections.reverse(bestMazePath);
        showCells("Path", bestMazePath);
//        return bestMazePath;
    }

    /**
     * Heuristic function based on euclidean distance
     * OBS: Cross distance = 1 point | Diagonal distance = 1.4 points
     * @param fromX X coordinate (line) of current cell
     * @param fromY Y coordinate (column) of current cell
     * @param toX X coordinate (line) of other cell
     * @param toY Y coordinate (column) of other cell
     * @return Distance between cells
     */
    public int heuristicFunction(int fromX, int fromY, int toX, int toY) {
        int dstX = Math.abs(fromX - toX);
        int dstY = Math.abs(fromY - toY);

        if (dstX > dstY) {
            return 14 * dstY + 10 * (dstX - dstY);
        } else {
            return 14*dstX + 10 * (dstY-dstX);
        }
    }
}
