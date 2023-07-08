package cpu.spec.scraper.validator;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SeleniumValidator {

    private final String pageUrl;

    public SeleniumValidator(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<WebElement> findElements(WebDriver rootElement, By query) throws ElementNotFoundException {
        List<WebElement> elements = rootElement.findElements(query);
        validate(elements, rootElement.getTitle(), query.toString());
        return elements;
    }

    public List<WebElement> findElements(WebElement rootElement, By query) throws ElementNotFoundException {
        List<WebElement> elements = rootElement.findElements(query);
        validate(elements, rootElement.getTagName(), query.toString());
        return elements;
    }

    public WebElement findElement(WebElement rootElement, By query) throws ElementNotFoundException {
        WebElement element = rootElement.findElement(query);
        validate(element, rootElement.getTagName(), query.toString());
        return element;
    }

    public WebElement findElement(WebDriver rootElement, By query) throws ElementNotFoundException {
        WebElement element = rootElement.findElement(query);
        validate(element, rootElement.getTitle(), query.toString());
        return element;
    }

    private void validate(WebElement element, String rootElement, String query) throws ElementNotFoundException {
        if (element == null) {
            throw new ElementNotFoundException(rootElement, query, pageUrl);
        }
    }

    private void validate(List<WebElement> elements, String rootElement, String query) throws ElementNotFoundException {
        if (elements == null || elements.isEmpty()) {
            throw new ElementNotFoundException(rootElement, query, pageUrl);
        }
    }
}
