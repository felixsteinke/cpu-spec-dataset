package cpu.spec.scraper.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to serve chrome drivers with a static evolving configuration.
 */
public abstract class ChromeDriverFactory {
    private static boolean setup = true;
    private static String referrerUrl = "https://www.google.com/";
    private static Map<String, String> cookies = new HashMap<>();

    /**
     * @return chrome driver with initially executed setup and custom configuration
     */
    public static ChromeDriver getDriver() {
        if (setup) {
            WebDriverManager.chromedriver().setup();
            setup = false;
        }
        ChromeOptions options = new ChromeOptions();
        // Disable browser
        options.addArguments("--headless", "--ignore-certificate-errors");
        // Set User-Agent
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        // Set Referer URL
        options.addArguments("--referer=" + referrerUrl);
        // Set Cookies
        options.addArguments(transformCookiesToArguments(cookies));
        return new ChromeDriver(options);
    }

    /**
     * @param url for the referrer url configuration of the driver
     */
    public static void setReferrerUrl(String url) {
        if (url == null) {
            referrerUrl = "https://www.google.com/";
            return;
        }
        referrerUrl = url;
    }

    /**
     * @param responseCookies for cookie configuration of the driver (null for empty map)
     */
    public static void setCookies(Map<String, String> responseCookies) {
        if (responseCookies == null) {
            cookies = new HashMap<>();
            return;
        }
        cookies = responseCookies;
    }

    /**
     * @param responseCookies for cookie configuration of the driver
     */
    public static void appendCookies(Map<String, String> responseCookies) {
        if (responseCookies == null) {
            return;
        }
        cookies.putAll(responseCookies);
    }

    private static List<String> transformCookiesToArguments(Map<String, String> map) {
        List<String> list = new ArrayList<>();
        for (var entry : map.entrySet()) {
            list.add("--cookie=" + entry.getKey() + "=" + entry.getValue());
        }
        return list;
    }
}
