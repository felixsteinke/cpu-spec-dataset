package cpu.spec.scraper.exception;

public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String rootElement, String query) {
        super("Element in '" + rootElement + "' not found with query: '" + query + "'");
    }
}
