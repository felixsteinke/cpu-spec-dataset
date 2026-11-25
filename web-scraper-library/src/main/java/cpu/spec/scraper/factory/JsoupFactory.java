package cpu.spec.scraper.factory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Factory to serve jsoup connections with a static evolving configuration.
 * The websites expect different user agents, check them with curl if requests are blocked, e.g.
 * {@code curl -s -o /dev/null -w "%{http_code}" -A "<user agent>" <url>}.
 */
public abstract class JsoupFactory {

    private static String referrerUrl = "https://www.google.com/";

    // AMD does not answer requests without a browser user agent (see https://www.useragentstring.com/)
    private static final String BROWSER_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:135.0) Gecko/20100101 Firefox/135.0";

    // Intel denies requests (HTTP 403) that claim to be a browser but are not one, the scraper identifies as a crawler
    private static final String CRAWLER_USER_AGENT = "Mozilla/5.0 (compatible; cpu-spec-scraper)";

    /**
     * @param url for the connection
     * @return connection with a browser user agent and custom configuration
     */
    public static Connection getConnection(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent(BROWSER_USER_AGENT);
        connection.referrer(referrerUrl);
        return connection;
    }

    /**
     * @param url for the connection
     * @return connection that identifies as a crawler, for websites that deny browser user agents of other clients
     */
    public static Connection getCrawlerConnection(String url) {
        return Jsoup.connect(url).userAgent(CRAWLER_USER_AGENT);
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
