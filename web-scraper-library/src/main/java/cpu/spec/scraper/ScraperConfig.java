package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;

import java.util.List;
import java.util.logging.Logger;

/**
 * Runtime options for all scrapers, passed as system properties (e.g. java -Dscraper.limit=5 -jar executable.jar).
 * <ul>
 *     <li>{@code scraper.limit}: only process the first N pages of each level, e.g. series and specification pages
 *     (smoke test, output to *.limited.csv)</li>
 *     <li>{@code scraper.delay}: milliseconds to wait between detail page requests</li>
 *     <li>{@code scraper.force}: replace the dataset instead of merging the records into it</li>
 * </ul>
 */
public abstract class ScraperConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * @return true if a smoke test limit is configured
     */
    public static boolean isLimited() {
        return limit() > 0;
    }

    /**
     * @param items all items (e.g. series or specification page links)
     * @return the first N items if a limit is configured, otherwise all items
     */
    public static <T> List<T> limit(List<T> items) {
        int limit = limit();
        if (limit > 0 && items.size() > limit) {
            LOGGER.warning("Smoke test: processing " + limit + " of " + items.size() + " items (scraper.limit).");
            return items.subList(0, limit);
        }
        return items;
    }

    /**
     * @return configured delay between detail page requests in milliseconds (default: defaultMillis)
     */
    public static long delayMillis(long defaultMillis) {
        return Long.getLong("scraper.delay", defaultMillis);
    }

    /**
     * @return true if datasets may be overwritten by runs with much fewer records
     */
    public static boolean isForced() {
        return Boolean.getBoolean("scraper.force");
    }

    /**
     * Waits the configured delay between requests.
     */
    public static void pause(long defaultMillis) {
        sleep(delayMillis(defaultMillis));
    }

    /**
     * Waits a fixed time (e.g. before a retry), independent of {@code scraper.delay}.
     */
    public static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static int limit() {
        return Integer.getInteger("scraper.limit", 0);
    }
}
