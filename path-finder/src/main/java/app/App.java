package app;

// external dependencies
import algorithms.AStarAlgorithm;
import logs.Logger;
import org.apache.commons.cli.*;

// project dependencies
import algorithms.genetic.GeneticAlgorithm;
import org.javatuples.Pair;

// built-in dependencies
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class App {

    private static int mazeSize = 0;
    private static int numAgentMoves = 0;
    private static char[][] maze;


    public static void main( String[] args ) {

        CommandLineParser parser = new DefaultParser();
        Options options = generateOptions();

        String syntax = "java -jar path-finder-jar-with-dependencies.jar " +
                "--filepath /home/documents/... --generations 100 --agents 10 " +
                "--agent-ratio 10 --move-ratio 15 --logfile /home/documents/...";

        String mazeFilePathSring = "";
        String logFilePathString = ".";
        String numGenerationString = "1";
        String numAgentsString = "3";
        String agentMutationRatioString = "60";
        String movementMutationRatioString = "1";
        boolean debug = false;

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
                debug = cmdLine.hasOption("d");
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


        Logger logger = Logger.getInstance();
        logger.setLoggerObject(logFilePathString, debug);

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
        Pair<Integer, Integer> solution = geneticAlgorithm.findPath(
                maze,
                mazeSize,
                numAgentMoves
        );

        if (!solution.equals(Pair.with(-1, -1))) {
            int[] in = new int[]{0,0};
            int[] out = new int[]{solution.getValue0(), solution.getValue1()};
            System.out.println(solution);

            for(int i=0; i<10; i++){
                for(int j=0; j<10; j++){
                    System.out.print(maze[i][j]);
                }
                System.out.println("");
            }

            AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(in, out);
            aStarAlgorithm.findPath(maze, mazeSize);
        }
    }

    public static Options generateOptions() {
        Options options = new Options();

        Option path = Option.builder("f").longOpt("filepath")
                .desc("path to maze file [REQUIRED]")
                .type(String.class)
                .hasArg()
                .build();
        Option generations = Option.builder("g").longOpt("generations")
                .desc("number of generations (default = 1) [OPTIONAL]")
                .type(Integer.class)
                .hasArg()
                .build();
        Option agents = Option.builder("a").longOpt("agents")
                .desc("number of agents, must be >= 3 (default=3) [OPTIONAL]")
                .type(Integer.class)
                .hasArg()
                .build();
        Option agentMutRatio = Option.builder("ar").longOpt("agent-ratio")
                .desc("agent mutation ratio, maps e.g. 5 -> 5% (default=10) [OPTIONAL]")
                .type(Integer.class)
                .hasArg()
                .build();
        Option moveMutRatio = Option.builder("mr").longOpt("move-ratio")
                .desc("movement mutation ratio, maps e.g 5 -> 5% (default=1) [OPTIONAL]")
                .type(Integer.class)
                .hasArg()
                .build();
        Option logFile = Option.builder("l").longOpt("log [OPTIONAL]")
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

        options.addOption("d", "debug", false, "turn on debugging [OPTIONAL]");
        options.addOption("h", "help", false, "show help");

        return options;
    }

    public static void loadMaze(String path) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(path));

            int fileLineCount = 0;

            String[] intermediateElements;

            char[] lineElements;
            char element;
            int mazeLineCount=0;

            while (file.ready()) {

                if (fileLineCount != 0) {
                    String line = file.readLine();
                    StringBuilder intermediateString = new StringBuilder();

                    intermediateElements = line.split(" ");

                    for (int i = 0; i < intermediateElements.length; i ++) {
                        intermediateString.append(intermediateElements[i]);
                    }

                    lineElements = intermediateString.toString().toCharArray();

                    if (mazeLineCount < mazeSize) {
                        for (int columnVal = 0; columnVal < lineElements.length; columnVal++) {
                            element = lineElements[columnVal];
                            if (element != ' ') element = lineElements[columnVal];
                            if (element == '0' || element == 'S') numAgentMoves++;
                            maze[mazeLineCount][columnVal] = element;
                        }
                        mazeLineCount++;
                    }
                }
                else {
                    mazeSize = Integer.parseInt(file.readLine());
                    maze = new char[mazeSize][mazeSize];
                }
                fileLineCount++;
            }
            file.close();
        }
        catch(IOException exception){
            System.out.println("File read failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }
}
