package cpu.spec.scraper.exception;

public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String element, String query) {
        super("Element in '" + element + "' not found with query: '" + query + "'");
    }
}
