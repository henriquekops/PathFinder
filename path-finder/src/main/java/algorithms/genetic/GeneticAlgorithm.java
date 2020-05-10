package algorithms.genetic;

// built-in dependencies
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

// external dependencies
import org.javatuples.Pair;

/***
 * Genetic algorithm class
 */
public class GeneticAlgorithm { // TODO: review comments

    /**
     * Genetic cycle variables
     */
    private final int numAgents;
    private final int numAgentMoves;
    private final int numGenerations;
    private final int mutationRatio;

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
    public GeneticAlgorithm(int numAgents, int numGenerations,
                            int numAgentMoves, int mutationRatio) {
        this.numAgents = numAgents;
        this.numAgentMoves = numAgentMoves;
        this.numGenerations = numGenerations;
        this.mutationRatio = mutationRatio;
    }

    /**
     * Applies the genetic algorithm for maze's path finding
     * @param maze Maze that contains a path to be found
     * @param mazeSize
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
            showPopulation("ORIGINAL", originalPopulation);

            // heuristic
            heuristicFunction(originalPopulation, maze, mazeSize);

            // elitism
            Agent bestFitAgent = elitismAlgorithm(originalPopulation);
            intermediatePopulation.add(bestFitAgent);

            // crossover
            for (int childIdx = 1; childIdx < this.numAgents; childIdx++) {
                Agent father = tournamentAlgorithm(originalPopulation, -1);
                Agent mother = tournamentAlgorithm(originalPopulation, father.getIdentifier());
                System.out.println("father: " + father.getIdentifier() +
                        " mother: " + mother.getIdentifier());
                Agent child = crossoverAlgorithm(father, mother, childIdx);
                intermediatePopulation.add(child);
            }

            System.out.println("");

            intermediatePopulation = mutatePopulation(intermediatePopulation);

            showPopulation("INTERMEDIATE", intermediatePopulation);

            // update current population
            Collections.copy(originalPopulation, intermediatePopulation);
            intermediatePopulation.clear();

        }

        return maze;
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
     * Selects an agent through elitism algorithm (best of all fits)
     * @param population Every agent's scores to apply the algorithm
     * @return Best agent's solution to carry to next generation
     */
    public Agent elitismAlgorithm(List<Agent> population) {
        Agent bestAgent = population.get(0);

        for (Agent agent : population) {
            if (agent.getScore() > bestAgent.getScore()) {
                bestAgent = agent;
            }
        }

        bestAgent.setIdentifier(0);
        return bestAgent;
    }

    /**
     * Selects an agent through tournament algorithm (best between two randomized fits)
     * @return Best fit agent
     */
    public Agent tournamentAlgorithm(List<Agent> population, int excludedAgentIdentifier) {
        Pair <Integer, Integer> agentsIndexPair = getRandomPairOfAgentIndexes(excludedAgentIdentifier);
        int firstAgentIndex = agentsIndexPair.getValue0();
        int secondAgentIndex = agentsIndexPair.getValue1();

        Agent firstAgent = population.get(firstAgentIndex);
        Agent secondAgent = population.get(secondAgentIndex);

        System.out.println("first agent: " + firstAgent.getIdentifier() +
                " second agent: " + secondAgent.getIdentifier());

        if ( firstAgent.getScore() > secondAgent.getScore()) {
            return firstAgent;
        }
        else {
            return secondAgent;
        }
    }

    /**
     * Crosses genetic load between two agents using mask technique
     * @param father
     * @param mother
     * @param childIdx
     */
    public Agent crossoverAlgorithm(Agent father, Agent mother, int childIdx) {

        Random random = new Random();
        Agent child = new Agent(0,0, childIdx);

        for (int move = 0; move < this.numAgentMoves; move ++ ) {
            int mask = random.nextInt(2);

            if (mask == 0) {
                child.getMoves().add(father.getMoves().get(move));
            }
            else if (mask == 1) {
                child.getMoves().add(mother.getMoves().get(move));
            }
        }
        return child;
    }

