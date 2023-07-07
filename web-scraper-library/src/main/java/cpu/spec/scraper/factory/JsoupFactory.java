package cpu.spec.scraper.factory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory to serve jsoup connections with a static evolving configuration.
 */
public abstract class JsoupFactory {

    private static String referrerUrl = "https://www.google.com/";
    private static Map<String, String> cookies = new HashMap<>();

    /**
     * @param url for the connection
     * @return jsoup connection with custom configuration
     */
    public static Connection getConnection(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.referrer(referrerUrl);
        connection.cookies(cookies);
        return connection;
    }

    /**
     * @param url for the referrer url configuration of the connection
     */
    public static void setReferrerUrl(String url) {
        if (url == null) {
            referrerUrl = "https://www.google.com/";
            return;
        }
        referrerUrl = url;
    }

    /**
     * @param responseCookies for cookie configuration of the connection (null for empty map)
     */
    public static void setCookies(Map<String, String> responseCookies) {
        if (responseCookies == null) {
            cookies = new HashMap<>();
            return;
        }
        cookies = responseCookies;
    }

    /**
     * @param responseCookies for cookie configuration of the connection
     */
    public static void appendCookies(Map<String, String> responseCookies) {
        if (responseCookies == null) {
            cookies = new HashMap<>();
            return;
        }
        cookies.putAll(responseCookies);
    }
}
