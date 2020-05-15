package algorithms.genetic;

// built-in dependencies
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

// external dependencies
import logs.Logger;
import org.apache.commons.lang.SerializationUtils;
import org.javatuples.Pair;

public class GeneticAlgorithm {

    private final int numGenerations;
    private final int numAgents;
    private final int movementMutationRatio;
    private final int agentMutationRatio;
    private final Logger logger;

    public GeneticAlgorithm(int numGenerations, int numAgents, int agentMutationRatio, int movementMutationRatio) {
        this.numGenerations = numGenerations;
        this.numAgents = numAgents;
        this.agentMutationRatio = agentMutationRatio;
        this.movementMutationRatio = movementMutationRatio;
        this.logger = Logger.getInstance();
    }

    public Pair<Integer, Integer> findPath(char[][] maze, int mazeSize, int numAgentMoves) {

        Population originalPopulation = new Population("original", numAgents, numAgentMoves);
        Population intermediatePopulation = new Population("intermediate", numAgents, numAgentMoves);

        originalPopulation.start(0, 0);

        System.out.println("\n//// Genetic algorithm Execution ////");
        for (int generation = 0; generation < numGenerations; generation++) {

            logger.log("\n\n//// Generation: " + (generation));
            System.out.println("//// Generation: " + (generation));

            logger.log("\n\n" + originalPopulation.toString());

            // heuristic
            heuristicFunction(originalPopulation, maze, mazeSize);

            logger.log("\n\n" + originalPopulation.toString());

            Agent solverAgent = originalPopulation.searchSolution();

            if (solverAgent != null) {
                String solutionString = "[SOLUTION]: Found at generation " + generation;
                List<Pair<Integer,Integer>> path = solverAgent.getLastCoordinates();
                logger.log(solutionString + "\n" + path);
                System.out.println(solutionString + "\n" + path);
                return path.get(path.size()-1);
            }

            // elitism
            Agent bestFitAgent = elitismAlgorithm(originalPopulation);
            intermediatePopulation.addAgent(bestFitAgent);

            // crossover
            crossoverAlgorithm(originalPopulation, intermediatePopulation);

            logger.log("\n\n" + intermediatePopulation.toString());

            // mutation
            intermediatePopulation.mutate(agentMutationRatio, movementMutationRatio);

            // update current population
            originalPopulation.copy(intermediatePopulation);
            intermediatePopulation.clear();

        }
        System.out.println("Did not found path :(");
        return Pair.with(-1, -1);
    }

    public void heuristicFunction(Population population, char[][] maze, int mazeSize) {
        for (Agent agent: population.getAgents()) {

            agent.resetCoordinates();
            List<Pair<Integer, Integer>> agentCoordinates = new ArrayList<>();

            logger.log("\n\n[HEURISTIC] Agent entry: " + agent.toString());

            for (String move : agent.getMoves()) {
                agentCoordinates.add(agent.getCoordinates());
                if (agent.foundWayOut()) break;
                scoreMove(agent, move, maze, mazeSize);
            }

            if (!agent.foundWayOut()) {
                agent.updateScore(+1000);
            }

            logger.log("\n\n[HEURISTIC] Agent out: " + agent.toString());
            logger.log("\n\n[HEURISTIC]: Agent coordinates: " + agentCoordinates.toString() + "\n");

            agent.setLastCoordinates(agentCoordinates);
        }
    }

    public void scoreMove(Agent agent, String move, char[][] maze, int mazeSize) {

        Pair<Integer, Integer> agentNextCoordinate = agent.mapNextPosition(move);
        int nextX = agentNextCoordinate.getValue0();
        int nextY = agentNextCoordinate.getValue1();

        if ( nextX >= 0 && nextX < mazeSize && nextY >= 0 && nextY < mazeSize) {

            char mazeCell = maze[nextX][nextY];

            switch (mazeCell) {
                case 'S':
                    agent.setCoordinate(agentNextCoordinate);
                    agent.setFoundWayOut(true);
                    break;
                case 'E':
                case '0':
                    agent.setCoordinate(agentNextCoordinate);
                    break;
                case '1':
                case 'B':
                    agent.updateScore(+5);
                    break;
            }
        }
        else {
            agent.updateScore(+20);
        }
    }

    public Agent elitismAlgorithm(Population population) {
        List<Agent> agents = population.getAgents();
        Agent pivot = agents.get(0);

        logger.log("\n\n[ELITISM] Pivot: " + pivot.toString());

        for (Agent agent : agents) {
            if (agent.getScore() < pivot.getScore()) {
                pivot = agent;
            }
        }

        Agent bestAgent = (Agent)SerializationUtils.clone(pivot);

        logger.log("\n\n[ELITISM] Best agent: " + bestAgent.toString());

        bestAgent.reset();

        return bestAgent;
    }

    public Agent tournamentAlgorithm(Population population, Agent excludedAgent) {
        List<Agent> agents = population.getAgents();
        Pair <Integer, Integer> agentsIndexPair = getRandomPairOfAgentIndexes(population.getNumAgents(), excludedAgent);

        int firstAgentId = agentsIndexPair.getValue0();
        int secondAgentId = agentsIndexPair.getValue1();

        Agent firstAgent = agents.get(firstAgentId);
        Agent secondAgent = agents.get(secondAgentId);

        logger.log("\n\n[TOURNAMENT] Agent 1: " + firstAgent.toString());
        logger.log("\n\n[TOURNAMENT] Agent 2: " + secondAgent.toString());

        if ( firstAgent.getScore() < secondAgent.getScore()) {
            return firstAgent;
        }
        else {
            return secondAgent;
        }
    }

    public void crossoverAlgorithm(Population originalPopulation, Population intermediatePopulation) {

        Random random = new Random();

        for (int childId = 1; childId < intermediatePopulation.getNumAgents(); childId++) {

            Agent child = new Agent(0,0, childId);

            Agent father = tournamentAlgorithm(originalPopulation, null);
            Agent mother = tournamentAlgorithm(originalPopulation, father);

            logger.log("\n\n[CROSSOVER] Father: " + father.toString());
            logger.log("\n\n[CROSSOVER] Mother: " + mother.toString());

            for (int move = 0; move < intermediatePopulation.getNumAgentMoves(); move ++ ) {
                int mask = random.nextInt(2);

                if (mask == 0) {
                    child.getMoves().add(father.getMoves().get(move));
                }
                else if (mask == 1) {
                    child.getMoves().add(mother.getMoves().get(move));
                }
            }

            logger.log("\n\n[CROSSOVER] Child: " + child.toString());

            intermediatePopulation.addAgent(child);
        }
    }

    public Pair<Integer, Integer> getRandomPairOfAgentIndexes(int boundary, Agent excludedAgent) {
        Random random = new Random();

        List<Integer> availableAgentIndexes = IntStream.rangeClosed(0, boundary - 1)
                .boxed()
                .collect(Collectors.toList());

        if (excludedAgent != null) {
            availableAgentIndexes.remove(excludedAgent.getIdentifier());
            boundary --;
        }

        int firstRandomIndex = random.nextInt(boundary);
        int firstAgentId = availableAgentIndexes.remove(firstRandomIndex);

        int secondRandomIndex = random.nextInt(boundary - 1);
        int secondAgentId = availableAgentIndexes.remove(secondRandomIndex);

        return Pair.with(firstAgentId, secondAgentId);
    }
}