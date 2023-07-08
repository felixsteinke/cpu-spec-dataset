package cpu.spec.scraper.utils;

import java.util.List;

public abstract class LogUtils {
    public static String exceptionMessage(Exception e, String causedUrl) {
        return e.getClass().getSimpleName() + ": " + e.getMessage() + " (" + causedUrl + ")";
    }

    public static String exceptionMessage(Exception e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    public static String progressMessage(List<?> current, List<?> total, String object) {
        return "Extracted " + current.size() + " of " + total.size() + " " + object + ".";
    }
}
