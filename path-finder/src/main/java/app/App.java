package app;

// project dependencies
import algorithms.GeneticAlgorithm;

/**
 * Main application class
 */
public class App 
{
    /*
    NOTES:
      Expected input:
        $ java main -p {--path} ~/UserX/... -g {--generations} 100 -s {--solutions} 5 [--debug]
     */

    public static void main( String[] args )
    {
        char[][] maze = {
                {'E', '0', '1'},
                {'1', '0', '1'},
                {'S', '1', '1'}
        };
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(3, 5, 3);
        char[][] solution = geneticAlgorithm.findPath(maze);

        String path = ""; //passado lá em cima no "expected input"

        setMatrix(path);
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
