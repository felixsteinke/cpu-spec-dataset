package cpu.spec.scraper.validator;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class SeleniumValidator {
    public static void validate(WebElement element, String rootElement, String query) throws ElementNotFoundException {
        if (element == null) {
            throw new ElementNotFoundException(rootElement, query);
        }
    }

    public static void validate(List<WebElement> elements, String rootElement, String query) throws ElementNotFoundException {
        if (elements == null || elements.isEmpty()) {
            throw new ElementNotFoundException(rootElement, query);
        }
    }
}
