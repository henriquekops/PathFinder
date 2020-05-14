package app;

// external dependencies
import org.apache.commons.cli.*;

// project dependencies
import algorithms.genetic.GeneticAlgorithm;

// built-in dependencies
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main application class
 */
public class App {

    private static int mazeSize = 0;
    private static int numAgentMoves = 0;
    private static char[][] maze;

    private static String MAZE_PATH;
    private static String LOG_PATH = "";
    private static String NUM_GENS = "1";
    private static String NUM_AGENTS = "3";
    private static String AGENT_MUT_RATIO = "10";
    private static String MOVE_MUT_RATIO = "1";


    public static void main( String[] args ) {

        CommandLineParser parser = new DefaultParser();
        Options options = generateOptions();

        String syntax = "java app --filepath ~/Documents/... --generations 100 --agents 10 --logfile ~/Documents/...";

        try {
            CommandLine cmdLine = parser.parse(options, args);

            if (cmdLine == null || cmdLine.hasOption("h")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(syntax, options);
            }

        }
        catch(ParseException exception) {
            System.out.println("Parsing failed. Reason: " + exception.getMessage());
        }

        loadMaze(MAZE_PATH);
        int numGenerations = Integer.parseInt(NUM_GENS);
        int numAgents = Integer.parseInt(NUM_AGENTS);
        int agentMutationRatio = Integer.parseInt(AGENT_MUT_RATIO);
        int moveMutationRatio = Integer.parseInt(MOVE_MUT_RATIO);


        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                numGenerations,
                numAgents,
                agentMutationRatio,
                moveMutationRatio

        );
        char[][] solution = geneticAlgorithm.findPath(
                maze,
                mazeSize,
                numAgentMoves
        );

//        int[] in = new int[] {0,0};
//        int[] out = new int[] {3,2};

//        AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(in, out);
//        aStarAlgorithm.findPath(maze, 4);
//
//        String path = ""; //passado lá em cima no "expected input"
//        sizeMatrix = 0;
    }

    public static Options generateOptions() {
        Options options = new Options();

        Option path = Option.builder(MAZE_PATH).argName("p").longOpt("filepath")
                .desc("path to maze file")
                .required()
                .type(String.class)
                .build();
        Option generations = Option.builder(NUM_GENS).argName("g").longOpt("generations")
                .desc("number of generations (default = 1)")
                .required()
                .type(Integer.class)
                .build();
        Option agents = Option.builder(NUM_AGENTS).argName("a").longOpt("agents")
                .desc("number of agents, must be >= 3 (default=3)")
                .type(Integer.class)
                .build();
        Option agentMutRatio = Option.builder(AGENT_MUT_RATIO).argName("a").longOpt("agents")
                .desc("agent mutation ratio, maps e.g. 5 -> 5% (default=10)")
                .type(Integer.class)
                .build();
        Option moveMutRatio = Option.builder(MOVE_MUT_RATIO).argName("a").longOpt("agents")
                .desc("movement mutation ratio, maps e.g 5 -> 5% (default=1)")
                .type(Integer.class)
                .build();
        Option logFile = Option.builder(LOG_PATH).argName("l").longOpt("log")
                .desc("path to log file (default='/log')")
                .type(String.class)
                .build();

        options.addOption(path);
        options.addOption(generations);
        options.addOption(agents);
        options.addOption(agentMutRatio);
        options.addOption(moveMutRatio);
        options.addOption(logFile);

        options.addOption("h", "help", false, "show help");

        return options;
    }

    public static void loadMaze(String path) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(path));

            int lineCount = 0;
            char element;

            while (file.ready()) {

                if(lineCount != 0){
                    String line = file.readLine();

                    for(int lineVal=0; lineVal<mazeSize; lineVal++){
                        element = line.substring(lineVal,lineVal+1).toCharArray()[0];
                        if (element == '0' || element == 'S') numAgentMoves++;
                        maze[lineCount][lineVal] = element;
                    }
                }
                else{
                    mazeSize = Integer.parseInt(file.readLine());
                    maze = new char[mazeSize][mazeSize];
                }
                lineCount++;
            }
            file.close();
        }
        catch(IOException exception){
            System.out.println("File read failed. Reason: " + exception.getMessage());
        }
    }
}
