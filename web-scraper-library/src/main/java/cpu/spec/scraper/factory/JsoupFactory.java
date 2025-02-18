package cpu.spec.scraper.factory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Factory to serve jsoup connections with a static evolving configuration.
 */
public abstract class JsoupFactory {

    private static String referrerUrl = "https://www.google.com/";

    // Needs to be updated at times in order for Intel to accept the connection (see https://www.useragentstring.com/)
    private static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:135.0) Gecko/20100101 Firefox/135.0";

    /**
     * @param url for the connection
     * @return connection with custom configuration
     */
    public static Connection getConnection(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent(userAgent);
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
