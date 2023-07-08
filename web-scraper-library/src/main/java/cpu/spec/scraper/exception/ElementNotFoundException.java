package cpu.spec.scraper.exception;

/**
 * Exception with appropriate message.
 */
public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String rootElement, String query, String url) {
        super("Element in '" + rootElement + "' not found with query: '" + query + "' (" + url + ")");
    }
}
