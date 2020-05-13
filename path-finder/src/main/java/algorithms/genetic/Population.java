package algorithms.genetic;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Population {

    private final List<Agent> agents;

    private final int numAgents;
    private final int numAgentMoves;
    private final int numPossibleMoves = 8;

    private final String identifier;
    private final String[] possibleMoves = {"N", "S", "E", "W", "NE", "NW", "SE", "SW"};

    public Population(String identifier, int numAgents, int numAgentMoves) {
        this.identifier = identifier;
        this.numAgents = numAgents;
        this.numAgentMoves = numAgentMoves;
        this.agents = new ArrayList<>();
    }

    // GETTERS

    public int getNumAgentMoves() {
        return this.numAgentMoves;
    }

    public int getNumAgents() {
        return this.numAgents;
    }

    public List<Agent> getAgents() {
        return this.agents;
    }

    // SETTERS

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void copy(Population otherPopulation) {
        Collections.copy(this.agents, otherPopulation.agents);
    }

    public void clear() {
        agents.clear();
    }

    // OTHERS

    public void start(int startX, int startY) {
        Random random = new Random();

        for (int agentId = 0; agentId < numAgents; agentId++) {
            Agent agent = new Agent(startX, startY, agentId);

            for (int move = 0; move < numAgentMoves; move++) {
                String agentMove = possibleMoves[random.nextInt(numPossibleMoves)];
                agent.getMoves().add(agentMove);
            }
            this.agents.add(agent);
        }
    }

    public void mutate(int agentMutationRatio, int movementMutationRatio) {
        Random random = new Random();

        String currMove, newMove;
        int currMoveIdx, agentIdx;
        int numAgentIdxRange = numAgents;

        int numAgentMutations = (int)Math.ceil(numAgents*(agentMutationRatio/100.0));
        int numMovementMutations = (int)Math.ceil(numAgentMoves*(movementMutationRatio/100.0));

        System.out.println("\n");
        System.out.println("[MUTATION] Agent mutation ratio: " + numAgentMutations);
        System.out.println("[MUTATION] Movement mutation ratio: " + numMovementMutations);

        List<Integer> availableAgentIndexes = IntStream.rangeClosed(0, numAgents - 1)
                .boxed()
                .collect(Collectors.toList());

        for (int numAgentMutation = 0; numAgentMutation < numAgentMutations; numAgentMutation++) {
            agentIdx = availableAgentIndexes.remove(random.nextInt(numAgentIdxRange--));
            Agent mutantAgent = agents.get(agentIdx);

            System.out.println("[MUTATION] Agent entry: " + mutantAgent.toString());

            for (int numMoveMutation = 0; numMoveMutation <= numMovementMutations; numMoveMutation++) {

                currMoveIdx = random.nextInt(numAgentMoves);
                currMove = mutantAgent.getMoves().get(currMoveIdx);

                List<String> filteredPossibleMoves = new ArrayList<>();
                for (int move = 0; move < numPossibleMoves; move ++) {
                    String possibleMove = possibleMoves[move];
                    if (!possibleMove.equals(currMove)) {
                        filteredPossibleMoves.add(possibleMove);
                    }
                }

                newMove = filteredPossibleMoves.get(random.nextInt(numPossibleMoves-1));

                mutantAgent.getMoves().set(currMoveIdx, newMove);
            }

            System.out.println("[MUTATION] Agent out: " + mutantAgent.toString());
        }
    }

    public boolean containsSolution() {
        for (Agent agent : this.agents) {
            if ( agent.getScore() == 0 && agent.foundWayOut()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder populationStrBuilder = new StringBuilder();
        populationStrBuilder.append("[POPULATION] ").append(identifier).append(":\n");

        for (Agent agent : agents) {
            populationStrBuilder
                    .append("[Agent] ")
                    .append(agent.getIdentifier())
                    .append(": ")
                    .append(agent.getMoves())
                    .append(" | Score = ")
                    .append(agent.getScore())
                    .append("\n");
        }
        return populationStrBuilder.toString();
    }
}
