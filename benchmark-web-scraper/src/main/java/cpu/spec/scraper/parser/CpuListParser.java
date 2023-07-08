package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.ChromeDriverFactory;
import cpu.spec.scraper.validator.SeleniumValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class CpuListParser {
    private static final String ENTRY_URL = "https://www.cpubenchmark.net/CPU_mega_page.html";

    /**
     * @return cpu links for sub routing (url decoded)
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks() throws Exception {
        WebDriver driver = ChromeDriverFactory.getDriver();
        SeleniumValidator validator = new SeleniumValidator(ENTRY_URL);
        List<String> specificationLinks = new ArrayList<>();

        try {
            driver.get(ENTRY_URL);
            Thread.sleep(250);

            WebElement selectElement = validator.findElement(driver, By.name("cputable_length"));
            Select select = new Select(selectElement);
            select.selectByVisibleText("All");
            Thread.sleep(250);

            WebElement tableBody = validator.findElement(driver, By.cssSelector("#cputable > tbody"));
            List<WebElement> tableRows = validator.findElements(tableBody, By.cssSelector("tr"));

            for (WebElement row : tableRows) {
                WebElement aSpec = row.findElement(By.tagName("a"));
                if (aSpec == null) {
                    continue;
                }
                specificationLinks.add(URLDecoder.decode(aSpec.getAttribute("href").replaceAll("cpu_lookup.php", "cpu.php"), StandardCharsets.UTF_8));
            }

        } catch (Exception exception) {
            driver.quit();
            throw exception;
        }
        driver.quit();
        return specificationLinks;
    }
}
