package app;

// project dependencies
import algorithms.genetic.GeneticAlgorithm;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Main application class
 */
public class App {
    /*
    NOTES:
      Expected input:
        $ java main -p {--path} ~/UserX/... -g {--generations} 100 -s {--solutions} 5 [--debug]
      Min number of agents is 3
     */

    private static int sizeMatrix;
    private static String[][] matrix;

    public static void main( String[] args )
    {

        char[][] maze = {
                //0    1    2    3
                {'E', '0', '0', '0'}, // 0
                {'1', 'B', '1', '0'}, // 1
                {'0', '0', '0', '0'}, // 2
                {'1', 'B', 'S', '1'}  // 3
        };
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(3, 5, 9, 5);
        char[][] solution = geneticAlgorithm.findPath(maze, 4);

//        int[] in = new int[] {0,0};
//        int[] out = new int[] {3,2};

//        AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(in, out);
//        aStarAlgorithm.findPath(maze, 4);
//
//        String path = ""; //passado lá em cima no "expected input"
//        sizeMatrix = 0;

//        setMatrix(path);
    }

    public static void setMatrix(String path){
        try {
            BufferedReader file = new BufferedReader(new FileReader(path));

            int contLine = 0;

            while (file.ready()) {
                if(contLine != 0){
                    String line = file.readLine();
                    for(int i=0; i<sizeMatrix; i++){
                        matrix[contLine][i] = line.substring(i,i+1);
                    }
                }else{
                    sizeMatrix = Integer.parseInt(file.readLine());
                    matrix = new String[sizeMatrix][sizeMatrix];
                }
                contLine++;
            }

            file.close();

        }catch(Exception e){
            System.out.println("Cannot read the file.");
        }
    }
}
