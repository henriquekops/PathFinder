package algorithms;

// built-in dependencies
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// external dependencies
import logs.Logger;
import org.javatuples.Pair;

public class AStarAlgorithm {

    private static class Cell {

        private int sourceCost;
        private final int targetCost;
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

        public Pair<Integer, Integer> getCoordinates() {
            return Pair.with(this.X, this.Y);
        }

    }

    private final int sourceX;
    private final int sourceY;

    private final int targetX;
    private final int targetY;

    private final List<Cell> walkableCells, chosenCells;
    private final List<Cell> clearCellCache = Collections.emptyList();

    private final Logger logger;

    private final int[][] neighbourCellsCoordinates = new int[][] {
            {0,+1}, {+1,0}, {-1,0}, {0,-1},
            {+1,+1},{-1,-1}, {-1,+1}, {+1,-1}
    };

    public AStarAlgorithm(int[] source, int[] target) {
        this.sourceX = source[0];
        this.sourceY = source[1];
        this.targetX = target[0];
        this.targetY = target[1];
        this.walkableCells = new ArrayList<>();
        this.chosenCells = new ArrayList<>();
        this.logger = Logger.getInstance();
    }

    public void findPath(char[][] maze, int mazeSize) {

        // create source and target cells
        Cell sourceCell = createCell(this.sourceX,this.sourceY);
        Cell targetCell = createCell(this.targetX, this.targetY);
        Cell currCell = null;

        // source is a free cell
        this.walkableCells.add(sourceCell);

        System.out.print("\n\n//// A* algorithm Execution ////");
        logger.log("\n\n//// A* algorithm Execution ////");

        // while there are free cells to walk through
        while (!walkableCells.isEmpty()) {

            // get best walkable cell using first as pivot
            currCell = getBestFreeCell(this.walkableCells.get(0));

            if (currCell.getCoordinates().equals(targetCell.getCoordinates())) {
                break;
            }

            logger.log("\n\nCurrent cell= " + currCell.getCoordinates());

            // choose the best cell to be part of the path
            walkableCells.remove(currCell);
            chosenCells.add(currCell);

            // get current cell's neighbours
            List<Cell> neighbourCells = getCellNeighbours(currCell, mazeSize);

            // for every neighbour
            for (Cell neighbour: neighbourCells) {

                // if it wasn't already chosen
                if (!contains(chosenCells,neighbour)){
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

    public int heuristicFunction(int fromX, int fromY, int toX, int toY) {
        int dstX = Math.abs(fromX - toX);
        int dstY = Math.abs(fromY - toY);

        if (dstX > dstY) {
            return 14 * dstY + 10 * (dstX - dstY);
        } else {
            return 14 * dstX + 10 * (dstY - dstX);
        }
    }

    public boolean contains(List<Cell> chosen, Cell neighbour) {
        boolean contains = false;

        for(int cell=0; cell<chosen.size(); cell++){
            if(chosen.get(cell).getCoordinates().equals(neighbour.getCoordinates())){
                contains = true;
                break;
            }
        }
        return contains;
    }

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

    public boolean isAlreadyWalkable(Cell unknownCell) {
        for (Cell cell : this.walkableCells) {
            if (cell.getCoordinates().equals(unknownCell.getCoordinates())) {
                return true;
            }
        }
        return false;
    }

    public Cell createCell(int x, int y) {
        // calculate heuristics
        int distSource = heuristicFunction(x, y, this.sourceX, this.sourceY);
        int distTarget = heuristicFunction(x, y, this.targetX, this.targetY);

        // return cell object
        return new Cell(x, y, distSource, distTarget);
    }

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

    public void retrievePath(Cell cell) {
        List<Cell> bestMazePath = new ArrayList<>();
        bestMazePath.add(cell);

        while (cell.previousCell != null) {
            cell = cell.previousCell;
            bestMazePath.add(cell);
        }
        Collections.reverse(bestMazePath);
        showCells("Path", bestMazePath);
    }

    public void showCells(String prefix, List<Cell> cells) {
        System.out.print("\n\n" + prefix + " cells: ");
        logger.log("\n\n" + prefix + " cells: ");
        for (Cell cell: cells) {
            System.out.print(cell.getCoordinates() + " ");
            logger.log(cell.getCoordinates() + " ");
        }
        System.out.print("\n\n");
        logger.log("\n\n");
    }
}
