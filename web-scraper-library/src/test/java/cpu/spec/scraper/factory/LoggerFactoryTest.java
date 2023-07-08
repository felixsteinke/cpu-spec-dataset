package cpu.spec.scraper.factory;

import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

class LoggerFactoryTest {
    @Test
    void getLogger() {
        Logger logger = LoggerFactory.getLogger(LoggerFactory.class);
        logger.fine("Test Debugging");
        logger.info("Test Information");
        logger.warning("Test Warning");
        logger.severe("Test Error");
    }
}
