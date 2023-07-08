package cpu.spec.scraper.factory;

import java.util.Date;
import java.util.logging.*;

/**
 * Factory to serve logger with a custom configuration.
 */
public abstract class LoggerFactory {
    private static final Level LOG_LEVEL = Level.ALL;

    private static final Logger LOGGER = Logger.getGlobal();

    static {
        LOGGER.setLevel(LOG_LEVEL); // Set desired log level
        LOGGER.setUseParentHandlers(false);
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
        LOGGER.addHandler(consoleHandler);
    }

    /**
     * @return custom logger object
     */
    public static Logger getLogger() {
        return LOGGER;
    }
}
