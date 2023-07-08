package cpu.spec.scraper.validator;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class JsoupValidator {
    public static void validate(Element element, String rootElement, String query) throws ElementNotFoundException {
        if (element == null) {
            throw new ElementNotFoundException(rootElement, query);
        }
    }

    public static void validate(Elements elements, String rootElement, String query) throws ElementNotFoundException {
        if (elements == null || elements.isEmpty()) {
            throw new ElementNotFoundException(rootElement, query);
        }
    }
}
