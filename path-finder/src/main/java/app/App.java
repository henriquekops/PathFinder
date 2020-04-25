package app;

// project dependencies
import algorithms.GeneticAlgorithm;

/**
 * Main application class
 */
public class App 
{
    /*
    NOTES:
      Expected input:
        $ java main -p {--path} ~/UserX/... -g {--generations} 100 -s {--solutions} 5 [--debug]
     */

    public static void main( String[] args )
    {
        char[][] maze = {
                {'E', '0', '1'},
                {'1', '0', '1'},
                {'S', '1', '1'}
        };
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(3, 5, 3);
        char[][] solution = geneticAlgorithm.findPath(maze);
    }
}
