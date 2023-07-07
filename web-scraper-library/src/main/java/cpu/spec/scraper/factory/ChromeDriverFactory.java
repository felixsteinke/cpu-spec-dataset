package cpu.spec.scraper.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.logging.Logger;

/**
 * Factory to serve chrome drivers with a static evolving configuration.
 */
public abstract class ChromeDriverFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverFactory.class);
    private static boolean setup = true;

    /**
     * @return chrome driver with initially executed setup and custom configuration
     */
    public static ChromeDriver getDriver() {
        if (setup) {
            LOGGER.info("Start setup chrome driver setup.");
            WebDriverManager.chromedriver().setup();
            setup = false;
            LOGGER.info("Finished chrome driver setup.");
        }
        ChromeOptions options = new ChromeOptions();
        // Disable browser
        options.addArguments("--headless", "--ignore-certificate-errors");
        // Set User-Agent
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        return new ChromeDriver(options);
    }
}
