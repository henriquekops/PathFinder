package algorithms;

// built-in dependencies
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

// external dependencies
import org.javatuples.Pair;

/***
 * Genetic algorithm class
 */
public class GeneticAlgorithm { // TODO: review comments

    // TODO: put in another file
    private class Agent {

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

    /**
     * Genetic cycle variables
     */
    private final int numAgents;
    private final int numAgentMoves;
    private final int numGenerations;

    /**
     * Genetic cycle collections
     */
    private final String[] possibleMoves = {"N", "S", "E", "W", "NE", "NW", "SE", "SW"};

    /***
     * Genetic class algorithm constructor
     * @param numAgents Number of agents to use for path finding
     * @param numGenerations Number of generations (cycles) to loop
     * @param numAgentMoves Number of maze's free cells for an agent to walk
     */
    public GeneticAlgorithm(int numAgents, int numGenerations, int numAgentMoves) {
        this.numAgents = numAgents;
        this.numAgentMoves = numAgentMoves;
        this.numGenerations = numGenerations;
    }

    /**
     * Applies the genetic algorithm for maze's path finding
     * @param maze Maze that contains a path to be found
     * @return Same maze from input but containing its solution
     */
    public char[][] findPath(char[][] maze, int mazeSize) {
        // start genetic cycle collections
        List<Agent> originalPopulation = startPopulation(0,0);
        List<Agent> intermediatePopulation = new ArrayList<>();

        // genetic cycle
        System.out.println("\n//// Genetic algorithm Execution ////");
        for (int generation = 0; generation < this.numGenerations; generation++) {

            // print
            System.out.println("//// Generation: " + (generation+1));
            showPopulation(originalPopulation);
            // showPopulation(intermediatePopulation);

            // heuristic
            heuristicFunction(originalPopulation, maze, mazeSize);

            // elitism
            Agent bestFitAgent = elitismAlgorithm(originalPopulation);
            intermediatePopulation.add(bestFitAgent);

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
     * Initializes agent population with random moves
     * @return Population started with random agent's moves
     */
    public List<Agent> startPopulation(int startX, int startY) {

        List<Agent> population = new ArrayList<>();
        Random random = new Random();

        for (int agentIdx = 0; agentIdx < this.numAgents; agentIdx++) {

            Agent agentObj = new Agent(startX, startY, agentIdx);

            for (int move = 0; move < this.numAgentMoves; move++) {
                String agentMove = this.possibleMoves[random.nextInt(this.numAgentMoves)];
                agentObj.moves.add(agentMove);
            }

            population.add(agentObj);
        }

        return population;
    }

    /**
     * Applies the heuristic function for generate fit score
     * @param maze Current maze to solve
     */
    public void heuristicFunction(List<Agent> population, char[][] maze, int mazeSize) {
        List<Character> agentPathInMaze;

        for (Agent agent: population) {
            agentPathInMaze = sanitizeAgentMovementsLoad(agent, maze, mazeSize);
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
    public List<Character> sanitizeAgentMovementsLoad(Agent agent, char[][] maze, int mazeSize) {
        List<Character> agentPathInMaze = new ArrayList<>();

        for (int move = 0; move < this.numAgentMoves; move++) {
            movementMapping(agent, move, mazeSize);
            agentPathInMaze.add(maze[agent.X][agent.Y]);
        }
        return agentPathInMaze;
    }

    /**
     * Map agent's movements to maze's positions
     * @param agent Agent index (population line)
     * @param move
     * @param mazeSize
     * @return Array containing mapped movements as maze's positions
     */
    public void movementMapping(Agent agent, int move, int mazeSize) { ;

        int newX = 0;
        int newY = 0;

        switch (agent.moves.get(move)) {
            case "N":
                newX = agent.X - 1;
                break;
            case "S":
                newX = agent.X + 1;
                break;
            case "E":
                newY = agent.Y + 1;
                break;
            case "W":
                newY = agent.Y - 1;
                break;
            case "NE":
                newX = agent.X - 1;
                newY = agent.Y + 1;
                break;
            case "NW":
                newX = agent.X - 1;
                newY = agent.Y- 1;
                break;
            case "SE":
                newX = agent.X + 1;
                newY = agent.Y + 1;
                break;
            case "SW":
                newX = agent.X + 1;
                newY = agent.Y - 1;
                break;
        }

        agent.setX(newX, mazeSize);
        agent.setY(newY, mazeSize);
    }

    /**
     * Selects an agent through elitism algorithm (best of all fits)
     * @param population Every agent's scores to apply the algorithm
     * @return Best agent's solution to carry to next generation
     */
    public Agent elitismAlgorithm(List<Agent> population) {
        Agent pivot = population.get(0);

        for (Agent agent : population) {
            if (agent.score > pivot.score) {
                pivot = agent;
            }
        }

        return pivot;
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
    private Agent tournamentAlgorithm(List<Agent> population) {

        Pair <Integer, Integer> agentsIndexPair = getRandomPairOfAgentIndexes();

        Agent firstAgent = population.get(agentsIndexPair.getValue0());
        Agent secondAgent = population.get(agentsIndexPair.getValue1());

        // Agent scores comparison
        if ( firstAgent.score > secondAgent.score) {
            return firstAgent;
        }
        else {
            return secondAgent;
        }
    }

    /**
     *
     * @return
     */
    public Pair<Integer, Integer> getRandomPairOfAgentIndexes() {
        Random random = new Random();
        List<Integer> availableAgentIndexes = IntStream.rangeClosed(0, this.numAgents-1)
                .boxed()
                .collect(Collectors.toList());

        int firstRandomIndex = random.nextInt(this.numAgents);
        int firstAgentIndex = availableAgentIndexes.remove(firstRandomIndex);

        int secondRandomIndex = random.nextInt(this.numAgents-1);
        int secondAgentIndex = availableAgentIndexes.remove(secondRandomIndex);

        return Pair.with(firstAgentIndex, secondAgentIndex);

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

//    /**
//     *
//     */
//    public String[][] mutatePopulation(String[][] population) {
//        int agent = this.randomizer.nextInt(this.numAgents);
//
//    }


    /**
     * Shows current population
     * @param population Agent population to show
     */
    public void showPopulation(List<Agent> population) {
        for (Agent agent: population) {
            System.out.print("[AGENT " + (agent.identifier) + "]: ");
            for (String move : agent.moves) {
                System.out.print(move + " ");
            }
            System.out.println(" | Score: " + agent.score);
        }
        System.out.println("");
    }

    /**
     * Show current agent's path in maze
     * @param agent Agent's identifier
     * @param path Path made by agent
     */
    private void showPath(Agent agent, List<Character> path) {
        System.out.print("[Agent " + agent.identifier + "] PATH: [ ");

        // show every object in path
        for (char c: path) {
            System.out.print(c + " ");
        }
        System.out.println("]");
    }
}