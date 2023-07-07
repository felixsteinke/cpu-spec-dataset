package cpu.spec.scraper.exception;

/**
 * Exception with appropriate message.
 */
public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String rootElement, String query) {
        super("Element in '" + rootElement + "' not found with query: '" + query + "'");
    }
}
