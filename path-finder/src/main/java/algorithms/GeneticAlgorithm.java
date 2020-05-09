package algorithms;

// built-in dependencies
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

/***
 * Genetic algorithm class
 */
public class GeneticAlgorithm {

    /*
    NOTES:
      Agent movements:
        N=up, S=down, E=right, W=left
        NE=diagonal-up-right, NW=diagonal-up-left, SE=diagonal-down-right, SW=diagonal-down-left
     */

    /**
     * Java default variables
     */
    private final Random randomizer;

    /**
     * Genetic cycle variables
     */
    private final int numAgents;
    private final int numAgentGeneticLoad;
    private final int numGenerations;

    /**
     * Genetic cycle collections
     */
    private final String[] possibleMoves = {"N", "S", "E", "W", "NE", "NW", "SE", "SW"};
    private String[][] originalPopulation;
    private String[][] intermediatePopulation;
    private int[] everyAgentScores;

    /***
     * Genetic class algorithm constructor
     * @param numAgents Number of agents to use for path finding
     * @param numGenerations Number of generations (cycles) to loop
     * @param numPossiblePaths Number of maze's free cells for an agent to walk
     */
    public GeneticAlgorithm(int numAgents, int numGenerations, int numPossiblePaths) {
        this.randomizer = new Random();
        this.numAgents = numAgents;
        this.numAgentGeneticLoad = numPossiblePaths;
        this.numGenerations = numGenerations;
        this.originalPopulation = new String[this.numAgents][this.numAgentGeneticLoad];
        this.intermediatePopulation = new String[this.numAgents][this.numAgentGeneticLoad];
        this.everyAgentScores = new int[this.numAgents];
    }

    /**
     * Applies the genetic algorithm for maze's path finding
     * @param maze Maze that contains a path to be found
     * @return Same maze from input but containing its solution
     */
    public char[][] findPath(char[][] maze) { // TODO: fix tournament duplication (multi-call)
        // start genetic cycle collections
        Arrays.fill(this.everyAgentScores, 0); // NOTE: temporary
        startPopulation(this.originalPopulation);

        // genetic cycle
        System.out.println("\n//// Genetic algorithm Execution ////");
        for (int generation = 0; generation < this.numGenerations; generation++) {

            // print
            System.out.println("//// Generation: " + (generation+1));
            showPopulation(this.originalPopulation, this.everyAgentScores);

            // heuristic
            heuristicFunction(maze);

            // elitism
            int firstBornAgent = elitismAlgorithm(this.everyAgentScores);
            this.intermediatePopulation[0] = Arrays.copyOf(
                    this.originalPopulation[firstBornAgent],
                    this.numAgentGeneticLoad
            );

            // crossover
            for (int child = 1; child < this.numAgents; child++) {

                int[] fAndM = tournamentAlgorithm();
                int father = fAndM[0];
                int mother = fAndM[1];

                String[] fatherGeneticLoad = Arrays.copyOf(this.originalPopulation[father], this.numAgentGeneticLoad);
                String[] motherGeneticLoad = Arrays.copyOf(this.originalPopulation[mother], this.numAgentGeneticLoad);

                crossoverAlgorithm(fatherGeneticLoad, motherGeneticLoad, child);
            }

            // update current population
            this.originalPopulation = this.intermediatePopulation;
        }

        return maze;
    }

    /**
     * Applies the heuristic function for generate fit score
     * @param maze Current maze to solve
     */
    public void heuristicFunction(char[][] maze) { // TODO: Unit test
        char[] agentPathInMaze;

        for (int agent = 0; agent < this.numAgents; agent++) {
            agentPathInMaze = sanitizeAgentGeneticLoad(agent, maze);
            showPath(agent, agentPathInMaze);
            //this.everyAgentScores[agent] = some calculation over agentPathInMaze
        }
        System.out.println("");
    }

    /**
     *
     * @param agent Agent's identifier to recover maze's path information
     * @param maze Current maze to solve
     * @return Agent maze's path info
     */
    public char[] sanitizeAgentGeneticLoad(int agent, char[][] maze) {
        int[] agentCurrentPosition = new int[] {0, 0};
        int[] agentNewPosition;
        int mazeSize = maze.length;

        char[] agentPathInMaze = new char[this.numAgentGeneticLoad];

        for (int gene = 0; gene < this.numAgentGeneticLoad; gene++) {
            agentNewPosition = movementMapping(agent, gene, agentCurrentPosition);
            agentCurrentPosition = validatePosition(agentCurrentPosition, agentNewPosition, mazeSize);
            agentPathInMaze[gene] = maze[agentCurrentPosition[0]][agentCurrentPosition[1]];
        }
        return agentPathInMaze;
    }

    /**
     * Map agent's movements to maze's positions
     * @param agent Agent index (population line)
     * @param gene Agent movement (population column)
     * @param agentCurrentPosition Current position of the agent in maze
     * @return Array containing mapped movements as maze's positions
     */
    public int[] movementMapping(int agent, int gene, int[] agentCurrentPosition) { ;

        int[] newAgentPosition;
        int mazeLine = agentCurrentPosition[0];
        int mazeCol = agentCurrentPosition[1];

        String agentMove = this.originalPopulation[agent][gene];

        switch (agentMove) {
            case "N":
                mazeLine -= 1;
                break;
            case "S":
                mazeLine += 1;
                break;
            case "E":
                mazeCol += 1;
                break;
            case "W":
                mazeCol -= 1;
                break;
            case "NE":
                mazeLine -= 1;
                mazeCol += 1;
                break;
            case "NW":
                mazeLine -= 1;
                mazeCol -= 1;
                break;
            case "SE":
                mazeLine += 1;
                mazeCol += 1;
                break;
            case "SW":
                mazeLine += 1;
                mazeCol -= 1;
                break;
        }

        newAgentPosition = new int[]{mazeLine, mazeCol};
        return newAgentPosition;
    }

