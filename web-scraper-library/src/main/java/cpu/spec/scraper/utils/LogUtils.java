package cpu.spec.scraper.utils;

public abstract class LogUtils {
    public static String exceptionMessage(Exception e, String causedUrl) {
        return e.getClass().getSimpleName() + ": " + e.getMessage() + " (" + causedUrl + ")";
    }

    public static String exceptionMessage(Exception e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
}
