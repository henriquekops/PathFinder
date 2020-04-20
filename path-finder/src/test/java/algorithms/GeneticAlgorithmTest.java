package algorithms;

// built-in dependencies
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit test for GeneticAlgorithm class.
 */
public class GeneticAlgorithmTest {

    /**
     * Find path method's test
     */
    @Test
    public void testFindPath()
    {
        String[][] maze = new String[10][10];
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0,0,0);
        assertEquals(geneticAlgorithm.findPath(maze), maze);
    }

    /**
     * Heuristic function method's test
     */
    @Test
    public void testHeuristicFunction()
    {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0,0,0);
        assertEquals(geneticAlgorithm.heuristicFunction(0), 0);
    }

    /**
     * Elitism algorithm method's test
     */
    @Test
    public void testElitismAlgorithm()
    {
        String[][] population = new String[10][10];
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0,0,0);
        assertEquals(geneticAlgorithm.elitismAlgorithm(population), 0);
    }

    /**
     * Tournament algorithm method's test
     */
    @Test
    public void testTournamentAlgorithm()
    {
        int[] everyAgentScore = new int[10];
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0,0,0);
        assertEquals(geneticAlgorithm.tournamentAlgorithm(everyAgentScore), 0);
    }

    /**
     * Crossover algorithm method's test
     */
    @Test
    public void testCrossoverAlgorithm()
    {
        String[][] population = new String[10][10];
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0,0,0);
        assertEquals(geneticAlgorithm.crossoverAlgorithm(population), population);
    }
}