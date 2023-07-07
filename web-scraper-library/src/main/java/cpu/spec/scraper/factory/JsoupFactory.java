package cpu.spec.scraper.factory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Factory to serve jsoup connections with a static evolving configuration.
 */
public abstract class JsoupFactory {

    private static String referrerUrl = "https://www.google.com/";

    /**
     * @param url for the connection
     * @return connection with custom configuration
     */
    public static Connection getConnection(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.referrer(referrerUrl);
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
}
