package cpu.spec.scraper.validator;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupValidator {
    private final String pageUrl;

    public JsoupValidator(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Elements select(Document rootElement, String query) throws ElementNotFoundException {
        Elements elements = rootElement.select(query);
        validate(elements, rootElement.tagName(), query);
        return elements;
    }

    public Elements select(Element rootElement, String query) throws ElementNotFoundException {
        Elements elements = rootElement.select(query);
        validate(elements, rootElement.tagName(), query);
        return elements;
    }

    public Element selectFirst(Element rootElement, String query) throws ElementNotFoundException {
        Element element = rootElement.selectFirst(query);
        validate(element, rootElement.tagName(), query);
        return element;
    }

    public Element selectFirst(Document rootElement, String query) throws ElementNotFoundException {
        Element element = rootElement.selectFirst(query);
        validate(element, rootElement.tagName(), query);
        return element;
    }

    private void validate(Element element, String rootElement, String query) throws ElementNotFoundException {
        if (element == null) {
            throw new ElementNotFoundException(rootElement, query, pageUrl);
        }
    }

    private void validate(Elements elements, String rootElement, String query) throws ElementNotFoundException {
        if (elements == null || elements.isEmpty()) {
            throw new ElementNotFoundException(rootElement, query, pageUrl);
        }
    }
}