    /**
     *
     * @param agentCurrentPosition Current agent's position in maze
     * @param agentNewPosition New possible agent's position in maze
     * @param mazeSize Parametrized maze's size
     * @return Valid agent's position in maze
     */
    public int[] validatePosition(int[] agentCurrentPosition, int[] agentNewPosition, int mazeSize) {
        int newLine = agentNewPosition[0];
        int newCol = agentNewPosition[1];

        if (newLine < 0 || newLine >= mazeSize || newCol < 0 || newCol >= mazeSize) {
            return agentCurrentPosition;
        }
        else{
            return agentNewPosition;
        }
    }

    /**
     * Selects an agent through elitism algorithm (best of all fits)
     * @param everyAgentScores Every agent's scores to apply the algorithm
     * @return Best agent's solution to carry to next generation
     */
    public int elitismAlgorithm(int[] everyAgentScores) {
        // elitism variables
        int chosenAgent = 0;
        int currentFitScore = everyAgentScores[0];

        // every agent's score comparison
        for (int agent = 1; agent < this.numAgents ; agent++) {
            if (currentFitScore < everyAgentScores[agent]) {
                chosenAgent = agent;
            }
        }
        return chosenAgent;
    }

    /**
     * Selects two agents: father and mother
     * @return father and mother agents
     */
    public int[] tournamentAlgorithm() {
        // List of available agent's indexes
        List<Integer> availableAgents = IntStream.rangeClosed(0, this.numAgents-1)
                .boxed()
                .collect(Collectors.toList());

        // Agent selection without duplication
        int fatherIdx = TournamentAlgorithm();
        int fatherValue = availableAgents.remove(fatherIdx);

        int motherIdx = TournamentAlgorithm();
        int motherValue = availableAgents.remove(motherIdx);

        int[] ret = {fatherValue, motherValue};
        return ret;
    }

    /**
     * Selects an agent through tournament algorithm (best between two randomized fits)
     * @return Best fit agent
     */
    private int TournamentAlgorithm() {
        // List of available agent's indexes
        List<Integer> availableAgents = IntStream.rangeClosed(0, this.numAgents-1)
                .boxed()
                .collect(Collectors.toList());

        // Agent selection without duplication
        int firstAgentIdx = this.randomizer.nextInt(this.numAgents);
        int firstAgent = availableAgents.remove(firstAgentIdx);

        int secondAgentIdx = this.randomizer.nextInt(this.numAgents-1);
        int secondAgent = availableAgents.remove(secondAgentIdx);

        // Agent scores comparison
        int firstAgentScore = this.everyAgentScores[firstAgent];
        int secondAgentScore = this.everyAgentScores[secondAgent];

        if (firstAgentScore > secondAgentScore) {
            return firstAgent;
        }
        else {
            return secondAgent;
        }
    }

    /**
     * Crosses genetic load between two agents using single point technique
     * @param fatherGeneticLoad Father's genetic load
     * @param motherGeneticLoad Mother's genetic load
     * @param child Child index to receive crossed genes
     */
    public void crossoverAlgorithm(String[] fatherGeneticLoad, String[] motherGeneticLoad, int child) {

        for (int gene = 0; gene < this.numAgentGeneticLoad; gene ++ ) {
            int mask = this.randomizer.nextInt(1);

            if (mask == 0) {
                this.intermediatePopulation[child][gene] = fatherGeneticLoad[gene];
            }
            else if (mask == 1) {
                this.intermediatePopulation[child][gene] = motherGeneticLoad[gene];
            }
        }
    }

    /**
     * Initializes agent population with random moves
     * @param population Population to store every agent's moves
     */
    private void startPopulation(String[][] population) {
        // quantity of possible agent's moves
        int numPossibleMoves = this.possibleMoves.length;

        // populate with random moves
        for (int agent = 0; agent < this.numAgents; agent++) {
            for (int gene = 0; gene < this.numAgentGeneticLoad; gene++) {
                String agentMove = this.possibleMoves[this.randomizer.nextInt(numPossibleMoves)];
                population[agent][gene] = agentMove;
            }
        }
    }

    /**
     * Shows current population
     * @param population Agent population to show
     * @param everyAgentScores Every agent score to show
     */
    public void showPopulation(String[][] population, int[] everyAgentScores) {
        // For every agent ...
        for(int agent = 0; agent < this.numAgents; agent++){
            System.out.print("[AGENT " + (agent+1) + "]: ");

            // ... show its genetic load ...
            for(int move = 0; move < this.numAgentGeneticLoad; move++){
                System.out.print(population[agent][move] + " ");
            }

            // ... and solution score :)
            System.out.println("| Fit score: " + everyAgentScores[agent]);
        }
        System.out.println("");
    }

    /**
     * Show current agent's path in maze
     * @param agent Agent's identifier
     * @param path Path made by agent
     */
    private void showPath(int agent, char[] path) {
        System.out.print("[Agent " + (agent+1) + "] PATH: [ ");

        // show every object in path
        for (char c: path) {
            System.out.print(c + " ");
        }
        System.out.println("]");
    }
}