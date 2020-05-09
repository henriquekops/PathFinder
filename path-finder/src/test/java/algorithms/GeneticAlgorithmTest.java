package algorithms;

// built-in dependencies
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit test for GeneticAlgorithm class.
 */
public class GeneticAlgorithmTest {

    /**
     * Heuristic function method's test
     */
    @Test
    public void testHeuristicFunction()
    {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(0,0,0);
        //assertEquals(geneticAlgorithm.heuristicFunction(), 0);
    }

    /**
     * Elitism algorithm method's test
     */
    @Test
    public void testElitismAlgorithm()
    {
        int[] everyAgentScores = {1, 2, 3, 4, 5, 6};
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(6,0,0);
        assertEquals(5, geneticAlgorithm.elitismAlgorithm(everyAgentScores));
    }

    /**
     * Tournament algorithm method's test
     */
    @Test
    public void testTournamentAlgorithm()
    {
        int[] everyAgentScore = {1, 2};
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(2,0,0);
//        assertEquals(1, geneticAlgorithm.tournamentAlgorithm(everyAgentScore));
    }
}