    /**
     *
     * @param population
     * @return
     */
    public List<Agent> mutatePopulation(List<Agent> population) {
        String newMovement;
        int currentMovementIdx;
        Random random = new Random();

        int agentIdx = random.nextInt(this.numAgents);
        Agent mutantAgent = population.get(agentIdx);

        int mutate = (int)Math.ceil(this.numAgentMoves*(this.mutationRatio/100.0));

        for (int i = 0; i < mutate; i++) {
            currentMovementIdx = random.nextInt(this.numAgentMoves);
            newMovement = this.possibleMoves[random.nextInt(8)];
            mutantAgent.getMoves().set(currentMovementIdx, newMovement);
        }

        System.out.println("Mutation: mutating agent " + mutantAgent.getIdentifier() + "\n");

        return population;
    }

    /**
     * Initializes agent population with random moves
     * @return Population started with random agent's moves
     */
    public List<Agent> startPopulation(int startX, int startY) {

        List<Agent> population = new ArrayList<>();
        Random random = new Random();

        for (int agentIdentifier = 0; agentIdentifier < this.numAgents; agentIdentifier++) {

            Agent agent = new Agent(startX, startY, agentIdentifier);

            for (int move = 0; move < this.numAgentMoves; move++) {
                String agentMove = this.possibleMoves[random.nextInt(8)];
                agent.getMoves().add(agentMove);
            }
            population.add(agent);
        }

        return population;
    }

    /**
     *
     * @param agent Agent's identifier to recover maze's path information
     * @param maze Current maze to solve
     * @return Agent maze's path info
     */
    public List<Character> sanitizeAgentMovementsLoad(Agent agent, char[][] maze, int mazeSize) {
        char mazeCellValue;
        List<Character> agentPathInMaze = new ArrayList<>();

        for (int move = 0; move < this.numAgentMoves; move++) {
            movementMapping(agent, move, mazeSize);
            mazeCellValue = maze[agent.getX()][agent.getY()];
            agentPathInMaze.add(mazeCellValue);
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

        switch (agent.getMoves().get(move)) {
            case "N":
                newX = agent.getX() - 1;
                break;
            case "S":
                newX = agent.getX() + 1;
                break;
            case "E":
                newY = agent.getY() + 1;
                break;
            case "W":
                newY = agent.getY() - 1;
                break;
            case "NE":
                newX = agent.getX() - 1;
                newY = agent.getY() + 1;
                break;
            case "NW":
                newX = agent.getX() - 1;
                newY = agent.getY()- 1;
                break;
            case "SE":
                newX = agent.getX() + 1;
                newY = agent.getY() + 1;
                break;
            case "SW":
                newX = agent.getX() + 1;
                newY = agent.getY() - 1;
                break;
        }

        agent.setX(newX, mazeSize);
        agent.setY(newY, mazeSize);
    }

    /**
     *
     * @return
     */
    public Pair<Integer, Integer> getRandomPairOfAgentIndexes(int excludeIdx) {
        Random random = new Random();
        int range = this.numAgents;

        List<Integer> availableAgentIndexes = IntStream.rangeClosed(0, this.numAgents - 1)
                .boxed()
                .collect(Collectors.toList());

        if (excludeIdx > 0) {
            availableAgentIndexes.remove(excludeIdx);
            range -= 1;
        }

        int firstRandomIndex = random.nextInt(range);
        int firstAgentIndex = availableAgentIndexes.remove(firstRandomIndex);

        int secondRandomIndex = random.nextInt(range - 1);
        int secondAgentIndex = availableAgentIndexes.remove(secondRandomIndex);

        return Pair.with(firstAgentIndex, secondAgentIndex);

    }

    /**
     * Shows current population
     * @param header
     * @param population Agent population to show
     */
    public void showPopulation(String header, List<Agent> population) {
        System.out.println(header);
        for (Agent agent: population) {
            System.out.print("[AGENT " + (agent.getIdentifier()) + "]: ");
            for (String move : agent.getMoves()) {
                System.out.print(move + " ");
            }
            System.out.println(" | Score: " + agent.getScore());
        }
        System.out.println("");
    }

    /**
     * Show current agent's path in maze
     * @param agent Agent's identifier
     * @param path Path made by agent
     */
    private void showPath(Agent agent, List<Character> path) {
        System.out.print("[Agent " + agent.getIdentifier() + "] PATH: [ ");

        // show every object in path
        for (char c: path) {
            System.out.print(c + " ");
        }
        System.out.println("]");
    }
}