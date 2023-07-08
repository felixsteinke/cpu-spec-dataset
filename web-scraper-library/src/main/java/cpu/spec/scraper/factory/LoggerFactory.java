package cpu.spec.scraper.factory;

import java.util.Date;
import java.util.logging.*;

/**
 * Factory to serve logger with a custom configuration.
 */
public abstract class LoggerFactory {
    private static final Level LOG_LEVEL = Level.ALL;

    /**
     * @param cls class where the logger is executed
     * @return custom logger object
     */
    public static Logger getLogger(Class<?> cls) {
        Logger logger = Logger.getLogger(cls.getClassLoader().getName());
        logger.setLevel(LOG_LEVEL); // Set desired log level
        logger.setUseParentHandlers(false);
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("\u001B[37m%1$tF %1$tT %2$s [%3$s]: %4$s\n",
                        new Date(record.getMillis()),
                        record.getSourceClassName(),
                        record.getLevel().getName(),
                        record.getMessage());
            }
        };

        // Create a console handler and set the formatter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        return logger;
    }
}
