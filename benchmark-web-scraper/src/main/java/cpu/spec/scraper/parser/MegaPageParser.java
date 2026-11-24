package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.ChromeDriverFactory;
import cpu.spec.scraper.validator.SeleniumValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class MegaPageParser {
    private static final String ENTRY_URL = "https://www.cpubenchmark.net/CPU_mega_page.html";

    /**
     * @return cpu links for sub routing (url decoded = clean texts)
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks() throws Exception {
        WebDriver driver = ChromeDriverFactory.getDriver();
        SeleniumValidator validator = new SeleniumValidator(ENTRY_URL);
        List<String> specificationLinks = new ArrayList<>();

        try {
            driver.get(ENTRY_URL);
            Thread.sleep(250);

            // the page length <select> can be covered by a cookie consent overlay (EU),
            // therefore the DataTables API is used to show all rows instead of clicking the "All" option
            validator.findElement(driver, By.name("cputable_length"));
            ((JavascriptExecutor) driver).executeScript("jQuery('#cputable').DataTable().page.len(-1).draw();");
            Thread.sleep(1000);

            WebElement tableBody = validator.findElement(driver, By.cssSelector("#cputable > tbody"));

            // collect all hrefs within a single script call (much faster than one webdriver call per row)
            @SuppressWarnings("unchecked")
            List<Object> hrefs = (List<Object>) ((JavascriptExecutor) driver).executeScript(
                    "return [...arguments[0].querySelectorAll('tr')]" +
                            ".map(tr => tr.querySelector('a[href]'))" +
                            ".filter(a => a !== null)" +
                            ".map(a => a.href);", tableBody);

            for (Object hrefObject : hrefs) {
                String href = String.valueOf(hrefObject).replaceAll("cpu_lookup.php", "cpu.php");
                specificationLinks.add(URLDecoder.decode(href, StandardCharsets.UTF_8));
            }
        } catch (Exception exception) {
            driver.quit();
            throw exception;
        }
        driver.quit();
        return specificationLinks;
    }
}
