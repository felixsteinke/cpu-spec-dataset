package cpu.spec.scraper.factory;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Factory to serve chrome drivers with a static evolving configuration.
 * The matching chromedriver is resolved automatically by the Selenium Manager (built into Selenium since 4.6).
 */
public abstract class ChromeDriverFactory {
    /**
     * @return chrome driver with custom configuration
     */
    public static ChromeDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        // Disable browser
        options.addArguments("--headless", "--ignore-certificate-errors");
        // Set User-Agent
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        return new ChromeDriver(options);
    }
}
