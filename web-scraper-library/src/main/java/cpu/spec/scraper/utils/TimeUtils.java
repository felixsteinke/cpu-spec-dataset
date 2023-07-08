package cpu.spec.scraper.utils;

import cpu.spec.scraper.factory.LoggerFactory;

import java.util.Random;
import java.util.logging.Logger;

public abstract class TimeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static long lastSleepTime = 0;

    public static void sleepBetween(long msBetween) {
        sleepBetween(msBetween, 0);
    }

    public static void sleepBetween(long msBetween, long minMs) {
        long currentTime = System.currentTimeMillis();
        long elapsedMs = currentTime - lastSleepTime;
        long remainingMs = msBetween - elapsedMs;

        if (elapsedMs < msBetween) {
            sleep(minMs, (long) Math.max(minMs * 1.25, remainingMs));
        }
        lastSleepTime = System.currentTimeMillis();
    }

    public static void sleep(long minMs, long maxMs) {
        if (minMs < 0) {
            minMs = minMs * (-1);
        }
        if (maxMs < 0) {
            maxMs = maxMs * (-1);
        }
        if (minMs > maxMs) {
            long temp = minMs;
            minMs = maxMs;
            maxMs = temp;
        }
        long randomMs = new Random().nextLong(maxMs - minMs + 1) + minMs;
        try {
            LOGGER.info("Sleep " + randomMs + " milliseconds.");
            Thread.sleep(randomMs);
        } catch (InterruptedException e) {
            LOGGER.warning(e.getMessage());
        }
    }
}
