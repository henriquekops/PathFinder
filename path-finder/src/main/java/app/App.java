package app;

// external dependencies
import org.apache.commons.cli.*;

// project dependencies
import algorithms.genetic.GeneticAlgorithm;

// built-in dependencies
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

/**
 * Main application class
 */
public class App {

    private static int mazeSize = 0;
    private static int numAgentMoves = 0;
    private static char[][] maze;


    public static void main( String[] args ) {

        CommandLineParser parser = new DefaultParser();
        Options options = generateOptions();

        String syntax = "java app --filepath ~/Documents/... --generations 100 --agents 10 --logfile ~/Documents/...";

        String mazeFilePathSring = "";
        String logFilePathString = ".";
        String numGenerationString = "1";
        String numAgentsString = "3";
        String agentMutationRatioString = "10";
        String movementMutationRatioString = "1";

        try {
            CommandLine cmdLine = parser.parse(options, args);

            if (cmdLine.getOptions().length == 0) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(syntax, options);
                System.exit(0);
            }

            if (cmdLine.hasOption("h")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(syntax, options);
                System.exit(0);
            }
            else {
                if (cmdLine.hasOption("f")) mazeFilePathSring = cmdLine.getOptionValue("f"); else return;
                if (cmdLine.hasOption("l")) logFilePathString = cmdLine.getOptionValue("l");
                if (cmdLine.hasOption("g")) numGenerationString = cmdLine.getOptionValue("g");
                if (cmdLine.hasOption("a")) numAgentsString = cmdLine.getOptionValue("a");
                if (cmdLine.hasOption("ar")) agentMutationRatioString = cmdLine.getOptionValue("ar");
                if (cmdLine.hasOption("mr")) movementMutationRatioString = cmdLine.getOptionValue("mr");
            }

        }
        catch(ParseException exception) {
            System.out.println("Parsing failed. Reason: " + exception.getMessage());
            System.exit(0);
        }

        if (mazeFilePathSring.length() == 0) { // TODO: Improve validation
            System.out.println("Read failed. Reason: file not found");
            System.exit(0);
        }

        loadMaze(mazeFilePathSring);

        int numGenerations = Integer.parseInt(numGenerationString);
        int numAgents = Integer.parseInt(numAgentsString);
        int agentMutationRatio = Integer.parseInt(agentMutationRatioString);
        int movementMutationRatio = Integer.parseInt(movementMutationRatioString);

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                numGenerations,
                numAgents,
                agentMutationRatio,
                movementMutationRatio

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

        Option path = Option.builder("f").longOpt("filepath")
                .desc("path to maze file")
                .type(String.class)
                .hasArg()
                .build();
        Option generations = Option.builder("g").longOpt("generations")
                .desc("number of generations (default = 1)")
                .type(Integer.class)
                .hasArg()
                .build();
        Option agents = Option.builder("a").longOpt("agents")
                .desc("number of agents, must be >= 3 (default=3)")
                .type(Integer.class)
                .hasArg()
                .build();
        Option agentMutRatio = Option.builder("ar").longOpt("agent-ratio")
                .desc("agent mutation ratio, maps e.g. 5 -> 5% (default=10)")
                .type(Integer.class)
                .hasArg()
                .build();
        Option moveMutRatio = Option.builder("mr").longOpt("move-ratio")
                .desc("movement mutation ratio, maps e.g 5 -> 5% (default=1)")
                .type(Integer.class)
                .hasArg()
                .build();
        Option logFile = Option.builder("l").longOpt("log")
                .desc("path to log file (default='.')")
                .type(String.class)
                .hasArg()
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

            String[] intermediateElements;

            char[] lineElements;
            char element;

            while (file.ready()) {

                if (lineCount != 0) {
                    String line = file.readLine();
                    StringBuilder intermediateString = new StringBuilder();

                    intermediateElements = line.split(" ");

                    for (int i = 0; i < intermediateElements.length; i ++) {
                        intermediateString.append(intermediateElements[i]);
                    }

                    lineElements = intermediateString.toString().toCharArray();

                    if (lineCount < mazeSize) {
                        for (int lineVal = 0; lineVal < lineElements.length; lineVal++) {
                            element = lineElements[lineVal];
                            if (element != ' ') element = lineElements[lineVal];
                            if (element == '0' || element == 'S') numAgentMoves++;
                            maze[lineCount][lineVal] = element;
                        }
                    }
                }
                else {
                    mazeSize = Integer.parseInt(file.readLine());
                    maze = new char[mazeSize][mazeSize];
                }
                lineCount++;
            }
            file.close();
        }
        catch(IOException exception){
            System.out.println("File read failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }
}
