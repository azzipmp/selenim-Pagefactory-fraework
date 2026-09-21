package framework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {

    private static final Logger LOGGER = Logger.getLogger(LoggerUtil.class.getName());
    private static final String LOG_FILE = "target/logs/framework.log";

    static {
        configureFileLogger();
    }

    private LoggerUtil() {
    }

    private static void configureFileLogger() {
        try {
            Path logPath = Paths.get(LOG_FILE);
            if (logPath.getParent() != null) {
                Files.createDirectories(logPath.getParent());
            }

            FileHandler fileHandler = new FileHandler(logPath.toString(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(true);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            throw new RuntimeException("Unable to configure file logger at " + LOG_FILE, e);
        }
    }

    public static String logFilePath() {
        return Paths.get(LOG_FILE).toAbsolutePath().toString();
    }

    public static void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}
