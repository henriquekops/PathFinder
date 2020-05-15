package logs;

// built-in dependencies
import java.io.*;

public class Logger {

    private FileWriter fileWriter;
    private static Logger INSTANCE;

    public static Logger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }

    public void setLoggerObject(String logFilePath) {
        File logFile = new File(logFilePath + "/path-finder-logs.txt");
        try {
            if(!logFile.createNewFile()) {
                boolean deleted = logFile.delete();
                boolean recreated = logFile.createNewFile();
                if (!deleted || !recreated ) {
                    throw new IOException("Logger failed to recreate file: delete=" + deleted
                            + " recreate=" + recreated);
                }
            }
            fileWriter = new FileWriter(logFile, true);
        } catch (IOException | SecurityException exception) {
            System.out.println("Logger build failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }

    public void log(String data) {
        try {
            fileWriter.write(data);
        } catch (IOException exception) {
            System.out.println("Logging failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }

    public void close() {
        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException exception) {
            System.out.println("Logger closing failed. Reason: " + exception.getMessage());
            System.exit(0);
        }
    }
}