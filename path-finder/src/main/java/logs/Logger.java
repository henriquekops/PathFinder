package logs;

// built-in dependencies
import java.io.*;

public class Logger {

    private boolean debug;
    private FileWriter fileWriter;
    private static Logger INSTANCE;

    public static Logger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }

    public void setLoggerObject(String logFilePath, boolean debug) {
        File logFile = new File(logFilePath + "/path-finder-logs.txt");
        try {
            logFile.createNewFile();
            fileWriter = new FileWriter(logFile, true);
        } catch (IOException | SecurityException exception) {
            System.out.println("Logger build failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
        this.debug = debug;
    }

    public void log(String data) {
        try {
            fileWriter.write(data);
            if (debug) System.out.print(data);
        } catch (IOException exception) {
            System.out.println("Logging failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException exception) {
            System.out.println("Logger closing failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }
}