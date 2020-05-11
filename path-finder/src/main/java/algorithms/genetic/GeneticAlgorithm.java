package algorithms.genetic;

// built-in dependencies
import java.util.*;
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

            showPopulation("ORIGINAL", originalPopulation);

            // elitism
            Agent bestFitAgent = elitismAlgorithm(originalPopulation);
            intermediatePopulation.add(bestFitAgent);

            // crossover
            for (int childIdx = 1; childIdx < this.numAgents; childIdx++) {

                // TODO: Drop duplicates
                Pair <Integer, Integer> agentsIndexPair = getRandomPairOfAgentIndexes(-1);
                Agent father = tournamentAlgorithm(originalPopulation, agentsIndexPair);

                agentsIndexPair = getRandomPairOfAgentIndexes(father.getIdentifier());
                Agent mother = tournamentAlgorithm(originalPopulation, agentsIndexPair);

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
        List<Pair<Integer, Integer>> agentCoordinates = new ArrayList<>();
        Pair<Integer, Integer> agentNextCoordinate;
        boolean stopEvaluation = false;
        boolean isInBoundary = false;

        for (Agent agent: population) {
            List<String> agentMoves = agent.getMoves();

            for (int moveIdx = 0; moveIdx < this.numAgentMoves && !stopEvaluation; moveIdx++) {
                String move = agentMoves.get(moveIdx);
                agentNextCoordinate = movementMapping(agent, move);

                isInBoundary = checkBoundary(agentNextCoordinate, agent, mazeSize);
                if (isInBoundary) {
                    stopEvaluation = checkMazeObject(agentNextCoordinate, agent, maze);
                }
                agentCoordinates.add(agent.getCoordinates());

            }
            if (!stopEvaluation) {
                agent.updateScore(1000);
            }
            checkLoop(agentCoordinates, agent);
            agentCoordinates.clear();
        }
        System.out.println("");
    }

    /**
     *
     * @param coordinate
     * @param agent
     * @param mazeSize
     */
    public boolean checkBoundary(Pair<Integer, Integer> coordinate, Agent agent, int mazeSize) {
        int nextXCoordinate = coordinate.getValue0();
        int nextYCoordinate = coordinate.getValue1();

        if ( nextXCoordinate > 0 && nextXCoordinate < mazeSize && nextYCoordinate > 0 && nextYCoordinate < mazeSize) {
            agent.updateScore(+1);
            agent.setCoordinate(coordinate);
            return true;
        }
        else {
            agent.updateScore(+1);
            return false;
        }
    }

    /**
     *
     * @param coordinate
     * @param agent
     * @param maze
     */
    public boolean checkMazeObject(Pair<Integer, Integer> coordinate, Agent agent, char[][] maze) {

        char mazeObject = maze[coordinate.getValue0()][coordinate.getValue1()];
        boolean stopEvaluation = false;

        switch (mazeObject) {
            case 'S':
                agent.setCoordinate(coordinate);
                stopEvaluation = true;
                break;
            case '0':
                agent.setCoordinate(coordinate);
                break;
            case '1':
            case 'B':
                agent.updateScore(+1);
                break;
        }
        return stopEvaluation;
    }

    public void checkLoop(List<Pair<Integer, Integer>> coordinates, Agent agent) {
        Set<Pair<Integer, Integer>> dropDuplicateCoordinates = new HashSet<>(coordinates);
        if (coordinates.size() != dropDuplicateCoordinates.size()) {
            agent.updateScore(+2);
        }
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
        bestAgent.setScore(0);
        return bestAgent;
    }

    /**
     * Selects an agent through tournament algorithm (best between two randomized fits)
     * @return Best fit agent
     */
    public Agent tournamentAlgorithm(List<Agent> population, Pair<Integer, Integer> agentsIndexPair) {

        int firstAgentId = agentsIndexPair.getValue0();
        int secondAgentId = agentsIndexPair.getValue1();

        Agent firstAgent = population.get(firstAgentId);
        Agent secondAgent = population.get(secondAgentId);

        System.out.println("first agent: " + firstAgent.getIdentifier() +
                " second agent: " + secondAgent.getIdentifier());

        if ( firstAgent.getScore() < secondAgent.getScore()) {
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
     * Map agent's movements to maze's positions
     * @param agent Agent index (population line)
     * @param move
     * @return Array containing mapped movements as maze's positions
     */
    public Pair<Integer, Integer> movementMapping(Agent agent, String move) {

        int nextXCoordinate = 0;
        int nextYCoordinate = 0;

        switch (move) {
            case "N":
                nextXCoordinate = agent.getX() - 1;
                break;
            case "S":
                nextXCoordinate = agent.getX() + 1;
                break;
            case "E":
                nextYCoordinate = agent.getY() + 1;
                break;
            case "W":
                nextYCoordinate = agent.getY() - 1;
                break;
            case "NE":
                nextXCoordinate = agent.getX() - 1;
                nextYCoordinate = agent.getY() + 1;
                break;
            case "NW":
                nextXCoordinate = agent.getX() - 1;
                nextYCoordinate = agent.getY()- 1;
                break;
            case "SE":
                nextXCoordinate = agent.getX() + 1;
                nextYCoordinate = agent.getY() + 1;
                break;
            case "SW":
                nextXCoordinate = agent.getX() + 1;
                nextYCoordinate = agent.getY() - 1;
                break;
        }

        return Pair.with(nextXCoordinate, nextYCoordinate);

    }

    /**
     *
     * @return
     */
    public Pair<Integer, Integer> getRandomPairOfAgentIndexes(int excludeId) { // [0, 1, 2, 3]
        Random random = new Random();
        int boundary = this.numAgents;

        List<Integer> availableAgentIndexes = IntStream.rangeClosed(0, this.numAgents - 1) // [0, 2, 3]
                .boxed()
                .collect(Collectors.toList());

        if (excludeId >= 0) {
            availableAgentIndexes.remove(excludeId);
            boundary -= 1;
        }

        int firstRandomIndex = random.nextInt(boundary);
        int firstAgentId = availableAgentIndexes.remove(firstRandomIndex);

        int secondRandomIndex = random.nextInt(boundary - 1);
        int secondAgentId = availableAgentIndexes.remove(secondRandomIndex);

        return Pair.with(firstAgentId, secondAgentId);

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