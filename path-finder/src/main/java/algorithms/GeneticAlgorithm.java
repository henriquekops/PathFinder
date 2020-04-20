package algorithms;

// External dependencies
import java.util.Random;

/***
 * Genetic algorithm class
 */
public class GeneticAlgorithm {

    /*
    NOTES:
      Expected input:
        $ java main -p {--path} ~/UserX/... -g {--generations} 100 -s {--solutions} 5 [--debug]
      Agent movements:
        N=up, S=down, E=right, W=left
        NE=diagonal-up-right, NW=diagonal-up-left, SE=diagonal-down-right, SW=diagonal-down-left
     */

    /**
     * Java default variables
     */
    private Random randomizer;

    /**
     * Genetic cycle variables
     */
    private int numAgents;
    private int numAgentGeneticLoad;
    private int numGenerations;

    /***
     * Genetic class algorithm constructor
     *
     * @param numAgents : Number of agents to use for path finding
     * @param numGenerations : Number of generations (cycles) to loop
     * @param numPossiblePaths : Number of maze's free cells for an agent to walk
     */
    public GeneticAlgorithm(int numAgents, int numGenerations, int numPossiblePaths) {
        this.randomizer = new Random();
        this.numAgents = numAgents;
        this.numAgentGeneticLoad = numPossiblePaths;
        this.numGenerations = numGenerations;
    }

    /**
     * Applies the genetic algorithm for maze's path finding
     * @param maze Maze that contains a path to be found
     * @return Same maze from input but containing its solution
     */
    public String[][] findPath(String[][] maze) {
        System.out.println("Not implemented yet :(");
        return maze;
    }

    /**
     * Applies the heuristic function for generate fit score
     * @param agent Agent's identifier to recover its maze's solution
     * @return Score given to parametrized agent
     */
    public int heuristicFunction(int agent) {
        System.out.println("Not implemented yet :(");
        return 0;
    }

    /**
     * Selects an agent through elitism algorithm (best of all fits)
     * @param population Population to extract best agent's genetic load
     * @return Best agent's solution to carry to next generation
     */
    public int elitismAlgorithm(String[][] population) {
        System.out.println("Not implemented yet :(");
        return 0;
    }

    /**
     * Selects an agent through tournament algorithm (best between two randomized fits)
     * @param everyAgentScores Every agent's scores to apply the algorithm
     * @return Best fit agent
     */
    public int tournamentAlgorithm(int[] everyAgentScores) {
        System.out.println("Not implemented yet :(");
        return 0;
    }

    /**
     * Crosses genetic load between two agents using single point technique
     *
     * @param population Current agent population
     * @return Intermediate agent population generated
     */
    public String[][] crossoverAlgorithm(String[][] population) {
        System.out.println("Not implemented yet :(");
        return population;
    }

    /**
     * Shows current population
     */
    public void showPopulation() {
        System.out.println("Not implemented yet :(");
    }

}